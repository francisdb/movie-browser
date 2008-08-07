package eu.somatik.moviebrowser.service.movieweb;

import com.flicklib.service.HttpSourceLoader;
import eu.somatik.moviebrowser.service.*;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
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
    @Ignore(value="Disabled for CI")
    public void testFetch() {
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        MovieWebParser parser = new MovieWebParser();
        MovieWebInfoFetcher fetcher = new MovieWebInfoFetcher(parser, new HttpSourceLoader());
        fetcher.fetch(movie);
        assertNotNull("MovieWebStars is null", movie.getMovieWebScore());
    }

}