package com.flicklib.service.movie.google;

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
public class GoogleInfoFetcherTest {


    /**
     * Test of fetch method, of class GoogleInfoFetcher.
     */
    @Test
    @Ignore(value="Disabled for CI")
    public void testFetch() {
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        GoogleParser googleParser = new GoogleParser();
        GoogleInfoFetcher instance = new GoogleInfoFetcher(googleParser, new HttpSourceLoader(null));
        MoviePage site = instance.fetch(movie, null);
        assertNotNull("Google score is null", site.getScore());
    }

}