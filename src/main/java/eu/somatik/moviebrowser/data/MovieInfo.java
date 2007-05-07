/*
 * MovieInfo.java
 *
 * Created on January 24, 2007, 11:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.data;

import eu.somatik.moviebrowser.*;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author francisdb
 */
public class MovieInfo {
    
    private Movie movie;
    
    private Image image;

    private File directory;
    private MovieStatus status;
    

    
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Creates a new instance of MovieInfo 
     * @param directory 
     */
    public MovieInfo(File directory) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.directory = directory;
        this.status = MovieStatus.NEW;
        this.movie = new Movie();
    }
    
    /**
     * 
     * @param listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @return 
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
     * @return 
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

    public String toString() {
        return directory.getName();
    }

    /**
     * 
     * @return 
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
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    
    

    
}
