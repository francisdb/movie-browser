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
package eu.somatik.moviebrowser.service;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 *
 * @author francisdb
 */
public interface InfoHandler {

    /**
     * Gets the score for a service
     * @param info
     * @param movieService
     * @return
     */
    Integer score(MovieInfo info, MovieService movieService);
    
    /**
     * Gets the url for a service
     * @param info
     * @param service
     * @return
     */
    String url(MovieInfo info, MovieService service);
    
    
    /**
     * Gets the votes for a service
     * @param info
     * @param service
     * @return
     */
    Integer votes(MovieInfo info, MovieService service);
    
    /**
     * Gets the id for a service
     * @param info
     * @param service
     * @return
     */
    String id(MovieInfo info, MovieService service);

}
