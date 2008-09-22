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

import com.google.inject.Inject;
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
import eu.somatik.moviebrowser.domain.FileGroup;
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
public class AdvancedFolderScanner implements FolderScanner {

    final static Logger LOGGER = LoggerFactory.getLogger(AdvancedFolderScanner.class);

    private final MovieNameExtractor movieNameExtractor;
   
    @Inject
    public AdvancedFolderScanner(final MovieNameExtractor movieNameExtractor) {
        this.movieNameExtractor = movieNameExtractor;
    }



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
        int subDirectory = 0;
        int compressedFiles = 0;
        Set<String> directoryNames = new HashSet<String>();
        for (File f : files) {
            if (f.isDirectory()) {
                subDirectory ++;
                directoryNames.add(f.getName().toLowerCase());
            } else {
                String ext = getExtension(f);
                if(ext == null){
                    LOGGER.warn("Ignoring file without extension: "+f.getAbsolutePath());
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
        if (compressedFiles>0) {
            StorableMovie sm = new StorableMovie();
            FileGroup fg = initStorableMovie(folder, sm);
            fg.addLocation(new MovieLocation(folder.getParent(), currentLabel));
            addCompressedFiles(sm, fg, files );
            add(sm);
            return;
        }
        if (subDirectory>=2 && subDirectory<=3) {
            // the case of :
            // Title_of_the_film/cd1/...
            // Title_of_the_film/cd2/...
            // with an optional sample directory
            // Title_of_the_film/sample/ 
            if (directoryNames.contains("cd1") && directoryNames.contains("cd2")) {
                StorableMovie sm = new StorableMovie();
                FileGroup fg = initStorableMovie(folder, sm);
                fg.addLocation(new MovieLocation(folder.getParent(), currentLabel));
                
                addCompressedFiles(sm, fg, files, "cd1");
                addCompressedFiles(sm, fg, files, "cd2");

                add(sm);
                return;
            }
        }
        for (File f : files) {
            if (f.isDirectory()) {
                browse(f);
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

        if (subDirectory>0) {
            genericMovieFindProcess(files);
        } else {
            
            int foundFiles = plainFileNames.size();
            switch (foundFiles) {
                case 0:
                    break;
                case 1: {
                    StorableMovie sm = new StorableMovie();
                    FileGroup fg = initStorableMovie(folder, sm);

                    fg.addLocation(new MovieLocation(folder.getParent(), currentLabel));
                    addFiles(sm, fg, files,  plainFileNames.iterator().next());
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
                        FileGroup fg = initStorableMovie(folder, sm);

                        fg.addLocation(new MovieLocation(folder.getParent(), currentLabel));
                        addFiles(sm, fg, files, name1);
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

    private void genericMovieFindProcess(File[] files) {
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
                        fg.addLocation(new MovieLocation(f.getParent(), currentLabel));
                        foundMovies.put(baseName, m);
                    } else {
                        fg = m.getUniqueFileGroup();
                    }
                    fg.addFile(new StorableMovieFile(f, FileType.getTypeByExtension(extension)));
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
    private void addFiles(StorableMovie sm, FileGroup fg, File[] files, String plainFileName) {
        for (File f : files) {
            if (!f.isDirectory()) {
                String baseName = getNameWithoutExt(f);
                String ext = getExtension(f);
                if (MovieFileFilter.VIDEO_EXT_EXTENSIONS.contains(ext)) {
                    if (LevenshteinDistance.distance(plainFileName, baseName) < 3) {
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
                    LOGGER.warn("Ignoring file without extension: "+f.getAbsolutePath());
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
