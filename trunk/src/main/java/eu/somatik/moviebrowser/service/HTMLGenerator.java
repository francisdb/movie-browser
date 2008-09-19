/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import eu.somatik.moviebrowser.gui.MovieInfoTableModel;
import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.StorableMovie;


/**
 * Generates HTML List of Movies. 
 * @author Ravi Undupitiya
 */
public class HTMLGenerator {
    
    private MovieInfoTableModel model; 
    
    public HTMLGenerator(MovieInfoTableModel model) {
        this.model = model;
    }
    
    public void GenerateHTMLFile(String libName, File index) {
        
        try {
            FileWriter outFile = new FileWriter(index.getPath());
            PrintWriter out = new PrintWriter(outFile);
          
            //Generate Simple Movie Catalog. 
            out.println("<html><head><title>" + libName + "</title><meta name='author' content='Generated using Movie Browser' /></head><body>");
            out.println("<h1>" + libName + "</h1>");
            out.println("<table><tr><th>Movie Browser Score<th>Title</th><th>Year</th><th>Director</th><th>Runtime</th></tr>");
            String title, director, url;
            int year, runtime, score=0;
            
            for(int x=0; x<model.getRowCount(); x++) {
                try {
                    final StorableMovie movie = model.getMovie(x).getMovie();
                    url = "http://www.imdb.com/title/tt" + movie.getMovieSiteInfo(MovieService.IMDB).getIdForSite();
                    
                    title = movie.getTitle();
                    year = movie.getYear();
                    runtime = movie.getRuntime();
                    director = movie.getDirector();  
                    out.println("<tr><td>" + score + "</td><td><a href='" + url + "'>" + title + "</a></td><td>" + year + "</td><td>" + director + "</td><td>" + runtime + "</td></tr>");
                }
                catch (NullPointerException e) {
                    url = "";
                    title = "";
                    year = 0;
                    runtime = 0;
                    director = "";
                }
                                
            }
            
            out.println("</table><br /><p style='font-size:11' align=center>Generated using <a href='http://code.google.com/p/movie-browser/'>Movie Browser</a>. The Movie Browser Score has been calculated using combined IMDB, Google, Flixster, Rotten Tomatoes and Movie Web ratings.</p>");
            out.println("</body></html>");
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
