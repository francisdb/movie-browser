package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.tools.IOTools;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class HttpSourceLoader implements SourceLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSourceLoader.class);
    
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
        InputStream is = null;
        try{
            method = new GetMethod(url);
            client.executeMethod(method);
            LOGGER.info(method.getURI().toString());
            is = method.getResponseBodyAsStream();
            source = IOTools.inputSreamToString(is);
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException ex){
                    LOGGER.error("Could not close InputStream", is);
                }
            }
            if(method != null){
                method.releaseConnection();
            }
        }
        return source;
    }

}
