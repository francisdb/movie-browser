/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flicklib.domain;

/**
 *
 * @author francisdb
 */
public enum MovieType {
    MOVIE("Movie"),
    TV_MOVIE("TV Movie"),
    VIDEO_MOVIE("Video Movie"),
    TV_SERIES("TV Series"),
    MINI_SERIES("Mini Series");

    private String name;

    private MovieType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



}
