package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 *
 * @author fdb
 */
public interface MovieInfoFetcher {
    /**
     * Fetched movie info from a servie and complements the movieInfo object
     * @param movieInfo
     */
    void fetch(MovieInfo movieInfo);
}
