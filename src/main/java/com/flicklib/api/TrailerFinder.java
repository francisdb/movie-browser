package com.flicklib.api;

import com.flicklib.domain.Movie;

/**
 * Finds the url for the trailer site/page
 * @author francisdb
 */
public interface TrailerFinder {

    String findTrailerUrl(Movie movie);

}
