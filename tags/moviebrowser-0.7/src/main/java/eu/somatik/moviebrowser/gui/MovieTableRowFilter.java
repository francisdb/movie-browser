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
