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

import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * 
 * This content provider shows hungarian titles, and gets the title, plot information from the porthu service, and 
 * fallbacks to IMDB. 
 * 
 * 
 * @author zsombor
 *
 */
public class PorthuContentProvider extends ImdbContentProvider implements ContentProvider {


    /* (non-Javadoc)
     * @see eu.somatik.moviebrowser.service.ui.ContentProvider#getPlot(eu.somatik.moviebrowser.domain.MovieInfo)
     */
    @Override
    public String getPlot(MovieInfo info) {
        StorableMovie movie = info.getMovie(); 
        if (movie!=null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(MovieService.PORTHU);
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

    /* (non-Javadoc)
     * @see eu.somatik.moviebrowser.service.ui.ContentProvider#getTitle(eu.somatik.moviebrowser.domain.MovieInfo)
     */
    @Override
    public String getTitle(MovieInfo info) {
        StorableMovie movie = info.getMovie(); 
        if (movie!=null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(MovieService.PORTHU);
            if (siteInfo!=null) {
                // alternate title is the translated title.
                String title = siteInfo.getAlternateTitle();
                if (title!=null) {
                    return title;
                }
                title = siteInfo.getTitle();
                if (title!=null) {
                    return title;
                }
            }
            return movie.getTitle();
        }
        return null;
    }

}
