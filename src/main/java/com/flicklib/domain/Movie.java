package com.flicklib.domain;

import java.util.HashSet;
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
    private MovieType type;
    /**
     * Runtime in minutes
     */
    private Integer runtime;
    private Set<String> genres;
    private Set<String> languages;

    // TODO add cast
    // private List<String> cast;
    /**
     * Constructs a new Movie
     */
    public Movie() {
        this.genres = new HashSet<String>();
        this.languages = new HashSet<String>();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the plot
     */
    public String getPlot() {
        return plot;
    }

    /**
     * @param plot
     *            the plot to set
     */
    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @param director
     *            the director to set
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * @return the runtime
     */
    public Integer getRuntime() {
        return runtime;
    }

    /**
     * @param runtime
     *            the runtime to set
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the genres
     */
    public Set<String> getGenres() {
        return genres;
    }

    /**
     * @param genres
     *            the genres to set
     */
    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    /**
     * @return the languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * @param languages
     *            the languages to set
     */
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    /**
     * @param genre
     */
    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    /**
     * @param language
     */
    public void addLanguage(String language) {
        this.languages.add(language);
    }
}
