package eu.somatik.moviebrowser.config;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Moviebrowser settings
 * @author francisdb
 */
public interface Settings {
    
    /**
     * Loads the user preferences
     * @return the preferences map
     */
    Map<String,String> loadPreferences();

    /**
     * Saves the user preferences
     * @param preferences 
     */
    void savePreferences(Map<String,String> preferences);
    
    /**
     * Adds a folder to the list of movie folders
     * @param newFolder
     */
    void addFolder(File newFolder);

    /**
     * Get the image cache folder
     * @return the image cache folder File
     */
    File getImageCacheDir();

    /**
     * Get the settings folder
     * @return the settings folder File
     */
    File getSettingsDir();

    /**
     * Loads all movie folders
     * @return the folders as Set of String
     */
    Set<String> loadFolders();

    /**
     * Saves all movie folders
     * @param folders as Set of String
     */
    void saveFolders(Set<String> folders);
    
    /**
     * Gets the timeout for web site requests
     * @return the timeout
     */
    int getSiteTimout();

    /**
     * Sets the timeout for web site requests
     * @param timeout 
     */
    void setSiteTimeout(int timeout);
    
    /**
     * Should return true if running in debug mode
     * @return true if in debug mode
     */
    boolean isDebugMode();
    
}
