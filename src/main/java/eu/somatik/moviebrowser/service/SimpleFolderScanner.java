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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

/**
 * Scans a folder for movies
 * 
 * @author francisdb
 */
@Singleton
public class SimpleFolderScanner implements FolderScanner {

    final static Logger LOGGER = LoggerFactory.getLogger(SimpleFolderScanner.class);

    List<MovieInfo> movies;
    String currentLabel;

    /**
     * Scans the folders
     * 
     * @param folders
     * @return a List of MovieInfo
     */
    @Override
    public synchronized List<MovieInfo> scan(final Set<String> folders) {
        File folder;
        movies = new ArrayList<MovieInfo>();
        for (String path : folders) {
            folder = new File(path);
            if (folder.exists()) {
                currentLabel = folder.getAbsolutePath();
                LOGGER.info("scanning "+folder.getAbsolutePath());
                browse(folder);
            }
        }
        return movies;
    }

    private void browse(File folder) {
        LOGGER.info("entering "+folder.getAbsolutePath());
        File[] files = folder.listFiles();

        Set<String> plainFileNames = new HashSet<String>();
        boolean hasSubDirectory = false;
        for (File f : files) {
            if (f.isDirectory()) {
                hasSubDirectory = true;
                browse(f);
            } else {
                String ext = getExtension(f);
                if (ext != null && MovieFileFilter.VIDEO_EXTENSIONS.contains(ext)) {
                    plainFileNames.add(getNameWithoutExt(f));
                }
            }
        }
        // We want to handle the following cases:
        // 1,
        // Title_of_the_film/abc.avi
        // Title_of_the_film/abc.srt
        // --> no subdirectory, one film -> the title should be name of the
        // directory
        //  
        // 2,
        // Title_of_the_film/abc-cd1.avi
        // Title_of_the_film/abc-cd1.srt
        // Title_of_the_film/abc-cd2.srt
        // Title_of_the_film/abc-cd2.srt
        //
        if (hasSubDirectory) {
            genericMovieFindProcess(files);
        } else {
            int foundFiles = plainFileNames.size();
            switch (foundFiles) {
                case 0:
                    break;
                case 1: {
                    StorableMovie sm = new StorableMovie();
                    sm.setTitle(MovieNameExtractor.removeCrap(folder));
                    sm.addLocation(new MovieLocation(folder.getParent(), currentLabel));
                    addFiles(sm, files, plainFileNames.iterator().next());
                    add(sm);
                    break;
                }
                case 2: {
                    Iterator<String> it = plainFileNames.iterator();
                    String name1 = it.next();
                    String name2 = it.next();
                    if (LevenshteinDistance.distance(name1, name2) < 3) {
                        // the difference is -cd1 / -cd2
                        StorableMovie sm = new StorableMovie();
                        sm.setTitle(MovieNameExtractor.removeCrap(folder));
                        sm.addLocation(new MovieLocation(folder.getParent(), currentLabel));
                        addFiles(sm, files, name1);
                        add(sm);
                        break;
                    }
                    // the difference is significant, we use the generic
                    // solution
                }
                default: {
                    genericMovieFindProcess(files);
                }
            }
        }
    }

    private void genericMovieFindProcess(File[] files) {
        Map<String, StorableMovie> foundMovies = new HashMap<String, StorableMovie>();
        for (File f : files) {
            if (!f.isDirectory()) {
                String extension = getExtension(f);
                if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(extension)) {
                    String baseName = MovieNameExtractor.removeCrap(f);
                    StorableMovie m = foundMovies.get(baseName);
                    if (m == null) {
                        m = new StorableMovie();
                        m.setTitle(baseName);
                        m.addLocation(new MovieLocation(f.getParent(), currentLabel));
                        foundMovies.put(baseName, m);
                    }
                    m.addFile(new StorableMovieFile(f, FileType.getTypeByExtension(extension)));
                }
            }
        }
        for (StorableMovie m : foundMovies.values()) {
            if (m.isValid()) {
                add(m);
            }
        }
    }

    /**
     * add the files, which has similar names, to the movie object
     * 
     * @param sm
     * @param files
     * @param next
     */
    private void addFiles(StorableMovie sm, File[] files, String plainFileName) {
        for (File f : files) {
            if (!f.isDirectory()) {
                String baseName = getNameWithoutExt(f);
                String ext = getExtension(f);
                if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(ext)) {
                    if (LevenshteinDistance.distance(plainFileName, baseName) < 3) {
                        sm.addFile(new StorableMovieFile(f, FileType.getTypeByExtension(ext)));
                    }
                }
            }
        }
    }

    private void add(StorableMovie movie) {
        LOGGER.info("film:"+movie.getTitle()+" found at: "+movie.getDirectoryPath()+" {"+movie.getFiles()+'}');
        movies.add(new MovieInfo(movie));
    }

    private String getExtension(File file) {
        String name = file.getName();
        int lastDotPos = name.lastIndexOf('.');
        if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < name.length() - 1) {
            return name.substring(lastDotPos + 1).toLowerCase();
        }
        return null;
    }

    private String getNameWithoutExt(File file) {
        String name = file.getName();
        int lastDotPos = name.lastIndexOf('.');
        if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < name.length() - 1) {
            return name.substring(0, lastDotPos);
        }
        return name;
    }

}
