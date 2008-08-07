/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.cache;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieInfo;
import java.io.File;

/**
 *
 * @author francisdb
 */
public interface ImageCache {

    /**
     *
     * @param info
     */
    void loadImg(MovieInfo info);

    void removeImgFromCache(Movie movie);

    /**
     * @param movie
     * @return the saved file
     */
    File saveImgToCache(Movie movie);

}
