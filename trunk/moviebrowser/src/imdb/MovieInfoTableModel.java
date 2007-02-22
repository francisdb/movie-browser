/*
 * MovieInfoTableModel.java
 *
 * Created on January 31, 2007, 1:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package imdb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private static final String COL_NAMES[] = {"?","Movie","Date","IMDB","Critics","Users"};
    private static final Class COL_CLASSES[] = {MovieStatus.class, Object.class, Date.class, String.class, String.class, String.class};
    
    private List<MovieInfo> movies;
    
    /** Creates a new instance of MovieInfoTableModel 
     * @param movies 
     */
    public MovieInfoTableModel(List<MovieInfo> movies) {
        this.movies = movies;
        for(MovieInfo movie:movies){
            movie.addPropertyChangeListener(this);
        }
    }

    public int getRowCount() {
        return movies.size();
    }

    public String getColumnName(int columnIndex){
        return COL_NAMES[columnIndex];
    }
    
    public Class getColumnClass(int columnIndex){
        return COL_CLASSES[columnIndex];
    }
    
    public int getColumnCount() {
        return COL_CLASSES.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value;
        switch(columnIndex){
            case 0:
                return movies.get(rowIndex).getStatus();
            case 1:
                return movies.get(rowIndex);
            case 2:
                return new Date(movies.get(rowIndex).getDirectory().lastModified());
            case 3:
                return movies.get(rowIndex).getRating();
            case 4:
                return movies.get(rowIndex).getTomatoesRating();
            case 5:
                return movies.get(rowIndex).getTomatoesRatingUsers();
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

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    

    
}
