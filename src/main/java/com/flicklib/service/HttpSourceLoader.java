package com.flicklib.service;

import eu.somatik.moviebrowser.tools.IOTools;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads a http request
 * @author francisdb
 */
public class HttpSourceLoader implements SourceLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSourceLoader.class);
    

    @Override
    public String load(String url) throws IOException {
        HttpClient client = new HttpClient();
        String source = null;
        GetMethod httpMethod = null;
        InputStream is = null;
        try{
            LOGGER.info("Loading "+url);
            httpMethod = new GetMethod(url);
            client.executeMethod(httpMethod);
            LOGGER.info("Finished loading at "+httpMethod.getURI().toString());
            is = httpMethod.getResponseBodyAsStream();
            source = IOTools.inputSreamToString(is);
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException ex){
                    LOGGER.error("Could not close InputStream", is);
                }
            }
            if(httpMethod != null){
                httpMethod.releaseConnection();
            }
        }
        return source;
    }

}