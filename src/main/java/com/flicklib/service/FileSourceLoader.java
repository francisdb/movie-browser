package com.flicklib.service;

import eu.somatik.moviebrowser.tools.IOTools;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a page source file from the class path
 * @author francisdb
 */
public class FileSourceLoader implements SourceLoader {

    @Override
    public String load(String url) throws IOException {
        return getOrPost(url);
    }
    
    private String getOrPost(String url) throws IOException {
        String source = null;
        InputStream fis = null;
        try {
            fis = FileSourceLoader.class.getClassLoader().getResourceAsStream(url);
            source = IOTools.inputSreamToString(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return source;
    }
}
