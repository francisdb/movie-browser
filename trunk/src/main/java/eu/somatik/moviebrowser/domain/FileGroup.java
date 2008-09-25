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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 
 * @author zsombor
 * 
 */
@Entity
@Table(name = "FileGroup")
@NamedQueries( { 
    @NamedQuery(name = "FileGroup.findByFile", query = "SELECT f.group FROM StorableMovieFile f WHERE f.name = :filename AND f.size = :size")
})
public class FileGroup {

    public final static long MB = 1024*1024;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    StorableMovie movie;

    @ManyToOne
    Language audio;

    @ManyToOne
    Language subtitle;

    @Enumerated(EnumType.STRING)
    ReleaseType type;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<StorableMovieFile> files;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<MovieLocation> locations;

    public FileGroup() {
        files = new HashSet<StorableMovieFile>();
        locations = new HashSet<MovieLocation>();
    }

    public Integer getId() {
        return id;
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }

    public Set<StorableMovieFile> getFiles() {
        return files;
    }

    public void setSubtitle(Language subtitle) {
        this.subtitle = subtitle;
    }

    public void setAudio(Language audio) {
        this.audio = audio;
    }

    public ReleaseType getType() {
        return type;
    }

    public void setType(ReleaseType type) {
        this.type = type;
    }

    public Language getAudio() {
        return audio;
    }

    public Language getSubtitle() {
        return subtitle;
    }
    
    public Set<MovieLocation> getLocations() {
        return locations;
    }
    
    public void setLocations(Set<MovieLocation> locations) {
        this.locations = locations;
    }

    
    /**
     * 
     */
    @Transient
    public void guessReleaseType() {
        if (getType()==null) {
            long size = getSize();
            if (size>500*MB && size<=900*MB) {
                setType(ReleaseType.ONE_CD);
            } else {
                if (size>900*MB && size<=1500*MB) {
                    setType(ReleaseType.TWO_CD);
                } else {
                    if (size>1500*MB && size<=4500*MB) {
                        setType(ReleaseType.DVD);
                    }
                }
            }
        }
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

    public MovieLocation getDirectory(String path) {
        for (MovieLocation f : locations) {
            if (f.getPath().equals(path)) {
                return f;
            }
        }
        MovieLocation f = new MovieLocation();
        f.setPath(path);
        addLocation(f);
        return f;
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
            if (f.getType() == type) {
                return f;
            }
        }
        return null;
    }


    public void addLocation(MovieLocation f) {
        if (f.getGroup()!= null) {
            f.getGroup().getLocations().remove(f);
        }
        f.setMovie(getMovie());
        f.setGroup(this);
        this.locations.add(f);
    }

    public void addFile(StorableMovieFile file) {
        if (file.getGroup()!=null) {
            file.getGroup().getFiles().remove(file);
        }
        file.setMovie(this.getMovie());
        file.setGroup(this);
        this.files.add(file);
    }


    @Transient
    public long getSize() {
        long size = 0;
        for (StorableMovieFile sm : getFiles()) {
            size += sm.getSize();
        }
        return size;
    }
    
    

}
