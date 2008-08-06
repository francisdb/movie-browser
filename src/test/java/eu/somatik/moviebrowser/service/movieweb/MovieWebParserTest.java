package eu.somatik.moviebrowser.service.movieweb;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieSite;
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
        String source = new FileSourceLoader().load("movieweb/pulp_fiction_summary.php.html");
        MovieSite site = new MovieSite();
        site.setMovie(new Movie());
        MovieWebParser instance = new MovieWebParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(100), site.getMovie().getMovieWebScore());
    }


}