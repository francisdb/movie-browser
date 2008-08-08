package com.flicklib.service.movie.imdb;

import com.flicklib.tools.Param;

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
     * @param localid 
     * @param movie
     * @return the imdb url
     */
    public static String generateImdbUrl(String localid) {
        return "http://www.imdb.com/title/tt" + Param.encode(localid) + "/";

    }
}
