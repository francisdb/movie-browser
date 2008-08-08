package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 *
 * @author francisdb
 */
public interface ScoreCalculator {
    
        /**
     * Calculates the global score
     * @param movie
     * @return
     */
    Integer calculate(MovieInfo movie);

}
