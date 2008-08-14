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
                weight = 1;
                // double weight
                if (movieService == movieService.IMDB) {
                    weight = 2;
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
