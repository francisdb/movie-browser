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
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * Calculates the average score for all services
 * @author francisdb
 */
@Singleton
public class InfoHandlerImpl implements InfoHandler {
    
    @Override
    public Integer score(MovieInfo info, MovieService movieService) {
        Integer val;
        StorableMovieSite site = info.siteFor(movieService);
        if (site == null) {
            val = null;
        } else {
            val = site.getScore();
        }
        return val;
    }

    @Override
    public String url(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = site.getUrl();
        }
        return val;
    }
    
    @Override
    public Integer votes(MovieInfo info, MovieService service) {
        Integer val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = site.getVotes();
        }
        return val;
    }

    @Override
    public String id(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = site.getIdForSite();
        }
        return val;
    }
    
    

}
