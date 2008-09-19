/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.somatik.moviebrowser.service.export;

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
public class HTMLExporter implements Exporter {
    
    private MovieInfoTableModel model; 
    
    public HTMLExporter(MovieInfoTableModel model) {
        this.model = model;
    }
    
    @Override
    public void exportToFile(String libName, File index) {
        
        try {
            FileWriter outFile = new FileWriter(index.getPath());
            PrintWriter out = new PrintWriter(outFile);
          
            //Generate Simple Movie Catalog. 
            out.println("<html><head><title>" + libName + "</title><meta name='author' content='Generated using Movie Browser' /></head><body>");
            out.println("<h1>" + libName + "</h1>");
            out.println("<table><tr><th>Movie Browser Score<th>Title</th><th>Year</th><th>Director</th><th>Runtime</th></tr>");
            String title;
            String director;
            String url;
            int year;
            int runtime;
            int score = 0;
            
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

    @Override
    public String getName() {
        return "html";
    }
}
