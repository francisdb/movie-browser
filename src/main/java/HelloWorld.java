import eu.somatik.moviebrowser.data.Genre;
import eu.somatik.moviebrowser.data.Language;
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

    Language message = new Language("Hello World with JPA"+Math.random());
    em.persist(message);

    tx.commit();
    em.close();

    // Second unit of work
    em = emf.createEntityManager();

    List messages =
        em.createQuery("select m from Language m order by m.name asc").getResultList();

    System.out.println( messages.size() + " message(s) found:" );

    for (Object m : messages) {
      Language loadedMsg = (Language) m;
      System.out.println(loadedMsg.getName());
    }

    em.close();

    // Shutting down the application
    emf.close();
  }

}
