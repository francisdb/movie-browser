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
            out.println("<table><tr><th>Title</th><th>Year</th><th>Runtime</th><th>Director</th></tr>");
            String title, director, url;
            int year, runtime, score;
            long id;
            
            for(int x=0; x<model.getRowCount(); x++) {
                try {
                    id = model.getMovie(x).getMovieFile().getMovie().getId();
                    url = "http://www.imdb.com/title/tt" + id;
                    
                    title = model.getMovie(x).getMovieFile().getMovie().getTitle();
                    year = model.getMovie(x).getMovieFile().getMovie().getYear();
                    runtime = model.getMovie(x).getMovieFile().getMovie().getRuntime();
                    director = model.getMovie(x).getMovieFile().getMovie().getDirector();
                }
                catch (NullPointerException e) {
                    url = "";
                    title = "";
                    year = 0;
                    runtime = 0;
                    director = "";
                }
                //score = get the global score. 
                
                
                out.println("<tr><td><a href='" + url + "'>" + title + "</a></td><td>" + year + "</td><td>" + runtime + "</td><td>" + director + "</td></tr>");
            }
            
            out.println("</table><br /><p style='font-size:11' align=center>Generated using <a href='http://code.google.com/p/movie-browser/'>Movie Browser</a></p>");
            out.println("</body></html>");
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
