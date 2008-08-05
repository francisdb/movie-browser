package eu.somatik.moviebrowser.service.google;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class GoogleInfoFetcherTest {

    public GoogleInfoFetcherTest() {
    }

    /**
     * Test of fetch method, of class GoogleInfoFetcher.
     */
    @Test
    @Ignore(value="Disabled for CI")
    public void testFetch() {
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        GoogleParser googleParser = new GoogleParser();
        GoogleInfoFetcher instance = new GoogleInfoFetcher(googleParser, new HttpSourceLoader());
        instance.fetch(movie);
        assertNotNull("Google score is null", movie.getGoogleScore());
    }

}