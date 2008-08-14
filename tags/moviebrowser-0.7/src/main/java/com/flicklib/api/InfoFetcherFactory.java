package com.flicklib.api;

import com.flicklib.domain.MovieService;

/**
 *
 * @author francisdb
 */
public interface InfoFetcherFactory{
    
    /**
     * returns a MovieService implementation for the specified MovieService
     * @param service
     * @return the MovieService implementation
     */
    MovieInfoFetcher get(MovieService service);
}
