package com.flicklib.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public class Movie {


    private String title;
    private String plot;    
    private Integer year;
    private String director;
   
    /**
     * Runtime in minutes 
     */
    private Integer runtime;
    
    private Set<String> genres;
    private Set<String> languages;
    
    // TODO add cast
    // private List<String> cast;

    public Movie() {
        this.genres = new HashSet<String>();
        this.languages = new HashSet<String>();
    }
    
    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }
    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
    
    
    
    public void addGenre(String genre){
        this.genres.add(genre);
    }
    
    public void addLanguage(String language){
        this.languages.add(language);
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
    
    
}
