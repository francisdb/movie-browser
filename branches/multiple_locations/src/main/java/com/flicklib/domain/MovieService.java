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
