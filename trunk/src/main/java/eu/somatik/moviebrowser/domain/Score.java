package eu.somatik.moviebrowser.domain;

import eu.somatik.moviebrowser.domain.MovieService;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author francisdb
 */
@Entity
public class Score {
    
    @Id
    private long id;
    
    @ManyToOne
    private Movie movie;
    private Integer score;
    private MovieService service;
    private Date time;

    public Score() {
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
    
    

}
