package com.flicklib.service.movie.flixter;

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
public class FlixterInfoFetcherTest {

    /**
     * Test of fetch method, of class FlixterInfoFetcher.
     */
    @Test
    @Ignore
    public void testFetch() {
        Movie movie = new Movie();
        movie.setTitle("The X-Files I Want to Believe");
        FlixterParser parser = new FlixterParser();
        FlixterInfoFetcher fetcher = new FlixterInfoFetcher(parser, new HttpSourceLoader(null));
        MoviePage site = fetcher.fetch(movie, null);
        assertEquals("http://www.flixster.com/movie/the-x-files-i-want-to-believe-the-x-files-2", site.getUrl());
        assertNotNull("MovieWebStars is null", site.getScore());
        movie = new Movie();
        movie.setTitle("the good the bad and the ugly");
        site = fetcher.fetch(movie, null);
        assertEquals("The Good, the Bad and the Ugly", site.getMovie().getTitle());
        assertEquals("http://www.flixster.com/movie/the-good-the-bad-and-the-ugly", site.getUrl());
        
    }

}