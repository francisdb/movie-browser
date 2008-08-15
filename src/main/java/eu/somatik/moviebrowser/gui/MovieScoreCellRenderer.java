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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

final class MovieScoreCellRenderer extends DefaultTableCellRenderer {

    private final Color[] colors;

    public MovieScoreCellRenderer() {
        super();
        colors = new Color[100];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(Math.min((100 - i) * 7, 255), Math.min(i * 3, 255), 128);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Integer score = (Integer) value;
        if (score != null) {
            if (isSelected) {
                this.setBackground(colors[score].darker());
            } else {
                this.setBackground(colors[score]);
            }
        } else {
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
            } else {
                this.setBackground(table.getBackground());
            }
        }
        return this;
    }
}
