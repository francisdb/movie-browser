package eu.somatik.moviebrowser.service;

import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author francisdb
 */
public class HttpSourceLoader implements SourceLoader {
    
    /**
     * Loads a http request and parses it to a jericho source
     * @param url
     * @return
     * @throws java.io.IOException
     */
    @Override
    public String load(String url) throws IOException {
        HttpClient client = new HttpClient();
        String source = null;
        HttpMethod method = null;
        try{
            method = new GetMethod(url);
            client.executeMethod(method);
            source = method.getResponseBodyAsString();
        }finally{
            if(method != null){
                method.releaseConnection();
            }
        }
        return source;
    }

}
