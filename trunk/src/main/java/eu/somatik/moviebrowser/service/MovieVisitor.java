package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

public class MovieVisitor {

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
