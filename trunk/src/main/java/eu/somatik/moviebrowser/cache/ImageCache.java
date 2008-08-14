package eu.somatik.moviebrowser.cache;

import eu.somatik.moviebrowser.domain.MovieInfo;
import java.awt.Image;
import java.io.File;

/**
 *
 * @author francisdb
 */
public interface ImageCache {

    /**
     *
     * @param info
     * @return the image or null if not found
     */
    Image loadImg(MovieInfo info);

    void removeImgFromCache(MovieInfo movie);

    /**
     * @param movie
     * @return the saved file
     */
    File saveImgToCache(MovieInfo movie);

}
