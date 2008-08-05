package eu.somatik.moviebrowser.service.google;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class GoogleParserTest {

    /**
     * Test of parse method, of class GoogleParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception {
        String source = new FileSourceLoader().load("google/reviews.html");
        Movie movie = new Movie();
        GoogleParser instance = new GoogleParser();
        instance.parse(source, movie);
        assertEquals(Integer.valueOf(78), movie.getGoogleScore());
    }

}