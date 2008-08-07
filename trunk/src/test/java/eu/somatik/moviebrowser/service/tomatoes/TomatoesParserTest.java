package eu.somatik.moviebrowser.service.tomatoes;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieSite;
import com.flicklib.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class TomatoesParserTest {

    public TomatoesParserTest() {
    }

    /**
     * Test of parse method, of class TomatoesParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception {
        String source = new FileSourceLoader().load("tomatoes/Pulp Fiction Movie Reviews, Pictures - Rotten Tomatoes.html");
        MovieSite site = new MovieSite();
        site.setMovie(new Movie());
        TomatoesParser instance = new TomatoesParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(96), site.getMovie().getTomatoScore());
    }

}