package com.flicklib.service.movie.tomatoes;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
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
        MoviePage site = new MoviePage();
        site.setMovie(new Movie());
        TomatoesParser instance = new TomatoesParser();
        instance.parse(source, site);
        assertEquals(Integer.valueOf(96), site.getScore());
    }

}