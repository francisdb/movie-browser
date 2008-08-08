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
    public Integer calculate(MovieInfo movie){
        int score = 0;
        int count = 0;
        
        Integer imdb = score(movie, MovieService.IMDB);
        if(imdb != null){
            // double weight
            score += imdb*2;
            count += 2;
        }
        Integer tomato = score(movie, MovieService.TOMATOES);
        if(tomato != null){
            score += tomato;
            count++;
        }
        Integer movieweb = score(movie, MovieService.MOVIEWEB);
        if(movieweb != null){
            score += movieweb;
            count++;
        }
        Integer google = score(movie, MovieService.GOOGLE);
        if(google != null){
            score += google;
            count++;
        }
        Integer flixter = score(movie, MovieService.FLIXTER);
        if(flixter != null){
            score += flixter;
            count++;
        }
        
        Integer value = null;
        if(count > 0){
            score = score / count;
            value = Integer.valueOf(score);
        }
        return value;
    }
    
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
    public String imgUrl(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(service).getImgUrl();
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
