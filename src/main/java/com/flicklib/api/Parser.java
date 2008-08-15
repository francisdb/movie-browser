package com.flicklib.api;

import com.flicklib.domain.MoviePage;


/**
 *
 * @author francisdb
 */
public interface Parser {
    /**
     * Parses the html page source to info for the movie
     * @param html
     * @param movieSite 
     */
    public void parse(String html, MoviePage movieSite);
}
