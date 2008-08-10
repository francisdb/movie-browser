package com.flicklib.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.tools.IOTools;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.config.Settings;

/**
 * Loads a http request
 * @author francisdb
 */
public class HttpSourceLoader implements SourceLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSourceLoader.class);
    
    private final Settings settings;

    @Inject
    public HttpSourceLoader(final Settings settings) {
        this.settings = settings;
    }
    
    

    @Override
    public String load(String url) throws IOException {
        HttpClient client = new HttpClient();
        if(settings != null){
            // wait max x sec
            client.getParams().setSoTimeout(settings.getSiteTimout());
            //LOGGER.info("Tiemout = "+client.getParams().getSoTimeout());
        }
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
