/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flicklib.tools;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author francisdb
 */
public class IOTools {

    private IOTools() {
        // Utility class
    }

    
    
    /**
     * Reads an inputstream to String
     * @param in
     * @return the result from reading the stream
     * @throws java.io.IOException
     */
    public static String inputSreamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}
