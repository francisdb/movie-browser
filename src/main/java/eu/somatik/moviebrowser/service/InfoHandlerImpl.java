package eu.somatik.moviebrowser.service;

import com.flicklib.domain.MovieService;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * Calculates the average score for all services
 * @author francisdb
 */
@Singleton
public class InfoHandlerImpl implements InfoHandler {
    
    @Override
    public Integer score(MovieInfo info, MovieService movieService) {
        Integer val;
        StorableMovieSite site = info.siteFor(movieService);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(movieService).getScore();
        }
        return val;
    }

    @Override
    public String url(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(service).getUrl();
        }
        return val;
    }
    
    @Override
    public Integer votes(MovieInfo info, MovieService service) {
        Integer val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(service).getVotes();
        }
        return val;
    }

    @Override
    public String id(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(service).getIdForSite();
        }
        return val;
    }
    
    

}
