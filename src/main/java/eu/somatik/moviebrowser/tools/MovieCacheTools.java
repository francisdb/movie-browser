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
package eu.somatik.moviebrowser.tools;

import com.flicklib.module.FlicklibModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.XmlMovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.module.MovieBrowserModule;

public class MovieCacheTools {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MovieBrowserModule(), new FlicklibModule());
        Settings settings = injector.getInstance(Settings.class);
        
        MovieCache cache = injector.getInstance(MovieCache.class);

        XmlMovieCache xml = new XmlMovieCache(settings);
        xml.startup();
        for (StorableMovie m : cache.list()) {
            m.setId(null);
            xml.insertOrUpdate(m);
        }
        xml.shutdown();
        
    }

}