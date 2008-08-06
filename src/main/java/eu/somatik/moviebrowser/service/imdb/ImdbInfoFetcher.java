package eu.somatik.moviebrowser.service.imdb;

import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieSite;
import eu.somatik.moviebrowser.service.SourceLoader;
import java.io.IOException;
import java.util.Date;
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
    public MovieSite fetch(Movie movie) {
        MovieSite site = new MovieSite();
        site.setMovie(movie);
        site.setService(MovieService.IMDB);
        site.setTime(new Date());
        // NOT SURE THIS IS NEEDED !!!
        if( movie.getImdbUrl() != null && movie.getImdbUrl().startsWith("http://www.imdb.com/title/tt")){
            movie.setImdbId(movie.getImdbUrl().replaceAll("[a-zA-Z:/.+=?]", "").trim());
        }
        
        // TODO should be better way to deal with direct IMDB results
        
        try {
            if(movie.getImdbUrl() == null){
                List<Movie> movies = imdbSearch.getResults(movie.getTitle());
                if (movies.size() == 0) {
                    throw new IOException("No movies found");
                } if(movies.size() == 1) {
                    // TODO copy all data instead of reload
                    movie.setImdbUrl(movies.get(0).getImdbUrl());
                    movie.setImdbId(movies.get(0).getImdbId());
                    site.setUrl(movies.get(0).getImdbUrl());
                }else{
                    // TAKE FIRST RESULT
                    // TODO Move code below to this level
                    movie.setImdbUrl(movies.get(0).getImdbUrl());
                    movie.setImdbId(movies.get(0).getImdbId());
                    site.setUrl(movies.get(0).getImdbUrl());
                }
            }

            String source = loader.load(movie.getImdbUrl());
            Source jerichoSource = new Source(source);
            jerichoSource.fullSequentialParse();
            imdbParser.parse(source, site);
        } catch (IOException ex) {
            LOGGER.error("Loading from IMDB failed", ex);
        }
        return site;
    }
}
