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

import eu.somatik.moviebrowser.domain.FileSystem;
import java.util.List;

import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

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
    //StorableMovie find(String path);

    /**
     * Return the list of all movies, found in the database  
     * @return
     */
    public List<StorableMovie> list();
    
    /**
     * @return the StorableMovieFile
     */
    //StorableMovieFile getOrCreateFile(String path);

    boolean isStarted();

    /**
     * @param movie
     */
    void insertOrUpdate(StorableMovie movie);

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
    
    void update(MovieLocation location);

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
     * Inserts a filesystem in to the database
     * @param fileSystem
     */
    void insert(FileSystem fileSystem);

    /**
     * Load stored movies
     * @param movie
     * @return the list of StorableMovieSites
     */
    //List<StorableMovieSite> loadSites(StorableMovie movie);
    
    
    /**
     * load a movie which associated with a given filename and size.
     * @param filename
     * @param size
     * @return
     */
    public StorableMovie findByFile(String filename, long size);
    
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
