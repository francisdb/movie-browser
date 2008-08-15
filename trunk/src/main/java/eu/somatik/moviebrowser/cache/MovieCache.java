/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
     * Selects a movie using its title
     * @param title
     * @return
     */
    public StorableMovie findMovieByTitle(String title);

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

    /**
     * Start the cache, the cache should not be used before it is started
     */
    void startup();

    /**
     * Clears the cache
     */
    void clear();
}
