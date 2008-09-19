/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.somatik.moviebrowser.domain;

import com.flicklib.domain.MovieService;
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

	@Override
	public String toString() {
	    return "StorableMovieSite["+id+","+service+",id:"+idForSite+",score:"+score+",votes:"+votes+']';
	}
}
