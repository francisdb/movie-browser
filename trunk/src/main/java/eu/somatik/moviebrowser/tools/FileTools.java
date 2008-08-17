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
package eu.somatik.moviebrowser.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class FileTools {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTools.class);

    private FileTools() {
    }


    /**
     * Deletes a directory and all its subdirectories
     * @param path
     * @return
     */
    public static boolean deleteDirectory(File path) {
        LOGGER.debug("Deleting recursively: "+path.getAbsolutePath());
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    boolean deleted = files[i].delete();
                    if(!deleted){
                        LOGGER.error("Could not delete: "+files[i].getAbsolutePath());
                    }
                }
            }
        }
        return path.delete();
    }
    
    /**
     * Renames a directory.
     * @param oldFile, newFile
     * @return boolean success
     */
    public static boolean renameDir(File oldFile, File newFile) {
        boolean success=false;
        if (newFile != null) {
            success = oldFile.renameTo(newFile);
        }
        return success;
    }
    
    /**
     * Loads a properties file
     * @param propsFile
     * @return the properties (empty if the propsFile does not exist)
     */
    public static Properties loadProperties(File propsFile){
        Properties props = new Properties();
        InputStream is = null;
        try {
            if (propsFile.exists()) {
                is = new FileInputStream(propsFile);
                props.load(is);
            }
        } catch (IOException ex) {
            LOGGER.error("Could not load properties from " + propsFile.getAbsolutePath(), ex);
        } catch (SecurityException ex) {
            LOGGER.error("Could not load preferences from " + propsFile.getAbsolutePath(), ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close inputstream for" + propsFile.getAbsolutePath(), ex);
                }
            }
        }
        return props;
    }
    
    /**
     * Stores a properties file, will create the path if needed
     * @param propsFile
     * @param the properties
     */
    public static void storePropeties(Properties properties, File propsFile) {
        File parent = propsFile.getParentFile();
        if(!parent.exists()){
            LOGGER.debug("Recursively creating folder for properties: "+parent.getAbsolutePath());
            parent.mkdirs();
        }
        propsFile.getParentFile().mkdirs();
        OutputStream os = null;
        try{
            os = new FileOutputStream(propsFile);
            properties.store(os, "Database version file");
        }catch(IOException ex){
            LOGGER.error("Could not save preferences to "+propsFile.getAbsolutePath(), ex);
        }catch(SecurityException ex){
            LOGGER.error("Could not save preferences to "+propsFile.getAbsolutePath(), ex);
        }finally{
            if(os!=null){
                try{
                    os.close();
                }catch(IOException ex){
                    LOGGER.error("Could not close outputstream for"+propsFile.getAbsolutePath(), ex);
                }
            }
        }
    }
}
