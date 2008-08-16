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
