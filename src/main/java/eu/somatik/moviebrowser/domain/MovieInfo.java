/*
 * MovieInfo.java
 *
 * Created on January 24, 2007, 11:47 PM
 *
 */

package eu.somatik.moviebrowser.domain;


import com.flicklib.domain.MovieService;
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
    
    private StorableMovieFile movieFile;
    private Map<MovieService, StorableMovieSite> sites;

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
        this.movieFile = new StorableMovieFile();
        this.sites = new HashMap<MovieService, StorableMovieSite>();
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

    public StorableMovieFile getMovieFile() {
        return movieFile;
    }

    public void setMovieFile(StorableMovieFile movieFile) {
        this.movieFile = movieFile;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    public Map<MovieService, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<MovieService, Integer> scores) {
        this.scores = scores;
    }

    public Map<MovieService, StorableMovieSite> getSites() {
        return sites;
    }

    public void setSites(Map<MovieService, StorableMovieSite> sites) {
        this.sites = sites;
    }
    
    public void addSite(StorableMovieSite storableMovieSite){
        this.sites.put(storableMovieSite.getService(), storableMovieSite);
    }
    
    public StorableMovieSite siteFor(MovieService service){
        return sites.get(service);
    }
    
}
