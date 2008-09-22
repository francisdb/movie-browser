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

import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 * Calculates scores by their weight
 * TODO get map of weights from settings
 * @author francisdb
 */
@Singleton
public class WeightedScoreCalculator implements ScoreCalculator {

    private final InfoHandler handler;

    /**
     * Constructs a new WeightedScoreCalculator
     * @param handler
     */
    @Inject
    public WeightedScoreCalculator(InfoHandler handler) {
        this.handler = handler;
    }

    @Override
    public Integer calculate(MovieInfo movie) {
        int score = 0;
        int count = 0;

        int weight;
        Integer serviceScore;
        for (MovieService movieService : MovieService.values()) {
            serviceScore = handler.score(movie, movieService);

            if (serviceScore != null) {
                weight = 2;
                // double weight
                if (movieService == movieService.IMDB) {
                    weight = 4;
                }
                // half weight
                if (movieService == movieService.MOVIEWEB) {
                    weight = 1;
                }
                score += serviceScore * weight;
                count += weight;
            }
        }
        
        Integer value = null;
        if (count > 0) {
            score = score / count;
            value = Integer.valueOf(score);
        }
        return value;
    }
}
