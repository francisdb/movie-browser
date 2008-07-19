/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.scanner;

import java.io.File;

/**
 *
 * @author francisdb
 */
public interface FileSystemScanner {

    /**
     * Finds the NFO file and looks for the imdb url inside it
     * @param dir
     * @return the nfo URL or null
     */
    String findNfoUrl(File dir);

    /**
     * Locates parent directory name and returns it
     * @param folder
     * @return the parent dir name or null if not found
     */
    File findParentDirectory(File folder);

    /**
     * Locates she sample file and returns it
     * @param folder
     * @return the sample file or null if not found
     */
    File findSample(File folder);

}
