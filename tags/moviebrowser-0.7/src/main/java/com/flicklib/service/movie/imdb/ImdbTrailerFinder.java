package com.flicklib.service.movie.imdb;

import com.flicklib.api.TrailerFinder;

/**
 *
 * @author francisdb
 */
public class ImdbTrailerFinder implements TrailerFinder{

    @Override
    public String findTrailerUrl(String title, String localId) {
        String url = ImdbUrlGenerator.generateImdbUrl(localId) + "trailers";
        return url;
    }

}
