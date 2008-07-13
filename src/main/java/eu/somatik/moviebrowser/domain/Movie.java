/*
 * Movie.java
 *
 * Created on May 7, 2007, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.domain;

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
    private Integer year;
    private String url;
    private String imgUrl;
    private String rating;
    private String tomatometer;
    private String movieWebStars;
    
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

    public String getTomatometer() {
        return tomatometer;
    }

    public void setTomatometer(String tomatometer) {
        this.tomatometer = tomatometer;
    }

    public String getMovieWebStars() {
        return movieWebStars;
    }

    public void setMovieWebStars(String movieWebStars) {
        this.movieWebStars = movieWebStars;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
    
    
    
    @Override
    public String toString() {
        return "Movie "+getTitle()+" -> "+getImdbId();
    }


    

}
