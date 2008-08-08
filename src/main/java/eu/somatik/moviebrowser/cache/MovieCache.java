package eu.somatik.moviebrowser.cache;

import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import java.util.List;

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
    StorableMovie find(String path);

    /**
     * @param path
     * @return the StorableMovieFile
     */
    StorableMovieFile getOrCreateFile(String path);

    boolean isStarted();

    /**
     * @param movie
     */
    void inserOrUpdate(StorableMovie movie);

    /**
     * @param movie
     */
    void remove(StorableMovie movie);
    
    
    /**
     * 
     * @param site
     */
    void remove(StorableMovieSite site);

    /**
     * 
     * @param movieFile
     */
    void update(StorableMovieFile movieFile);

    /**
     * @param movieFile 
     */
    void remove(StorableMovieFile movieFile);
    
    /**
     * 
     * @param site
     */
    void insert(StorableMovieSite site);

    /**
     * Load stored movies
     * @param movie
     * @return the list of StorableMovieSites
     */
    List<StorableMovieSite> loadSites(StorableMovie movie);
    
    /**
     * Shuts down the cache
     */
    void shutdown();

    void startup();
}
