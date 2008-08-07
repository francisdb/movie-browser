package eu.somatik.moviebrowser.service.tomatoes;

import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieSite;
import com.flicklib.service.SourceLoader;
import java.io.IOException;

import java.util.Date;
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
    public MovieSite fetch(Movie movie) {
        MovieSite site = new MovieSite();
        site.setMovie(movie);
        site.setService(MovieService.TOMATOES);
        site.setTime(new Date());
        if (!"".equals(movie.getImdbId())) {
            try {
                String url = generateTomatoesUrl(movie);
                site.getMovie().setTomatoUrl(url);
                site.setUrl(url);
                String source = sourceLoader.load(movie.getTomatoUrl());
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
    private String generateTomatoesUrl(Movie movie) {
        return "http://www.rottentomatoes.com/alias?type=imdbid&s=" + movie.getImdbId();
    }
}
