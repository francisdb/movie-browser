/*
 * MovieInfo.java
 *
 * Created on January 24, 2007, 11:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.domain;

import eu.somatik.moviebrowser.domain.MovieService;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author francisdb
 */
public class MovieInfo {
    
    private Movie movie;
    
    private Image image;

    private File directory;
    private MovieStatus status;
    
    private Map<MovieService,Integer> scores;
    
    private PropertyChangeSupport propertyChangeSupport;
    
    
    /** Creates a new instance of MovieInfo 
     * @param directory 
     */
    public MovieInfo(File directory) {
        this.scores = new HashMap<MovieService, Integer>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.directory = directory;
        this.status = MovieStatus.NEW;
        this.movie = new Movie();
        this.movie.setPath(directory.getAbsolutePath());
    }
    
    /**
     * 
     * @param listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void addScore(MovieService service, Integer score){
        scores.put(service, score);
    }
    
    public Integer getScore(MovieService service){
        return scores.get(service);
    }

    /**
     * 
     * @return the Image
     */
    public Image getImage() {
        return image;
    }

    /**
     * 
     * @param image 
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * 
     * @return the Directory
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * 
     * @param directory 
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    @Override
	public String toString() {
        return directory.getName();
    }

    /**
     * 
     * @return the MovieStatus
     */
    public MovieStatus getStatus() {
        return status;
    }


    /**
     * 
     * @param status 
     */
    public void setStatus(MovieStatus status) {
        MovieStatus oldValue = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("loading", oldValue, this.status);
    }
    
    /**
     * @param movie
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * @return the Movie
     */
    public Movie getMovie() {
        return movie;
    }

    
    

    
}