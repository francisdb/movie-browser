package com.flicklib.service.movie.imdb;

import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.SourceLoader;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class ImdbInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbInfoFetcher.class);
    private final ImdbSearch imdbSearch;
    private final Parser imdbParser;
    private final SourceLoader loader;

    @Inject
    public ImdbInfoFetcher(ImdbSearch imdbSearch, final @Imdb Parser imdbParser, SourceLoader loader) {
        this.imdbSearch = imdbSearch;
        this.imdbParser = imdbParser;
        this.loader = loader;
    }

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.IMDB);

        if (id != null) {
            LOGGER.info("Generating IMDB url form known IMDB id");
            site.setUrl(ImdbUrlGenerator.generateImdbUrl(id));
            site.setIdForSite(id);
        }

        // TODO should be better way to deal with direct IMDB results

        try {
            if (site.getUrl() == null) {
                List<MoviePage> movies = imdbSearch.getResults(movie.getTitle());
                if (movies.size() == 0) {
                    throw new IOException("No movies found");
                }
                if (movies.size() == 1) {
                    // TODO copy all data instead of reload
                    site.setIdForSite(movies.get(0).getIdForSite());
                    site.setUrl(movies.get(0).getUrl());
                } else {
                    // TAKE FIRST RESULT
                    // TODO Move code below to this level
                    site.setIdForSite(movies.get(0).getIdForSite());
                    site.setUrl(movies.get(0).getUrl());
                }
            }

            String source = loader.load(site.getUrl());
            Source jerichoSource = new Source(source);
            jerichoSource.fullSequentialParse();
            imdbParser.parse(source, site);
        } catch (IOException ex) {
            LOGGER.error("Loading from IMDB failed", ex);
        }
        return site;
    }
}
