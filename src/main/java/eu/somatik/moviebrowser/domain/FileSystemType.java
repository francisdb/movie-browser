/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.domain;

/**
 *
 * @author francisdb
 */
public enum FileSystemType {
    /**
     * Just see all subdirs as movie locations
     */
    SIMPLE,

    /**
     * Do a recursive scan and find movies
     */
    ADVANCED
}
