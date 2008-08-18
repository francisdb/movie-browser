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

import eu.somatik.moviebrowser.api.FolderScanner;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Scans a folder for movies
 * @author francisdb
 */
@Singleton
public class SimpleFolderScanner implements FolderScanner {

    /**
     * Scans the folders
     * @param folders
     * @return a List of MovieInfo
     */
    @Override
    public List<MovieInfo> scan(final Set<String> folders) {
        File folder;
        List<MovieInfo> movies = new ArrayList<MovieInfo>();
        for (String path : folders) {
            folder = new File(path);
            if (folder.exists()) {
                for (File file : folder.listFiles(new MovieFileFilter(true))) {
                    //if (file.isDirectory()) {
                    movies.add(new MovieInfo(file));
                    //}
                }
            }
        }
        return movies;
    }

}
