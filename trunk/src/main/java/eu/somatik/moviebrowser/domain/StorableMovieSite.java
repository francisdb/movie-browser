package eu.somatik.moviebrowser.domain;

import com.flicklib.domain.*;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author francisdb
 */
@Entity
@Table(name="Site")
@NamedQueries(
    @NamedQuery(name="StorableMovieSite.findByMovie", query="SELECT s FROM StorableMovieSite s WHERE s.movie = :movie")
)
public class StorableMovieSite {
    @Id
    @GeneratedValue
    private long id;
    
    @ManyToOne
    private StorableMovie movie;
    
    private MovieService service;
    
    @Column(unique=true)
    private String url;
    
    private Integer score;
    
    private Integer votes;
    
    private Date time;
    
    private String idForSite;
    
    private String imgUrl;

    public StorableMovieSite() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
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
