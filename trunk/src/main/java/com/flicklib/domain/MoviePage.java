/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flicklib.domain;

/**
 *
 * @author francisdb
 */
public class MoviePage {
    
    private Movie movie;
    
    private String idForSite;
    
    private MovieService service;
    
    private String url;
    
    /**
     * Score from 0 - 100
     */
    private Integer score;
    
    private Integer votes;
    
    private String imgUrl;

    public MoviePage() {
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public MovieService getService() {
        return service;
    }

    public void setService(MovieService service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getIdForSite() {
        return idForSite;
    }

    public void setIdForSite(String idForSite) {
        this.idForSite = idForSite;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    


}
