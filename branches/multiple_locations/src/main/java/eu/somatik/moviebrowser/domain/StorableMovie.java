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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author francisdb
 */
@Entity
@Table(name="Movie")
@NamedQueries( {
		@NamedQuery(name = "StorableMovie.findByTitle", query = "SELECT m FROM StorableMovie m WHERE m.title = :title"),
		@NamedQuery(name = "StorableMovie.findAll", query = "SELECT s FROM StorableMovie s") })
public class StorableMovie {
    
    @Id
    @GeneratedValue
    private Long id;

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

    @OneToMany(mappedBy="movie", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<StorableMovieFile> files;
    
    @OneToMany(mappedBy="movie", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MovieLocation> locations;
    
    
    /** Creates a new instance of StorableMovie */
    public StorableMovie() {
        this.genres = new HashSet<Genre>();
        this.languages = new HashSet<Language>();
        this.files = new HashSet<StorableMovieFile>();
        this.locations = new HashSet<MovieLocation>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


    public Set<StorableMovieFile> getFiles() {
		return files;
	}

    public void addFile(StorableMovieFile file) {
    	if (file.getMovie()!=null) {
    		file.getMovie().getFiles().remove(file);
    	}
    	file.setMovie(this);
    	this.files.add(file);
    }
    
    /**
     * Return a file based on the file type.
     *     
     * @param type
     * @return
     */
    @Transient
    public StorableMovieFile getFileByType(FileType type) {
    	for (StorableMovieFile f : files) {
    		if (f.getType()==type) {
    			return f;
    		}
    	}
    	return null;
    }
    
    public MovieLocation getDirectory(String path) {
    	for (MovieLocation f : locations) {
			if (f.getPath().equals(path)) {
				return f;
			}
    	}
    	MovieLocation f = new MovieLocation ();
    	f.setPath(path);
    	addLocation(f);
    	return f;
    }
    
    public void addLocation(MovieLocation f) {
    	if (f.getMovie()!=null) {
    		f.getMovie().getLocations().remove(f);
    	}
    	f.setMovie(this);
    	this.locations.add(f);
	}
    
    public Set<MovieLocation> getLocations() {
		return locations;
	}

	@Transient
    public MovieLocation getDirectory() {
		for (MovieLocation f : locations) {
			return f;
		}
		return null;
    }
    
    @Transient
	public String getDirectoryPath() {
    	MovieLocation smf = getDirectory();
		return smf != null ? smf.getPath() : null;
	}
    
    @Transient
    public long getSize() {
    	long size = 0;
    	for (StorableMovieFile f : files) {
    		size += f.getSize();
    	}
    	return size;
    }
    
    
    @Override
    public String toString() {
        return "Movie "+getId()+": "+getTitle();
    }

}
