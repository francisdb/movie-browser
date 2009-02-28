/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Zsombor Gegesy
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
package eu.somatik.moviebrowser.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.tools.LevenshteinDistance;

import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.Persistent;
import eu.somatik.moviebrowser.domain.StorableMovie;

/**
 * 
 * @author zsombor
 *
 */
public abstract class InMemoryDatabase  implements MovieDatabase{

    /**
     * This class is used to generate unique ID-s for the persistent objects.
     * 
     * @author zsombor
     *
     */
    protected static class IdGenerator {

        private long maxId;

        public IdGenerator() {}
        
        /**
         * ensure that the given Persistent object has a unique ID.
         */
        public void checkId(Persistent p) {
            if (p.getId() == null) {
                maxId++;
                p.setId(maxId);
            } else {
                maxId = Math.max(maxId, p.getId());
            }
        }

        public void reset() {
            maxId = 0;
        }
    }
    
    static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabase.class);

    protected Map<Long, StorableMovie> movies;
    protected boolean started = false;
    protected final AtomicBoolean stuffToSave = new AtomicBoolean(false);

    @Override
    public synchronized void clear() {
        LOGGER.info("Clearing database");
        movies.clear();
        stuffToSave.set(true);
        save();
    }

    @Override
    public FileGroup findByFile(String filename, long size) {
        startIfNotStarted();
    
        for (StorableMovie t : movies.values()) {
            FileGroup g = t.hasFiles(filename, size);
            if (g != null) {
                StorableMovie movie = g.getMovie().clone();
                // return the cloned movie + filegroup
                return movie.hasFiles(filename, size);
            }
        }
        return null;
    }

    @Override
    public StorableMovie findMovieByTitle(String title) {
        startIfNotStarted();
    
        String lowerCase = title.toLowerCase();
        for (StorableMovie t : movies.values()) {
            if (LevenshteinDistance.distance(t.getTitle().toLowerCase(), lowerCase) < 4) {
                // we are happy with similar titles :-)
                return t.clone();
            }
        }
        return null;
    }

    @Override
    public synchronized StorableMovie insertOrUpdate(StorableMovie movie) {
        startIfNotStarted();
        // give ID to the objects.
        checkId(movie);
    
        movie.setLastModified(new Date());
        if (movie.getCreated() == null) {
        	movie.setCreated(new Date());
        }
        // we store the cloned one - a snapshot of the current object, and
        // return the original passed in.
        // this ensures, that later modifications doesn't shows up in
        // accidentally
        StorableMovie clone = movie.clone();
        LOGGER.info("adding movie " + movie.getTitle());
        movies.put(clone.getId(), clone);
        stuffToSave.set(true);
        return movie;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public List<StorableMovie> list() {
        startIfNotStarted();
        List<StorableMovie> dbValues = new ArrayList<StorableMovie>(movies.size());
        // we have to clone the movie graph, to avoid accidental modifications.
        for (StorableMovie m : movies.values()) {
            dbValues.add(m.clone());
        }
        return dbValues;
    }

    @Override
    public void remove(StorableMovie movie) {
        movies.remove(movie.getId());
        stuffToSave.set(true);
    }

    @Override
    public void shutdown() {
        if (started) {
            save();
        }
        LOGGER.info(this.getClass().getSimpleName() + " shut down.");
    }

    @Override
    public void startup() {
        load();
    }

    void startIfNotStarted() {
        if (!started) {
            startup();
            started = true;
        }
    }

    abstract protected void load();

    abstract protected void save();
    
    /**
     * This method ensures that every object has an unique ID in it's class
     * @param movie
     */
    abstract protected void checkId(StorableMovie movie);

}
