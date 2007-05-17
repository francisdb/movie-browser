import eu.somatik.moviebrowser.data.Genre;
import eu.somatik.moviebrowser.data.Language;
import eu.somatik.moviebrowser.data.Movie;
import java.util.*;
import javax.persistence.*;

public class HelloWorld {

  public static void main(String[] args) {

    // Start EntityManagerFactory
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("movies-hibernate");

    // First unit of work
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    Language language = new Language("lang"+Math.random());
    //em.persist(language);
    
    Genre genre = new Genre("genre" +Math.random());
    //em.persist(genre);

    Movie movie = new Movie();
    movie.setPath("path"+Math.random());
    movie.setTitle("movie"+Math.random());
    movie.addGenre(genre);
    movie.addLanguage(language);
    em.persist(movie);
    
    tx.commit();
    em.close();

    // Second unit of work
    em = emf.createEntityManager();

    List objects =
        em.createQuery("select m from Language m order by m.name asc").getResultList();

    for (Object m : objects) {
      Language loadedMsg = (Language) m;
      System.out.println(loadedMsg.getName());
    }
    
    objects =
        em.createQuery("select m from Movie m order by m.title asc").getResultList();

    for (Object m : objects) {
      Movie theMovie = (Movie) m;
      System.out.println(theMovie.getTitle());
    }

    em.close();

    // Shutting down the application
    emf.close();
  }

}
