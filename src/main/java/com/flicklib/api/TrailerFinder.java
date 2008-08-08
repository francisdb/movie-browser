package com.flicklib.api;

/**
 * Finds the url for the trailer site/page
 * @author francisdb
 */
public interface TrailerFinder {

    /**
     * Finds a trailer for this title or id
     * @param title
     * @param localId
     * @return the url String for this title
     */
    String findTrailerUrl(String title, String localId);

}
