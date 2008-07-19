package eu.somatik.moviebrowser.cache;

import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;

/**
 *
 * @author francisdb
 */
public interface MovieCache {

    /**
     *
     * @param path the movie path
     * @return the movie of null if not found in cache
     */
    Movie find(String path);

    /**
     * @param name
     * @return the Genre
     */
    Genre getOrCreateGenre(String name);

    /**
     * @param name
     * @return the Language
     */
    Language getOrCreateLanguage(String name);

    boolean isStarted();

    /**
     *
     */
    void printList();

    /**
     * @param movie
     */
    void saveMovie(Movie movie);

    /**
     * Shuts down the cache
     */
    void shutdown();

    void startup();

}
