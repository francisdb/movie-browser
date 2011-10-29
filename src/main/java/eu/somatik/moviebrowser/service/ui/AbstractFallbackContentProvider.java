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
package eu.somatik.moviebrowser.service.ui;

import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * An abstract content provider that falls back to IMDB and implements plot and title content
 * @author francisdb
 */
public abstract class AbstractFallbackContentProvider extends ImdbContentProvider implements ContentProvider{

    protected final MovieService movieService;

    /**
     * Creates a new provider for the selected movieService
     * @param movieService
     */
    public AbstractFallbackContentProvider(String id) {
        this.movieService = MovieService.valueOf(id);
    }

     /* (non-Javadoc)
     * @see eu.somatik.moviebrowser.service.ui.ContentProvider#getPlot(eu.somatik.moviebrowser.domain.MovieInfo)
     */
    @Override
    public String getPlot(MovieInfo info) {
        StorableMovie movie = info.getMovie();
        if (movie!=null) {
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(movieService);
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
            StorableMovieSite siteInfo = movie.getMovieSiteInfo(movieService);
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
