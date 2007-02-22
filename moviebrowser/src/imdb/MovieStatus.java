/*
 * MovieStatus.java
 *
 * Created on February 14, 2007, 10:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package imdb;

/**
 *
 * @author francisdb
 */
public enum MovieStatus {
    /**
     * New in list
     */
    NEW, 
    
    /**
     * Loading imdb data
     */
    LOADING_IMDB, 
    
    /**
     * Loading tomatoes data
     */
    LOADING_TOMATOES, 
    
    /**
     * Loading completed
     */
    LOADED    
}
