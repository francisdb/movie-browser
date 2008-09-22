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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Location")
public class MovieLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String path;

    String label;

    @ManyToOne
    StorableMovie movie;

    @ManyToOne
    FileGroup group;
    
    public MovieLocation() {

    }

    public MovieLocation(String path, String label) {
        this.path = path;
        this.label = label;
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

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MovieLocation[id:"+id+",label:"+label+",path:"+path+"]";
    }

}
