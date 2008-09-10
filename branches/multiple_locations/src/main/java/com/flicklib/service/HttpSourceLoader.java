/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.flicklib.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.tools.IOTools;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Loads a http request
 * @author francisdb
 */
public class HttpSourceLoader implements SourceLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSourceLoader.class);
    
    private final Integer timeout;

    @Inject
    public HttpSourceLoader(@Named(value="http.timeout") final Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String load(String url) throws IOException {
        HttpClient client = new HttpClient();
        if(timeout != null){
            // wait max x sec
            client.getParams().setSoTimeout(timeout);
            //LOGGER.info("Timeout = "+client.getParams().getSoTimeout());
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
