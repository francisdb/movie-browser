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
package com.flicklib.api;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;

/**
 *
 * @author fdb
 */
public interface MovieInfoFetcher {
    /**
     * Fetched movie info from a service and complements the movieInfo object
     * @param movie
     * @param id possible known id for this site, null for none
     * @return the parsed moviePage
     */
    MoviePage fetch(Movie movie, String id);
    
}
