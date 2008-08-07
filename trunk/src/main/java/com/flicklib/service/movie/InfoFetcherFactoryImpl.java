package com.flicklib.service.movie;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.MovieService;
import com.flicklib.service.movie.flixter.Flixter;
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

    @Inject
    public InfoFetcherFactoryImpl(
            final @Imdb MovieInfoFetcher imdbInfoFetcher,
            final @MovieWeb MovieInfoFetcher movieWebInfoFetcher,
            final @RottenTomatoes MovieInfoFetcher tomatoesInfoFetcher,
            final @Google MovieInfoFetcher googleInfoFetcher,
            final @Flixter MovieInfoFetcher flixterInfoFetcher,
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
            case FLIXTER:
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
