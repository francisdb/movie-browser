/*
 * Movie.java
 *
 * Created on May 7, 2007, 9:31 PM
 *
 */

package com.flicklib.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

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
    
    private String imgUrl;
    
    /**
     * TODO refactor all scores to some other entity class
     */
    private Integer imdbScore;
    private Integer tomatoScore;
    private Integer movieWebScore;
    private Integer omdbScore;
    private Integer googleScore;
    private Integer flixterScore;
    
    /**
     * TODO refactor all urls to some other entity class
     */
    private String imdbUrl;
    private String tomatoUrl;
    private String moviewebUrl;
    private String omdbUrl;
    private String googleUrl;
    private String flixterUrl;
    
    /**
     * Runtime in minutes 
     */
    private Integer runtime;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Genre> genres;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Language> languages;
    
    @OneToMany
    private List<MovieSite> sites;

    
    
    
    /** Creates a new instance of Movie */
    public Movie() {
        this.genres = new HashSet<Genre>();
        this.languages = new HashSet<Language>();
        this.sites = new ArrayList<MovieSite>();
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

    public Integer getImdbScore() {
        return imdbScore;
    }

    public void setImdbScore(Integer imdbScore) {
        this.imdbScore = imdbScore;
    }

    public Integer getMovieWebScore() {
        return movieWebScore;
    }

    public void setMovieWebScore(Integer movieWebScore) {
        this.movieWebScore = movieWebScore;
    }

    public Integer getTomatoScore() {
        return tomatoScore;
    }

    public void setTomatoScore(Integer tomatoScore) {
        this.tomatoScore = tomatoScore;
    }

    public Integer getGoogleScore() {
        return googleScore;
    }

    public void setGoogleScore(Integer googleScore) {
        this.googleScore = googleScore;
    }

    public Integer getOmdbScore() {
        return omdbScore;
    }

    public void setOmdbScore(Integer omdbScore) {
        this.omdbScore = omdbScore;
    }

    public Integer getFlixterScore() {
        return flixterScore;
    }

    public void setFlixterScore(Integer flixterScore) {
        this.flixterScore = flixterScore;
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

    public String getFlixterUrl() {
        return flixterUrl;
    }

    public void setFlixterUrl(String flixterUrl) {
        this.flixterUrl = flixterUrl;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getImdbUrl() {
        return imdbUrl;
    }

    public void setImdbUrl(String imdbUrl) {
        this.imdbUrl = imdbUrl;
    }

    public String getMoviewebUrl() {
        return moviewebUrl;
    }

    public void setMoviewebUrl(String moviewebUrl) {
        this.moviewebUrl = moviewebUrl;
    }

    public String getOmdbUrl() {
        return omdbUrl;
    }

    public void setOmdbUrl(String omdbUrl) {
        this.omdbUrl = omdbUrl;
    }

    public String getTomatoUrl() {
        return tomatoUrl;
    }

    public void setTomatoUrl(String tomatoUrl) {
        this.tomatoUrl = tomatoUrl;
    }

    public List<MovieSite> getSites() {
        return sites;
    }

    public void setSites(List<MovieSite> sites) {
        this.sites = sites;
    }   
    
    public MovieSite siteFor(MovieService service){
        MovieSite site = null;
        Iterator<MovieSite> siteIterator = sites.iterator();
        MovieSite next;
        while(site == null && siteIterator.hasNext()){
            next = siteIterator.next();
            if(next.getService() == service){
                site = next;
            }
        }
        return site;
    }
    
    
    @Override
    public String toString() {
        return "Movie "+getTitle()+" -> "+getImdbId();
    }


    

}
