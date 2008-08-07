package eu.somatik.moviebrowser.cache;

import com.flicklib.domain.Genre;
import com.flicklib.domain.Language;
import com.flicklib.domain.Movie;

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
     * @param movie
     */
    void saveMovie(Movie movie);
    
        /**
     * @param movie
     */
    void removeMovie(Movie movie);

    /**
     * Shuts down the cache
     */
    void shutdown();

    void startup();

}
