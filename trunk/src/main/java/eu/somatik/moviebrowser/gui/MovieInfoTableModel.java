/*
 * MovieInfoTableModel.java
 *
 * Created on January 31, 2007, 1:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.gui;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;

import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.service.InfoHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author francisdb
 */
public class MovieInfoTableModel extends AbstractTableModel implements PropertyChangeListener {

    private final InfoHandler infoHandler;
    public static final String MOVIE_COLUMN_NAME = "Movie";
    public static final String STATUS_COLUMN_NAME = "?";
    public static final String SCORE_COLUMN_NAME = "Score";
    /**
     * Movie column number
     */
    public static final int MOVIE_COL = 1;
    private static final String COL_NAMES[] = {
        STATUS_COLUMN_NAME,
        MOVIE_COLUMN_NAME,
        "Year",
        "Date",
        "Runtime",
        SCORE_COLUMN_NAME,
        "IMDB",
        "Tomato",
        "MWeb",
        "Google",
        "Flixter"
    };
    private static final Class<?> COL_CLASSES[] = {
        MovieStatus.class,
        Object.class,
        Integer.class,
        Date.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class
    };
    private List<MovieInfo> movies;

    /** 
     * Creates a new instance of MovieInfoTableModel 
     * @param infoHandler 
     */
    public MovieInfoTableModel(final InfoHandler infoHandler) {
        this.infoHandler = infoHandler;
        this.movies = new ArrayList<MovieInfo>();
    }

    @Override
    public int getRowCount() {
        return movies.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COL_NAMES[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COL_CLASSES[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return COL_CLASSES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MovieInfo info = movies.get(rowIndex);
        StorableMovie movie = info.getMovieFile().getMovie();
        switch (columnIndex) {
            case 0:
                return info.getStatus();
            case 1:
                return info;
            case 2:
                return movie == null?null:movie.getYear();
            case 3:
                return new Date(info.getDirectory().lastModified());
            case 4:
                return movie == null?null:movie.getRuntime();
            case 5:
                return infoHandler.calculate(info);
            case 6:
                return infoHandler.score(info, MovieService.IMDB);
                //return movies.get(rowIndex).getMovie().getImdbScore();
            case 7:
                return infoHandler.score(info, MovieService.TOMATOES);
                //return movies.get(rowIndex).getMovieFile().getMovie().getTomatoScore();
            case 8:
                return infoHandler.score(info, MovieService.MOVIEWEB);
                //return movies.get(rowIndex).getMovieFile().getMovie().getMovieWebScore();
            case 9:
                return infoHandler.score(info, MovieService.GOOGLE);
                //return movies.get(rowIndex).getMovieFile().getMovie().getGoogleScore();
            case 10:
                return infoHandler.score(info, MovieService.FLIXTER);
                //return movies.get(rowIndex).getMovieFile().getMovie().getFlixterScore();
            default:
                assert false : "Should never come here";
                return null;
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final MovieInfo info = (MovieInfo) evt.getSource();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int index = movies.indexOf(info);
                fireTableRowsUpdated(index, index);
            }
        });
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Adds all movies
     * @param items 
     */
    public void addAll(List<MovieInfo> items) {
        if (items.size() != 0) {
            int firstRow = movies.size();
            movies.addAll(items);
            for (MovieInfo movie : items) {
                movie.addPropertyChangeListener(this);
            }
            this.fireTableRowsInserted(firstRow, firstRow + items.size() - 1);
        }
    }

    /**
     * Clears the movie list
     */
    public void clear() {
        movies.clear();
        this.fireTableDataChanged();
    }
    
    public MovieInfo getMovie(int rowIndex){
        return movies.get(rowIndex);
    }
}
