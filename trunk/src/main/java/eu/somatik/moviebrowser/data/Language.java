/*
 * Language.java
 *
 * Created on May 7, 2007, 9:34 PM
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
public class Language {
    
    @Id
    private String name;
    
    /** Creates a new instance of Language */
    public Language() {
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
