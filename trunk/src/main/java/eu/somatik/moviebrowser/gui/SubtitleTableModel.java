package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.domain.Subtitle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class SubtitleTableModel extends AbstractTableModel{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SubtitleTableModel.class);
    
    public static final String LANG_COL = "Language";
    
    private static final String[] columnNames = {LANG_COL, "File Name", "#CD", "Type", "Source"};
    
    private List<Subtitle> subs;

    public SubtitleTableModel() {
        this.subs = new ArrayList<Subtitle>();
    }

    @Override
    public int getRowCount() {
        return subs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Subtitle getSubtitle(int rowIndex){
        return subs.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return subs.get(rowIndex).getLanguage();
            case 1:
                return subs.get(rowIndex).getFileName();
            case 2:
                return subs.get(rowIndex).getNoCd();
            case 3:
                return subs.get(rowIndex).getType();
            case 4:
                return subs.get(rowIndex).getSubSource();
            default:
                assert false: "Should never come here";
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public void add(Subtitle sub){
        if(sub == null){
            LOGGER.error("Trying to add null object");
        }else{
            int row = subs.size();
            subs.add(sub);
            this.fireTableRowsInserted(row, row);
        }
    }
    
    /**
     * Clears the subtitles list
     */
    public void clear(){
        subs.clear();
        this.fireTableDataChanged();
    }

}
