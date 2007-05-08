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
import javax.persistence.Query;



/**
 *
 * @author francisdb
 */
public class MovieCache {
    
    private final  EntityManagerFactory emf;
    
    /** Creates a new instance of MovieCache */
    public MovieCache() {
        emf = Persistence.createEntityManagerFactory("movies-hibernate");
        printList();
    }
    
    /**
     * Shuts down the cache
     */
    public void shutdown(){
        emf.close();
    }
    
    public void persist(Object object){
        System.out.println("Persisting " + object);
        EntityManager em =  emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(object);
        transaction.commit();
        em.close();
    }
    
    public Genre getOrCreateGenre(String name){
        EntityManager em =  emf.createEntityManager();
        Genre found = em.find(Genre.class, name);
        em.close();
        if(found == null){
            System.out.println("New genre " + name);
            found = new Genre();
            found.setName(name);
            persist(found);
        }
        return found;
    }
    
    public Language getOrCreateLanguage(String name){
        EntityManager em =  emf.createEntityManager();
        Language found = em.find(Language.class, name);
        em.close();
        if(found == null){
            System.out.println("New language " + name);
            found = new Language();
            found.setName(name);
            persist(found);
        }
        return found;
    }
    
    public void printList(){
        System.out.println("Printing movie list");
        try{
            EntityManager em =  emf.createEntityManager();
            Query query = em.createQuery("SELECT m FROM Movie m");
            Movie movie;
            for(Object result :query.getResultList()){
                movie = (Movie) result;
                System.out.println(movie);
            }
            em.close();
        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
        System.err.println("Printing done");
    }
    
}
