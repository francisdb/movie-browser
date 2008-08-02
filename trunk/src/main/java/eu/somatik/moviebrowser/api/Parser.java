package eu.somatik.moviebrowser.api;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.domain.Movie;

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
    public void parse(Source source, Movie movie);
}
