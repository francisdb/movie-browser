/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.cache;

import eu.somatik.moviebrowser.domain.MovieInfo;
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

    void removeImgFromCache(MovieInfo movie);

    /**
     * @param movie
     * @return the saved file
     */
    File saveImgToCache(MovieInfo movie);

}
