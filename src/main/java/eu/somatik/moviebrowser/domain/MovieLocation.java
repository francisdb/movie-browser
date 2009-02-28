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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Location")
public class MovieLocation implements Cloneable, Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String path;

    String label;

    @ManyToOne
    StorableMovie movie;

    @ManyToOne
    FileGroup group;
    /**
     * mark if the folder is safe to rename, so it is not shared with other films.
     */
    @Column(nullable=false, columnDefinition="boolean default 0")
    boolean folderRenamingSafe = false;

    public MovieLocation() {

    }

    public MovieLocation(String path, String label) {
        this.path = path;
        this.label = label;
    }

    public MovieLocation(MovieLocation oldLoc) {
        this.path = oldLoc.path;
        this.label = oldLoc.label;
        this.folderRenamingSafe = oldLoc.folderRenamingSafe;
    }

    
    public MovieLocation(String path, String label, boolean renamingSafe) {
        this.path = path;
        this.label = label;
        this.folderRenamingSafe = renamingSafe;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }
    
    public FileGroup getGroup() {
        return group;
    }
    
    public void setGroup(FileGroup group) {
        this.group = group;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isFolderRenamingSafe() {
        return folderRenamingSafe;
    }
    
    public void setFolderRenamingSafe(boolean folderRenamingSafe) {
        this.folderRenamingSafe = folderRenamingSafe;
    }
    @Override
    public String toString() {
        return "MovieLocation[id:"+id+",label:"+label+",path:"+path+"]";
    }

    @Override
    public MovieLocation clone() throws CloneNotSupportedException {
        return (MovieLocation) super.clone();
    }
    
}
