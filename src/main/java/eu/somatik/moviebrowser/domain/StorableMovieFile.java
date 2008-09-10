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

import java.io.File;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author francisdb
 */
@Entity
@Table(name="File")
public class StorableMovieFile {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;
    
    @ManyToOne
    private StorableMovie movie;
    
    
    private String name;
    
    @Enumerated(value=EnumType.STRING)
    private FileType type;
    
    private long size;
    

    public StorableMovieFile() {
    }

    public StorableMovieFile(File file, FileType contentType) {
		setType(contentType);
		setSize(file.length());
    	setName(file.getName());
    }

    
    public Long getId() {
		return id;
	}
    
    public void setId(Long id) {
		this.id = id;
	}
    
    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
    
    
   

}
