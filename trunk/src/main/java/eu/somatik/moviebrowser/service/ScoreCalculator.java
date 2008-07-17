/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates the average score for all services
 * @author francisdb
 */
public class ScoreCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCalculator.class);
    
    public Integer calculate(Movie movie){
        LOGGER.info("Starting calculation");
        int score = 0;
        int count = 0;
        
        if(movie.getImdbScore() != null){
            // double weight
            score += movie.getImdbScore()*2;
            count += 2;
        }
        if(movie.getTomatoScore() != null){
            score += movie.getTomatoScore();
            count++;
        }
        if(movie.getMovieWebScore() != null){
            score += movie.getMovieWebScore();
            count++;
        }
        
        Integer value = null;
        if(count > 0){
            score = score / count;
            value = Integer.valueOf(score);
        }
        LOGGER.info("Calculation done");
        return value;
    }

}
