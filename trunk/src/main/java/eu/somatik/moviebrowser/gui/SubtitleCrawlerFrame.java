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

import com.flicklib.api.SubtitlesLoader;
import com.flicklib.domain.MovieService;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.awt.Desktop;

import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.flicklib.domain.Subtitle;
import com.flicklib.service.sub.SubtitleSourceLoader;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.tools.SwingTools;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author  rug
 */
public class SubtitleCrawlerFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubtitleCrawlerFrame.class);
    private final SubtitleTableModel model;
    private final SubtitlesLoader subtitlesLoader;
    private final SubtitlesLoader subtitlesLoader2;
    private final MovieInfo movie;

    /** Creates new form SubtitleCrawlerFrame
     * @param files
     * @param movie 
     * @param subtitlesLoader
     * @param iconLoader 
     */
    public SubtitleCrawlerFrame(List<String> files, MovieInfo movie, final SubtitlesLoader subtitlesLoader, final IconLoader iconLoader) {
        this.subtitlesLoader = subtitlesLoader;
        this.subtitlesLoader2 = new SubtitleSourceLoader();
        this.model = new SubtitleTableModel();
        this.movie = movie;
        this.setIconImage(iconLoader.loadIcon("images/32/video-x-generic.png").getImage());
        initComponents();
        this.setTitle("Subtitle Crawler " + files.toString());
        crawl(files, movie);
        searchButton.setEnabled(false);

        subtitlesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        subtitlesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subtitlesTable.getColumn(SubtitleTableModel.LANG_COL).setCellRenderer(new LangIconRenderer(iconLoader));
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

        statusProgressBar.setForeground(new java.awt.Color(0, 153, 51));
        statusProgressBar.setString("");
        statusProgressBar.setStringPainted(true);

        infoLabel.setText("Double click on the subtitle you require below to begin download. Use the search box to refine your search.");

        subtitlesTable.setAutoCreateRowSorter(true);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                    .addComponent(statusProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchText, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton))
                    .addComponent(infoLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    String searchKey = searchText.getText();
    List<String> searchList = new ArrayList<String>();
    searchList.add(searchKey);
    model.clear();
    crawl(searchList, movie);
}//GEN-LAST:event_searchButtonActionPerformed

private void subtitlesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subtitlesTableMouseClicked
}//GEN-LAST:event_subtitlesTableMouseClicked

private void subtitlesTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subtitlesTableMousePressed
    if (evt.getClickCount() == 2) {
        Subtitle sub = model.getSubtitle(subtitlesTable.convertRowIndexToModel(subtitlesTable.getSelectedRow()));
        System.out.print(sub.getFileUrl());
        if (SwingUtilities.isLeftMouseButton(evt)) {
            try {
                Desktop.getDesktop().browse(new URI(sub.getFileUrl()));
            } catch (IOException ex) {
                LOGGER.error("Could not open " + sub.getFileUrl(), ex);
            } catch (URISyntaxException ex) {
                LOGGER.error("Could not open " + sub.getFileUrl(), ex);
            }
        }
    }
}//GEN-LAST:event_subtitlesTableMousePressed

    public void crawl(final List<String> files, final MovieInfo movie) {
        statusProgressBar.setIndeterminate(true);
        statusProgressBar.setString("Crawling sites for subtitles. This may take a while...");
        new SwingWorker<List<Subtitle>,Void>() {

            @Override
            protected List<Subtitle> doInBackground() throws Exception {
                String fileName;
                List<Subtitle> results = new ArrayList<Subtitle>();
                String imdbId = movie.siteFor(MovieService.IMDB).getIdForSite();
                results.addAll(subtitlesLoader2.search(movie.getMovieFile().getMovie().getTitle(), imdbId));
                
                Iterator<String> i = files.iterator();
                while (i.hasNext()) {
                    fileName = i.next();
                    
                    // remove extension
                    int lastDot = fileName.lastIndexOf('.');
                    if(lastDot > 0){
                        fileName = fileName.substring(0, lastDot);
                    }
                    LOGGER.info("fileName = " + fileName);
                    try {
                        //Add other methods to get subs from other sources.
                        results.addAll(subtitlesLoader.search(fileName, imdbId));
                    } catch (IOException ex) {
                        LOGGER.error("Exception while fetching subtitles", ex);
                    }
                }
                

                return results;
            }

            @Override
            protected void done() {
                try {
                    List<Subtitle> subtitles = get();
                    model.addAll(subtitles);
                    SwingTools.packColumns(subtitlesTable, 3);
                    searchButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    LOGGER.error("Worker interrupted", ex);
                } catch (ExecutionException ex) {
                    LOGGER.error("Fetching subs failed", ex.getCause());
                } finally {
                    statusProgressBar.setIndeterminate(false);
                    statusProgressBar.setString(model.getRowCount() + " Results Found.");
                }
            }

        }.execute();

    }


    
    private static final class LangIconRenderer extends DefaultTableCellRenderer{
        
        private static final Map<String,String> LANG_COUNTRY = new HashMap<String, String>();
        static {
            LANG_COUNTRY.put("da", "dk");
            LANG_COUNTRY.put("pb", "br");//?
            LANG_COUNTRY.put("el", "gr");
            LANG_COUNTRY.put("en", "gb");
            LANG_COUNTRY.put("fa", "ir");
            LANG_COUNTRY.put("zh", "cn");
            LANG_COUNTRY.put("he", "il");
            LANG_COUNTRY.put("ko", "kr");
            LANG_COUNTRY.put("uk", "ua");
            LANG_COUNTRY.put("po", "br");//?
        }
        
        private final Map<String, ImageIcon> iconCache;
        private final IconLoader iconLoader;
        
        
        public LangIconRenderer(final IconLoader iconLoader) {
            this.iconLoader = iconLoader;
            this.iconCache = new HashMap<String, ImageIcon>();
        }
        
        private ImageIcon getIcon(final String lang){
            ImageIcon icon = iconCache.get(lang);
            if(icon == null && !iconCache.containsKey(lang)){
                String country = LANG_COUNTRY.get(lang);
                if(country == null){
                    country = lang;
                }
                // TODO don't save the same flags twice
                icon = iconLoader.loadIcon("images/flags/"+country+".png");
                iconCache.put(lang, icon);
            }
            return icon;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String lang = (String) value;
            setIcon(getIcon(lang));
            return this;
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
