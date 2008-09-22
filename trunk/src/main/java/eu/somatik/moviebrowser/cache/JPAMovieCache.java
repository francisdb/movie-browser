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
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import eu.somatik.moviebrowser.tools.FileTools;
import java.util.Date;

/**
 *
 * @author francisdb
 */
@Singleton
public class JPAMovieCache implements MovieCache {

    /**
     * Augment this value whenever the data model changes (jpa domain classes)
     */
    private static final int DATABASE_VERSION = 2;
    private static final String VERSION_FILE = "version";
    private static final Logger LOGGER = LoggerFactory.getLogger(JPAMovieCache.class);
    private static final boolean DEBUG = true;
    private final Settings settings;
    private EntityManagerFactory emf;

    /** 
     * Creates a new instance of MovieCache 
     * @param settings 
     */
    @Inject
    public JPAMovieCache(final Settings settings) {
        this.settings = settings;
    }

    
    protected EntityManager getEntityManager() {
    	if (emf==null) {
    		startup();
    	}
    	return emf.createEntityManager();
    }
    
    @Override
    public boolean isStarted() {
        return emf != null;
    }

    @Override
    public void startup() {
        LOGGER.info("Starting up the cache.");
        File databaseDir = new File(getDatabaseUrl());
        if (databaseDir.exists()) {
            checkComatibility();
        }else{
            setVersion(DATABASE_VERSION);
        }
        Map<String, String> props = new HashMap<String, String>();
        String databaseLocation = getDatabaseUrl() + File.separator + "moviecache";
        props.put("hibernate.connection.url", "jdbc:hsqldb:file:" + databaseLocation + ";shutdown=true");
        if (DEBUG) {
            props.put("hibernate.show_sql", "true");
        }
        this.emf = Persistence.createEntityManagerFactory("movies-hibernate", props);
    }

    private void checkComatibility() {
        if (loadVersion() != DATABASE_VERSION) {
            // not sure this is a good place to lounch a gui dialog...
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Movie cache not compatible with new version.\nCache will be cleared!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                });
            } catch (InvocationTargetException ex) {
                LOGGER.error("Showing message failed", ex.getCause());
            } catch (InterruptedException ex) {
                LOGGER.warn("Showing message interrupted");
            }
            clear();
        }

    }

    private int loadVersion() {
        File prefsFile = new File(getDatabaseUrl(), VERSION_FILE);
        Properties props = FileTools.loadProperties(prefsFile);
        int version = 0;
        String storedVersion = (String) props.get("version");
        if(storedVersion == null){
            LOGGER.warn("Could not determine database version");
        }else{
            version = Integer.valueOf(storedVersion);
        }
        return version;
    }

    private void setVersion(int version) {
        Properties props = new Properties();
        props.put("version", Integer.toString(version));
        File propsFile = new File(getDatabaseUrl(), VERSION_FILE);
        FileTools.storePropeties(props, propsFile);
    }

    private String getDatabaseUrl() {
        return settings.getSettingsDir() + File.separator + "database";
    }

    /**
     * Shuts down the cache
     */
    @Override
    public void shutdown() {
        if (emf != null) {
            emf.close();
            LOGGER.info("Cache shutdown complete.");
        } else {
            LOGGER.warn("Cache shutdown skipped, cache was not started.");
        }
    }

    /**
     * 
     * @param path the movie path
     * @return the movie of null if not found in cache
     */
/*    @Override
    public StorableMovie find(String path) {
        StorableMovie found = null;
        if (path != null) {
            EntityManager em = null;
            try {
                em = emf.createEntityManager();
                found = em.find(StorableMovie.class, path);
            } finally {
                closeAndCleanup(em);
            }
        }
        return found;
    }*/
    
    @Override
    public List<StorableMovie> list() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNamedQuery("StorableMovie.findAll");
            return (List<StorableMovie>) query.getResultList();
        } finally {
            closeAndCleanup(em);
        }
    }
    

    @Override
    public void remove(StorableMovie movie) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            StorableMovie found = em.find(StorableMovie.class, movie.getId());
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(found);
            transaction.commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    @Override
    public void remove(StorableMovieSite site) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            if (site.getId()!=null) {
                StorableMovieSite found = em.find(StorableMovieSite.class, site.getId());
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();
                //StorableMovieSite found = em.merge(site);
                found.setMovie(null);
                em.remove(found);
                transaction.commit();
            }
        } finally {
            closeAndCleanup(em);
        }
    }

    @Override
    public void remove(StorableMovieFile file) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            StorableMovieFile found = em.find(StorableMovieFile.class, file.getId());
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(found);
            transaction.commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    public void insert(StorableMovieFile movieFile) {
        LOGGER.info("saving path " + movieFile.getName());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            if (movieFile.getMovie() != null) {
                replaceLanguageGenre(movieFile.getMovie(), em);
                if (movieFile.getMovie().getId() == null) {
                    em.persist(movieFile.getMovie());
                } else {
                    movieFile.setMovie(em.merge(movieFile.getMovie()));
                }
            }
            em.persist(movieFile);
            em.getTransaction().commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    private void replaceLanguageGenre(StorableMovie movie, EntityManager em) {
        Set<Genre> genresToPersist = new HashSet<Genre>(movie.getGenres());
        movie.getGenres().clear();
        for (Genre genre : genresToPersist) {
            genre = getOrCreateGenre(genre.getName(), em);
            movie.addGenre(genre);
        }

        Set<Language> languagesToPersist = new HashSet<Language>(movie.getLanguages());
        movie.getLanguages().clear();
        for (Language language : languagesToPersist) {
            language = getOrCreateLanguage(language.getName(), em);
            movie.addLanguage(language);
        }
    }

    @Override
    public void update(StorableMovieFile movieFile) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            if (movieFile.getMovie() != null) {
                if (movieFile.getMovie().getId() == null) {
                    em.persist(movieFile.getMovie());
                } else {
                    movieFile.setMovie(em.merge(movieFile.getMovie()));
                }
            }
            em.merge(movieFile);
            em.getTransaction().commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    @Override
    public void update(MovieLocation location) {
        EntityManager em = null;
        try {
        	em = getEntityManager();
        	em.getTransaction().begin();
        	if (location.getId()==null) {
        		em.persist(location);
        	} else {
        		em.merge(location);
        	}
            em.getTransaction().commit();
        } finally {
            closeAndCleanup(em);
        }
    	
    }
    
    /**
     * @param movie
     */
    @Override
    public StorableMovie insertOrUpdate(StorableMovie movie) {
        EntityManager em = null;
        StorableMovie stored = null;
        try {
            em = getEntityManager();

            replaceLanguageGenre(movie, em);

/*            StorableMovie found = movie.getId() != null ? em.find(
					StorableMovie.class, movie.getId()) : null;*/
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            movie.setLastModified(new Date());

            if (movie.getId() == null) {
                LOGGER.trace("Saving movie " + movie.getTitle());
                em.persist(movie);
                stored = movie;
            } else {
                LOGGER.trace("Updating movie " + movie.getId());
                /*movie = */ 
                stored = em.merge(movie);
                LOGGER.info("updated to "+stored);
            }
            transaction.commit();
            return stored;
        } finally {
            closeAndCleanup(em);
        }
    }


    
    /**
     * @param name
     * @return the Genre
     */
    private Genre getOrCreateGenre(String name, EntityManager em) {
        Genre found = null;
        found = em.find(Genre.class, name);
        if (found == null) {
            em.getTransaction().begin();
            LOGGER.trace("New genre " + name);
            found = new Genre();
            found.setName(name);
            em.persist(found);
            em.getTransaction().commit();
        }
        return found;
    }

    /**
     * @param name
     * @return the Language
     */
    private Language getOrCreateLanguage(String name, EntityManager em) {
        Language found = null;
        found = em.find(Language.class, name);
        if (found == null) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            LOGGER.trace("New language " + name);
            found = new Language();
            found.setName(name);
            em.persist(found);
            transaction.commit();
        }
        return found;
    }

    @Override
    public void insert(StorableMovieSite site) {
        if (site == null) {
            LOGGER.error("Trying to save null site");
        } else {
            EntityManager em = null;
            try {
                em = getEntityManager();
                em.getTransaction().begin();
                LOGGER.trace("id = "+site.getId());
                em.persist(site);
                em.getTransaction().commit();
            } finally {
                closeAndCleanup(em);
            }
        }
    }

    @Override
    public void insert(FileSystem fileSystem) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(fileSystem);
            em.getTransaction().commit();
        } finally {
            closeAndCleanup(em);
        }
    }
    
    

    public List<StorableMovieSite> loadSites(StorableMovie movie) {
        List<StorableMovieSite> sites = new ArrayList<StorableMovieSite>();
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNamedQuery("StorableMovieSite.findByMovie");
            query.setParameter("movie", movie);
            List<?> results = query.getResultList();
            for (Object result : results) {
                sites.add((StorableMovieSite) result);
            }
        } finally {
            closeAndCleanup(em);
        }
        return sites;
    }

    @Override
    public void clear(){
        shutdown();
        File databaseDir = new File(getDatabaseUrl());
        if (databaseDir.exists()) {
            boolean deleted = false;
            int count = 0;
            // try 5 times with 1 sec waiting
            while (!deleted && count < 5) {
                if (count != 0) {
                    int sleep = 1000 * count;
                    LOGGER.debug("Sleeping " + sleep + " sec before retry...");
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException ex) {
                        count = Integer.MAX_VALUE;
                        LOGGER.error("Sleep interrupted", ex);
                    }
                }
                deleted = FileTools.deleteDirectory(databaseDir);
                count++;
            }

            if (deleted) {
                LOGGER.info("Deleted " + databaseDir.getAbsolutePath());
                setVersion(DATABASE_VERSION);
            } else {
                // TODO throw exception?
                LOGGER.error("Could not delete " + databaseDir.getAbsolutePath());
            }
        } else {
            LOGGER.warn("Database folder does not exist " + databaseDir.getAbsolutePath());
            setVersion(DATABASE_VERSION);
        }
        startup();
    }

    @Override
    public StorableMovie findMovieByTitle(String title) {
        StorableMovie movie = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNamedQuery("StorableMovie.findByTitle");
            query.setParameter("title", title);
            try {
                movie = (StorableMovie) query.getSingleResult();
            } catch (NoResultException ex) {
                LOGGER.debug("No movie found with title: " + title);
            }
        } finally {
            closeAndCleanup(em);
        }
        return movie;
    }

    @Override
    public FileGroup findByFile(String filename, long size) {
        FileGroup movie = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNamedQuery("FileGroup.findByFile");
            query.setParameter("filename", filename);
            query.setParameter("size", size);
            
            try {
                movie = (FileGroup) query.getSingleResult();
            } catch (NoResultException ex) {
                LOGGER.debug("No movie found with filename: " + filename + ", size:" + size);
            }
        } finally {
            closeAndCleanup(em);
        }
        return movie;
        
    }
    
    private void closeAndCleanup(final EntityManager em) {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                LOGGER.warn("Rolling back transaction!!!");
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
