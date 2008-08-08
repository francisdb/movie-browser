package com.flicklib.api;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;

/**
 *
 * @author fdb
 */
public interface MovieInfoFetcher {
    /**
     * Fetched movie info from a service and complements the movieInfo object
     * @param movie
     * @param id possible known id for this site, null for none
     * @return the parsed moviePage
     */
    MoviePage fetch(Movie movie, String id);
    
}
