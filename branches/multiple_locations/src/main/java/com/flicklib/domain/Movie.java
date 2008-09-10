/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
