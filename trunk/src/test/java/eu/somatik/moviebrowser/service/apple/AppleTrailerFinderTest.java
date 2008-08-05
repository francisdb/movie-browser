package eu.somatik.moviebrowser.service.apple;

import eu.somatik.moviebrowser.service.apple.AppleTrailerFinder;
import eu.somatik.moviebrowser.domain.Movie;

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
        Movie movie = new Movie();
        movie.setTitle("Big fish");
        AppleTrailerFinder instance = new AppleTrailerFinder();
        String url = instance.findTrailerUrl(movie);
        assertEquals("http://www.apple.com/trailers/sony_pictures/big_fish/", url);
    }

}