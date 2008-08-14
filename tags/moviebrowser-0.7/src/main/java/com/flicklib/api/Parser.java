package com.flicklib.api;

import com.flicklib.domain.MoviePage;


/**
 *
 * @author francisdb
 */
public interface Parser {
    /**
     * Parses the html page source to info for the movie
     * @param source
     * @param movieSite 
     */
    public void parse(String source, MoviePage movieSite);
}
