/*
 * MovieCache.java
 *
 * Created on April 9, 2007, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.cache;

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
public class MovieCache {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieCache.class);
    
    private final  EntityManagerFactory emf;
    private final MovieDAO movieDAO;
    
    /** 
     * Creates a new instance of MovieCache 
     */
    public MovieCache() {
        LOGGER.info("Starting up the cache.");
        emf = Persistence.createEntityManagerFactory("movies-hibernate");
        this.movieDAO = new MovieDAO(emf);
        //printList();
    }
    
    /**
     * Shuts down the cache
     */
    public void shutdown(){
        emf.close();
        LOGGER.info("Cache shutdown complete.");
    }
    
    /**
     * 
     * @param path the movie path
     * @return the movie of null if not found in cache
     */
    public Movie find(String path){
        EntityManager em =  emf.createEntityManager();
        Movie found = em.find(Movie.class, path);
        em.close();
        return found;
    }
    
    /**
     * @param movie
     */
    public void saveMovie(Movie movie){
        EntityManager em =  emf.createEntityManager();
        Movie found = em.find(Movie.class, movie.getPath());
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        if(found==null){
            LOGGER.trace("Saving movie "+movie.getPath());
            em.persist(movie);
        }else{
            LOGGER.trace("Updating movie "+movie.getPath());
            /*movie = */em.merge(movie);
        }
        transaction.commit();
        em.close();
    }
    
    /**
     * @param name
     * @return the Genre
     */
    public Genre getOrCreateGenre(String name){
        EntityManager em =  emf.createEntityManager();
        
        Genre found = em.find(Genre.class, name);
        if(found == null){
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
    public Language getOrCreateLanguage(String name){
        EntityManager em =  emf.createEntityManager();
        Language found = em.find(Language.class, name);
        if(found == null){
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
    public void printList(){
        LOGGER.info("Printing movie list");
        for(Movie movie:movieDAO.loadMovies()){
            LOGGER.info(movie.getPath()+""+movie);
        }
    }
    
}
