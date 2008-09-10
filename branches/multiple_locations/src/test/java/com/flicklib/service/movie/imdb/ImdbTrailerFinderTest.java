package com.flicklib.service.movie.imdb;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class ImdbTrailerFinderTest {

    /**
     * Test of findTrailerUrl method, of class ImdbTrailerFinder.
     */
    @Test
    public void testFindTrailerUrl() {
        ImdbTrailerFinder instance = new ImdbTrailerFinder();
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        site.setIdForSite("123");
        String url = instance.findTrailerUrl(site.getMovie().getTitle(), site.getIdForSite());
        assertEquals("http://www.imdb.com/title/tt123/trailers", url);
    }

}