/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service.parser;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.FileSourceLoader;
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
        Source source = new FileSourceLoader().load("tomatoes/Pulp Fiction Movie Reviews, Pictures - Rotten Tomatoes.html");
        Movie movie = new Movie();
        TomatoesParser instance = new TomatoesParser();
        instance.parse(source, movie);
        assertEquals(Integer.valueOf(96), movie.getTomatoScore());
    }

}