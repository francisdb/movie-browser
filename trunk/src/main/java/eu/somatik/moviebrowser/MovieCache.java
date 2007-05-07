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
import eu.somatik.moviebrowser.data.MovieInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    /* the default framework is embedded*/
    private String framework = "embedded";
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    
    private final  EntityManagerFactory emf;
    
    /** Creates a new instance of MovieCache */
    public MovieCache() {
        emf = Persistence.createEntityManagerFactory("movies");
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
    
    
  /*
   *            The driver is installed by loading its class.
   *            In an embedded environment, this will start up Derby, since it is not already running.
   */
    private void loadDriver(){
        try     {
            java.lang.Class.forName(driver).newInstance();
            java.lang.System.out.println("Loaded enbedded derby driver.");
        } catch (InstantiationException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }
    
    private Connection getConnection() throws SQLException{
        Connection conn = null;
        Properties props = new Properties();
        props.put("user", "moviebrowser");
        props.put("password", "moviebrowser");
        
            /*
               The connection specifies create=true to cause
               the database to be created. To remove the database,
               remove the directory derbyDB and its contents.
               The directory derbyDB will be created under
               the directory that the system property
               derby.system.home points to, or the current
               directory if derby.system.home is not set.
             */
        conn = DriverManager.getConnection(protocol +"moviedb;create=true", props);
        return conn;
    }
    
    private void test(){
        try {
            
            
            loadDriver();
            
            Connection conn = getConnection();
            
            System.out.println("Connected to and created database derbyDB");
            
            conn.setAutoCommit(false);
            
            /*
               Creating a statement lets us issue commands against
               the connection.
             */
            Statement s = conn.createStatement();
            
            /*
               We create a table, add a few rows, and update one.
             */
            s.execute("create table derbyDB(num int, addr varchar(40))");
            System.out.println("Created table derbyDB");
            s.execute("insert into derbyDB values (1956,'Webster St.')");
            System.out.println("Inserted 1956 Webster");
            s.execute("insert into derbyDB values (1910,'Union St.')");
            System.out.println("Inserted 1910 Union");
            s.execute(
                    "update derbyDB set num=180, addr='Grand Ave.' where num=1956");
            System.out.println("Updated 1956 Webster to 180 Grand");
            
            s.execute(
                    "update derbyDB set num=300, addr='Lakeshore Ave.' where num=180");
            System.out.println("Updated 180 Grand to 300 Lakeshore");
            
            /*
               We select the rows and verify the results.
             */
            ResultSet rs = s.executeQuery(
                    "SELECT num, addr FROM derbyDB ORDER BY num");
            
            if (!rs.next()) {
                throw new Exception("Wrong number of rows");
            }
            
            if (rs.getInt(1) != 300) {
                throw new Exception("Wrong row returned");
            }
            
            if (!rs.next()) {
                throw new Exception("Wrong number of rows");
            }
            
            if (rs.getInt(1) != 1910) {
                throw new Exception("Wrong row returned");
            }
            
            if (rs.next()) {
                throw new Exception("Wrong number of rows");
            }
            
            System.out.println("Verified the rows");
            
            s.execute("drop table derbyDB");
            System.out.println("Dropped table derbyDB");
            
            /*
               We release the result and statement resources.
             */
            rs.close();
            s.close();
            System.out.println("Closed result set and statement");
            
            /*
               We end the transaction and the connection.
             */
            conn.commit();
            conn.close();
            System.out.println("Committed transaction and closed connection");
            
            /*
               In embedded mode, an application should shut down Derby.
               If the application fails to shut down Derby explicitly,
               the Derby does not perform a checkpoint when the JVM shuts down, which means
               that the next connection will be slower.
               Explicitly shutting down Derby with the URL is preferred.
               This style of shutdown will always throw an "exception".
             */
            boolean gotSQLExc = false;
            
            if (framework.equals("embedded")) {
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException se) {
                    gotSQLExc = true;
                }
                
                if (!gotSQLExc) {
                    System.out.println("Database did not shut down normally");
                } else {
                    System.out.println("Database shut down normally");
                }
            }
        } catch (Throwable e) {
            System.out.println("exception thrown:");
            
            if (e instanceof SQLException) {
                printSQLError((SQLException)e);
            } else {
                e.printStackTrace();
            }
        }
    }
    
    static void printSQLError(SQLException e) {
        while (e != null) {
            System.out.println(e.toString());
            e = e.getNextException();
        }
    }
    
}
