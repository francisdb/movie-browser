/*
 * Configuration.java
 *
 * Created on May 11, 2007, 10:55:35 PM
 *
 */
package eu.somatik.moviebrowser.config;

import com.google.inject.Singleton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton implementation for the settings
 * @author francisdb
 */
@Singleton
public class SettingsImpl implements Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsImpl.class);
    
    private static final String SETTINGS_DIR = ".moviebrowser";
    private static final String IMG_CACHE = "images";
    private static final String PREFERENCES = "preferences.properties";
    private static final String FOLDER_SETTINGS = "folders.lst";

    /**
     *
     * @return the folders
     */
    @Override
    public final Set<String> loadFolders() {
        Set<String> folders = new HashSet<String>();
        File folderSettings = openFolderSettings();
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(folderSettings);

            bufferedReader =
                    new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                if (line.trim().length() != 0) {
                    folders.add(line);
                    LOGGER.info("Search folder: " + line);
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("File input error: ", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close reader: ", ex);
                }
            }
        }
        return folders;
    }

    @Override
    public void addFolder(File newFolder) {
        final Set<String> folders = loadFolders();
        folders.add(newFolder.getAbsolutePath());
        saveFolders(folders);
    }

    /**
     *
     * @param folders
     */
    @Override
    public final void saveFolders(Set<String> folders) {
        File folderSettings = openFolderSettings();
        FileWriter writer; // declare a file output object
        PrintWriter printWriter; // declare a print stream object

        try {
            writer = new FileWriter(folderSettings, false);
            printWriter = new PrintWriter(writer);
            for (String folder : folders) {
                if (folder.trim().length() != 0) {
                    printWriter.println(folder);
                }
            }
            printWriter.close();
        } catch (IOException ex) {
            LOGGER.error("Error writing to file: ", ex);
        }
    }

    private File openFolderSettings() {
        File settingsDir = new File(System.getProperty("user.home"), SETTINGS_DIR);
        settingsDir.mkdirs();

        File folderSettings = new File(settingsDir, FOLDER_SETTINGS);
        if (!folderSettings.exists()) {
            try {
                LOGGER.info("First run, creating " + folderSettings.getAbsolutePath());
                boolean succes = folderSettings.createNewFile();
                if (!succes) {
                    throw new IOException("Could not create file: " + folderSettings.getAbsolutePath());
                }
            } catch (IOException ex) {
                LOGGER.error("File io error: ", ex);
            }
        }

        return folderSettings;
    }

    @Override
    public File getSettingsDir() {
        File settingsDir = new File(System.getProperty("user.home"), SETTINGS_DIR);
        if (!settingsDir.exists()) {
            settingsDir.mkdirs();
        }
        return settingsDir;
    }

    /**
     * @return the imageCacheDir
     */
    @Override
    public File getImageCacheDir() {
        File cache = new File(getSettingsDir(), IMG_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }
    
    
    private Properties defaultPreferences(){
        Properties properties = new Properties();
        properties.put("lookandfeel", UIManager.getSystemLookAndFeelClassName());
        return properties;
    }

    @Override
    public Map<String, String> loadPreferences() {
        Properties props = defaultPreferences();
        File prefsFile = new File(getSettingsDir(), PREFERENCES);
        InputStream is = null;
        try{
            if(prefsFile.exists()){
                is = new FileInputStream(prefsFile);
                props.load(is);
            }
         }catch(IOException ex){
            LOGGER.error("Could not load preferences to "+prefsFile.getAbsolutePath(), ex);
        }catch(SecurityException ex){
            LOGGER.error("Could not load preferences to "+prefsFile.getAbsolutePath(), ex);
        }finally{
            if(is!=null){
                try{
                    is.close();
                }catch(IOException ex){
                    LOGGER.error("Could not load outputstream for"+prefsFile.getAbsolutePath(), ex);
                }
            }
        }
        Map<String, String> preferences = new HashMap<String, String>();
        for(Map.Entry<Object,Object> entry:props.entrySet()){
            preferences.put((String)entry.getKey(), (String)entry.getValue());
        }
        return preferences;
    }

    @Override
    public void savePreferences(Map<String, String> preferences) {
        Properties props = new Properties();
        for(Map.Entry<String,String> entry:preferences.entrySet()){
            props.put(entry.getKey(), entry.getValue());
        }
        File prefsFile = new File(getSettingsDir(), PREFERENCES);
        OutputStream os = null;
        try{
            os = new FileOutputStream(prefsFile);
            props.store(os, "Movie browser configuration file");
        }catch(IOException ex){
            LOGGER.error("Could not save preferences to "+prefsFile.getAbsolutePath(), ex);
        }catch(SecurityException ex){
            LOGGER.error("Could not save preferences to "+prefsFile.getAbsolutePath(), ex);
        }finally{
            if(os!=null){
                try{
                    os.close();
                }catch(IOException ex){
                    LOGGER.error("Could not close outputstream for"+prefsFile.getAbsolutePath(), ex);
                }
            }
        }
    }

    @Override
    public boolean isDebugMode() {
        boolean debug = false;
        Map<String, String> prefs = loadPreferences();
        if(prefs.containsKey("debug") && "true".equals(prefs.get("debug"))){
            LOGGER.info("Starting in DEBUG mode!");
            debug = true;
        }
        return debug;    
    }
}
