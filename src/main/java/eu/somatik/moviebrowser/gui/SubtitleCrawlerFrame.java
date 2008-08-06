/*
 * SubtitleCrawlerFrame.java
 *
 * Created on 31 July 2008, 02:22
 */
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.api.SubtitlesLoader;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.awt.Desktop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import eu.somatik.moviebrowser.domain.Subtitle;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
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

    /** Creates new form SubtitleCrawlerFrame
     * @param files
     * @param imdbID
     * @param subtitlesLoader
     * @param iconLoader 
     */
    public SubtitleCrawlerFrame(List<String> files, String imdbID, final SubtitlesLoader subtitlesLoader, final IconLoader iconLoader) {
        this.subtitlesLoader = subtitlesLoader;
        this.model = new SubtitleTableModel();

        initComponents();
        this.setTitle("Subtitle Crawler " + files.toString());
        crawl(files, imdbID);
        searchButton.setEnabled(false);

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

    public void crawl(final List<String> files, final String imdbID) {
        statusProgressBar.setIndeterminate(true);
        new SwingWorker<Set<Subtitle>,Void>() {

            @Override
            protected Set<Subtitle> doInBackground() throws Exception {
                String fileName;
                Set<Subtitle> results = new HashSet<Subtitle>();
                Iterator<String> i = files.iterator();
                while (i.hasNext()) {
                    fileName = i.next();
                    // regex . is any character
                    String split[] = fileName.split("\\.");
                    if (split.length != 0) {
                        fileName = split[0];
                    }

                    LOGGER.info("fileName = " + fileName);
                    try {
                        //Add other methods to get subs from other sources.
                        results.addAll(subtitlesLoader.getOpenSubsResults(fileName));
                    } catch (IOException ex) {
                        LOGGER.error("Exception while fetching subtitles", ex);
                    }

                }

                if (results.size() == 0) {
                    try {
                        //Get subtitlesource
                        results.add(makeDummyEntry(imdbID));
                    } catch (Exception ex) {
                        LOGGER.error("Error retrieveng subtitlesource.org results. ", ex);
                    }
                }
                return results;
            }

            @Override
            protected void done() {
                try {
                    Set<Subtitle> subtitles = get();
                    for (Subtitle subtitle:subtitles) {
                        model.add(subtitle);
                    }
                    searchButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    LOGGER.error("Worker interrupted", ex);
                } catch (ExecutionException ex) {
                    LOGGER.error("Fetching subs failed", ex.getCause());
                } finally {
                    statusProgressBar.setIndeterminate(false);
                }
            }

        }.execute();

    }

    public Subtitle makeDummyEntry(String imdbID) throws IOException {
        Subtitle sub = new Subtitle();
        sub.setFileName("http://www.subtitlesource.org/title/tt" + imdbID);
        sub.setSubSource("SubtitleSource.org");
        sub.setLanguage("Various");
        sub.setNoCd("N/A");
        sub.setType("N/A");
        return sub;
    }
    
    private static final class LangIconRenderer extends DefaultTableCellRenderer{
        
        private final Map<String, ImageIcon> iconCache;
        private final IconLoader iconLoader;
        
        
        public LangIconRenderer(final IconLoader iconLoader) {
            this.iconLoader = iconLoader;
            this.iconCache = new HashMap<String, ImageIcon>();
        }
        
        private ImageIcon getIcon(final String lang){
            ImageIcon icon = iconCache.get(lang);
            if(icon == null && !iconCache.containsKey(lang)){
                icon = iconLoader.loadIcon("images/flags/"+lang+".png");
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
