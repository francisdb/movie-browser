/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service.parser;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.FileSourceLoader;
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
        Source source = new FileSourceLoader().load("imdb/Pulp Fiction (1994).html");
        Movie movie = new Movie();
        ImdbParser instance = new ImdbParser(new MovieCacheMock());
        instance.parse(source, movie);
        assertEquals(Integer.valueOf(89), movie.getImdbScore());
        assertEquals("Pulp Fiction", movie.getTitle());
        assertEquals(Integer.valueOf(1994), movie.getYear());
        assertEquals("Pulp%20Fiction%20%281994%29_files/MV5BMjE0ODk2NjczOV5BMl5BanBnXkFtZTYwNDQ0NDg4.jpg", movie.getImgUrl());
        assertEquals("The lives of two mob hit men, a boxer, a gangster's wife, and a pair of\ndiner bandits intertwine in four tales of violence and redemption.", movie.getPlot());
        assertEquals(Integer.valueOf(154), movie.getRuntime());
        assertEquals("(298,638 votes)", movie.getVotes());
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
        public void printList() {
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