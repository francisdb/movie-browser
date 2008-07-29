package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.service.fetcher.MovieWebInfoFetcher;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.service.parser.MovieWebParser;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieWebInfoFetcherTest {

    public MovieWebInfoFetcherTest() {
    }


    /**
     * Test of load method, of class MovieWebInfoFetcher.
     */
    @Test
    @Ignore
    public void testFetch() {
        MovieInfo movieInfo = new MovieInfo(new File("/tmp"));
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        movieInfo.setMovie(movie);
        MovieWebParser parser = new MovieWebParser();
        MovieWebInfoFetcher fetcher = new MovieWebInfoFetcher(parser, new HttpSourceLoader());
        fetcher.fetch(movieInfo.getMovie());
        assertNotNull("MovieWebStars is null", movie.getMovieWebScore());
    }

}