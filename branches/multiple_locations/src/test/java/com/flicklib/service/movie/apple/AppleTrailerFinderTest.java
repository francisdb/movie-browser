package com.flicklib.service.movie.apple;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class AppleTrailerFinderTest {

    /**
     * Test of findTrailerUrl method, of class AppleTrailerFinder.
     */
    @Test
    public void testFindTrailerUrl() {
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        site.getMovie().setTitle("Big fish");
        AppleTrailerFinder instance = new AppleTrailerFinder();
        String url = instance.findTrailerUrl(site.getMovie().getTitle(), site.getIdForSite());
        assertEquals("http://www.apple.com/trailers/sony_pictures/big_fish/", url);
    }

}