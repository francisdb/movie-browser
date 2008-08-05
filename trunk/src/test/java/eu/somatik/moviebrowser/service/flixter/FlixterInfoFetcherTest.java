package eu.somatik.moviebrowser.service.flixter;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
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
        movie.setTitle("Pulp Fiction");
        FlixterParser parser = new FlixterParser();
        FlixterInfoFetcher fetcher = new FlixterInfoFetcher(parser, new HttpSourceLoader());
        fetcher.fetch(movie);
        assertNotNull("MovieWebStars is null", movie.getFlixterScore());
    }

}