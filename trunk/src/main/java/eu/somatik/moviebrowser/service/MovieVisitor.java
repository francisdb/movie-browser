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
package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

/**
 * Implements the visitor patern for a movie
 * 
 * @author francisdb
 */
public abstract class MovieVisitor {

    public void visitMovie(StorableMovie movie) {}

    public void visitFile(FileGroup fileGroup, StorableMovieFile file) {}
    
    public void visitGroup(FileGroup fileGroup) {}
    
    public void visitLocation(FileGroup fileGroup, MovieLocation location) {}
    
    public void startVisit(StorableMovie movie) {
        visitMovie(movie);
        for (FileGroup fg : movie.getGroups()) {
            visitGroup(fg);
            for (StorableMovieFile file : fg.getFiles()) {
                visitFile(fg, file);
            }
            for (MovieLocation location : fg.getLocations()) {
                visitLocation(fg, location);
            }
        }
    }
    
}
