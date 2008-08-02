package eu.somatik.moviebrowser.service.movieweb;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieWebParserTest {

    public MovieWebParserTest() {
    }

    /**
     * Test of parse method, of class MovieWebParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception {
        Source source = new FileSourceLoader().load("movieweb/pulp_fiction_summary.php.html");
        Movie movie = new Movie();
        MovieWebParser instance = new MovieWebParser();
        instance.parse(source, movie);
        assertEquals(Integer.valueOf(100), movie.getMovieWebScore());
    }


}