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
        String databaseLocation = settings.getSettingsDir() + File.separator + "database/moviecache";
        props.put("hibernate.connection.url", "jdbc:hsqldb:file:" + databaseLocation + ";shutdown=true");
        this.emf = Persistence.createEntityManagerFactory("movies-hibernate", props);
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
                if (em != null) {
                    em.close();
                }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
            if (em != null) {
                em.close();
            }
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
                if (em != null) {
                    em.close();
                }
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
            if (em != null) {
                em.close();
            }
        }
        return sites;
    }    //    private void printList() {
//        LOGGER.info("Printing movie list");
//        for (StorableMovie movie : movieDAO.loadMovies()) {
//            LOGGER.info(movie.getPath() + "" + movie);
//        }
//    }
    /**
     * Clear the movies list from DB. Simillar to deleteMovie() in MovieDAO, but doesn't work, 
     * as it throws an exception saying: Removing a detached instance.
     */
//    public void removeFromList(StorableMovie movie) {
//        //System.out.println(list.getPath());
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//        eu.somatik.moviebrowser.cache.MovieDAO movies = new eu.somatik.moviebrowser.cache.MovieDAO(emf);
//        movies.deleteMovie(movie);
//        //em.remove(movie);
//        //transaction.commit();
//        //em.remove(movie);
//        //em.close();
//    }
}
