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
package eu.somatik.moviebrowser.domain;

import com.flicklib.domain.MovieType;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author francisdb
 */
@Entity
@Table(name="Movie")
@NamedQuery(name="StorableMovie.findByTitle", query="SELECT m FROM StorableMovie m WHERE m.title = :title")
public class StorableMovie {
    
    @Id
    @GeneratedValue
    private long id;

    @Column(unique=true, nullable=false)
    private String title;
    
    private String plot;
    private Integer year;
    
    private String director;

    private MovieType type;
    
    /**
     * Runtime in minutes 
     */
    private Integer runtime;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Genre> genres;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private Set<Language> languages;

    
    
    
    /** Creates a new instance of StorableMovie */
    public StorableMovie() {
        this.genres = new HashSet<Genre>();
        this.languages = new HashSet<Language>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
     * @return the runtime
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }



    
    
    @Override
    public String toString() {
        return "Movie "+getId()+": "+getTitle();
    }

}
