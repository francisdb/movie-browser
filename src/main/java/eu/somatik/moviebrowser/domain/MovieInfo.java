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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import com.flicklib.domain.MovieService;

/**
 * 
 * @author francisdb
 */
public class MovieInfo {

    private StorableMovie movie;

    private File directory;
    private MovieStatus status;

    private boolean needRefetch = false;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Creates a new instance of MovieInfo
     * 
     * @param directory
     */
    public MovieInfo(File directory) {
        this.directory = directory;
        this.status = MovieStatus.NEW;
        this.movie = new StorableMovie();
    }

    public MovieInfo(StorableMovie movieFile) {
        this.status = MovieStatus.LOADED;
        this.movie = movieFile;
        String dirPath = this.movie.getDirectoryPath();
        if (dirPath != null) {
            this.directory = new File(dirPath);
        }
    }

    /**
     * This should trigger an update in the table
     */
    public void triggerUpdate() {
        propertyChangeSupport.firePropertyChange("triggerUpdate", System.nanoTime(), System.nanoTime());
    }

    /**
     * 
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @return the Directory
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * 
     * @param directory
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return movie.getTitle();
    }

    /**
     * 
     * @return the MovieStatus
     */
    public MovieStatus getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     */
    public void setStatus(MovieStatus status) {
        // MovieStatus oldValue = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("status", null, this.status);
    }

    public StorableMovie getMovie() {
        return movie;
    }

    public void setMovie(StorableMovie movie) {
        this.movie = movie;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    public void addSite(StorableMovieSite storableMovieSite) {
        this.movie.addSiteInfo(storableMovieSite);
    }

    public StorableMovieSite siteFor(MovieService service) {
        return movie.getMovieSiteInfo(service);
    }

    public String siteForUrl(MovieService service) {
        StorableMovieSite sms = siteFor(service);
        if (sms != null) {
            return sms.getUrl();
        }
        return null;
    }
    
    public void setNeedRefetch(boolean needRefetch) {
        this.needRefetch = needRefetch;
    }
    
    public boolean isNeedRefetch() {
        return needRefetch;
    }

}
