/*
 * MovieCache.java
 *
 * Created on April 9, 2007, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser;

import eu.somatik.moviebrowser.data.Genre;
import eu.somatik.moviebrowser.data.Language;
import eu.somatik.moviebrowser.data.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;



/**
 *
 * @author francisdb
 */
public class MovieCache {
    
    private final  EntityManagerFactory emf;
    private final MovieDAO movieDAO;
    
    /** Creates a new instance of MovieCache */
    public MovieCache() {
        System.out.println("Starting up the cache.");
        emf = Persistence.createEntityManagerFactory("movies-hibernate");
        this.movieDAO = new MovieDAO(emf);
        //printList();
    }
    
    /**
     * Shuts down the cache
     */
    public void shutdown(){
        emf.close();
        System.out.println("Cache shutdown complete.");
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
    
    public void saveMovie(Movie movie){
        EntityManager em =  emf.createEntityManager();
        Movie found = em.find(Movie.class, movie.getPath());
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        if(found==null){
            System.out.println("Saving movie "+movie.getPath());
            em.persist(movie);
        }else{
            System.out.println("Updating movie "+movie.getPath());
            movie = em.merge(movie);
        }
        transaction.commit();
        em.close();
    }
    
    public Genre getOrCreateGenre(String name){
        EntityManager em =  emf.createEntityManager();
        
        Genre found = em.find(Genre.class, name);
        if(found == null){
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            System.out.println("New genre " + name);
            found = new Genre();
            found.setName(name);
            em.persist(found);
            transaction.commit();
        }
        em.close();
        return found;
    }
    
    public Language getOrCreateLanguage(String name){
        EntityManager em =  emf.createEntityManager();
        Language found = em.find(Language.class, name);
        if(found == null){
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            System.out.println("New language " + name);
            found = new Language();
            found.setName(name);
            em.persist(found);
            transaction.commit();
        }
        
        em.close();
        return found;
    }
    
    public void printList(){
        System.out.println("Printing movie list");
        for(Movie movie:movieDAO.loadMovies()){
            System.out.println(movie.getPath()+""+movie);
        }
    }
    
}
