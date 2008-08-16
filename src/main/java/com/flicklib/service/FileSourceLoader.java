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

import com.flicklib.tools.IOTools;
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
