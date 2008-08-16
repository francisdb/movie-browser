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
package com.flicklib.service.movie.tomatoes;

import com.flicklib.api.MovieInfoFetcher;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.SourceLoader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fdb
 */
@Singleton
public class TomatoesInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomatoesInfoFetcher.class);

    private final SourceLoader sourceLoader;
    private final Parser tomatoesParser;

    @Inject
    public TomatoesInfoFetcher(final @RottenTomatoes Parser tomatoesParser, final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
        this.tomatoesParser = tomatoesParser;
    }

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.TOMATOES);
        if (id == null || "".equals(id)) {
            LOGGER.error("IMDB id missing", new IOException("No imdb id available, not implemented"));
        }else{
            try {
                String url = generateTomatoesUrl(id);
                site.setUrl(url);
                String source = sourceLoader.load(site.getUrl());
                tomatoesParser.parse(source, site);
            } catch (IOException ex) {
                LOGGER.error("Loading from rotten tomatoes failed", ex);
            }
        }
        return site;
    }

    /**
     *
     * @param movie 
     * @return the tomatoes url
     */
    private String generateTomatoesUrl(String imdbId) {
        return "http://www.rottentomatoes.com/alias?type=imdbid&s=" + imdbId;
    }
}
