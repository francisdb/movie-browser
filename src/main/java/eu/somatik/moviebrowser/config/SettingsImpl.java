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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.domain.MovieService;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.tools.FileTools;

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
    
    private static final String FOLDERS_PROPERTY = "folders";
    private static final String LOOK_AND_FEEL_PROPERTY = "lookandfeel";
    private static final String RENAME_TITLES = "renameTitles";
    private static final String SAVE_ALBUM_ART = "saveAlbumArt";
    
    private Map<String, String> preferences;

    /**
     *
     * @return the folders
     */
    @Override
    public final Set<String> loadFolders() {
        Map<String, String> prefs = loadPreferences();
        String folderString = prefs.get(FOLDERS_PROPERTY);
        if(folderString == null){
            folderString = "";
        }
        String[] folderStrings = folderString.split(File.pathSeparator);
        Set<String> folders = new LinkedHashSet<String>();
        for (String folder : folderStrings) {
            folders.add(folder);
        }
        return folders;
    }

    @Override
    public void addFolder(File newFolder) {
        final Set<String> folders = loadFolders();
        if (!folders.contains(newFolder.getAbsolutePath())) {
            folders.add(newFolder.getAbsolutePath());
            saveFolders(folders);
        } else {
            LOGGER.warn("Trying to add folder that is allready in the list: " + newFolder);
        }
    }

    /**
     *
     * @param folders
     */
    @Override
    public final void saveFolders(Set<String> folders) {
        StringBuilder folderString = new StringBuilder();
        for (String folder : folders) {
            if (folder.trim().length() != 0) {
                if(folderString.length() > 0){
                    folderString.append(File.pathSeparator);
                }
                folderString.append(folder);
            }
        }

        Map<String, String> prefs = loadPreferences();
        prefs.put(FOLDERS_PROPERTY, folderString.toString());
        savePreferences(prefs);
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
        properties.put("flags.service.flixster","true");
        properties.put("flags.service.rottenttomatoes","true");
        properties.put("flags.service.google","true");
        properties.put("flags.service.movieweb","true");
        properties.put("flags.service.imdb","true");
        return properties;
    }

    @Override
    public Map<String, String> loadPreferences() {
        if (preferences==null) {
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
            preferences = new HashMap<String, String>();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                preferences.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return preferences;
    }

    @Override
    public void savePreferences(Map<String, String> preferences) {
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
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
        Map<String,String> prefs = loadPreferences();
        prefs.put(RENAME_TITLES, Boolean.valueOf(value).toString());
        savePreferences(prefs);
    }

    @Override
    public boolean getRenameTitles() {
        Map<String,String> prefs = loadPreferences();
        String value = prefs.get(RENAME_TITLES);
        return Boolean.valueOf(value);
    }
    
    @Override
    public void setSaveAlbumArt(boolean value) {
        Map<String,String> prefs = loadPreferences();
        prefs.put(SAVE_ALBUM_ART, Boolean.valueOf(value).toString());
        savePreferences(prefs);
    }

    @Override
    public boolean getSaveAlbumArt() {
        Map<String,String> prefs = loadPreferences();
        String value = prefs.get(SAVE_ALBUM_ART);
        return Boolean.valueOf(value);
    }

    @Override
    public String getLookAndFeelClassName() {
        Map<String, String> prefs = loadPreferences();
        return prefs.get(LOOK_AND_FEEL_PROPERTY);
    }

    @Override
    public void setLookAndFeelClassName(String className) {
        Map<String, String> prefs = loadPreferences();
        prefs.put(LOOK_AND_FEEL_PROPERTY, className);
        savePreferences(prefs);
    }

    @Override
    public boolean isServiceEnabled(String name, boolean defaultValue) {
        Map<String, String> prefs = loadPreferences();
        String value = prefs.get("flags.service."+name);
        if (value==null) {
            return defaultValue;
        } else {
            return Boolean.valueOf(value);
        }
    }

    @Override
    public void setServiceEnabled(String name, boolean value) {
        Map<String, String> prefs = loadPreferences();
        prefs.put("flags.service."+name, Boolean.toString(value));
        savePreferences(prefs);
    }

    private String getPreferredServiceName() {
        Map<String, String> prefs = loadPreferences();
        return prefs.get("pref.service");
    }

    @Override
    public MovieService getPreferredService() {
        try {
            String name = getPreferredServiceName();
            return MovieService.valueOf(name);
        } catch (NullPointerException e) {
        } catch (IllegalArgumentException e) {
        }
        return MovieService.IMDB;
    }

    public void setPreferredService(MovieService service) {
        Map<String, String> prefs = loadPreferences();
        prefs.put("pref.service", service.name());
        savePreferences(prefs);
    }
}
