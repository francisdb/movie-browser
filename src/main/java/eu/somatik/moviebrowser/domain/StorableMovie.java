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

import com.flicklib.domain.MovieService;
import com.flicklib.domain.MovieType;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author francisdb
 */
@Entity
@Table(name = "Movie")
@NamedQueries( { 
    @NamedQuery(name = "StorableMovie.findByTitle", query = "SELECT m FROM StorableMovie m WHERE m.title = :title"),
    @NamedQuery(name = "StorableMovie.findAll", query = "SELECT s FROM StorableMovie s"), 
    @NamedQuery(name = "StorableMovie.findByFile", query = "SELECT f.movie FROM StorableMovieFile f WHERE f.name = :filename AND f.size = :size")
    
})
public class StorableMovie {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    private String plot;
    private Integer year;

    private String director;

    private MovieType type;

    /**
     * Runtime in minutes
     */
    private Integer runtime;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Genre> genres;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Language> languages;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    /*    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<StorableMovieFile> files;

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MovieLocation> locations;*/
    
    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<StorableMovieSite> siteInfo;
    
    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FileGroup> groups;
    

    /** Creates a new instance of StorableMovie */
    public StorableMovie() {
        this.genres = new HashSet<Genre>();
        this.languages = new HashSet<Language>();
        this.siteInfo = new HashSet<StorableMovieSite>();
        this.groups = new HashSet<FileGroup> ();
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
    public void addLanguage(Language language) {
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


    /**
     * valid means, have at least one MovieLocation and one or more VIDEO_CONTENT
     * @return
     */
    @Transient
    public boolean isValid() {
        if (getGroups().isEmpty()) {
            return false;
        }
        boolean hasLocation = false;
        for (FileGroup g : getGroups()) {
            if (!g.getLocations().isEmpty()) {
                hasLocation = true;
            }
        }
        if (!hasLocation) {
            return false;
        }
        for (FileGroup g : getGroups()) {
            for (StorableMovieFile sm : g.getFiles()) {
                if (sm.getType() == FileType.VIDEO_CONTENT) {
                    return true;
                }
            }
        }
        return false;
    }
    
    

    
    public Set<StorableMovieSite> getSiteInfo() {
        return siteInfo;
    }
    
    public Set<FileGroup> getGroups() {
        return groups;
    }
    
    
    @Transient
    public StorableMovieSite getMovieSiteInfo(MovieService service) {
        for (StorableMovieSite s : siteInfo) {
            if (s.getService()==service) {
                return s;
            }
        }
        return null;
    }
    
    public StorableMovieSite getMovieSiteInfoOrCreate(MovieService service) {
        StorableMovieSite sms = getMovieSiteInfo(service);
        if (sms==null) {
            sms = new StorableMovieSite();
            sms.setService(service);
            addSiteInfo(sms);
        }
        return sms;
    }
    
    
    @Transient
    public void addSiteInfo(StorableMovieSite sms) {
        if (sms.getMovie()!=this) {
            if (sms.getMovie()!=null) {
                sms.getMovie().getSiteInfo().remove(sms);
            }
            getSiteInfo().add(sms);
            sms.setMovie(this);
        }
    }
    
    @Transient
    public void addFileGroup(FileGroup fg) {
        if (fg.getMovie()!=null) {
            fg.getMovie().getGroups().remove(fg);
        }
        fg.setMovie(this);
        getGroups().add(fg);
        for (StorableMovieFile f : fg.getFiles()) {
            if (f.getMovie()!=this) {
                f.setMovie(this);
            }
        }
        for (MovieLocation l : fg.getLocations()) {
            l.setMovie(this);
        }
    }


    @Transient 
    public Set<MovieLocation> getLocations() {
        Set<MovieLocation> s = new HashSet<MovieLocation>();
        for (FileGroup f : getGroups()) {
            s.addAll(f.getLocations());
        }
        return s;
    }
    
    @Transient
    public FileGroup getUniqueFileGroup() {
        if (getGroups().size()==1) {
            return getGroups().iterator().next();
        }
        return null;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return lastModified;
    }
    
    @Transient
    public long getSize() {
        long size = 0;
        for (FileGroup g : getGroups()) {
            size += g.getSize();
        }
        return size;
    }

    @Override
    public String toString() {
        return "Movie " + getId() + ": " + getTitle();
    }

}
