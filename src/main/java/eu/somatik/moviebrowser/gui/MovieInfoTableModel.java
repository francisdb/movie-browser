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
package eu.somatik.moviebrowser.gui;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;

import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.ScoreCalculator;
import eu.somatik.moviebrowser.service.WeightedScoreCalculator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author francisdb
 */
public class MovieInfoTableModel extends AbstractTableModel implements PropertyChangeListener, Iterable<MovieInfo> {

    private static final long serialVersionUID = 1L;
    
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

    
    private final InfoHandler infoHandler;
    private final ScoreCalculator calculator;
    
    private List<MovieInfo> movies;

    /** 
     * Creates a new instance of MovieInfoTableModel 
     * @param infoHandler 
     */
    public MovieInfoTableModel(final InfoHandler infoHandler) {
        this.infoHandler = infoHandler;
        this.calculator = new WeightedScoreCalculator(infoHandler);
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
        StorableMovie movie = info.getMovie();
        switch (columnIndex) {
            case 0:
                return info.getStatus();
            case 1:
                return info.getMovie().getTitle();
            case 2:
                return movie == null?null:movie.getYear();
            case 3:
                return new Date(info.getDirectory().lastModified());
            case 4:
                return movie == null?null:movie.getRuntime();
            case 5:
                return calculator.calculate(info);
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
                return infoHandler.score(info, MovieService.FLIXSTER);
                //return movies.get(rowIndex).getMovieFile().getMovie().getFlixterScore();
            default:
                assert false : "Should never come here";
                return null;
        }
    }


    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MovieInfo info = (MovieInfo) evt.getSource();
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
    
    public MovieInfo getMovieInfo(int rowIndex){
        return movies.get(rowIndex);
    }

    @Override
    public Iterator<MovieInfo> iterator() {
        return movies.iterator();
    }
}
