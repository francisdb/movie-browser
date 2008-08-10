package com.flicklib.service.movie.movieweb;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.HttpSourceLoader;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieWebInfoFetcherTest {


    /**
     * Test of load method, of class MovieWebInfoFetcher.
     */
    @Test
    @Ignore(value="Disabled for CI")
    public void testFetch() {
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        MovieWebParser parser = new MovieWebParser();
        MovieWebInfoFetcher fetcher = new MovieWebInfoFetcher(parser, new HttpSourceLoader(null));
        MoviePage site = fetcher.fetch(movie, null);
        assertNotNull("MovieWebStars is null", site.getScore());
    }

}