/*
 * Genre.java
 *
 * Created on May 7, 2007, 9:35 PM
 *
 */

package com.flicklib.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author francisdb
 */
@Entity
public class Genre {
    
    @Id
    private String name;
    
    /** Creates a new instance of Genre */
    public Genre() {
    	// nothing here
    }

    /**
     * Constructs a new Genre object
     *
     * @param name
     */
    public Genre(String name) {
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
