package eu.somatik.moviebrowser.service.tomatoes;

import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.service.SourceLoader;
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
    public void fetch(Movie movie) {
        if (!"".equals(movie.getImdbId())) {
            try {
                movie.setTomatoUrl(generateTomatoesUrl(movie));
                String source = sourceLoader.load(movie.getTomatoUrl());
                tomatoesParser.parse(source, movie);
            } catch (IOException ex) {
                LOGGER.error("Loading from rotten tomatoes failed", ex);
            }
        }
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
