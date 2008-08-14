package com.flicklib.service.movie.movieweb;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieWebParserTest {
	
    /**
     * Test of parse method, of class MovieWebParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception {
        String source = new FileSourceLoader().load("movieweb/pulp_fiction_summary.php.html");
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        MovieWebParser instance = new MovieWebParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(100), site.getScore());
    }


}