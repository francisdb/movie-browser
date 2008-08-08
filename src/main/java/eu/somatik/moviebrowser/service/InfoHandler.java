package eu.somatik.moviebrowser.service;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 *
 * @author francisdb
 */
public interface InfoHandler {

    /**
     * Calculates the global score
     * @param movie
     * @return
     */
    Integer calculate(MovieInfo movie);

    /**
     * Gets the score for a service
     * @param info
     * @param movieService
     * @return
     */
    Integer score(MovieInfo info, MovieService movieService);
    
    /**
     * Gets the url for a service
     * @param info
     * @param service
     * @return
     */
    String url(MovieInfo info, MovieService service);
    
    /**
     * Gets the imgUrl for a service
     * @param info
     * @param service
     * @return
     */
    String imgUrl(MovieInfo info, MovieService service);
    
    /**
     * Gets the votes for a service
     * @param info
     * @param service
     * @return
     */
    Integer votes(MovieInfo info, MovieService service);
    
    /**
     * Gets the id for a service
     * @param info
     * @param service
     * @return
     */
    String id(MovieInfo info, MovieService service);

}
