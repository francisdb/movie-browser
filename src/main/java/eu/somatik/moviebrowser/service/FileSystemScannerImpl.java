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
package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.api.FileSystemScanner;
import com.google.inject.Singleton;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author francisdb
 *
 */
@Singleton
public class FileSystemScannerImpl implements FileSystemScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemScannerImpl.class);

    private static final String IMDB_URLS[] = {
        "http://www.imdb.com/title/",
        "http://us.imdb.com/title/",
        "http://www.imdb.com/title?",
        "http://us.imdb.com/title?",
        "http://imdb.com/title/"
    };
	 
    
    /**
     * Locates she sample file and returns it
     * @param folder
     * @return the sample file or null if not found 
     */
    @Override
    public File findSample(File folder){
    	File sample = null;
        if(folder.isDirectory()){
            for(File file:folder.listFiles()){
                    if("sample".equals(file.getName().toLowerCase())){
                            for(File sampleFolderFile:file.listFiles()){
                                    if(sampleFolderFile.getName().toLowerCase().endsWith(".avi")){
                                            sample = sampleFolderFile;
                                    }
                            }
                    }
            }
        }
    	return sample;
    }
    
    /**
     * Locates parent directory name and returns it
     * @param folder
     * @return the parent dir name or null if not found 
     */
    @Override
    public File findParentDirectory(File folder) {
        File name = null;
        name = folder.getParentFile();
        return name;
    }
    
    /**
     * Finds the NFO file and looks for the imdb url inside it
     * @param dir
     * @return the nfo URL or null
     */
    @Override
    public String findNfoImdbUrl(File dir){
        String url = null;
        if(dir.isDirectory()){
            for(File file:dir.listFiles()){
                if(file.getName().toLowerCase().endsWith(".nfo")){
                    LOGGER.debug("checking nfo: "+file.getName());
                    url = findImdbUrl(file); 
                }
            }
        }
        return url;
    }
    
    private String findImdbUrl(File nfoFile){
    	String url = null;
        DataInputStream dis = null;
        FileInputStream fis = null;
    	try{
            fis = new FileInputStream(nfoFile);
            dis = new DataInputStream(fis);
            int x= fis.available();
            byte b[]= new byte[x];
            dis.readFully(b);
            
            String content = new String(b).toLowerCase();
            int start = -1;
            int i = 0;
            String urlStart = null;
            while(start == -1 && i < IMDB_URLS.length){
                urlStart = IMDB_URLS[i];
                //System.out.println("looking for "+urlStart);
                start = content.indexOf(urlStart);
                i++;
            }
            
            if(start != -1){
                i = start + urlStart.length();
                int end = -1;
                char character;
                while(i < content.length()-1 && end == -1){
                    character = content.charAt(i);
                    //System.out.println((int)character);
                    if(character == '\n' || character == '\t' || character == '\n' || character == (char)32){
                        end = i;
                    }
                    i++;
                }
                
                //if the url is the end of the file
                if(i == content.length()-1){
                    end = content.length()-1;
                }
                
                url = content.substring(start,end);
                LOGGER.info("IMDB url found: "+url);
            }
            
        }catch(IOException ex){
            LOGGER.error("Could not find IMDB url", ex);
        }finally{
            if(dis != null){
                try {
                    dis.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close nfo file", ex);
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close nfo file", ex);
                }
            }
        }
        return url;
    }
    
}
