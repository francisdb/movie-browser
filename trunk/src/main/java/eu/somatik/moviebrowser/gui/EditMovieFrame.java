/*
 * EditMovieFrame.java
 *
 * Created on 17 July 2008, 22:21
 */
package eu.somatik.moviebrowser.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.ImageIcon;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.lang.Exception;


import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieInfo;
import eu.somatik.moviebrowser.service.MovieFinder;
import com.flicklib.service.movie.imdb.ImdbSearch;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  rug
 */
public class EditMovieFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditMovieFrame.class);
    private final ImdbSearch imdbSearch;
    private final DefaultListModel listModel;
    private final MovieInfo movieInfo;
    private final MovieFinder movieFinder;

    /** 
     * Creates new form EditMovieFrame
     * @param movieInfo 
     * @param imdbSearch
     * @param movieFinder 
     */
    public EditMovieFrame(MovieInfo movieInfo, ImdbSearch imdbSearch, MovieFinder movieFinder) {
        this.imdbSearch = imdbSearch;
        this.movieFinder = movieFinder;
        this.movieInfo = movieInfo;
        File file = new File(movieInfo.getMovie().getPath());
        String searchkey = file.getName();


        this.listModel = new DefaultListModel();
        initComponents();
        searchTextField.setText(searchkey);
        resultsList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    resultsListDoubleClick();
                }
            }
        });
        resultsList.setCellRenderer(new MovieListCellRenderer());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchTextField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsList = new javax.swing.JList();
        updateButton = new javax.swing.JButton();
        statusProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Movie");
        setIconImage(new ImageIcon(EditMovieFrame.getFrames().getClass().getResource("/images/movie.png")).getImage());

        searchLabel.setText("Look for:");

        searchButton.setText("Find");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resultsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resultsList.setToolTipText("Double click on results to go to IMDB page.");
        resultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultsListMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resultsListMouseReleased(evt);
            }
        });
        resultsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                resultsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(resultsList);

        updateButton.setText("Update");
        updateButton.setToolTipText("Select the correct title from results and click me to update cache.");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        statusProgressBar.setForeground(new java.awt.Color(255, 153, 51));
        statusProgressBar.setString("");
        statusProgressBar.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateButton)
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(searchLabel)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    statusProgressBar.setIndeterminate(true);
    statusProgressBar.setString("Searching...");

    SwingWorker<List<Movie>, Void> worker =
            new SwingWorker<List<Movie>, Void>() {

                @Override
                public List<Movie> doInBackground() throws Exception {
                    return imdbSearch.getResults(searchTextField.getText().trim());
                }

                @Override
                public void done() {
                    try {
                        showResults(get());
                    } catch (InterruptedException ex) {
                        LOGGER.error("Get request intterrupted: ", ex);
                        statusProgressBar.setIndeterminate(false);
                        statusProgressBar.setString("Error Retrieving Results");
                    } catch (ExecutionException ex) {
                        LOGGER.error("Get request failed: ", ex.getCause());
                        statusProgressBar.setIndeterminate(false);
                        statusProgressBar.setString("Error Retrieving Results");
                    }
                }
            };
    worker.execute();
}//GEN-LAST:event_searchButtonActionPerformed

private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
    Movie selectedMovie = (Movie) resultsList.getSelectedValue();
    if (selectedMovie == null) {
        JOptionPane.showMessageDialog(EditMovieFrame.this, "No movie selected");
    } else {
        movieInfo.setImage(null);
        movieInfo.getMovie().setImgUrl(null);
        movieInfo.getMovie().setPlot(null);
        movieInfo.getMovie().setImdbId(selectedMovie.getImdbId());
        movieInfo.getMovie().setImdbUrl(selectedMovie.getImdbUrl());
        movieFinder.reloadMovie(movieInfo);
        this.dispose();
    }

}//GEN-LAST:event_updateButtonActionPerformed

private void resultsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsListMouseClicked
}//GEN-LAST:event_resultsListMouseClicked

private void resultsListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsListMouseReleased
}//GEN-LAST:event_resultsListMouseReleased

private void resultsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_resultsListValueChanged
    Movie movie = (Movie) resultsList.getSelectedValue();   
    String tooltip = "You have selected " + movie.getTitle();
    resultsList.setToolTipText(tooltip + ". Double click selection to go to the IMDB page or click Update button to update.");
    updateButton.setToolTipText(tooltip + ". Click here to update.");
}//GEN-LAST:event_resultsListValueChanged

private void resultsListDoubleClick() {
    Movie movie = (Movie) resultsList.getSelectedValue();
    String url = movie.getImdbUrl();
    try {
        Desktop.getDesktop().browse(new URI(url));
    } catch (URISyntaxException ex) {
        LOGGER.error("Failed launching default browser for " + url, ex);
    } catch (IOException ex) {
        LOGGER.error("Failed launching default browser for " + url, ex);
    }   
}    

 
 private void showResults(List<Movie> movies){
     
        listModel.clear();
        for(Movie movie : movies){
            listModel.addElement(movie);
        }
        
        resultsList.setModel(listModel);
        statusProgressBar.setIndeterminate(false);
        if(listModel.isEmpty()) {
            statusProgressBar.setString("No Results Found");
        } else {
            statusProgressBar.setString(listModel.getSize() + " Results Found");
        }
 }
 
     private static class MovieListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Movie movie = (Movie) value;
            setText(movie.getTitle()+" ("+movie.getYear()+")");
            return this;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList resultsList;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JProgressBar statusProgressBar;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables



}
