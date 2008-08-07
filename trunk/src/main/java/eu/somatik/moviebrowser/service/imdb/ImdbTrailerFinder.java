package eu.somatik.moviebrowser.service.imdb;

import com.flicklib.api.TrailerFinder;
import eu.somatik.moviebrowser.domain.Movie;

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
