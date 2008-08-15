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

import eu.somatik.moviebrowser.domain.MovieStatus;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

final class MovieStatusCellRenderer extends DefaultTableCellRenderer {

    private final Icon defaultIcon;
    private final Icon loadedIcon;
    private final Icon loadingIcon;
    private final Icon failedIcon;

    public MovieStatusCellRenderer(final IconLoader iconLoader) {
        super();
        this.defaultIcon = iconLoader.loadIcon("images/16/bullet_black.png");
        this.loadedIcon = iconLoader.loadIcon("images/16/bullet_green.png");
        this.loadingIcon = iconLoader.loadIcon("images/16/bullet_orange.png");
        this.failedIcon = iconLoader.loadIcon("images/16/bullet_red.png");
        setIcon(defaultIcon);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        MovieStatus movieStatus = (MovieStatus) value;
        switch (movieStatus) {
            case NEW:
                setIcon(defaultIcon);
                break;
            case CACHED:
                setIcon(loadedIcon);
                break;
            case LOADED:
                setIcon(loadedIcon);
                break;
            case LOADING:
                setIcon(loadingIcon);
                break;
			case ERROR:
				setIcon(failedIcon);
				break;
        }
        setText(null);
        return this;
    }
}
