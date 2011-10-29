/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Zsombor Gegesy
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
package eu.somatik.moviebrowser.service.ui;

import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.Services;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * This content provider use IMDB informations, but if not present, shows the generic informations, which stored in the
 * StoredMovie object.
 *  
 * @author zsombor
 *
 */
public class ImdbContentProvider implements ContentProvider {

    @Override
    public String getImageUrl(MovieInfo info) {
        return getImageUrl(info, MovieService.getById(Services.IMDB));
    }
    
    protected String getImageUrl(MovieInfo info, MovieService service) {
        StorableMovie movie = info.getMovie(); 
        if (movie!=null && service != null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(service);
            if (siteInfo!=null) {
                String imgUrl = siteInfo.getImgUrl();
                if (imgUrl!=null) {
                    return imgUrl;
                }
            }
        }
        return null;
    }

    @Override
    public String getPlot(MovieInfo info) {
        StorableMovie movie = info.getMovie(); 
        if (movie!=null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(MovieService.getById(Services.IMDB));
            if (siteInfo!=null) {
                String plot = siteInfo.getPlot();
                if (plot!=null) {
                    return plot;
                }
            }
            return movie.getPlot();
        }
        return null;
    }

    @Override
    public String getTitle(MovieInfo info) {
        StorableMovie movie = info.getMovie(); 
        if (movie!=null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(MovieService.getById(Services.IMDB));
            if (siteInfo!=null) {
                String title = siteInfo.getTitle();
                if (title!=null) {
                    return title;
                }
                title = siteInfo.getOriginalTitle();
                if (title!=null) {
                    return title;
                }
            }
            return movie.getTitle();
        }
        return null;
    }

}
