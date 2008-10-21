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

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.config.Settings;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import java.io.File;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  rug
 */
public class SettingsFrame extends javax.swing.JFrame {
    
    private static final MovieService[] SERVICES = new MovieService[]{  
        MovieService.IMDB,
        MovieService.PORTHU
    };
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private final Settings settings;
    private final MainFrame mainFrame;
    private File selectedFile;
    private DefaultListModel model;
    private boolean needRescan = false;
    
    /**
     * Creates new form SettingsFrame
     * @param settings
     * @param mainFrame 
     */
    public SettingsFrame(final Settings settings,
                         final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.settings = settings;
        initComponents();
        loadMovieLocations();
        getSettingsValues();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        locationsList = new javax.swing.JList();
        addLocationButton = new javax.swing.JButton();
        deleteLocationButton = new javax.swing.JButton();
        movieLocationsLabel = new javax.swing.JLabel();
        websitesLabel = new javax.swing.JLabel();
        rottenTomatoesCheckBox = new javax.swing.JCheckBox();
        omdbCheckBox = new javax.swing.JCheckBox();
        googleCheckBox = new javax.swing.JCheckBox();
        moviewebCheckBox = new javax.swing.JCheckBox();
        flixsterCheckBox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        subtitlesLabel = new javax.swing.JLabel();
        subSourceCheckBox = new javax.swing.JCheckBox();
        openSubsCheckBox = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        timeoutLabel = new javax.swing.JLabel();
        timeoutText = new javax.swing.JTextField();
        secondsLabel = new javax.swing.JLabel();
        miscLabel = new javax.swing.JLabel();
        renameTitlesCheckBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        okayButton = new javax.swing.JButton();
        saveCoverArtCheckBox = new javax.swing.JCheckBox();
        portHuCheckbox = new javax.swing.JCheckBox();
        preferSiteComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        setTitle("Settings");
        setIconImage(new ImageIcon(SettingsFrame.getFrames().getClass().getResource("/images/movie.png")).getImage());
        setResizable(false);

        locationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        locationsList.setToolTipText("Your Movie library locations.");
        jScrollPane1.setViewportView(locationsList);

        addLocationButton.setText("Add");
        addLocationButton.setToolTipText("Click here to add a folder. ");
        addLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLocationButtonActionPerformed(evt);
            }
        });

        deleteLocationButton.setText("Remove");
        deleteLocationButton.setToolTipText("Select a folder from the list on the left and click here to delete that folder.");
        deleteLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLocationButtonActionPerformed(evt);
            }
        });

        movieLocationsLabel.setText("Locations Movie Browser will look for your movie files.");

        websitesLabel.setText("Additional websites to use for information and ratings.");

        rottenTomatoesCheckBox.setSelected(true);
        rottenTomatoesCheckBox.setText("Rotten Tomatoes");
        rottenTomatoesCheckBox.setToolTipText("Select this to get information from www.rottentomatoes.com");
        rottenTomatoesCheckBox.setName("rottenttomatoes"); // NOI18N

        omdbCheckBox.setText("OMDB");
        omdbCheckBox.setToolTipText("Select this to get information from www.omdb.org");
        omdbCheckBox.setName("omdb"); // NOI18N

        googleCheckBox.setSelected(true);
        googleCheckBox.setText("Google");
        googleCheckBox.setToolTipText("Select this to get information from www.google.com");
        googleCheckBox.setName("google"); // NOI18N

        moviewebCheckBox.setSelected(true);
        moviewebCheckBox.setText("Movie Web");
        moviewebCheckBox.setToolTipText("Select this to get information from www.movieweb.com");
        moviewebCheckBox.setName("movieweb"); // NOI18N

        flixsterCheckBox.setSelected(true);
        flixsterCheckBox.setText("Flixster");
        flixsterCheckBox.setToolTipText("Select this to get information from www.flixster.com");
        flixsterCheckBox.setName("flixster"); // NOI18N

        subtitlesLabel.setText("Websites to use when crawling for subtitles.");

        subSourceCheckBox.setSelected(true);
        subSourceCheckBox.setText("Subtitle Source");
        subSourceCheckBox.setToolTipText("Check this to use www.subtitlesource.org");

        openSubsCheckBox.setSelected(true);
        openSubsCheckBox.setText("Open Subtitles");
        openSubsCheckBox.setToolTipText("Check this to use www.opensubtitles.org");

        timeoutLabel.setText("Timeout to use:");

        timeoutText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeoutText.setText("10");

        secondsLabel.setText("Seconds.");

        miscLabel.setText("Miscellaneous settings to use when parsing movie sites.");

        renameTitlesCheckBox.setText("Automatically rename movie folder to IMDB title.");
        renameTitlesCheckBox.setToolTipText("Select this to automatically rename movie directories to the IMDB title matched by Movie Browser when parsing.");

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });

        saveCoverArtCheckBox.setSelected(true);
        saveCoverArtCheckBox.setText("Save Cover Art in movie folder.");
        saveCoverArtCheckBox.setToolTipText("Select this option to automatically save cover art to the movie directory.");

        portHuCheckbox.setText("Port.hu");
        portHuCheckbox.setName("porthu"); // NOI18N

        preferSiteComboBox.setModel(getMovieServices());
        preferSiteComboBox.setToolTipText("Select the main site for information retrieval");

        jLabel1.setText("Preferred site:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(deleteLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)))
                    .addComponent(websitesLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rottenTomatoesCheckBox)
                            .addComponent(moviewebCheckBox))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(omdbCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(flixsterCheckBox))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(googleCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(portHuCheckbox)))))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(subSourceCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(openSubsCheckBox))
                    .addComponent(subtitlesLabel))
                .addContainerGap(187, Short.MAX_VALUE))
            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(movieLocationsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(miscLabel)
                .addContainerGap(101, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(379, Short.MAX_VALUE)
                .addComponent(okayButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preferSiteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(saveCoverArtCheckBox)
                    .addComponent(renameTitlesCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(timeoutLabel)
                        .addGap(7, 7, 7)
                        .addComponent(timeoutText, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondsLabel)))
                .addContainerGap(124, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(movieLocationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(addLocationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteLocationButton)))
                .addGap(11, 11, 11)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(websitesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rottenTomatoesCheckBox)
                    .addComponent(flixsterCheckBox)
                    .addComponent(omdbCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moviewebCheckBox)
                    .addComponent(googleCheckBox)
                    .addComponent(portHuCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subtitlesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subSourceCheckBox)
                    .addComponent(openSubsCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(miscLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(renameTitlesCheckBox)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeoutLabel)
                    .addComponent(timeoutText, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveCoverArtCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preferSiteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okayButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
        setSettingsValues();
        storeMovieLocations();
        if(needRescan) {
            mainFrame.scanFolders();
        }
        mainFrame.refreshColumns();
        this.setVisible(false);
    }//GEN-LAST:event_okayButtonActionPerformed

    private void addLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLocationButtonActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            if (!model.contains(newFolder.getAbsolutePath())) {
                model.addElement(newFolder.getAbsolutePath());
                selectedFile = newFolder;
                needRescan = true;
            }
        } else {
            LOGGER.debug("No Selection ");
        }
    }//GEN-LAST:event_addLocationButtonActionPerformed

    private void deleteLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLocationButtonActionPerformed

        model.removeElementAt(locationsList.getSelectedIndex());
        /*
        Set<String> folders = new LinkedHashSet<String>();
        for(int i=0; i<locationsList.getModel().getSize(); i++) {
            folders.add(locationsList.getModel().getElementAt(i).toString());
        }
        settings.saveFolders(folders);*/
        needRescan = true;

    }//GEN-LAST:event_deleteLocationButtonActionPerformed
    
    private void loadMovieLocations() {
        model = new DefaultListModel();
        for(String folder:settings.loadFolders()){
            model.addElement(folder);
        }
        locationsList.setModel(model);
    }
    
    private void storeMovieLocations() {
        Set<String> folders = new LinkedHashSet<String>();
        for(int i=0; i<locationsList.getModel().getSize(); i++) {
            folders.add(locationsList.getModel().getElementAt(i).toString());
        }
        settings.saveFolders(folders);
        
    }
    
    
    private Map<JCheckBox, MovieService> getCheckboxes() {
        Map<JCheckBox, MovieService> cbs = new HashMap<JCheckBox, MovieService>();
        cbs.put(omdbCheckBox, MovieService.OMDB);
        cbs.put(flixsterCheckBox, MovieService.FLIXSTER);
        cbs.put(googleCheckBox, MovieService.GOOGLE);
        cbs.put(moviewebCheckBox, MovieService.MOVIEWEB);
        cbs.put(portHuCheckbox, MovieService.PORTHU);
        cbs.put(rottenTomatoesCheckBox, MovieService.TOMATOES);
        return cbs;
    }
    
    private ComboBoxModel getMovieServices() {
        return new DefaultComboBoxModel(SERVICES);
    }
    
    
    
    private void getSettingsValues() {
        renameTitlesCheckBox.setSelected(settings.getRenameTitles());    
        saveCoverArtCheckBox.setSelected(settings.getSaveAlbumArt());
        for (Map.Entry<JCheckBox, MovieService> entry : getCheckboxes().entrySet()) {
            boolean value = settings.isServiceEnabled(entry.getValue().name().toLowerCase(), entry.getKey().isSelected());
            entry.getKey().setSelected(value);
        }
        preferSiteComboBox.setSelectedItem(settings.getPreferredService());
    }
    
    private void setSettingsValues() {
        settings.setRenameTitles(renameTitlesCheckBox.isSelected());
        settings.setSaveAlbumArt(saveCoverArtCheckBox.isSelected());
        for (Map.Entry<JCheckBox, MovieService> entry : getCheckboxes().entrySet()) {
            settings.setServiceEnabled(entry.getValue().name().toLowerCase(), entry.getKey().isSelected());
        }
        MovieService item = (MovieService) preferSiteComboBox.getSelectedItem();
        settings.setPreferredService(item);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLocationButton;
    private javax.swing.JButton deleteLocationButton;
    private javax.swing.JCheckBox flixsterCheckBox;
    private javax.swing.JCheckBox googleCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JList locationsList;
    private javax.swing.JLabel miscLabel;
    private javax.swing.JLabel movieLocationsLabel;
    private javax.swing.JCheckBox moviewebCheckBox;
    private javax.swing.JButton okayButton;
    private javax.swing.JCheckBox omdbCheckBox;
    private javax.swing.JCheckBox openSubsCheckBox;
    private javax.swing.JCheckBox portHuCheckbox;
    private javax.swing.JComboBox preferSiteComboBox;
    private javax.swing.JCheckBox renameTitlesCheckBox;
    private javax.swing.JCheckBox rottenTomatoesCheckBox;
    private javax.swing.JCheckBox saveCoverArtCheckBox;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JCheckBox subSourceCheckBox;
    private javax.swing.JLabel subtitlesLabel;
    private javax.swing.JLabel timeoutLabel;
    private javax.swing.JTextField timeoutText;
    private javax.swing.JLabel websitesLabel;
    // End of variables declaration//GEN-END:variables
    
}
