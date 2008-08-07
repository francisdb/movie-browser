package com.flicklib.service.movie.imdb;

import com.flicklib.api.TrailerFinder;
import com.flicklib.domain.Movie;

/**
 *
 * @author francisdb
 */
public class ImdbTrailerFinder implements TrailerFinder{

    @Override
    public String findTrailerUrl(Movie movie) {
        String url = ImdbUrlGenerator.generateImdbUrl(movie) + "trailers";
        return url;
    }

}
