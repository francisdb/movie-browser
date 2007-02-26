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
        this.movieInfo.setStatus(MovieStatus.LOADING_TOMATOES);
          if(!"".equals(movieInfo.getImdbId())){
             Session s = new Session();
            Response r = null;
            try {
                r = s.get(MovieFinder.generateTomatoesUrl(movieInfo));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(r != null){
                Source source = new Source(r.getBody());
                //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
                source.fullSequentialParse();

                Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
                System.out.println(titleElement.getContent().extractText());
                List spanElements=source.findAllElements(HTMLElementName.SPAN);
                for (Iterator i=spanElements.iterator(); i.hasNext();) {
                    Element spanElement=(Element)i.next();
                    String cssClass=spanElement.getAttributeValue("class");
                    if (cssClass!=null && "subnav_button_percentage".equals(cssClass)){
                        String userRating = spanElement.getContent().extractText();
                        if(!"".equals(userRating)){
                            movieInfo.setTomatoesRatingUsers(userRating);
                        }
                    }
                }

                List divElements=source.findAllElements(HTMLElementName.DIV);
                for (Iterator i=divElements.iterator(); i.hasNext();) {
                    Element divElement=(Element)i.next();
                    String elementId=divElement.getAttributeValue("id");
                    if (elementId!=null && "critics_tomatometer_score_txt".equals(elementId)){
                        String criticsRating = divElement.getContent().extractText();
                        if(!"".equals(criticsRating)){
                            movieInfo.setTomatoesRating(criticsRating);
                        }
                    }
                }
            }
        }
        this.movieInfo.setStatus(MovieStatus.LOADED);
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
