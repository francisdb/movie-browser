/*
 * MovieCache.java
 *
 * Created on April 9, 2007, 2:55 PM
 *
 */
package eu.somatik.moviebrowser.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovie;

import eu.somatik.moviebrowser.domain.StorableMovieSite;
import eu.somatik.moviebrowser.tools.FileTools;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class JPAMovieCache implements MovieCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAMovieCache.class);
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

    @Override
    public boolean isStarted() {
        return emf != null;
    }

    @Override
    public void startup() {
        LOGGER.info("Starting up the cache.");
        Map<String, String> props = new HashMap<String, String>();
        String databaseLocation = getDatabaseUrl() + File.separator + "moviecache";
        props.put("hibernate.connection.url", "jdbc:hsqldb:file:" + databaseLocation + ";shutdown=true");
        this.emf = Persistence.createEntityManagerFactory("movies-hibernate", props);
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
        } else {
            LOGGER.warn("Cache shutdown failed, cache was not started.");
        }
        LOGGER.info("Cache shutdown complete.");
    }

    /**
     * 
     * @param path the movie path
     * @return the movie of null if not found in cache
     */
    @Override
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
    }

    @Override
    public void remove(StorableMovie movie) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
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
            em = emf.createEntityManager();
            StorableMovieSite found = em.find(StorableMovieSite.class, site.getId());
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(found);
            transaction.commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    @Override
    public void remove(StorableMovieFile file) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            StorableMovieFile found = em.find(StorableMovieFile.class, file.getPath());
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(found);
            transaction.commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    public void insert(StorableMovieFile movieFile) {
        LOGGER.info("saving path " + movieFile.getPath());
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            if (movieFile.getMovie() != null) {
                replaceLanguageGenre(movieFile.getMovie(), em);
                if (movieFile.getMovie().getId() == 0) {
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
            em = emf.createEntityManager();
            em.getTransaction().begin();
            if (movieFile.getMovie() != null) {
                if (movieFile.getMovie().getId() == 0) {
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

    /**
     * @param movie
     */
    @Override
    public void inserOrUpdate(StorableMovie movie) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            replaceLanguageGenre(movie, em);

            StorableMovie found = em.find(StorableMovie.class, movie.getId());
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();


            if (found == null) {
                LOGGER.trace("Saving movie " + movie.getId());
                em.persist(movie);
            } else {
                LOGGER.trace("Updating movie " + movie.getId());
                /*movie = */ em.merge(movie);
            }
            transaction.commit();
        } finally {
            closeAndCleanup(em);
        }
    }

    @Override
    public StorableMovieFile getOrCreateFile(String path) {
        StorableMovieFile found = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            found = em.find(StorableMovieFile.class, path);
            if (found == null) {
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();
                LOGGER.trace("New file " + path);
                found = new StorableMovieFile();
                found.setPath(path);
                em.persist(found);
                transaction.commit();
            }
        } finally {
            closeAndCleanup(em);
        }
        return found;
    }

    /**
     * @param name
     * @return the Genre
     */
    private Genre getOrCreateGenre(String name, EntityManager em) {
        Genre found = null;
        found = em.find(Genre.class, name);
        if (found == null) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            LOGGER.trace("New genre " + name);
            found = new Genre();
            found.setName(name);
            em.persist(found);
            transaction.commit();
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
                em = emf.createEntityManager();
                em.getTransaction().begin();
                em.persist(site);
                em.getTransaction().commit();
            } finally {
                closeAndCleanup(em);
            }
        }
    }

    @Override
    public List<StorableMovieSite> loadSites(StorableMovie movie) {
        List<StorableMovieSite> sites = new ArrayList<StorableMovieSite>();
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
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
    public void clear() {
        shutdown();
        File databaseDir = new File(getDatabaseUrl());
        if (databaseDir.exists()) {
            boolean deleted = false;
            int count = 0;
            // try 5 times with 1 sec waiting
            while (deleted == false && count < 5) {
                if (count != 0) {
                    int sleep = 1000 * count;
                    LOGGER.debug("Sleeping "+sleep+" sec before retry...");
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
            } else {
                LOGGER.error("Could not delete " + databaseDir.getAbsolutePath());
            }
        } else {
            LOGGER.warn("Database folder does not exist " + databaseDir.getAbsolutePath());
        }
        startup();
    }

    @Override
    public StorableMovie findMovieByTitle(String title) {
        StorableMovie movie = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Query query = em.createNamedQuery("StorableMovie.findByTitle");
            query.setParameter("title", title);
            try{
                movie = (StorableMovie) query.getSingleResult();
            }catch(NoResultException ex){
                LOGGER.debug("No movie found with title: "+title);
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
