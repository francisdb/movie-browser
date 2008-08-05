package eu.somatik.moviebrowser.service.imdb;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class ImdbSearchTest {




    /**
     * Test of getResults method, of class ImdbSearch.
     * @throws Exception 
     */
    @Test
    @Ignore
    public void testGetResults_String() throws Exception {
        ImdbSearch instance = new ImdbSearch(new HttpSourceLoader(), new ImdbParser(null));
        List<Movie> result = instance.getResults("Pulp Fiction");
        assertTrue(result.size() > 0);
        assertEquals("Pulp Fiction", result.get(0).getTitle());
        
        result = instance.getResults("The Dark Knight");
        assertTrue(result.size() > 0);
        assertEquals("The Dark Knight", result.get(0).getTitle());
    }

    /**
     * Test of generateImdbTitleSearchUrl method, of class ImdbSearch.
     */
    @Test
    public void testGenerateImdbTitleSearchUrl() {
        String title = "Pulp Fiction";
        ImdbSearch instance = new ImdbSearch(new HttpSourceLoader(), new ImdbParser(null));
        String expResult = "http://www.imdb.com/Tsearch?title=Pulp+Fiction";
        String result = instance.generateImdbTitleSearchUrl(title);
        assertEquals(expResult, result);
        
    }


}