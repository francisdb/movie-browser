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
package eu.somatik.moviebrowser.database;

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.domain.MovieService;
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
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * @author zsombor
 * 
 */
@Singleton
public class XmlMovieDatabase extends InMemoryDatabase implements MovieDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlMovieDatabase.class);

    private final static class GenreConverter implements SingleValueConverter {

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") Class cls) {
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

    private final static class LanguageConverter implements SingleValueConverter {

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") Class cls) {
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
    
    private final static class MovieServiceConverter implements SingleValueConverter {
        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") Class cls) {
            return cls.equals(MovieService.class);
        }

        @Override
        public Object fromString(String name) {
            return MovieService.getById(name);
        }

        @Override
        public String toString(Object g) {
            return ((MovieService) g).getId();
        }
        
    }

    private final IdGenerator movieIdGenerator = new IdGenerator();
    private final IdGenerator fileIdGenerator = new IdGenerator();
    private final IdGenerator groupIdGenerator = new IdGenerator();
    private final IdGenerator locationIdGenerator = new IdGenerator();
    private final IdGenerator siteInfoIdGenerator = new IdGenerator();
    private final XStream xstream;
    private final Timer timer;

    private String path;
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
        this.timer = new Timer("SaveTimer", true);
        this.timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                save();
            }
        }, 0, 1000*10);
    }


    private final XStream initXstream(){
        XStream xstream = new XStream();
        xstream.alias("movie", StorableMovie.class);
        xstream.useAttributeFor(StorableMovie.class, "id");
//        xstream.addImplicitCollection(StorableMovie.class, "genres", Genre.class);
//        xstream.addImplicitCollection(StorableMovie.class, "languages", Language.class);
//        xstream.addImplicitCollection(StorableMovie.class, "siteInfo", StorableMovieSite.class);

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
//        xstream.addImplicitCollection(StorableMovieSite.class, "genres", Genre.class);
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
        xstream.registerConverter(new MovieServiceConverter());
        return xstream;
    }

    @Override
    protected synchronized void save() {
        // do we need to save data?
        if(stuffToSave.get()){
            long start = System.currentTimeMillis();
            // make defensive copy and save
            List<StorableMovie> dbValues = new ArrayList<StorableMovie>(movies.size());
            dbValues.addAll(movies.values());

            saveToFile(dbValues);
            stuffToSave.set(false);
            long passed = System.currentTimeMillis() - start;
            LOGGER.info("Saved movies to disk: "+passed+" msec");
        }
    }

    @Override
    protected synchronized void load() {
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
            os = new BufferedWriter(new FileWriter(path + ".save"));
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
        File tempFile = new File(path + ".save");
        File saveFile = new File(path);
        saveFile.delete();
        tempFile.renameTo(saveFile);
        
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

    /**
     * This method ensures that every object has an unique ID in it's class
     * @param movie
     */
    @Override
    protected void checkId(StorableMovie movie) {
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

    
    @Override
    public void shutdown() {
        timer.cancel();
        super.shutdown();
    }
}
