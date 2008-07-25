/*
 * MovieCache.java
 *
 * Created on April 9, 2007, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.cache;

import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class MovieCacheImpl implements MovieCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieCacheImpl.class);
    
    private EntityManagerFactory emf;
    private MovieDAO movieDAO;

    /** 
     * Creates a new instance of MovieCache 
     */
    public MovieCacheImpl() {
        // nothing here
    }

    @Override
    public boolean isStarted() {
        return emf != null;
    }

    @Override
    public void startup() {
        LOGGER.info("Starting up the cache.");
        this.emf = Persistence.createEntityManagerFactory("movies-hibernate");
        this.movieDAO = new MovieDAO(emf);
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
    public Movie find(String path) {
        EntityManager em = emf.createEntityManager();
        Movie found = em.find(Movie.class, path);
        em.close();
        return found;
    }

    @Override
    public void removeMovie(Movie movie){
        EntityManager em = emf.createEntityManager();
        Movie found = em.find(Movie.class, movie.getPath());
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(found);
        transaction.commit();
        em.close();
    }
    
    /**
     * @param movie
     */
    @Override
    public void saveMovie(Movie movie) {
        EntityManager em = emf.createEntityManager();
        Movie found = em.find(Movie.class, movie.getPath());
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        if (found == null) {
            LOGGER.trace("Saving movie " + movie.getPath());
            em.persist(movie);
        } else {
            LOGGER.trace("Updating movie " + movie.getPath());
            /*movie = */            em.merge(movie);
        }
        transaction.commit();
        em.close();
    }

    /**
     * @param name
     * @return the Genre
     */
    @Override
    public Genre getOrCreateGenre(String name) {
        EntityManager em = emf.createEntityManager();

        Genre found = em.find(Genre.class, name);
        if (found == null) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            LOGGER.trace("New genre " + name);
            found = new Genre();
            found.setName(name);
            em.persist(found);
            transaction.commit();
        }
        em.close();
        return found;
    }

    /**
     * @param name
     * @return the Language
     */
    @Override
    public Language getOrCreateLanguage(String name) {
        EntityManager em = emf.createEntityManager();
        Language found = em.find(Language.class, name);
        if (found == null) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            LOGGER.trace("New language " + name);
            found = new Language();
            found.setName(name);
            em.persist(found);
            transaction.commit();
        }

        em.close();
        return found;
    }

    /**
     * 
     */
    @Override
    public void printList() {
        LOGGER.info("Printing movie list");
        for (Movie movie : movieDAO.loadMovies()) {
            LOGGER.info(movie.getPath() + "" + movie);
        }
    }
    /**
     * Clear the movies list from DB. Simillar to deleteMovie() in MovieDAO, but doesn't work, 
     * as it throws an exception saying: Removing a detached instance.
     */
//    public void removeFromList(Movie movie) {
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
