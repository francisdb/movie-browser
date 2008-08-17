/*
 * SettingsFrame.java
 *
 * Created on August 17, 2008, 10:15 AM
 */

package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.gui.MainFrame;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  rug
 */
public class SettingsFrame extends javax.swing.JFrame {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private final Settings settings;
    private final MainFrame mainFrame;
    private File selectedFile;
    private DefaultListModel model;
    
    /** Creates new form SettingsFrame */
    public SettingsFrame(final Settings settings,
                         final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.settings = settings;
        initComponents();
        getMovieLocations();
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

        jTextField1.setText("jTextField1");

        setTitle("Settings");
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

        omdbCheckBox.setText("OMDB");
        omdbCheckBox.setToolTipText("Select this to get information from www.omdb.org");

        googleCheckBox.setSelected(true);
        googleCheckBox.setText("Google");
        googleCheckBox.setToolTipText("Select this to get information from www.google.com");

        moviewebCheckBox.setSelected(true);
        moviewebCheckBox.setText("Movie Web");
        moviewebCheckBox.setToolTipText("Select this to get information from www.movieweb.com");

        flixsterCheckBox.setSelected(true);
        flixsterCheckBox.setText("Flixster");
        flixsterCheckBox.setToolTipText("Select this to get information from www.flixster.com");

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

        miscLabel.setText("Miscellaneous settings.");

        renameTitlesCheckBox.setText("Automatically rename movie folder to IMDB title.");
        renameTitlesCheckBox.setToolTipText("Select this to automatically rename movie directories to the IMDB title matched by Movie Browser when parsing.");

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(deleteLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                    .addComponent(googleCheckBox))))))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(subSourceCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(openSubsCheckBox))
                    .addComponent(subtitlesLabel))
                .addContainerGap(98, Short.MAX_VALUE))
            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(movieLocationsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(miscLabel)
                .addContainerGap(219, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(renameTitlesCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(timeoutLabel)
                        .addGap(7, 7, 7)
                        .addComponent(timeoutText, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondsLabel)))
                .addContainerGap(26, Short.MAX_VALUE))
            .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(292, Short.MAX_VALUE)
                .addComponent(okayButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                    .addComponent(googleCheckBox))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okayButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okayButtonActionPerformed

    private void addLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLocationButtonActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            settings.addFolder(newFolder);
            selectedFile = newFolder;
            mainFrame.fillTable();
            getMovieLocations();
        } else {
            LOGGER.debug("No Selection ");
        }
    }//GEN-LAST:event_addLocationButtonActionPerformed

    private void deleteLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLocationButtonActionPerformed

        model.removeElementAt(locationsList.getSelectedIndex());
        
        Set<String> folders = new HashSet<String>();
        for(int i=0; i<locationsList.getModel().getSize(); i++) {
            folders.add(locationsList.getModel().getElementAt(i).toString());
        }
        settings.saveFolders(folders);
        mainFrame.fillTable();
    }//GEN-LAST:event_deleteLocationButtonActionPerformed
    
    private void getMovieLocations() {
        model = new DefaultListModel();
        locationsList.removeAll();
        Iterator x;
        x = settings.loadFolders().iterator();
        
        int i=1;
        while(x.hasNext()) {
            String value = (String) x.next();
            model.addElement(value);
        }
        
        locationsList.setModel(model);
    }
    
    public boolean getRenameTitles() {
        return renameTitlesCheckBox.isSelected();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLocationButton;
    private javax.swing.JButton deleteLocationButton;
    private javax.swing.JCheckBox flixsterCheckBox;
    private javax.swing.JCheckBox googleCheckBox;
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
    private javax.swing.JCheckBox renameTitlesCheckBox;
    private javax.swing.JCheckBox rottenTomatoesCheckBox;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JCheckBox subSourceCheckBox;
    private javax.swing.JLabel subtitlesLabel;
    private javax.swing.JLabel timeoutLabel;
    private javax.swing.JTextField timeoutText;
    private javax.swing.JLabel websitesLabel;
    // End of variables declaration//GEN-END:variables
    
}
