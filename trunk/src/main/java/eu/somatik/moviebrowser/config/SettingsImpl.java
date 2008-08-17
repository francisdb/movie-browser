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
package eu.somatik.moviebrowser.config;

import com.google.inject.Singleton;
import eu.somatik.moviebrowser.tools.FileTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
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
    private boolean renameTitles;

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

    private Properties defaultPreferences() {
        Properties properties = new Properties();
        properties.put("lookandfeel", UIManager.getSystemLookAndFeelClassName());
        return properties;
    }

    @Override
    public Map<String, String> loadPreferences() {
        Properties props = defaultPreferences();
        File prefsFile = new File(getSettingsDir(), PREFERENCES);
        InputStream is = null;
        try {
            if (prefsFile.exists()) {
                is = new FileInputStream(prefsFile);
                props.load(is);
            }
        } catch (IOException ex) {
            LOGGER.error("Could not load preferences to " + prefsFile.getAbsolutePath(), ex);
        } catch (SecurityException ex) {
            LOGGER.error("Could not load preferences to " + prefsFile.getAbsolutePath(), ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not load outputstream for" + prefsFile.getAbsolutePath(), ex);
                }
            }
        }
        Map<String, String> preferences = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            preferences.put((String) entry.getKey(), (String) entry.getValue());
        }
        return preferences;
    }

    @Override
    public void savePreferences(Map<String, String> preferences) {
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            props.put(entry.getKey(), entry.getValue());
        }
        File prefsFile = new File(getSettingsDir(), PREFERENCES);
        FileTools.storePropeties(props, prefsFile);
    }

    @Override
    public boolean isDebugMode() {
        boolean debug = false;
        Map<String, String> prefs = loadPreferences();
        if (prefs.containsKey("debug") && "true".equals(prefs.get("debug"))) {
            LOGGER.info("Starting in DEBUG mode!");
            debug = true;
        }
        return debug;
    }

    @Override
    public String getApplicationVersion() {
        String version = null;
        InputStream is = null;
        try {
            String pom = "META-INF/maven/org.somatik/moviebrowser/pom.properties";
            URL resource = SettingsImpl.class.getClassLoader().getResource(pom);
            if (resource == null) {
                throw new IOException("Could not load pom properties: " + pom);
            }
            is = resource.openStream();
            Properties props = new Properties();
            props.load(is);
            version = props.getProperty("version");
        } catch (IOException ex) {
            LOGGER.error("Could not read pom.properties", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close InputStream", ex);
                }
            }
        }
        return version;
    }

    @Override
    public String getLatestApplicationVersion() {
        String latestVersion = null;
        String latestVersionInfoURL = "http://movie-browser.googlecode.com/svn/site/latest";
        LOGGER.info("Checking latest version info from: " + latestVersionInfoURL);
        BufferedReader in = null;
        try {
            // Set up the streams
            LOGGER.info("Fetcing latest version info from: " + latestVersionInfoURL);
             URL url = new URL(latestVersionInfoURL); 
            
            // Read all the text returned by the server
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                latestVersion = str;
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching latest version info from: " + latestVersionInfoURL, ex);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                LOGGER.error("Could not close inputstream", ex);
            }
        }
        return latestVersion;
    }
    
    @Override
    public void setRenameTitles(boolean value) {
        renameTitles = value;
    }
    
    @Override
    public boolean getRenameTitles() {
        return renameTitles;
    }
}
