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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flicklib.tools.LevenshteinDistance;
import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.Persistent;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * @author zsombor
 * 
 */
public class XmlMovieCache implements MovieCache {

    final static class GenreConverter implements SingleValueConverter {
        @Override
        public boolean canConvert(Class cls) {
            return cls.equals(Genre.class);
        }

        @Override
        public Object fromString(String name) {
            return new Genre(name);
        }

        @Override
        public String toString(Object g) {
            return ((Genre)g).getName();
        }
    }

    final static class LanguageConverter implements SingleValueConverter {
        @Override
        public boolean canConvert(Class cls) {
            return cls.equals(Language.class);
        }

        @Override
        public Object fromString(String name) {
            return new Language(name);
        }

        @Override
        public String toString(Object g) {
            return ((Language)g).getName();
        }
    }

    /**
     * This class is used to generate unique ID-s for the persistent objects.
     * 
     * @author zsombor
     *
     */
    static class IdGenerator {
        long maxId;
        
        /**
         * ensure that the given Persistent object has a unique ID.
         */
        public void checkId(Persistent p) {
            if (p.getId()==null) {
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
    
    XStream xstream;
    String path;
    Map<Long, StorableMovie> movies = new HashMap<Long, StorableMovie>();
    
    IdGenerator movieIdGenerator = new IdGenerator();
    IdGenerator fileIdGenerator = new IdGenerator();
    IdGenerator groupIdGenerator = new IdGenerator();
    IdGenerator locationIdGenerator = new IdGenerator();
    IdGenerator siteInfoIdGenerator = new IdGenerator();
    
    boolean started = false;

    @Inject
    public XmlMovieCache(final Settings settings) {
        this();
        path = settings.getSettingsDir() + File.separator + "database.xml";
    }

    /**
     * 
     */
    public XmlMovieCache() {
        xstream = new XStream();
        xstream.alias("movie", StorableMovie.class);
        xstream.useAttributeFor(StorableMovie.class, "id");
        xstream.addImplicitCollection(StorableMovie.class, "genres",  Genre.class);
        xstream.addImplicitCollection(StorableMovie.class, "languages",  Language.class);
        xstream.addImplicitCollection(StorableMovie.class, "siteInfo",  StorableMovieSite.class);
        
        xstream.alias("group", FileGroup.class);
        xstream.useAttributeFor(FileGroup.class, "id");
        xstream.useAttributeFor(FileGroup.class, "type");
        xstream.omitField(FileGroup.class, "movie");
        
        xstream.alias("file", StorableMovieFile.class);
        xstream.useAttributeFor(StorableMovieFile.class, "id");
        xstream.useAttributeFor(StorableMovieFile.class, "name");
        xstream.useAttributeFor(StorableMovieFile.class, "type");
        xstream.useAttributeFor(StorableMovieFile.class, "size");
        xstream.omitField(StorableMovieFile.class, "movie");
        xstream.omitField(StorableMovieFile.class, "group");
        
        xstream.alias("location", MovieLocation.class);
        xstream.useAttributeFor(MovieLocation.class, "id");
        xstream.useAttributeFor(MovieLocation.class, "label");
        xstream.useAttributeFor(MovieLocation.class, "path");
        xstream.useAttributeFor(MovieLocation.class, "folderRenamingSafe");
        xstream.omitField(MovieLocation.class, "movie");
        xstream.omitField(MovieLocation.class, "group");
        
        xstream.alias("site", StorableMovieSite.class);
        xstream.omitField(StorableMovieSite.class, "movie");
        xstream.useAttributeFor(StorableMovieSite.class, "id");
        xstream.useAttributeFor(StorableMovieSite.class, "service");
        xstream.useAttributeFor(StorableMovieSite.class, "url");
        xstream.useAttributeFor(StorableMovieSite.class, "score");
        xstream.useAttributeFor(StorableMovieSite.class, "votes");
        xstream.useAttributeFor(StorableMovieSite.class, "idForSite");
        xstream.useAttributeFor(StorableMovieSite.class, "imgUrl");
        
        xstream.alias("genre", Genre.class);
        xstream.alias("language", Language.class);
        xstream.registerConverter(new LanguageConverter());
        xstream.registerConverter(new GenreConverter());
        
    }
    
    

    public XmlMovieCache(String path) {
        this();
        this.path = path;
    }

    synchronized void save() {
        List<StorableMovie> dbValues = new ArrayList<StorableMovie>(movies.size());
        dbValues.addAll(movies.values());

        Writer os = null;
        try {
            os = new BufferedWriter(new FileWriter(path));
            xstream.toXML(dbValues, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void load() {
        Reader reader = null;
        List<StorableMovie> dbValues = null;
        
        resetIds();
        
        try {
            reader = new FileReader(path);
            dbValues = (List<StorableMovie>) xstream.fromXML(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            movies.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dbValues != null) {
            for (StorableMovie movie : dbValues) {
                movie.initParentLinks();
                movies.put(movie.getId(), movie);
                // we have to initialize the max ID values.
                checkId(movie);
            }
        }
        started = true;

    }

    synchronized void resetIds() {
        movieIdGenerator.reset();
        fileIdGenerator.reset();
        groupIdGenerator.reset();
        locationIdGenerator.reset();
        siteInfoIdGenerator.reset();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieCache#clear()
     */
    @Override
    public synchronized void clear() {
        movies.clear();
        save();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieCache#findByFile(java.lang.String,
     * long)
     */
    @Override
    public FileGroup findByFile(String filename, long size) {
        checkStarted();
        
        for (StorableMovie t : movies.values()) {
            FileGroup g = t.hasFiles(filename, size);
            if (g != null) {
                StorableMovie movie= g.getMovie().clone();
                // return the cloned movie + filegroup
                return movie.hasFiles(filename, size);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieCache#findMovieByTitle(java.lang.String
     * )
     */
    @Override
    public StorableMovie findMovieByTitle(String title) {
        checkStarted();
        
        String lowerCase = title.toLowerCase();
        for (StorableMovie t : movies.values()) {
            if (LevenshteinDistance.distance(t.getTitle().toLowerCase(), lowerCase) < 4) {
                // we are happy with similar titles :-)
                return t.clone();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeeu.somatik.moviebrowser.cache.MovieCache#insertOrUpdate(eu.somatik.
     * moviebrowser.domain.StorableMovie)
     */
    @Override
    public synchronized StorableMovie insertOrUpdate(StorableMovie movie) {
        checkStarted();
        // give ID to the objects.
        checkId(movie);

        // we store the cloned one - a snapshot of the current object, and
        // return the original passed in.
        // this ensures, that later modifications doesn't shows up in
        // accidentally
        StorableMovie clone;
        clone = movie.clone();
        movies.put(clone.getId(), clone);
        save();
        return movie;
    }

    /**
     * This method ensures that every object has an unique ID in it's class
     * @param movie
     */
    private void checkId(StorableMovie movie) {
        this.movieIdGenerator.checkId(movie);
        for (FileGroup g : movie.getGroups()) {
            this.groupIdGenerator.checkId(g);
            for (StorableMovieFile f : g.getFiles()) {
                this.fileIdGenerator.checkId(f);
            }
        }
        for (StorableMovieSite s : movie.getSiteInfo()) {
            this.siteInfoIdGenerator.checkId(s);
        }
        for (MovieLocation l : movie.getLocations()) {
            this.locationIdGenerator.checkId(l);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieCache#isStarted()
     */
    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public List<StorableMovie> list() {
        checkStarted();
        List<StorableMovie> dbValues = new ArrayList<StorableMovie>(movies.size());
        // we have to clone the movie graph, to avoid accidental modifications.
        for (StorableMovie m : movies.values()) {
            dbValues.add(m.clone());
        }
        return dbValues;
    }

    private void checkStarted() {
        if (!started) {
            startup();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieCache#remove(eu.somatik.moviebrowser
     * .domain.StorableMovie)
     */
    @Override
    public void remove(StorableMovie movie) {
        movies.remove(movie.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieCache#shutdown()
     */
    @Override
    public void shutdown() {
        if (started) {
            save();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieCache#startup()
     */
    @Override
    public void startup() {
        load();
    }

}
