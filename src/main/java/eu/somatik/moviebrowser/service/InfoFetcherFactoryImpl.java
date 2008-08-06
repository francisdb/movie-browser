package eu.somatik.moviebrowser.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.api.InfoFetcherFactory;
import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.domain.MovieService;
import eu.somatik.moviebrowser.service.flixter.Flixter;
import eu.somatik.moviebrowser.service.google.Google;
import eu.somatik.moviebrowser.service.imdb.Imdb;
import eu.somatik.moviebrowser.service.movieweb.MovieWeb;
import eu.somatik.moviebrowser.service.omdb.Omdb;
import eu.somatik.moviebrowser.service.tomatoes.RottenTomatoes;

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
