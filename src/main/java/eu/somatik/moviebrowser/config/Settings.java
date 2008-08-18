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
     * Should return true if running in debug mode
     * @return true if in debug mode
     */
    boolean isDebugMode();

    /**
     * Returns the curen version
     * @return the version as string
     */
    String getApplicationVersion();
    
    /**
     * Fetches the latest version info from the net
     * @return the version as string
     */
    String getLatestApplicationVersion();
    
    /**
     * Sets whether or not to rename titles to IMDB titles.
     * @param true or false as boolean.
     */
    void setRenameTitles(boolean value);
    
    /**
     * Returns the value set in setRenameTitles() method
     * @return boolean value
     */
    boolean getRenameTitles();

    /**
     * Gets the look and feel
     * @return
     */
    String getLookAndFeelClassName();

    /**
     * Sets the look and feel
     */
    void setLookAndFeelClassName(String className);
}
