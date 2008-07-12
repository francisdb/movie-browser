/*
 * Language.java
 *
 * Created on May 7, 2007, 9:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
