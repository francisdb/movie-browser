/*
 * MovieInfoTableModel.java
 *
 * Created on January 31, 2007, 1:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser;

import eu.somatik.moviebrowser.data.MovieInfo;
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
    
    /**
     * Movie column number
     */
    public static final int MOVIE_COL = 1;
    private static final String COL_NAMES[] = {"?","Movie","Date","Runtime","IMDB","Critics","Users"};
    private static final Class<?> COL_CLASSES[] = {MovieStatus.class, Object.class, Date.class, Integer.class, String.class, String.class, String.class};
    
    private List<MovieInfo> movies;
    
    /** 
     * Creates a new instance of MovieInfoTableModel 
     */
    public MovieInfoTableModel() {
        this.movies = new ArrayList<MovieInfo>();
    }

    public int getRowCount() {
        return movies.size();
    }

    @Override
	public String getColumnName(int columnIndex){
        return COL_NAMES[columnIndex];
    }
    
    @Override
	public Class<?> getColumnClass(int columnIndex){
        return COL_CLASSES[columnIndex];
    }
    
    public int getColumnCount() {
        return COL_CLASSES.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return movies.get(rowIndex).getStatus();
            case 1:
                return movies.get(rowIndex);
            case 2:
                return new Date(movies.get(rowIndex).getDirectory().lastModified());
            case 3:
                return movies.get(rowIndex).getMovie().getRuntime();
            case 4:
                return movies.get(rowIndex).getMovie().getRating();
            case 5:
                return movies.get(rowIndex).getMovie().getTomatoesRating();
            case 6:
                return movies.get(rowIndex).getMovie().getTomatoesRatingUsers();
            default:
                assert false: "Should never come here";
                return null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final MovieInfo info = (MovieInfo)evt.getSource();
        SwingUtilities.invokeLater(new Runnable() {
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
