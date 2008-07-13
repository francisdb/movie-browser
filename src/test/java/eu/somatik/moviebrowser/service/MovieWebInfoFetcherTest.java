/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class MovieWebInfoFetcherTest {

    public MovieWebInfoFetcherTest() {
    }


    /**
     * Test of fetch method, of class MovieWebInfoFetcher.
     */
    @Test
    public void testFetch() {
        MovieInfo movieInfo = new MovieInfo(new File("/tmp"));
        Movie movie = new Movie();
        movie.setTitle("Pulp Fiction");
        movieInfo.setMovie(movie);
        MovieWebInfoFetcher fetcher = new MovieWebInfoFetcher();
        fetcher.fetch(movieInfo.getMovie());
        assertNotNull("MovieWebStars is null", movie.getMovieWebStars());
        assertTrue("No data for MovieWebStart", movie.getMovieWebStars().length() > 0);
    }

}