package eu.somatik.moviebrowser.api;

import eu.somatik.moviebrowser.domain.MovieService;

/**
 *
 * @author francisdb
 */
public interface InfoFetcherFactory{
    
    MovieInfoFetcher get(MovieService service);
}
