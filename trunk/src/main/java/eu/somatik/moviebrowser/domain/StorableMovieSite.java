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
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author francisdb
 */
@Entity
@Table(name = "Site",uniqueConstraints={@UniqueConstraint(columnNames={"movie_id","service"})})

@NamedQueries(@NamedQuery(name = "StorableMovieSite.findByMovie", query = "SELECT s FROM StorableMovieSite s WHERE s.movie = :movie"))
public class StorableMovieSite {
	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private StorableMovie movie;

	private MovieService service;

	@Column(unique = true)
	private String url;

	private Integer score;

	private Integer votes;

	private Date time;

	private String idForSite;

	private String imgUrl;

	/**
	 * Constructs a new StorableMovieSite
	 */
	public StorableMovieSite() {
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the StorableMovie
	 */
	public StorableMovie getMovie() {
		return this.movie;
	}

	/**
	 * @param movie
	 */
	public void setMovie(StorableMovie movie) {
		this.movie = movie;
	}

	/**
	 * @return the score
	 */
	public Integer getScore() {
		return this.score;
	}

	/**
	 * @param score
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	/**
	 * @return the MovieService
	 */
	public MovieService getService() {
		return this.service;
	}

	/**
	 * @param service
	 */
	public void setService(MovieService service) {
		this.service = service;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return (Date) this.time.clone();
	}

	/**
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = (Date) time.clone();
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the votes
	 */
	public Integer getVotes() {
		return this.votes;
	}

	/**
	 * @param votes
	 *            the votes to set
	 */
	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	/**
	 * @return the idForSite
	 */
	public String getIdForSite() {
		return idForSite;
	}

	/**
	 * @param idForSite
	 *            the idForSite to set
	 */
	public void setIdForSite(String idForSite) {
		this.idForSite = idForSite;
	}

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl
	 *            the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
