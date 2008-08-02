package eu.somatik.moviebrowser.api;

import eu.somatik.moviebrowser.domain.Movie;

/**
 * Finds the url for the trailer site/page
 * @author francisdb
 */
public interface TrailerFinder {

    String findTrailerUrl(Movie movie);

}
