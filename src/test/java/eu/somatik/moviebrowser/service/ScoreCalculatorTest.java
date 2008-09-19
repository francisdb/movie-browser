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

import java.io.File;

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
        MovieInfo movie = new MovieInfo((File) null);
        InfoHandler handler = new InfoHandlerImpl();
        ScoreCalculator calc = new WeightedScoreCalculator(handler);
        assertNull("Result should be null when no data", calc.calculate(movie));
        
        StorableMovieSite site = new StorableMovieSite();
        site.setService(MovieService.IMDB);
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), calc.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.TOMATOES);
        site.setScore(32);
        movie.addSite(site);
        site = new StorableMovieSite();
        site.setService(MovieService.MOVIEWEB);
        site.setScore(32);
        movie.addSite(site);
        assertEquals(Integer.valueOf(32), calc.calculate(movie));
        
        site = new StorableMovieSite();
        site.setService(MovieService.OMDB);
        site.setScore(80);
        movie.addSite(site);
        assertEquals(Integer.valueOf((32*4+80)/5), calc.calculate(movie));
        
    }

}