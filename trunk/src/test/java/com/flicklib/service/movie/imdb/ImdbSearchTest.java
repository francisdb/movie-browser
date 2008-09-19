package com.flicklib.service.movie.imdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.flicklib.domain.MoviePage;
import com.flicklib.service.HttpSourceLoader;

/**
 *
 * @author francisdb
 */
public class ImdbSearchTest {




    /**
     * Test of getResults method, of class ImdbSearch.
     * TODO fix nullpointer because of imdbparser dependency!
     * @throws Exception 
     */
    @Test
    //@Ignore
    public void testGetResults_String() throws Exception {
        ImdbSearch instance = new ImdbSearch(new HttpSourceLoader(null), new ImdbParser());
        List<MoviePage> result = instance.getResults("Pulp Fiction");
        assertTrue(result.size() > 0);
        assertEquals("Pulp Fiction", result.get(0).getMovie().getTitle());
        
        result = instance.getResults("Die Hard 4");
        assertTrue(result.size() > 0);
        assertEquals("Live Free or Die Hard", result.get(0).getMovie().getTitle());
        
        result = instance.getResults("Black Tie White Noise");
        assertTrue(result.size() > 0);
        assertEquals(Integer.valueOf(1993), result.get(0).getMovie().getYear());
        assertEquals("David Bowie: Black Tie White Noise", result.get(0).getMovie().getTitle());
    }

    /**
     * Test of generateImdbTitleSearchUrl method, of class ImdbSearch.
     */
    @Test
    public void testGenerateImdbTitleSearchUrl() {
        String title = "Pulp Fiction";
        ImdbSearch instance = new ImdbSearch(new HttpSourceLoader(null), new ImdbParser());
        String expResult = "http://www.imdb.com/find?q=Pulp+Fiction;s=tt;site=aka";
        String result = instance.generateImdbTitleSearchUrl(title);
        assertEquals(expResult, result);
        
    }


}