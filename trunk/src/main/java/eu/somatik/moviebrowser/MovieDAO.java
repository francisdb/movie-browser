/*
 * MovieDAO.java
 *
 * Created on May 13, 2007, 8:37:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser;

import eu.somatik.moviebrowser.data.Movie;
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
    
    public MovieDAO(final EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public void addMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        manager.persist(movie);
        transaction.commit();
        manager.close();
    }
    
    public void deleteMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        manager.remove(movie);
        transaction.commit();
        manager.close();
    }
    
    public void updateMovie(Movie movie){
        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        movie = manager.merge(movie);
        transaction.commit();
        manager.close();
    }
    
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
