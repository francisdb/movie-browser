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
import javax.persistence.Id;

/**
 *
 * @author francisdb
 */
@Entity
public class Language {
    
    @Id
    private String name;
    
    /** Creates a new instance of Language */
    public Language() {
    	// empty
    }

    /**
     * Constructs a new Language object
     *
     * @param name
     */
    public Language(String name) {
        this.name = name;
    }
    
    
    
    /**
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    
    
}
