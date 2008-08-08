package com.flicklib.api;

/**
 * Finds the url for the trailer site/page
 * @author francisdb
 */
public interface TrailerFinder {

    String findTrailerUrl(String title, String localId);

}
