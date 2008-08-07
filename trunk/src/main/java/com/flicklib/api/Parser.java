package com.flicklib.api;

import com.flicklib.domain.MovieSite;

/**
 *
 * @author francisdb
 */
public interface Parser {
    /**
     * Parses the html page source to info for the movie
     * @param source
     * @param movie
     */
    public void parse(String source, MovieSite movie);
}
