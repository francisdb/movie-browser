/*
 * RottenTomatoesThread.java
 *
 * Created on February 12, 2007, 11:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import java.util.Iterator;
import java.util.List;
import org.jdesktop.http.Response;
import org.jdesktop.http.Session;

/**
 *
 * @author francisdb
 */
public class RottenTomatoesThread implements Runnable{
    private MovieInfo movieInfo;
    
        
        private static int threads;
    
    
    /** 
     * Creates a new instance of RottenTomatoesThread 
     * @param movieInfo 
     */
    public RottenTomatoesThread(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
        RottenTomatoesThread.incThreads();
    }

    public void run() {

        RottenTomatoesThread.decThreads();
    }
    
    /**
     * increaded the number of threads
     */
    public static void incThreads(){
        threads++;
        System.out.println("Threads: "+threads);
    }
    
    /**
     * decreases the number of threads
     */
    public static void decThreads(){
        threads--;
        System.out.println("Threads: "+threads);
    }
    
}
