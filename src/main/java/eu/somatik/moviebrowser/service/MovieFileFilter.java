package eu.somatik.moviebrowser.service;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Checks if a file has a know video extension
 * @author francisdb
 */
public class MovieFileFilter implements FileFilter {

    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(new String[]{
                "avi", "mpg", "mpeg", "divx", "mkv", "xvid", "m4v", "mov", "flv"
            });
    private boolean acceptFolders;

    public MovieFileFilter(boolean acceptFolders) {
        this.acceptFolders = acceptFolders;
    }

    @Override
    public boolean accept(File file) {
        boolean isVideo = false;
        if (file.isDirectory()) {
            isVideo = acceptFolders;
        } else {
            String name = file.getName();
            int lastDotPos = name.lastIndexOf('.');
            if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < name.length() - 1) {
                String ext = name.substring(lastDotPos + 1).toLowerCase();
                if (VIDEO_EXTENSIONS.contains(ext)) {
                    isVideo = true;
                }
            }

        }
        return isVideo;
    }

    public String clearMovieExtension(File file) {
        String name = file.getName().toLowerCase();
        int lastDotPos = name.lastIndexOf('.');
        if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < name.length() - 1) {
            String ext = name.substring(lastDotPos + 1).toLowerCase();
            if (VIDEO_EXTENSIONS.contains(ext)) {
                name = name.substring(0, lastDotPos);
            }
        }
        return name;
    }
}
