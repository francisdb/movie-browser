package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.Movie;

/**
 * Calculates the average score for all services
 * @author francisdb
 */
public class ScoreCalculator {

    public Integer calculate(Movie movie){
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
        if(movie.getGoogleScore() != null){
            score += movie.getGoogleScore();
            count++;
        }
        if(movie.getFlixterScore() != null){
            score += movie.getFlixterScore();
            count++;
        }
        
        Integer value = null;
        if(count > 0){
            score = score / count;
            value = Integer.valueOf(score);
        }
        return value;
    }

}
