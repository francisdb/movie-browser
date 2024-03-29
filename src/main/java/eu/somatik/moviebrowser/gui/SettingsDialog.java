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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.Services;


/**
 *
 * @author  rug
 */
public class SettingsDialog extends javax.swing.JDialog {

    //TODO move this to the correct location
    public static final String[] MAIN_SERVICES = new String[]{
        Services.IMDB,
        Services.PORTHU,
        Services.CINEBEL,
        Services.OFDB
    };
    
    private final IconLoader iconLoader;
    private final Map<String, JCheckBox> serviceCheckBoxes;

    private SettingsDialogController controller;
    
    /**
     * Creates new form SettingsDialog
     * @param settings
     * @param mainFrame
     * @param iconLoader 
     */
    public SettingsDialog(final MovieService preferredService,
                         final IconLoader iconLoader) {
        this.iconLoader = iconLoader;
        initComponents();
        this.serviceCheckBoxes = mapServiceCheckBoxes();
        this.locationsList.setModel(new DefaultListModel());
        this.preferSiteComboBox.setSelectedItem(preferredService);
    }

    private Map<String, JCheckBox> mapServiceCheckBoxes(){
        Map<String, JCheckBox> cbs = new HashMap<String, JCheckBox>();
        //cbs.put(MovieService.OMDB, omdbCheckBox);
        cbs.put(Services.FLIXSTER, flixsterCheckBox);
        cbs.put(Services.GOOGLE, googleCheckBox);
        cbs.put(Services.MOVIEWEB, moviewebCheckBox);
        cbs.put(Services.PORTHU, portHuCheckbox);
        cbs.put(Services.TOMATOES, rottenTomatoesCheckBox);
        cbs.put(Services.CINEBEL, cinebelCheckBox);
        cbs.put(Services.OFDB, ofdbCheckBox);
        cbs.put(Services.XPRESSHU, xpressHuCheckBox);
        cbs.put(Services.BLIPPR, blipprCheckBox);
        return cbs;
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
        blipprCheckBox = new javax.swing.JCheckBox();
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
        cinebelCheckBox = new javax.swing.JCheckBox();
        ofdbCheckBox = new javax.swing.JCheckBox();
        xpressHuCheckBox = new javax.swing.JCheckBox();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setIconImage(iconLoader.loadIcon("images/movie.png").getImage());
        setModal(true);
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

        rottenTomatoesCheckBox.setText("Rotten Tomatoes");
        rottenTomatoesCheckBox.setToolTipText("Select this to get information from www.rottentomatoes.com");
        rottenTomatoesCheckBox.setName("rottenttomatoes"); // NOI18N

        omdbCheckBox.setText("OMDB");
        omdbCheckBox.setToolTipText("Select this to get information from www.omdb.org");
        omdbCheckBox.setName("omdb"); // NOI18N

        googleCheckBox.setText("Google");
        googleCheckBox.setToolTipText("Select this to get information from www.google.com");
        googleCheckBox.setName("google"); // NOI18N

        moviewebCheckBox.setText("Movie Web");
        moviewebCheckBox.setToolTipText("Select this to get information from www.movieweb.com");
        moviewebCheckBox.setName("movieweb"); // NOI18N

        flixsterCheckBox.setText("Flixster");
        flixsterCheckBox.setToolTipText("Select this to get information from www.flixster.com");
        flixsterCheckBox.setName("flixster"); // NOI18N
        
        blipprCheckBox.setText("Blippr");
        blipprCheckBox.setToolTipText("Select this to get information from www.blippr.com");
        blipprCheckBox.setName("blippr"); // NOI18N

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

        renameTitlesCheckBox.setText("Automatically rename movie folder");
        renameTitlesCheckBox.setToolTipText("Select this to automatically rename movie directories to the IMDB title matched by Movie Browser when parsing.");

        okayButton.setText("Save");
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

        cinebelCheckBox.setText("Cinebel");

        ofdbCheckBox.setText("OFDb");

        xpressHuCheckBox.setText("Xpress.hu");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(addLocationButton, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                .addComponent(deleteLocationButton, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)))
                        .addComponent(websitesLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(xpressHuCheckBox)
                                .addComponent(rottenTomatoesCheckBox)
                                .addComponent(moviewebCheckBox))
                            .addGap(12)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(blipprCheckBox)
                                .addComponent(googleCheckBox)
                                .addComponent(omdbCheckBox))
                            .addGap(70)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(portHuCheckbox)
                                .addComponent(flixsterCheckBox))
                            .addGap(31)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(ofdbCheckBox)
                                .addComponent(cinebelCheckBox))
                            .addGap(71)))
                    .addContainerGap())
                .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12)
                            .addComponent(subSourceCheckBox)
                            .addGap(18)
                            .addComponent(openSubsCheckBox))
                        .addComponent(subtitlesLabel))
                    .addContainerGap(274, Short.MAX_VALUE))
                .addComponent(jSeparator3, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(movieLocationsLabel, GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addGap(21)
                    .addComponent(miscLabel)
                    .addContainerGap(181, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(509, Short.MAX_VALUE)
                    .addComponent(okayButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addComponent(jSeparator4, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(29)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(saveCoverArtCheckBox)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(preferSiteComboBox, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
                        .addComponent(renameTitlesCheckBox)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(1)
                            .addComponent(timeoutLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(timeoutText, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(secondsLabel)))
                    .addContainerGap(306, Short.MAX_VALUE))
                .addComponent(jSeparator2, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(moviewebCheckBox)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                .addComponent(blipprCheckBox)
                                .addComponent(xpressHuCheckBox)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(movieLocationsLabel, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18)
                                    .addComponent(addLocationButton)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(deleteLocationButton)))
                            .addGap(11)
                            .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(websitesLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(rottenTomatoesCheckBox)
                                .addComponent(omdbCheckBox)
                                .addComponent(cinebelCheckBox)
                                .addComponent(flixsterCheckBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(googleCheckBox)
                                .addComponent(ofdbCheckBox)
                                .addComponent(portHuCheckbox))
                            .addGap(18)
                            .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 4, GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(subtitlesLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(subSourceCheckBox)
                        .addComponent(openSubsCheckBox))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addGap(3)
                    .addComponent(miscLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(renameTitlesCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(saveCoverArtCheckBox)
                    .addGap(12)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(timeoutLabel)
                        .addComponent(timeoutText, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addComponent(secondsLabel))
                    .addGap(11)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(preferSiteComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addPreferredGap(ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                    .addComponent(jSeparator4, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(okayButton)
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
        controller.okayButtonPressed();
    }//GEN-LAST:event_okayButtonActionPerformed

    private void addLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLocationButtonActionPerformed
        controller.addLocationButtonpressed();
    }//GEN-LAST:event_addLocationButtonActionPerformed

    private void deleteLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLocationButtonActionPerformed
        controller.deleteLocationPressed();
    }//GEN-LAST:event_deleteLocationButtonActionPerformed


    private ComboBoxModel getMovieServices() {
        return new DefaultComboBoxModel(MAIN_SERVICES);
    }
    
    public Map<String, Boolean> getServiceSelection() {
        Map<String, Boolean> cbs = new HashMap<String, Boolean>();
        for(Map.Entry<String, JCheckBox> entry:serviceCheckBoxes.entrySet()){
            cbs.put(entry.getKey(), entry.getValue().isSelected());
        }
        return cbs;
    }

    public void setServiceSelection(Map<String, Boolean> selection) {
        for(Map.Entry<String, Boolean> entry:selection.entrySet()){
            serviceCheckBoxes.get(entry.getKey()).setSelected(entry.getValue());
        }
    }

    public boolean isRenameTitlesSelected() {
        return renameTitlesCheckBox.isSelected();
    }

    public boolean isSaveCoverArtSelected() {
        return saveCoverArtCheckBox.isSelected();
    }

    public void setSaveCoverArtSelected(boolean selected) {
        saveCoverArtCheckBox.setSelected(selected);
    }
    
    public MovieService getSelectedPreferredSite(){
        return (MovieService) preferSiteComboBox.getSelectedItem();
    }

    public String getSelectedLocation(){
        return (String) locationsList.getSelectedValue();
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getSelectedLocations(){
        return (Enumeration<String>) ((DefaultListModel)locationsList.getModel()).elements();
    }

    public void setController(SettingsDialogController controller) {
        this.controller = controller;
    }

    public void setMovieLocations(Set<String> locations){
        for(String location:locations){
            ((DefaultListModel)this.locationsList.getModel()).addElement(location);
        }
    }

    public void addMovieLocation(String location){
        ((DefaultListModel)this.locationsList.getModel()).addElement(location);
    }

    public void removeMovieLocation(String location){
        ((DefaultListModel)this.locationsList.getModel()).removeElement(location);
    }

    public boolean hasMovieLocation(String location){
        return ((DefaultListModel)this.locationsList.getModel()).contains(location);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLocationButton;
    private javax.swing.JCheckBox cinebelCheckBox;
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
    private javax.swing.JCheckBox blipprCheckBox;
    private javax.swing.JCheckBox ofdbCheckBox;
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
    private javax.swing.JCheckBox xpressHuCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
