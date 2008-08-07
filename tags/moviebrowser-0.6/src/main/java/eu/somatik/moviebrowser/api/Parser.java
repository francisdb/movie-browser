package eu.somatik.moviebrowser.api;

import eu.somatik.moviebrowser.domain.MovieSite;

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
