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

/**
 * 
 * This content provider shows hungarian titles, and gets the title, plot information from the porthu service, and 
 * fallbacks to IMDB. 
 * 
 * 
 * @author zsombor
 *
 */
public class PorthuContentProvider extends AbstractFallbackContentProvider implements ContentProvider {

    public PorthuContentProvider() {
        super(Services.PORTHU);
    }
    
    @Override
    public String getImageUrl(MovieInfo info) {
        String xpressImage = getImageUrl(info, MovieService.getById(Services.XPRESSHU));
        return xpressImage != null ? xpressImage : super.getImageUrl(info);
    }

}
