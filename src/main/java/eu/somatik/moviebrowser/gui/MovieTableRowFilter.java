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

import java.util.Set;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;

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
        MovieInfoTableModel model = (MovieInfoTableModel) entry.getModel();
        MovieInfo info = model.getMovieInfo(entry.getIdentifier());
        StorableMovie movie = info.getMovie();
        if (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(filterText)) {
            return true;
        } else if (movie.getDirectorList().toLowerCase().contains(filterText)) {
            return true;
        } else if (movie.getActorList().toLowerCase().contains(filterText)) {
            return true;
        } else if (movie.getPlot() != null && movie.getPlot().toLowerCase().contains(filterText)) {
            return true;
        } else if (movie.getYear() != null && movie.getYear().toString().contains(filterText)) {
            return true;
        } else {
            Set<MovieLocation> locations = info.getMovie().getLocations();
            for (MovieLocation l : locations) {
                String path = l.getPath();
                if (path!=null && path.toLowerCase().contains(filterText)) {
                    return true;
                }
            }
            for (Genre genre : info.getMovie().getGenres()) {
                String[] split = filterText.split(" ");
				for (int i = 0; i < split.length; i++) {
					if (genre.getName().toLowerCase().equals(split[i])) {
						return true;
					}
				}   
            }
        }
        return false;
    }
}
