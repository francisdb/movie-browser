package eu.somatik.moviebrowser.service.fetcher;

import eu.somatik.moviebrowser.domain.Movie;

/**
 *
 * @author fdb
 */
public interface MovieInfoFetcher {
    /**
     * Fetched movie info from a servie and complements the movieInfo object
     * @param movieInfo
     */
    void fetch(Movie movieInfo);
    
}
