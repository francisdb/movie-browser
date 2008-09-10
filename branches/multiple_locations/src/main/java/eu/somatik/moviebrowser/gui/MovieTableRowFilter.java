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

import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;

/**
 *
 * @author francisdb
 */
public class MovieTableRowFilter extends RowFilter<TableModel, Integer> {
    private final String filterText;

    public MovieTableRowFilter(String filterText) {
        this.filterText = filterText;
    }

    @Override
    public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
        boolean include = false;
        MovieInfoTableModel model = (MovieInfoTableModel) entry.getModel();
        MovieInfo info = model.getMovie(entry.getIdentifier());
        StorableMovie movie = info.getMovieFile().getMovie();
        if (info.getMovieFile().getPath().toLowerCase().contains(filterText)) {
            include = true;
        } else if (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(filterText)) {
            include = true;
        } else if (movie.getDirector() != null && movie.getDirector().toLowerCase().contains(filterText)) {
            include = true;
        } else if (movie.getPlot() != null && movie.getPlot().toLowerCase().contains(filterText)) {
            include = true;
        } else if (movie.getYear() != null && movie.getYear().toString().contains(filterText)) {
            include = true;
        } else {
            for (Genre genre : info.getMovieFile().getMovie().getGenres()) {
                    String[] split = filterText.split(" ");
                    for(int i=0; i<split.length; i++) {
                        if(genre.getName().toLowerCase().equals(split[i])) {
                            include = true;
                        }
                    }   
            }
        }
        return include;
    }
}
