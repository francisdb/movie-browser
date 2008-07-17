/*
 * Configuration.java
 *
 * Created on May 11, 2007, 10:55:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String SETTINGS_DIR = ".moviebrowser";
    private static final String IMG_CACHE = "images";
    private static final String FOLDER_SETTINGS = "folders.lst";

    private Settings() {
        // utlity class
    }

    /**
     *
     * @return the folders
     */
    public static final Set<String> loadFolders() {
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

    public static void addFolder(File newFolder) {
        final Set<String> folders = Settings.loadFolders();
        folders.add(newFolder.getAbsolutePath());
        Settings.saveFolders(folders);
    }

    /**
     *
     * @param folders
     */
    public static final void saveFolders(Set<String> folders) {
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

    private static File openFolderSettings() {
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

    private static File getSettingsDir() {
        File settingsDir = new File(System.getProperty("user.home"), SETTINGS_DIR);
        if (!settingsDir.exists()) {
            settingsDir.mkdirs();
        }
        return settingsDir;
    }

    /**
     * @return the imageCacheDir
     */
    public static File getImageCacheDir() {
        File cache = new File(getSettingsDir(), IMG_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }
}
