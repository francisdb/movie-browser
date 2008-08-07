/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.api;

import com.flicklib.domain.MovieInfo;
import java.util.List;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public interface FolderScanner {

    /**
     * Scans the folders
     * @param folders
     * @return a List of MovieInfo
     */
    List<MovieInfo> scan(final Set<String> folders);

}
