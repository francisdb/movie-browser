package eu.somatik.moviebrowser.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author francisdb
 */
@Entity
@Table(name="File")
public class StorableMovieFile {
    
    @Id
    private String path;
    
    @ManyToOne
    private StorableMovie movie;

    public StorableMovieFile() {
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
   

}
