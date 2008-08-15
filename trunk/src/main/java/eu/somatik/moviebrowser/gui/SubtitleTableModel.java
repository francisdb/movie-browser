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

import com.flicklib.domain.Subtitle;
import java.util.ArrayList;
import java.util.Collection;
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
    
    private static final String[] COl_NAMES = {LANG_COL, "File Name", "#CD", "Type", "Source"};
    
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
        return COl_NAMES.length;
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
        return COl_NAMES[column];
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
     * Adds all movies
     * @param items 
     */
    public void addAll(Collection<Subtitle> items){
        if(items.size() != 0){
            int firstRow = subs.size();
            subs.addAll(items);
//            for(Subtitle subtitle:items){
//                subtitle.addPropertyChangeListener(this);
//            }
            this.fireTableRowsInserted(firstRow, firstRow+items.size()-1);
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
