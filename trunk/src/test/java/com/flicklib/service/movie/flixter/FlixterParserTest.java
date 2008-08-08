package com.flicklib.service.movie.flixter;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.FileSourceLoader;
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
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        FlixterParser instance = new FlixterParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(60), site.getScore());
        assertEquals(Integer.valueOf(14259), site.getVotes());
        assertEquals("The X-Files: I Want to Believe (The X Files 2)", site.getMovie().getTitle());
    }

}