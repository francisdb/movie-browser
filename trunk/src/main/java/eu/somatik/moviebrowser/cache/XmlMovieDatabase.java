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
import java.util.List;
import java.util.Map;

import com.flicklib.tools.LevenshteinDistance;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsombor
 * 
 */
@Singleton
public class XmlMovieDatabase implements MovieDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlMovieDatabase.class);

    final static class GenreConverter implements SingleValueConverter {

        @SuppressWarnings("unchecked")
        @Override
        public boolean canConvert(Class cls) {
            return cls.equals(Genre.class);
        }

        @Override
        public Object fromString(String name) {
            return Genre.get(name);
        }

        @Override
        public String toString(Object g) {
            return ((Genre) g).getName();
        }
    }

    final static class LanguageConverter implements SingleValueConverter {

        @SuppressWarnings("unchecked")
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
            return ((Language) g).getName();
        }
    }

    /**
     * This class is used to generate unique ID-s for the persistent objects.
     * 
     * @author zsombor
     *
     */
    private static class IdGenerator {

        private long maxId;

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
    private final IdGenerator movieIdGenerator = new IdGenerator();
    private final IdGenerator fileIdGenerator = new IdGenerator();
    private final IdGenerator groupIdGenerator = new IdGenerator();
    private final IdGenerator locationIdGenerator = new IdGenerator();
    private final IdGenerator siteInfoIdGenerator = new IdGenerator();
    private final XStream xstream;
    private final Timer timer;
    private final AtomicBoolean stuffToSave;

    private String path;
    private Map<Long, StorableMovie> movies;

    private boolean started = false;

    private static final String DBFILE = "database.xml";


    @Inject
    public XmlMovieDatabase(final Settings settings) {
        this();
        this.movies = new ConcurrentHashMap<Long, StorableMovie>();
        File localFile = new File(DBFILE);
        if(localFile.exists()){
            this.path = localFile.getAbsolutePath();
            LOGGER.info("Using local database file: "+path);
        }else{
            this.path = settings.getSettingsDir() + File.separator + DBFILE;
            LOGGER.info("Using default database file: "+path);
        }
        
    }

    /**
     * 
     */
    public XmlMovieDatabase() {
        this.xstream = initXstream();
        this.stuffToSave = new AtomicBoolean(false);
        this.timer = new Timer("SaveTimer", true);
        this.timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                save();
            }
        }, 0, 1000*10);
    }

    public XmlMovieDatabase(String path) {
        this();
        this.path = path;
    }


    private final XStream initXstream(){
        XStream xstream = new XStream();
        xstream.alias("movie", StorableMovie.class);
        xstream.useAttributeFor(StorableMovie.class, "id");
        xstream.addImplicitCollection(StorableMovie.class, "genres", Genre.class);
        xstream.addImplicitCollection(StorableMovie.class, "languages", Language.class);
        xstream.addImplicitCollection(StorableMovie.class, "siteInfo", StorableMovieSite.class);

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
        xstream.addImplicitCollection(StorableMovieSite.class, "genres", Genre.class);
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
        return xstream;
    }

    /**
     * FIXME don't do this on every new movie!
     */
    private synchronized void save() {
        if(stuffToSave.get()){
            long start = System.currentTimeMillis();
            // make defensive copy and save
            List<StorableMovie> dbValues = new ArrayList<StorableMovie>(movies.size());
            dbValues.addAll(movies.values());

            saveToFile(dbValues);
            stuffToSave.set(false);
            long passed = System.currentTimeMillis() - start;
            LOGGER.info("Saved movies to disk: "+passed+" msec");
        }else{
            // nothning to save
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        resetIds();
        List<StorableMovie> dbValues = loadFromFile();
        if (dbValues != null && dbValues.size() > 0) {
            for (StorableMovie movie : dbValues) {
                movie.initParentLinks();
                LOGGER.trace("adding movie " + movie.getTitle());
                movies.put(movie.getId(), movie);
                // we have to initialize the max ID values.
                checkId(movie);
            }
        }else{
            movies.clear();
        }
        started = true;
    }

    private void saveToFile(List<StorableMovie> movies) {
        Writer os = null;
        try {
            os = new BufferedWriter(new FileWriter(path));
            xstream.toXML(movies, os);
        } catch (IOException e) {
            LOGGER.error("Could not save cache to xml", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close stream", ex);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<StorableMovie> loadFromFile() {
        File file = new File(path);
        List<StorableMovie> dbValues = null;
        Reader reader = null;
        try {
            reader = new FileReader(file);
            dbValues = (List<StorableMovie>) xstream.fromXML(reader);
        } catch (XStreamException e) {
            LOGGER.error("Movie database corrupt , maken backup and creating new database!\n" + e.getMessage(), e);
            try {
                reader.close();
            } catch (IOException ex) {
                LOGGER.error("Could not close reader", ex);
            }
            File backupFile = new File(file.getAbsolutePath() + ".corrupt." + System.currentTimeMillis() + ".bak");
            file.renameTo(backupFile);
        } catch (FileNotFoundException e) {
            LOGGER.warn("Could not load cache from xml as it does not exist, probably first startup.");
        } catch (IOException e) {
            LOGGER.error("Could not load cache from xml", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close reader", ex);
                }
            }
        }
        return dbValues;
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
     * @see eu.somatik.moviebrowser.cache.MovieDatabase#clear()
     */
    @Override
    public synchronized void clear() {
        LOGGER.info("Clearing database");
        movies.clear();
        stuffToSave.set(true);
        save();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieDatabase#findByFile(java.lang.String,
     * long)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieDatabase#findMovieByTitle(java.lang.String
     * )
     */
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

    /*
     * (non-Javadoc)
     * 
     * @seeeu.somatik.moviebrowser.cache.MovieDatabase#insertOrUpdate(eu.somatik.
     * moviebrowser.domain.StorableMovie)
     */
    @Override
    public synchronized StorableMovie insertOrUpdate(StorableMovie movie) {
        startIfNotStarted();
        // give ID to the objects.
        checkId(movie);

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
     * @see eu.somatik.moviebrowser.cache.MovieDatabase#isStarted()
     */
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

    private void startIfNotStarted() {
        if (!started) {
            startup();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.somatik.moviebrowser.cache.MovieDatabase#remove(eu.somatik.moviebrowser
     * .domain.StorableMovie)
     */
    @Override
    public void remove(StorableMovie movie) {
        movies.remove(movie.getId());
        stuffToSave.set(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieDatabase#shutdown()
     */
    @Override
    public void shutdown() {
        timer.cancel();
        if (started) {
            save();
        }
        LOGGER.info(this.getClass().getSimpleName() + " shut down.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.somatik.moviebrowser.cache.MovieDatabase#startup()
     */
    @Override
    public void startup() {
        load();
    }
}
