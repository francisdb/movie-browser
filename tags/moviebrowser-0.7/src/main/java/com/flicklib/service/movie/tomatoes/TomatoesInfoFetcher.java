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
