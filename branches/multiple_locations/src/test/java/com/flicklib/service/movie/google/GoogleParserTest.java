package com.flicklib.service.movie.google;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.FileSourceLoader;
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
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        GoogleParser instance = new GoogleParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(78), site.getScore());
    }

}