/*
 * SubtitleCrawlerFrame.java
 *
 * Created on 31 July 2008, 02:22
 */

package eu.somatik.moviebrowser.gui;

import javax.swing.ImageIcon;
import java.util.List;
import java.util.Iterator;

/**
 *
 * @author  rug
 */
public class SubtitleCrawlerFrame extends javax.swing.JFrame {

    /** Creates new form SubtitleCrawlerFrame */
    public SubtitleCrawlerFrame(List<String> files) {
        initComponents();
        this.setTitle("Subtitle Crawler (" + files.toString() + ")");
        crawl(files);
        searchButton.setEnabled(false);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusProgressBar = new javax.swing.JProgressBar();
        infoLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        subtitlesTable = new javax.swing.JTable();
        searchText = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Subtitle Crawler");
        setIconImage(new ImageIcon(EditMovieFrame.getFrames().getClass().getResource("/images/movie.png")).getImage());

        statusProgressBar.setForeground(new java.awt.Color(0, 153, 51));

        infoLabel.setText("Double click on the subtitle you require below to begin download. Use the search box to refine your search.");

        subtitlesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Language", "Name", "Type", "Source"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        subtitlesTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(subtitlesTable);
        subtitlesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        subtitlesTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        subtitlesTable.getColumnModel().getColumn(1).setMinWidth(20);
        subtitlesTable.getColumnModel().getColumn(2).setPreferredWidth(10);

        searchButton.setText("Search");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addComponent(statusProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(searchText, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton))
                    .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

public void crawl(List<String> files) {
    String fileName;
    Iterator<String> i = files.iterator();
    while(i.hasNext()) {
        fileName = (String) i.next();
        fileName = fileName.substring(0,(fileName.length()-4));
        if(fileName.endsWith(".")) {
            fileName = fileName.substring(0, (fileName.length()-1));
        }
        System.out.println(fileName);
    }
}
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel infoLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchText;
    private javax.swing.JProgressBar statusProgressBar;
    private javax.swing.JTable subtitlesTable;
    // End of variables declaration//GEN-END:variables

}
