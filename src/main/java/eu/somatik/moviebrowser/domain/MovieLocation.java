package eu.somatik.moviebrowser.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Location")
public class MovieLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String path;

    String label;

    @ManyToOne
    StorableMovie movie;

    public MovieLocation() {

    }

    public MovieLocation(String path, String label) {
        this.path = path;
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MovieLocation[id:"+id+",label:"+label+",path:"+path+"]";
    }

}
