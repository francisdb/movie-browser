package com.flicklib.api;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieSite;

/**
 *
 * @author fdb
 */
public interface MovieInfoFetcher {
    /**
     * Fetched movie info from a servie and complements the movieInfo object
     * @param movie
     * @return 
     */
    MovieSite fetch(Movie movie);
    
}
