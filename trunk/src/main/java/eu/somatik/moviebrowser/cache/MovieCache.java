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

import java.util.List;

import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.StorableMovie;

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
     * Return the list of all movies, found in the database  
     * @return
     */
    public List<StorableMovie> list();
    

    boolean isStarted();

    /**
     * @param movie
     */
    StorableMovie insertOrUpdate(StorableMovie movie);

    /**
     * @param movie
     */
    void remove(StorableMovie movie);
    
    
    /**
     * load a movie which associated with a given filename and size.
     * @param filename
     * @param size
     * @return
     */
    public FileGroup findByFile(String filename, long size);
    
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
