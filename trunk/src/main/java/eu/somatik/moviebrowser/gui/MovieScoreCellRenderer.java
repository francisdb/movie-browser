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
