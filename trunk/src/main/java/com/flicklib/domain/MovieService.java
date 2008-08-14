package com.flicklib.domain;

/**
 *
 * @author francisdb
 */
public enum MovieService {
    /**
     * http://www.imdb.com
     */
    IMDB("IMDB", "http://www.imdb.com"),
    
    /**
     * http://www.rottentomatoes.com
     */
    TOMATOES("Rotten Tomatoes", "http://www.rottentomatoes.com"),
    
    /**
     * http://www.movieweb.com
     */
    MOVIEWEB("MovieWeb", "http://www.movieweb.com/"),
    
    /**
     * http://www.omdb.com
     */
    OMDB("OMDB", "http://www.omdb.com"),
    
    /**
     * http://www.google.com/movies
     */
    GOOGLE("Google movies", "http://www.google.com/movies"),
    
    /**
     * http://www.flixster.com
     */
    FLIXSTER("Flixter", "http://www.flixter.com");

    private final String name;
    private final String url;

    MovieService(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

}
