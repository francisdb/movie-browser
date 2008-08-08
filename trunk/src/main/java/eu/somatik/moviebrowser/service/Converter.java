package eu.somatik.moviebrowser.service;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * TODO this class could work using reflection!
 * @author francisdb
 */
public class Converter {

    public void convert(StorableMovie storableMovie, Movie movie) {
        movie.setPlot(storableMovie.getPlot());
        movie.setRuntime(storableMovie.getRuntime());
        movie.setTitle(storableMovie.getTitle());
        movie.setYear(storableMovie.getYear());
        movie.setDirector(storableMovie.getDirector());
        for (Language lang : storableMovie.getLanguages()) {
            movie.addLanguage(lang.getName());
        }
        for (Genre genre : storableMovie.getGenres()) {
            movie.addGenre(genre.getName());
        }
    }
    
    public void convert(Movie movie, StorableMovie storableMovie){
        storableMovie.setTitle(movie.getTitle());
        storableMovie.setPlot(movie.getPlot());
        storableMovie.setRuntime(movie.getRuntime());
        storableMovie.setYear(movie.getYear());
        storableMovie.setDirector(movie.getDirector());
        for (String lang : movie.getLanguages()) {
            storableMovie.addLanguage(new Language(lang));
        }
        for (String genre :movie.getGenres()) {
            storableMovie.addGenre(new Genre(genre));
        }
    }
    
    public void convert(StorableMovieSite storableMovieSite, MoviePage movieSite){
        movieSite.setService(storableMovieSite.getService());
        movieSite.setScore(storableMovieSite.getScore());
        movieSite.setVotes(storableMovieSite.getVotes());
        movieSite.setIdForSite(storableMovieSite.getIdForSite());
        movieSite.setUrl(storableMovieSite.getUrl());
        movieSite.setImgUrl(storableMovieSite.getImgUrl());
    }
    
    public void convert(MoviePage movieSite, StorableMovieSite storableMovieSite){
        storableMovieSite.setService(movieSite.getService());
        storableMovieSite.setIdForSite(movieSite.getIdForSite());
        storableMovieSite.setScore(movieSite.getScore());
        storableMovieSite.setVotes(movieSite.getVotes());
        storableMovieSite.setUrl(movieSite.getUrl());
        storableMovieSite.setImgUrl(movieSite.getImgUrl());
    }
}
