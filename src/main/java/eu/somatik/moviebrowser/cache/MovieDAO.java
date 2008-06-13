/*
 * MovieDAO.java
 *
 * Created on May 13, 2007, 8:37:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.cache;

import eu.somatik.moviebrowser.domain.Movie;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 *
 * @author francisdb
 */
public class MovieDAO {
    
    private final EntityManagerFactory emf;
    
    /**
     * Constructs a new MovieDAO object
     *
     * @param emf
     */
    public MovieDAO(final EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    /**
     * @param movie
     */
    public void addMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        manager.persist(movie);
        transaction.commit();
        manager.close();
    }
    
    /**
     * @param movie
     */
    public void deleteMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        manager.remove(movie);
        transaction.commit();
        manager.close();
    }
    
    /**
     * @param movie
     */
    public void updateMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        /*movie = */manager.merge(movie);
        transaction.commit();
        manager.close();
    }
    
    /**
     * @return the list of movies in the database
     */
    public List<Movie> loadMovies(){
        List<Movie> movies = new ArrayList<Movie>();
        EntityManager manager = emf.createEntityManager();
        List<?> objects = manager.createQuery("select m from Movie m order by m.title asc").getResultList();
        for (Object item : objects) {
            movies.add((Movie)item);
        }
        manager.close();
        return movies;
    }
    
}
