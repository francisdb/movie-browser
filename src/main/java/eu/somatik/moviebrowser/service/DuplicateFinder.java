/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Zsombor Gegesy
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

import java.util.ArrayList;
import java.util.List;

import eu.somatik.moviebrowser.database.MovieDatabase;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

/**
 * 
 * @author zsombor
 *
 */
public class DuplicateFinder {

    MovieDatabase database;

    public DuplicateFinder(MovieDatabase database) {
        this.database = database;
    }
    
    public List<MovieInfo> filter(List<MovieInfo> input) {
        List<MovieInfo> result = new ArrayList<MovieInfo>(input.size());
        for (MovieInfo info : input) {
            if (!check(info)) {
                result.add(info);
            }
        }
        return result;
    }

    public List<StorableMovie> filterStorableMovies(List<StorableMovie> input) {
        List<StorableMovie> result = new ArrayList<StorableMovie>(input.size());
        for (StorableMovie info : input) {
            if (!check(info)) {
                result.add(info);
            }
        }
        return result;
    }

    protected boolean check(MovieInfo info) {
        return check(info.getMovie());
    }

    /**
     * Check that the movie already in the database
     * @param movie
     * @return true, if in the database.
     */
    protected boolean check(StorableMovie movie) {
        for (FileGroup newlyFoundFileGroup : movie.getGroups()) {
            for (StorableMovieFile newlyFoundFile : newlyFoundFileGroup.getFiles()) {
                if (newlyFoundFile.getType()==FileType.VIDEO_CONTENT) {
                    FileGroup storedFileGroup = database.findByFile(newlyFoundFile.getName(), newlyFoundFile.getSize());
                    if (storedFileGroup!=null) {
                        if (!storedFileGroup.equals(newlyFoundFileGroup)) {
                            if (deepCompare(newlyFoundFileGroup, storedFileGroup)) {
                                MovieLocation newLocation = newlyFoundFileGroup.getDirectory();
                                MovieLocation movieLocationIfExists = storedFileGroup.getMovieLocationIfExists(newLocation.getPath());
                                if (movieLocationIfExists==null) {
                                    // we found at a new location, we should add that location to the file
                                    storedFileGroup.addLocation(new MovieLocation(newLocation));
                                    database.insertOrUpdate(storedFileGroup.getMovie());
                                }
                                return true;
                            }
                        }
                    }
                    
                }
            }
        }
        return false;
    }

    private boolean deepCompare(FileGroup fileGroup, FileGroup group) {
        boolean result = false;
        for (StorableMovieFile file : fileGroup.getFiles()) {
            // only check for video files
            if (file.getType()==FileType.VIDEO_CONTENT) {
                // the other group has 
                if (!group.hasFiles(file.getName(), file.getSize())) {
                    return false;
                } else {
                    result = true;
                }
            }
        }
        return result;
    }
    
}
