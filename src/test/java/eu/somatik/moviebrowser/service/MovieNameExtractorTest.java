package eu.somatik.moviebrowser.service;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieNameExtractorTest {

    @Test
    public void test(){
        MovieNameExtractor extractor = new MovieNameExtractor();
        File file = new File("movie.name.1905.DVDRiP.XViD-JUnit");
        String name = extractor.removeCrap(file);
        assertEquals("movie name", name);
    }

}