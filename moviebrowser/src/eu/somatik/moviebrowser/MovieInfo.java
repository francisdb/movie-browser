/*
 * MovieInfo.java
 *
 * Created on January 24, 2007, 11:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package imdb;

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
    
    private Image image;
    private List<String> genres;
    private List<String> languages;
    private String rating;
    private String tomatoesRating;
    private String tomatoesRatingUsers;
    private String votes;
    private String plot;
    private String title;
    private String url;
    private String imdbId;
    private File directory;
    private MovieStatus status;
    private String runtime;
    
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Creates a new instance of MovieInfo 
     * @param directory 
     */
    public MovieInfo(File directory) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.directory = directory;
        this.status = MovieStatus.NEW;
        this.genres = new LinkedList<String>();
        this.languages = new LinkedList<String>();
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
    public List<String> getGenres() {
        return Collections.unmodifiableList(genres);
    }

    /**
     * 
     * @param genre 
     */
    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    /**
     * 
     * @return the List of language strings
     */
    public List<String> getLanguages() {
        return languages;
    }
       
    /**
     * 
     * @param language 
     */
    public void addLanguage(String language){
        this.languages.add(language);
    }

    /**
     * 
     * @return 
     */
    public String getRating() {
        return rating;
    }

    /**
     * 
     * @param rating 
     */
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    /**
     * 
     * @return 
     */
    public String getTomatoesRating() {
        return tomatoesRating;
    }

    /**
     * 
     * @param tomatoesRating 
     */
    public void setTomatoesRating(String tomatoesRating) {
        this.tomatoesRating = tomatoesRating;
    }
    
    /**
     * 
     * @return 
     */
    public String getTomatoesRatingUsers() {
        return tomatoesRatingUsers;
    }

    /**
     * 
     * @param tomatoesRatingUsers 
     */
    public void setTomatoesRatingUsers(String tomatoesRatingUsers) {
        this.tomatoesRatingUsers = tomatoesRatingUsers;
    }

    /**
     * 
     * @return 
     */
    public String getVotes() {
        return votes;
    }

    /**
     * 
     * @param votes 
     */
    public void setVotes(String votes) {
        this.votes = votes;
    }

    /**
     * 
     * @return 
     */
    public String getPlot() {
        return plot;
    }

    /**
     * 
     * @param plot 
     */
    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * 
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return 
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url 
     */
    public void setUrl(String url) {
        this.url = url;
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
    public String getImdbId() {
        return imdbId;
    }

    /**
     * 
     * @param imdbId 
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
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
     * @return 
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * 
     * @param runtime 
     */
    public void setRuntime(String runtime) {
        this.runtime = runtime;
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
    
    
    

    
}
