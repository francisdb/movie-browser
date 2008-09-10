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
package com.flicklib.module;

import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.api.Parser;
import com.flicklib.api.SubtitlesLoader;
import com.flicklib.service.HttpSourceLoader;
import com.flicklib.service.SourceLoader;
import com.flicklib.service.movie.InfoFetcherFactoryImpl;
import com.flicklib.service.movie.flixter.Flixster;
import com.flicklib.service.movie.flixter.FlixterInfoFetcher;
import com.flicklib.service.movie.flixter.FlixterParser;
import com.flicklib.service.movie.google.Google;
import com.flicklib.service.movie.google.GoogleInfoFetcher;
import com.flicklib.service.movie.google.GoogleParser;
import com.flicklib.service.movie.imdb.Imdb;
import com.flicklib.service.movie.imdb.ImdbInfoFetcher;
import com.flicklib.service.movie.imdb.ImdbParser;
import com.flicklib.service.movie.movieweb.MovieWeb;
import com.flicklib.service.movie.movieweb.MovieWebInfoFetcher;
import com.flicklib.service.movie.movieweb.MovieWebParser;
import com.flicklib.service.movie.omdb.Omdb;
import com.flicklib.service.movie.omdb.OmdbFetcher;
import com.flicklib.service.movie.tomatoes.RottenTomatoes;
import com.flicklib.service.movie.tomatoes.TomatoesInfoFetcher;
import com.flicklib.service.movie.tomatoes.TomatoesParser;
import com.flicklib.service.sub.OpenSubtitlesLoader;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 *
 * @author francisdb
 */
public class FlicklibModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SourceLoader.class).to(HttpSourceLoader.class);

        bind(SubtitlesLoader.class).to(OpenSubtitlesLoader.class);

        bind(InfoFetcherFactory.class).to(InfoFetcherFactoryImpl.class);

        bind(Parser.class).annotatedWith(MovieWeb.class).to(MovieWebParser.class);
        bind(Parser.class).annotatedWith(Imdb.class).to(ImdbParser.class);
        bind(Parser.class).annotatedWith(RottenTomatoes.class).to(TomatoesParser.class);
        bind(Parser.class).annotatedWith(Google.class).to(GoogleParser.class);
        bind(Parser.class).annotatedWith(Flixster.class).to(FlixterParser.class);

        bind(MovieInfoFetcher.class).annotatedWith(Imdb.class).to(ImdbInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(MovieWeb.class).to(MovieWebInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(RottenTomatoes.class).to(TomatoesInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Google.class).to(GoogleInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Flixster.class).to(FlixterInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Omdb.class).to(OmdbFetcher.class);

        bindConstant().annotatedWith(Names.named("http.timeout")).to(20 * 1000);
    }
}
