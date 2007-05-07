/*
 * Movie.java
 *
 * Created on May 7, 2007, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author francisdb
 */
@Entity
public class Movie {
    
    private String imdbId;
    
    private String votes;
    private String plot;
    
    @Id
    private String title;
    private String url;
    private String rating;
    private String tomatoesRating;
    private String tomatoesRatingUsers;
    
    /**
     * Runtime in minutes 
     */
    private Integer runtime;
    
    @OneToMany( cascade={CascadeType.PERSIST} ,fetch=FetchType.LAZY)
    private List<Genre> genres;
    
    @OneToMany( cascade={CascadeType.PERSIST} ,fetch=FetchType.LAZY)
    private List<Language> languages;
    
    
    
    /** Creates a new instance of Movie */
    public Movie() {
        this.genres = new LinkedList<Genre>();
        this.languages = new LinkedList<Language>();
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
    public List<Genre> getGenres() {
        return Collections.unmodifiableList(genres);
    }

    /**
     * 
     * @param genre 
     */
    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    /**
     * 
     * @return the List of language strings
     */
    public List<Language> getLanguages() {
        return languages;
    }
       
    /**
     * 
     * @param language 
     */
    public void addLanguage(Language language){
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
    public Integer getRuntime() {
        return runtime;
    }

    /**
     * 
     * @param runtime 
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
    
    @Override
    public String toString() {
        return "Movie "+getTitle()+" -> "+getImdbId();
    }

    

}
