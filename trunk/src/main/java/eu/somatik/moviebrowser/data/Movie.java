/*
 * Movie.java
 *
 * Created on May 7, 2007, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.data;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author francisdb
 */
@Entity
public class Movie {
    
    @Id
    private String path;
    private String imdbId;
    
    private String votes;
    private String plot;
    
    private String title;
    private String url;
    private String imgUrl;
    private String rating;
    private String tomatoesRating;
    private String tomatoesRatingUsers;
    
    /**
     * Runtime in minutes 
     */
    private Integer runtime;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Genre> genres;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Language> languages;
    
    
    
    /** Creates a new instance of Movie */
    public Movie() {
        this.genres = new HashSet<Genre>();
        this.languages = new HashSet<Language>();
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * 
     * @return  the cotes
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
     * @return the plot
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
     * @return the title
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
     * @return the url
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
     * @return the imdbId
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
     * @return the genres
     */
    public Set<Genre> getGenres() {
        return genres;
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
    public Set<Language> getLanguages() {
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
     * @return the rating
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
     * @return the tomatoesRating
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
     * @return tomatoesRatingUsers
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
     * @return the runtime
     */
    public Integer getRuntime() {
        return runtime;
    }
    
    
    /**
     * @return the imgUrl
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * @param imgUrl
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
