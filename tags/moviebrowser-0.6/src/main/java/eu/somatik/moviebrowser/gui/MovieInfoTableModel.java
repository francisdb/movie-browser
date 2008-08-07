/*
 * MovieInfoTableModel.java
 *
 * Created on January 31, 2007, 1:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;

import eu.somatik.moviebrowser.service.ScoreCalculator;
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
public class MovieInfoTableModel extends AbstractTableModel implements PropertyChangeListener{
    
    private final ScoreCalculator calculator;
    
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
     */
    public MovieInfoTableModel() {
        this.calculator = new ScoreCalculator();
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
        switch(columnIndex){
            case 0:
                return movies.get(rowIndex).getStatus();
            case 1:
                return movies.get(rowIndex);
            case 2:
                return movies.get(rowIndex).getMovie().getYear();
            case 3:
                return new Date(movies.get(rowIndex).getDirectory().lastModified());
            case 4:
                return movies.get(rowIndex).getMovie().getRuntime();
            case 5:
                return calculator.calculate(movies.get(rowIndex).getMovie());
            case 6:
                return movies.get(rowIndex).getMovie().getImdbScore();
            case 7:
                return movies.get(rowIndex).getMovie().getTomatoScore();
            case 8:
                return movies.get(rowIndex).getMovie().getMovieWebScore();
            case 9:
                return movies.get(rowIndex).getMovie().getGoogleScore();
            case 10:
                return movies.get(rowIndex).getMovie().getFlixterScore();
            default:
                assert false: "Should never come here";
                return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final MovieInfo info = (MovieInfo)evt.getSource();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int index = movies.indexOf(info);
                fireTableRowsUpdated(index,index);
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
    public void addAll(List<MovieInfo> items){
        if(items.size() != 0){
            int firstRow = movies.size();
            movies.addAll(items);
            for(MovieInfo movie:items){
                movie.addPropertyChangeListener(this);
            }
            this.fireTableRowsInserted(firstRow, firstRow+items.size()-1);
        }
    }
    
    /**
     * Clears the movie list
     */
    public void clear(){
        movies.clear();
        this.fireTableDataChanged();
    }

    
}
