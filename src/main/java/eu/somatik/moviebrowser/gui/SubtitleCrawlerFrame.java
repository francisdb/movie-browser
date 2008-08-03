/*
 * SubtitleCrawlerFrame.java
 *
 * Created on 31 July 2008, 02:22
 */

package eu.somatik.moviebrowser.gui;

import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.URISyntaxException;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;

import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;

/**
 *
 * @author  rug
 */
public class SubtitleCrawlerFrame extends javax.swing.JFrame {

    private DefaultTableModel model;
    private static final Logger LOGGER = LoggerFactory.getLogger(SubtitleCrawlerFrame.class);
    private final HttpSourceLoader httpLoader;
    
    /** Creates new form SubtitleCrawlerFrame */
    public SubtitleCrawlerFrame(List<String> files, String imdbID, HttpSourceLoader httpLoader) {
        this.httpLoader = httpLoader;
        
        //Prepare table model
        String[] columnNames = {"Language", "File Name", "Type", "Source"};
        model = new DefaultTableModel(null, columnNames);
        
        initComponents();
        this.setTitle("Subtitle Crawler " + files.toString());
        crawl(files, imdbID);
        searchButton.setEnabled(false);
        
    };

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

        subtitlesTable.setModel(model);
        subtitlesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        subtitlesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                subtitlesTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subtitlesTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(subtitlesTable);
        subtitlesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        subtitlesTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        subtitlesTable.getColumnModel().getColumn(1).setMinWidth(20);
        subtitlesTable.getColumnModel().getColumn(2).setPreferredWidth(10);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(searchText, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton))
                    .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_searchButtonActionPerformed

private void subtitlesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subtitlesTableMouseClicked

}//GEN-LAST:event_subtitlesTableMouseClicked

private void subtitlesTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subtitlesTableMousePressed
if (evt.getClickCount() == 2) {
    String URL = (String) subtitlesTable.getValueAt(subtitlesTable.getSelectedRow(), subtitlesTable.convertColumnIndexToView(1));
    System.out.print(URL);
    if (SwingUtilities.isLeftMouseButton(evt)) {
        try {
            Desktop.getDesktop().browse(new URI(URL));
        } catch (IOException ex) {
            LOGGER.error("Could not open " + URL, ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Could not open " + URL, ex);
        }
    }
}
}//GEN-LAST:event_subtitlesTableMousePressed

public void crawl(List<String> files, String imdbID) {
    String fileName;
    Set<String[]> results = new HashSet<String[]>();
    Iterator<String> i = files.iterator();
    while(i.hasNext()) {
        fileName = (String) i.next();
        fileName = fileName.substring(0,(fileName.length()-4));
        if(fileName.endsWith(".")) {
            fileName = fileName.substring(0, (fileName.length()-1));
        }
        
        System.out.println(fileName);
        try {
            //Add other methods to get subs from other sources. 
        }
        catch (Exception ex) {
            LOGGER.error("Error retrieveng subtitle results. ", ex);
        }
    }
    
    try {
        //Get subtitlesource
        results.add(getSubtitleSourceResults(imdbID));
    }
    catch (Exception ex) {
         LOGGER.error("Error retrieveng subtitlesource.org results. ", ex);
    }
    
    Iterator<String[]> j = results.iterator();
    String[] result;
    while (j.hasNext()) {
        result = (String[]) j.next();
        model.addRow(result);
    }
    
    searchButton.setEnabled(true);
}

   public String[] getSubtitleSourceResults(String imdbID) throws IOException{
       String[] results = {"N/A", "http://www.subtitlesource.org/title/tt" + imdbID, "Various", "subtitlesource.org"};
       return  results;
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
