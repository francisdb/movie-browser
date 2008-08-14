package com.flicklib.service.movie.omdb;

import com.flicklib.domain.Movie;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class OmdbFetcherTest {
	
    /**
     * Test of fetch method, of class OmdbFetcher.
     */
    @Test
    @Ignore
    public void testFetch() {
        Movie movie = new Movie();
        OmdbFetcher instance = new OmdbFetcher();
        instance.fetch(movie, null);
        assertNotNull(movie);
    }

}