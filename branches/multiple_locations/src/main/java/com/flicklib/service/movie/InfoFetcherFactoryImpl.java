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
package com.flicklib.service.movie;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.MovieService;
import com.flicklib.service.movie.flixter.Flixster;
import com.flicklib.service.movie.google.Google;
import com.flicklib.service.movie.imdb.Imdb;
import com.flicklib.service.movie.movieweb.MovieWeb;
import com.flicklib.service.movie.omdb.Omdb;
import com.flicklib.service.movie.tomatoes.RottenTomatoes;

/**
 *
 * @author francisdb
 */
@Singleton
public class InfoFetcherFactoryImpl implements InfoFetcherFactory{
    
    private final MovieInfoFetcher imdbInfoFetcher;
    private final MovieInfoFetcher movieWebInfoFetcher;
    private final MovieInfoFetcher tomatoesInfoFetcher;
    private final MovieInfoFetcher googleInfoFetcher;
    private final MovieInfoFetcher flixterInfoFetcher;
    private final MovieInfoFetcher omdbInfoFetcher;

    /**
     * Constructs a new InfoFetcherFactoryImpl
     * @param imdbInfoFetcher
     * @param movieWebInfoFetcher
     * @param tomatoesInfoFetcher
     * @param googleInfoFetcher
     * @param flixterInfoFetcher
     * @param omdbInfoFetcher
     */
    @Inject
    public InfoFetcherFactoryImpl(
            final @Imdb MovieInfoFetcher imdbInfoFetcher,
            final @MovieWeb MovieInfoFetcher movieWebInfoFetcher,
            final @RottenTomatoes MovieInfoFetcher tomatoesInfoFetcher,
            final @Google MovieInfoFetcher googleInfoFetcher,
            final @Flixster MovieInfoFetcher flixterInfoFetcher,
            final @Omdb MovieInfoFetcher omdbInfoFetcher) {
        this.imdbInfoFetcher = imdbInfoFetcher;
        this.movieWebInfoFetcher = movieWebInfoFetcher;
        this.tomatoesInfoFetcher = tomatoesInfoFetcher;
        this.googleInfoFetcher = googleInfoFetcher;
        this.flixterInfoFetcher = flixterInfoFetcher;
        this.omdbInfoFetcher = omdbInfoFetcher;
    }
    
    

    @Override
    public MovieInfoFetcher get(MovieService service) {
        MovieInfoFetcher fetcher = null;
        switch(service){
            case FLIXSTER:
                fetcher = flixterInfoFetcher;
                break;
            case GOOGLE:
                fetcher = googleInfoFetcher;
                break;
            case IMDB:
                fetcher = imdbInfoFetcher;
                break;
            case MOVIEWEB:
                fetcher = movieWebInfoFetcher;
                break;
            case OMDB:
                fetcher = omdbInfoFetcher;
                break;
            case TOMATOES:
                fetcher = tomatoesInfoFetcher;
                break;
            default:
                throw new AssertionError("Unknown service: "+service);
        }
        return fetcher;
    }

}
