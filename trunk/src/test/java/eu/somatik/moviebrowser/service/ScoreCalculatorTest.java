package eu.somatik.moviebrowser.service;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class ScoreCalculatorTest {

    /**
     * Test of calculate method, of class InfoHandlerImpl.
     */
    @Test
    public void testCalculate() {
        MovieInfo movie = new MovieInfo(null);
        InfoHandlerImpl instance = new InfoHandlerImpl();
        assertNull("Result should be null when no data", instance.calculate(movie));
        
        StorableMovieSite site = new StorableMovieSite();
        site.setService(MovieService.IMDB);
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), instance.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.TOMATOES);
        site.setScore(32);
        movie.addSite(site);
        site = new StorableMovieSite();
        site.setService(MovieService.MOVIEWEB);
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), instance.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.TOMATOES);
        site.setScore(80);
        movie.addSite(site);
        assertEquals(Integer.valueOf((32*3+80)/4), instance.calculate(movie));
        
    }

}