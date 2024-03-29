/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.somatik.moviebrowser.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        movie.getDirectors().addAll(storableMovie.getDirectors());
        movie.setType(storableMovie.getType());
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
        storableMovie.setDirectors(new ArrayList<String>(movie.getDirectors()));
        storableMovie.setActors(new ArrayList<String>(movie.getCast()));
        storableMovie.setType(movie.getType());
        for (String lang : movie.getLanguages()) {
            storableMovie.addLanguage(new Language(lang));
        }
        for (String genre :movie.getGenres()) {
            storableMovie.addGenre(Genre.get(genre));
        }
    }

    public void convert(MoviePage movie, StorableMovie storableMovie){
        storableMovie.setTitle(movie.getTitle());
        storableMovie.setPlot(movie.getPlot());
        storableMovie.setRuntime(movie.getRuntime());
        storableMovie.setYear(movie.getYear());
        storableMovie.setDirectors(new ArrayList<String>(movie.getDirectors()));
        storableMovie.setActors(new ArrayList<String>(movie.getActors()));
        storableMovie.setType(movie.getType());
        storableMovie.getLanguages().clear();
        for (String lang : movie.getLanguages()) {
            storableMovie.addLanguage(new Language(lang));
        }
        storableMovie.getGenres().clear();
        for (String genre :movie.getGenres()) {
            storableMovie.addGenre(Genre.get(genre));
        }
    }

    public void convert(MoviePage movieSite, StorableMovieSite storableMovieSite){
        storableMovieSite.setService(movieSite.getService());
        storableMovieSite.setIdForSite(movieSite.getIdForSite());
        storableMovieSite.setScore(movieSite.getScore());
        storableMovieSite.setVotes(movieSite.getVotes());
        storableMovieSite.setUrl(movieSite.getUrl());
        storableMovieSite.setImgUrl(movieSite.getImgUrl());
        storableMovieSite.setTitle(movieSite.getTitle());
        storableMovieSite.setAlternateTitle(movieSite.getAlternateTitle());
        storableMovieSite.setOriginalTitle(movieSite.getOriginalTitle());
        storableMovieSite.setPlot(movieSite.getPlot());
        Set<Genre> genres = storableMovieSite.getGenres();
        if(genres == null){
            genres = new HashSet<Genre>();
        }else{
            genres.clear();
        }
        for (String genre :movieSite.getGenres()) {
            genres.add(Genre.get(genre));
        }
        storableMovieSite.setActors(new ArrayList<String>(movieSite.getActors()));
        storableMovieSite.setDirectors(new ArrayList<String>(movieSite.getDirectors()));
    }
}
