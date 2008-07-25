/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Source;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author francisdb
 */
public class HttpLoader {
    
    /**
     * Loads a http request and parses it to a jericho source
     * @param url
     * @return
     * @throws java.io.IOException
     */
    public Source fetch(String url) throws IOException {
        HttpClient client = new HttpClient();
        Source source = null;
        HttpMethod method = null;
        try{
            method = new GetMethod(url);
            client.executeMethod(method);
            source = new Source(method.getResponseBodyAsStream());
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();
        }finally{
            if(method != null){
                method.releaseConnection();
            }
        }
        return source;
    }

}
