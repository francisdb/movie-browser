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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flicklib.domain.MovieService;
import com.flicklib.service.movie.flixter.FlixsterInfoFetcher;
import com.flicklib.service.movie.imdb.ImdbInfoFetcher;
import com.flicklib.service.movie.movieweb.MovieWebInfoFetcher;
import com.flicklib.service.movie.tomatoes.TomatoesInfoFetcher;

import eu.somatik.moviebrowser.Services;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 *
 * @author francisdb
 */
public class WeightedScoreCalculatorTest {

    @BeforeClass
    public static void setup() {
        new ImdbInfoFetcher(null);
        new TomatoesInfoFetcher(null);
        new MovieWebInfoFetcher(null);
        new FlixsterInfoFetcher(null);
    }
    /**
     * Test of calculate method, of class WeightedScoreCalculator.
     */
    @Test
    public void testCalculate() {
        MovieInfo movie = new MovieInfo();
        InfoHandler handler = new InfoHandlerImpl();
        ScoreCalculator calc = new WeightedScoreCalculator(handler);
        assertNull("Result should be null when no data", calc.calculate(movie));
        
        StorableMovieSite site = new StorableMovieSite();
        site.setService(MovieService.getById(Services.IMDB));
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), calc.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.getById(Services.TOMATOES));
        site.setScore(32);
        movie.addSite(site);
        site = new StorableMovieSite();
        site.setService(MovieService.getById(Services.MOVIEWEB));
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), calc.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.getById(Services.FLIXSTER));
        site.setScore(80);
        movie.addSite(site);
        assertEquals(Integer.valueOf((32*7+80*2)/9), calc.calculate(movie));
        
    }

}