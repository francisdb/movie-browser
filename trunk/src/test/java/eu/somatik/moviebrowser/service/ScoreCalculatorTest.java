/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import com.flicklib.domain.Movie;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class ScoreCalculatorTest {

    /**
     * Test of calculate method, of class ScoreCalculator.
     */
    @Test
    public void testCalculate() {
        Movie movie = new Movie();
        ScoreCalculator instance = new ScoreCalculator();
        assertNull("Result should be null when no data", instance.calculate(movie));
        
        movie.setImdbScore(32);
        assertEquals(Integer.valueOf(32), instance.calculate(movie));
        
        movie.setTomatoScore(32);
        movie.setMovieWebScore(32);
        assertEquals(Integer.valueOf(32), instance.calculate(movie));
        
        movie.setTomatoScore(80);
        assertEquals(Integer.valueOf((32*3+80)/4), instance.calculate(movie));
        
    }

}