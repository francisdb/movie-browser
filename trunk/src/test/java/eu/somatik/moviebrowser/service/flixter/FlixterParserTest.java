package eu.somatik.moviebrowser.service.flixter;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieSite;
import eu.somatik.moviebrowser.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class FlixterParserTest {


    /**
     * Test of parse method, of class FlixterParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception{
        String source = new FileSourceLoader().load("flixter/the-x-files-i-want-to-believe-the-x-files-2.html");
        MovieSite site = new MovieSite();
        site.setMovie(new Movie());
        FlixterParser instance = new FlixterParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(60), site.getMovie().getFlixterScore());
    }

}