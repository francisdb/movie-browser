package com.flicklib.service.movie.imdb;

import com.flicklib.domain.Movie;

/**
 *
 * @author francisdb
 */
public class ImdbUrlGenerator {

    private ImdbUrlGenerator() {
        // Utility class
    }
    
    
    /**
     * Generates the imdb url from the imdb id
     * @param movie 
     * @return the imdb url
     */
    public static String generateImdbUrl(Movie movie) {
        String id = movie.getImdbId();
        if ("".equals(id)) {
            return movie.getImdbUrl();
        } else {
            return "http://www.imdb.com/title/tt" + id + "/";
        }
    }
}
