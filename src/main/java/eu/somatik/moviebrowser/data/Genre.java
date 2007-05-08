/*
 * Genre.java
 *
 * Created on May 7, 2007, 9:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.data;

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
    }

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
     * @return 
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    

    
    
}
