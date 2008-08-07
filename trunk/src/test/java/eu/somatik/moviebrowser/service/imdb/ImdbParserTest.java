package eu.somatik.moviebrowser.service.imdb;

import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieSite;
import com.flicklib.service.FileSourceLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class ImdbParserTest {

    public ImdbParserTest() {
    }


    /**
     * Test of parse method, of class ImdbParser.
     * @throws Exception 
     */
    @Test
    public void testParse() throws Exception{
        String source = new FileSourceLoader().load("imdb/Pulp Fiction (1994).html");
        MovieSite site = new MovieSite();
        site.setMovie(new Movie());
        ImdbParser instance = new ImdbParser(new MovieCacheMock());
        instance.parse(source, site);
        assertEquals(Integer.valueOf(89), site.getMovie().getImdbScore());
        assertEquals("Pulp Fiction", site.getMovie().getTitle());
        assertEquals(Integer.valueOf(1994), site.getMovie().getYear());
        assertEquals("Pulp%20Fiction%20%281994%29_files/MV5BMjE0ODk2NjczOV5BMl5BanBnXkFtZTYwNDQ0NDg4.jpg", site.getMovie().getImgUrl());
        assertEquals("The lives of two mob hit men, a boxer, a gangster's wife, and a pair of\ndiner bandits intertwine in four tales of violence and redemption.", site.getMovie().getPlot());
        assertEquals(Integer.valueOf(154), site.getMovie().getRuntime());
        assertEquals("(298,638 votes)", site.getMovie().getVotes());
        // TODO test other fields
    }

    private static class MovieCacheMock implements MovieCache {

        public MovieCacheMock() {
        }

        @Override
        public Movie find(String path) {
            throw new UnsupportedOperationException("Not implemented in mock");
        }

        @Override
        public Genre getOrCreateGenre(String name) {
            return new Genre(name);
        }

        @Override
        public Language getOrCreateLanguage(String name) {
            return new Language(name);
        }

        @Override
        public boolean isStarted() {
            throw new UnsupportedOperationException("Not implemented in mock");
        }

        @Override
        public void saveMovie(Movie movie) {
            throw new UnsupportedOperationException("Not implemented in mock");
        }

        @Override
        public void removeMovie(Movie movie) {
            throw new UnsupportedOperationException("Not implemented in mock");
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("Not implemented in mock");
        }

        @Override
        public void startup() {
            throw new UnsupportedOperationException("Not implemented in mock");
        }
    }

}