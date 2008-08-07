package eu.somatik.moviebrowser.service;

import com.google.inject.Singleton;
import com.flicklib.domain.MovieInfo;
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
