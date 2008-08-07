package com.flicklib.api;

import com.flicklib.domain.MovieService;

/**
 *
 * @author francisdb
 */
public interface InfoFetcherFactory{
    
    MovieInfoFetcher get(MovieService service);
}
