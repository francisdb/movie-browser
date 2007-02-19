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
import javax.swing.SwingUtilities;

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
    
    /** Creates a new instance of MovieInfo */
    public MovieInfo(File directory) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.directory = directory;
        this.status = MovieStatus.NEW;
        this.genres = new LinkedList<String>();
        this.languages = new LinkedList<String>();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<String> getGenres() {
        return Collections.unmodifiableList(genres);
    }

    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    public List<String> getLanguages() {
        return languages;
    }
       
    public void addLanguage(String language){
        this.languages.add(language);
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public String getTomatoesRating() {
        return tomatoesRating;
    }

    public void setTomatoesRating(String tomatoesRating) {
        this.tomatoesRating = tomatoesRating;
    }
    
    public String getTomatoesRatingUsers() {
        return tomatoesRatingUsers;
    }

    public void setTomatoesRatingUsers(String tomatoesRatingUsers) {
        this.tomatoesRatingUsers = tomatoesRatingUsers;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String toString() {
        return directory.getName();
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    

    


    public void setStatus(MovieStatus status) {
        MovieStatus oldValue = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("loading", oldValue, this.status);
    }
    
    
    

    
}
