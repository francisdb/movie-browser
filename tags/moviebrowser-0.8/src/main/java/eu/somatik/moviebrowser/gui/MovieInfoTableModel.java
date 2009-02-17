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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.ScoreCalculator;
import eu.somatik.moviebrowser.service.WeightedScoreCalculator;
import eu.somatik.moviebrowser.service.ui.ContentProvider;

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
    //public static final int MOVIE_COL = 1;
    private static final String COL_NAMES[] = {
        STATUS_COLUMN_NAME,
        MOVIE_COLUMN_NAME,
        "Year",
        "Date",
        "Runtime",
        SCORE_COLUMN_NAME,
        "Copies"
    };
    private static final Class<?> COL_CLASSES[] = {
        MovieStatus.class,
        Object.class,
        Integer.class,
        Date.class,
        Integer.class,
        Integer.class,
        Integer.class,
    };

    
    private final InfoHandler infoHandler;
    private final ScoreCalculator calculator;
    private final Settings settings;
    
    private List<MovieInfo> movies;

    private List<MovieService> extraColumns;
    
    private ContentProvider contentProvider;
    
    /** 
     * Creates a new instance of MovieInfoTableModel 
     * @param infoHandler 
     */
    public MovieInfoTableModel(final InfoHandler infoHandler, final MovieFinder finder, final ContentProvider contentProvider, final Settings settings) {
        this.infoHandler = infoHandler;
        this.settings = settings;
        this.calculator = new WeightedScoreCalculator(infoHandler);
        this.movies = new ArrayList<MovieInfo>();
        //this.finder = finder;
        this.contentProvider = contentProvider;
        calculateExtraColumns();
    }

    private void calculateExtraColumns() {
        extraColumns = settings.getEnabledServices();
    }

    @Override
    public int getRowCount() {
        return movies.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex<COL_CLASSES.length) {
            return COL_NAMES[columnIndex];
        } else {
            MovieService service = extraColumns.get(columnIndex - COL_NAMES.length);
            return service.getShortName();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex<COL_CLASSES.length) {
            return COL_CLASSES[columnIndex];
        } else {
            // extra column, it's Integer
            return Integer.class;
        }
    }

    @Override
    public int getColumnCount() {
        return COL_CLASSES.length+ extraColumns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MovieInfo info = movies.get(rowIndex);
        StorableMovie movie = info.getMovie();
        switch (columnIndex) {
            case 0:
                return info.getStatus();
            case 1:
                return contentProvider.getTitle(info);
            case 2:
                return movie == null?null:movie.getYear();
            case 3:
                return info.getMovie().getLastModified();
            case 4:
                return movie == null?null:movie.getRuntime();
            case 5:
                return calculator.calculate(info);
            case 6:
                return info.getMovie().getCopyCount();
            default:
                MovieService service = extraColumns.get(columnIndex - COL_NAMES.length);
                return infoHandler.score(info, service);
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
    public void addAll(Collection<MovieInfo> items) {
        if (items.size() != 0) {
            int firstRow = movies.size();
            movies.addAll(items);
            for (MovieInfo movie : items) {
                movie.addPropertyChangeListener(this);
            }
            this.fireTableRowsInserted(firstRow, firstRow + items.size() - 1);
        }
    }

    public void addAllMovie(Collection<StorableMovie> items) {
        if (items.size() != 0) {
            int firstRow = movies.size();
            for (StorableMovie movie : items) {
            	MovieInfo info = new MovieInfo(movie);
                info.addPropertyChangeListener(this);
                movies.add(info);
            }
            this.fireTableRowsInserted(firstRow, firstRow + items.size() - 1);
        }
    }

    
    /**
     * Adds all movies
     * @param item 
     */
    public void delete(MovieInfo item) {
        int row = movies.indexOf(item);
        movies.remove(item);
        this.fireTableRowsDeleted(row, row);
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

    public void refreshColumns(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
        calculateExtraColumns();
        this.fireTableStructureChanged();
    }
    
}
