package eu.somatik.moviebrowser.gui;

import com.flicklib.domain.MovieSearchResult;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class MovieListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        MovieSearchResult movieSite = (MovieSearchResult) value;
        StringBuilder buf = new StringBuilder();
        buf.append(movieSite.getTitle());
        if (movieSite.getOriginalTitle() != null) {
            buf.append('/').append(movieSite.getOriginalTitle());
        }
        if (movieSite.getAlternateTitle() != null) {
            buf.append('/').append(movieSite.getAlternateTitle());
        }
        if (movieSite.getYear() != null || movieSite.getDescription() != null || movieSite.getType() != null) {
            buf.append(" (");
            if (movieSite.getYear() != null) {
                buf.append(movieSite.getYear()).append(' ');
            }
            if (movieSite.getDescription() != null) {
                buf.append(movieSite.getDescription()).append(' ');
            }
            if (movieSite.getType() != null) {
                buf.append(movieSite.getType().getName());
            }
            buf.append(')');
        }
        setText(buf.toString());
        return this;
    }
}
