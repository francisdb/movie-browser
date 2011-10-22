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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.ReleaseType;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

/**
 * This tree model helps to introspect the underlying data model of one movie.
 *  
 * @author zsombor
 *
 */
public class MovieFileTreeTableModel extends AbstractTreeTableModel {

    final static int NAME = 0;
    final static int TYPE = 1;
    final static int SIZE = 2;
    final static int LABEL = 3;
    
    
    final static int COLUMN_COUNT = 3;
    
    final static Object DUMMY_RATING = new Object();
    StorableMovie movie;
    
    static class RowInfo<T> {
        T row;
        @SuppressWarnings("rawtypes")
        List children;
        
        @SuppressWarnings("rawtypes")
        public RowInfo(T value, int size) {
            this.row = value;
            this.children = new ArrayList(size);
        }
        public Object getValue(int column) {
            return null;
        }
        
        public Object getChild(int index) {
            return children.get(index);
        }
        
        public int getChildCount() {
            return children.size();
        }
        
        public int indexOf(Object child) {
            return children.indexOf(child);
        }
    }
    
    static class MovieRowInfo extends RowInfo<StorableMovie> {

        @SuppressWarnings("unchecked")
        public MovieRowInfo(StorableMovie value) {
            super(value,value.getGroups().size()+1);
            children.add(DUMMY_RATING);
            children.addAll(row.getGroups());
        } 
        
        @Override
        public Object getValue(int column) {
            switch (column) {
                case NAME : return row.getTitle();
                case LABEL : return row.getDirectorList();
                case TYPE : return row.getGenres().toString();
                default :
                    return null;
            }
        }
    }
    
    static class RatingsRowInfo extends RowInfo<StorableMovie> {
        public RatingsRowInfo(StorableMovie value) {
            super(value,value.getSiteInfo().size());
            children.addAll(value.getSiteInfo());
        }

        @Override
        public Object getValue(int column) {
            if (column==NAME) {
                return "Ratings";
            }
            return null;
        }
        
    }
    
    static class FileGroupRowInfo extends RowInfo<FileGroup> {

        @SuppressWarnings("unchecked")
        public FileGroupRowInfo(FileGroup value) {
            super(value, value.getFiles().size() + value.getLocations().size());
            children.addAll(value.getLocations());
            children.addAll(value.getFiles());
        }
        
        @Override
        public Object getValue(int column) {
            switch(column) {
                case NAME : {
                    StringBuilder info = new StringBuilder();
                    info.append("Files:");
                    if (row.getType()!=null) {
                        info.append(row.getType().getLabel());
                    }
                    Language l = row.getAudio();
                    if (l!=null) {
                        info.append(",").append(l.getName());
                    }
                    l = row.getSubtitle();
                    if (l!=null) {
                        info.append("(subtitle:").append(l.getName()).append(')');
                    }
                    return info.toString();
                }
                case TYPE :
                    ReleaseType type = row.getType();
                    if(type == null){
                        return null;
                    }else{
                        return type.getLabel();
                    }
                case SIZE :
                    return Long.valueOf(row.getSize());
                default : 
                    return null;
            }
        }
    }
    
    static class FileRowInfo extends RowInfo<StorableMovieFile> {

        public FileRowInfo(StorableMovieFile value) {
            super(value, 0);
        }
        
        @Override
        public Object getValue(int column) {
            switch(column) {
                case NAME : return row.getName();
                case TYPE : return row.getType().getLabel();
                case SIZE : return row.getSize();
                default : return null;
            }
        }
    }
    
    static class LocationRowInfo extends RowInfo<MovieLocation> {

        public LocationRowInfo(MovieLocation value) {
            super(value, 0);
        }
        
        @Override
        public Object getValue(int column) {
            switch(column) {
                case NAME : return row.getPath();
                case TYPE : return row.getLabel();
                default : return null;
            }
        }
    }
    
    static class SiteRowInfo extends RowInfo<StorableMovieSite> {

        public SiteRowInfo(StorableMovieSite value) {
            super(value, 0);
        }
        
        @Override
        public Object getValue(int column) {
            switch (column) {
                case NAME : return row.getService().getName();
                case SIZE : {
                    StringBuilder b = new StringBuilder();
                    if (row.getScore()!=null) {
                        b.append(row.getScore());
                        if (row.getVotes()!=null) {
                            b.append('/').append(row.getVotes());
                        }
                    }
                    return b.toString();
                }
                case LABEL : return row.getVotes();
                default : return null;
            }
        }
        
    }
    
    
    
    
    Map<Object,RowInfo<?>> rowInfos = new HashMap<Object, RowInfo<?>>();
    /**
     *
     */
    public MovieFileTreeTableModel() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param root
     */
    public MovieFileTreeTableModel(StorableMovie root) {
        super(root);
        this.movie = root;
    }
    
    public void setMovie(StorableMovie movie) {
        this.movie = movie;
        this.rowInfos.clear();
        if(movie != null){
            this.rowInfos.put(DUMMY_RATING, new RatingsRowInfo(movie));
        }
        this.root = movie;
        this.modelSupport.fireNewRoot();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case NAME : return "Name";
            case TYPE : return "Type";
            case SIZE : return "Size";
            case LABEL : return "Description";
        }
        return super.getColumnName(column);
    }

    
    /* (non-Javadoc)
     * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }
    
    RowInfo<?> getRowInfo(Object node) {
        RowInfo<?> rowInfo = rowInfos.get(node);
        if (rowInfo==null) {
            if (node instanceof StorableMovie) {
                rowInfo = new MovieRowInfo((StorableMovie) node);
            }
            if (node instanceof FileGroup) {
                rowInfo = new FileGroupRowInfo((FileGroup) node);
            }
            if (node instanceof StorableMovieFile) {
                rowInfo = new FileRowInfo((StorableMovieFile) node);
            }
            if (node instanceof MovieLocation) {
                rowInfo = new LocationRowInfo((MovieLocation) node);
            }
            if (node instanceof StorableMovieSite) {
                rowInfo = new SiteRowInfo((StorableMovieSite) node);
            }
            
            if (rowInfo!=null) {
                rowInfos.put(node, rowInfo);
            }
        }
        
        return rowInfo;
    }

    /* (non-Javadoc)
     * @see org.jdesktop.swingx.treetable.TreeTableModel#getValueAt(java.lang.Object, int)
     */
    @Override
    public Object getValueAt(Object node, int column) {
        RowInfo<?> ri = getRowInfo(node);
        if (ri!=null) {
            return ri.getValue(column);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(Object parent, int index) {
        RowInfo<?> ri = getRowInfo(parent);
        if (ri!=null) {
            return ri.getChild(index);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(Object parent) {
        RowInfo<?> ri = getRowInfo(parent);
        if (ri!=null) {
            return ri.getChildCount();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        RowInfo<?> ri = getRowInfo(parent);
        if (ri!=null) {
            return ri.indexOf(child);
        }
        return 0;
    }

}
