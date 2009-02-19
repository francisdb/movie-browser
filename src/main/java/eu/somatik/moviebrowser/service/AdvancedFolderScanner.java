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

import com.flicklib.tools.LevenshteinDistance;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
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
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import java.util.Arrays;

/**
 * Scans a folder for movies
 * 
 * @author francisdb
 */
@Singleton
public class AdvancedFolderScanner implements FolderScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedFolderScanner.class);

    /**
     * If a folder contains no other than these it is a movie folder
     * TODO make regex?
     */
    private static final String[] MOVIE_SUB_DIRS =
            new String[]{"subs", "subtitles", "cd1", "cd2", "cd3", "cd4", "sample", "covers", "cover", "approved", "info" };

    private final MovieNameExtractor movieNameExtractor;
   
    @Inject
    public AdvancedFolderScanner(final MovieNameExtractor movieNameExtractor) {
        this.movieNameExtractor = movieNameExtractor;
    }



    private List<MovieInfo> movies;
    private String currentLabel;

    /**
     * Scans the folders
     * 
     * @param folders
     * @return a List of MovieInfo
     *
     * TODO get rid of the synchronized and create a factory or pass all state data
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
                try {
					browse(folder.getCanonicalFile());
				} catch (IOException e) {
					LOGGER.error("error during acquiring canonical path for "+folder.getAbsolutePath() +", "+e.getMessage(), e);
				}
            }
        }
        return movies;
    }

    /**
     * 
     * @param folder
     * @return true, if it contained movie file
     */
    private boolean browse(File folder) {
        LOGGER.trace("entering "+folder.getAbsolutePath());
        File[] files = folder.listFiles();

        Set<String> plainFileNames = new HashSet<String>();
        int subDirectories = 0;
        int compressedFiles = 0;
        Set<String> directoryNames = new HashSet<String>();
        for (File f : files) {
            if (f.isDirectory()) {
                subDirectories ++;
                directoryNames.add(f.getName().toLowerCase());
            } else {
                String ext = getExtension(f);
                if(ext == null){
                    LOGGER.trace("Ignoring file without extension: "+f.getAbsolutePath());
                }else{
                    if (FileType.getTypeByExtension(ext)==FileType.COMPRESSED) {
                        compressedFiles ++;
                    }
                    if (ext != null && MovieFileFilter.VIDEO_EXTENSIONS.contains(ext)) {
                        plainFileNames.add(getNameWithoutExt(f));
                    }
                }
            }
        }
        // check for multiple compressed files, the case of:
        // Title_of_the_film/abc.rar
        // Title_of_the_film/abc.r01
        // Title_of_the_film/abc.r02
        if (compressedFiles > 0) {
            StorableMovie sm = new StorableMovie();
            FileGroup fg = initStorableMovie(folder, sm);
            fg.addLocation(new MovieLocation(folder.getPath(), currentLabel, true));
            addCompressedFiles(sm, fg, files );
            add(sm);
            return true;
        }
        if (subDirectories >= 2 && subDirectories <= 5) {
            // the case of :
            // Title_of_the_film/cd1/...
            // Title_of_the_film/cd2/...
            // with an optional sample/subs directory
            // Title_of_the_film/sample/
            // Title_of_the_film/subs/
            // Title_of_the_film/subtitles/
            // or
            // Title_of_the_film/bla1.avi
            // Title_of_the_film/bla2.avi
            // Title_of_the_film/sample/
            // Title_of_the_film/subs/
            if (isMovieFolder(directoryNames)) {
                StorableMovie sm = new StorableMovie();
                FileGroup fg = initStorableMovie(folder, sm);
                fg.addLocation(new MovieLocation(folder.getPath(), currentLabel, true));
                for(String cdFolder:getCdFolders(directoryNames)){
                    addCompressedFiles(sm, fg, files, cdFolder);
                }
                for(File file: folder.listFiles()){
                    if(!file.isDirectory()){
                        String ext = getExtension(file);
                        if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(ext)) {
                            fg.addFile(new StorableMovieFile(file, FileType.getTypeByExtension(ext), fg));
                        }
                    }
                }
                add(sm);
                return true;
            }
        }
        boolean subFolderContainMovie = false;
        for (File f : files) {
            if (f.isDirectory() && !f.getName().equalsIgnoreCase("sample")) {
                subFolderContainMovie |= browse(f);
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

        if (subDirectories>0 && subFolderContainMovie) {
            return genericMovieFindProcess(files) || subFolderContainMovie;
        } else {
            
            int foundFiles = plainFileNames.size();
            switch (foundFiles) {
                case 0:
                    return subFolderContainMovie;
                case 1: {
                    StorableMovie sm = new StorableMovie();
                    FileGroup fg = initStorableMovie(folder, sm);
                    fg.addLocation(new MovieLocation(folder.getPath(), currentLabel, true));
                    addFiles(sm, fg, files,  plainFileNames.iterator().next());
                    add(sm);
                    return true;
                }
                case 2: {
                    Iterator<String> it = plainFileNames.iterator();
                    String name1 = it.next();
                    String name2 = it.next();
                    if (LevenshteinDistance.distance(name1, name2) < 3) {
                        // the difference is -cd1 / -cd2
                        StorableMovie sm = new StorableMovie();
                        FileGroup fg = initStorableMovie(folder, sm);

                        fg.addLocation(new MovieLocation(folder.getPath(), currentLabel, true));
                        addFiles(sm, fg, files, name1);
                        add(sm);
                        return true;
                    }
                    // the difference is significant, we use the generic
                    // solution
                }
                default: {
                    return genericMovieFindProcess(files);
                }
            }
        }
    }


    /**
     * check that every sub folder name is some common movie related folder name, eg 'cd1', 'cd2', 'sample', etc...
     * 
     * @param subDirectoryNames
     * @return
     */
    private boolean isMovieFolder(Set<String> subDirectoryNames){
        boolean movieFolder = true;
        List<String> valid = Arrays.asList(MOVIE_SUB_DIRS);
        Iterator<String> iter = subDirectoryNames.iterator();
        String next;
        while(iter.hasNext() && movieFolder){
            next = iter.next();
            movieFolder = valid.contains(next);
            if(!movieFolder){
                LOGGER.trace("not movie folder because: " + next);
            }
        }
        return movieFolder;
    }

    /**
     * Return a set of directory names, which starts with 'cd','disk' or 'part'.
     * 
     * @param subDirectoryNames
     * @return
     */
    private Set<String> getCdFolders(Set<String> subDirectoryNames){
        Set<String> valid = new HashSet<String>();
        for(String folder:subDirectoryNames){
            if(folder.startsWith("cd") || folder.startsWith("disk") || folder.startsWith("part")){
                valid.add(folder);
            }
        }
        return valid;
    }

    /**
     * add the compressed files to the file group, which are in the specified directory.
     * @param sm
     * @param fg
     * @param fileList
     * @param folderName
     */
    private void addCompressedFiles(StorableMovie sm, FileGroup fg, File[] fileList, String folderName) {
        for (File f : fileList) {
            if (f.isDirectory() && folderName.equals(f.getName().toLowerCase())) {
                addCompressedFiles(sm, fg, f.listFiles());
            }
        }
    }

    /**
     * initialize a FileGroup 
     * @param folder
     * @param sm
     * @return
     */
    private FileGroup initStorableMovie(File folder, StorableMovie sm) {
        FileGroup fg = new FileGroup();
        fg.setAudio(movieNameExtractor.getLanguageSuggestion(folder.getName()));
        sm.addFileGroup(fg);
        sm.setTitle(movieNameExtractor.removeCrap(folder));
        return fg;
    }

    /**
     * Handle the one directory with several different movies case.
     * @param files
     * @return true, if a movie found
     */
    private boolean genericMovieFindProcess(File[] files) {
        Map<String, StorableMovie> foundMovies = new HashMap<String, StorableMovie>();
        for (File f : files) {
            if (!f.isDirectory()) {
                String extension = getExtension(f);
                if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(extension)) {
                    String baseName = movieNameExtractor.removeCrap(f);
                    StorableMovie m = foundMovies.get(baseName);
                    FileGroup fg;
                    if (m == null) {
                        m = new StorableMovie();
                        m.setTitle(baseName);
                        fg = new FileGroup();
                        fg.setAudio(movieNameExtractor.getLanguageSuggestion(f.getName()));
                        m.addFileGroup(fg);
                        fg.addLocation(new MovieLocation(f.getParent(), currentLabel, false));
                        foundMovies.put(baseName, m);
                    } else {
                        fg = m.getUniqueFileGroup();
                    }
                    fg.addFile(new StorableMovieFile(f, FileType.getTypeByExtension(extension)));
                }
            }
        }
        boolean foundMovie = false;
        for (StorableMovie m : foundMovies.values()) {
            if (m.isValid()) {
                add(m);
                foundMovie = true;
            }
        }
        return foundMovie;
    }

    /**
     * add the files, which has similar names, to the movie object
     * 
     * @param sm
     * @param files
     * @param next
     */
    private void addFiles(StorableMovie sm, FileGroup fg, File[] files, String plainFileName) {
        for (File f : files) {
            if (!f.isDirectory()) {
                String baseName = getNameWithoutExt(f);
                String ext = getExtension(f);
                if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(ext)) {
                    if (LevenshteinDistance.distance(plainFileName, baseName) <= 3) {
                        fg.addFile(new StorableMovieFile(f, FileType.getTypeByExtension(ext), fg));
                    }
                }
            }
        }
    }
    
    private void addCompressedFiles(StorableMovie sm, FileGroup fg, File[] files) {
        for (File f : files) {
            if (!f.isDirectory()) {
                String ext = getExtension(f);
                if(ext == null){
                    LOGGER.trace("Ignoring file without extension: "+f.getAbsolutePath());
                }else{
                    FileType type = FileType.getTypeByExtension(ext);
                    if (type==FileType.COMPRESSED || type==FileType.NFO || type==FileType.SUBTITLE) {
                        fg.addFile(new StorableMovieFile(f, type, fg));
                    }
                }
            }
        }
    }

    private void add(StorableMovie movie) {
        FileGroup uniqueFileGroup = movie.getUniqueFileGroup();
        LOGGER.info("film:"+movie.getTitle()+" found at: "+uniqueFileGroup.getDirectoryPath()+" {"+uniqueFileGroup.getFiles()+'}');
        for (FileGroup g : movie.getGroups()) {
            g.guessReleaseType();
        }
        movies.add(new MovieInfo(movie));
    }

    private String getExtension(File file) {
        return getExtension(file.getName());
    }

    private String getExtension(String fileName) {
        int lastDotPos = fileName.lastIndexOf('.');
        if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < fileName.length() - 1) {
            return fileName.substring(lastDotPos + 1).toLowerCase();
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
