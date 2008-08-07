package com.flicklib.domain;

import com.flicklib.domain.MovieService;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author francisdb
 */
@Entity
public class MovieSite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    private Movie movie;
    
    private MovieService service;
    
    @Column(unique=true)
    private String url;
    
    private Integer score;
    
    private Integer votes;
    
    private Date time;

    public MovieSite() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getTime() {
        return (Date) time.clone();
    }

    public void setTime(Date time) {
        this.time = (Date) time.clone();
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
    
    

}
