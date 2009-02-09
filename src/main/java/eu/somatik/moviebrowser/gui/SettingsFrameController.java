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
import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.config.Settings;
import java.awt.Component;
import java.io.File;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class SettingsFrameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsFrameController.class);

    private final Settings settings;
    private final MovieBrowser movieBrowser;
    private final SettingsFrame settingsFrame;

    private boolean needRescan;
    private File selectedFile;

    // TODO get rid of this
    private final MainFrame mainFrame;

    public SettingsFrameController(Settings settings, MovieBrowser movieBrowser, SettingsFrame settingsFrame, MainFrame mainFrame) {
        this.settingsFrame = settingsFrame;
        this.settingsFrame.setController(this);
        this.mainFrame = mainFrame;
        this.movieBrowser = movieBrowser;
        this.settings = settings;

        this.needRescan = true;
    }


    void load(Component componentToCenterOn){
        settingsFrame.setLocationRelativeTo(componentToCenterOn);
        loadMovieLocations();
        setSettingsValues();
        settingsFrame.setVisible(true);
    }

    void okayButtonPressed() {
        setSettingsValues();
        storeMovieLocations();
        if(needRescan) {
            mainFrame.scanFolders();
        }
        mainFrame.refreshColumns();
        settingsFrame.setVisible(false);
    }


    private void storeMovieLocations() {
        Set<String> folders = new LinkedHashSet<String>();
        Enumeration<String> enumeration = settingsFrame.getSelectedLocations();
        while (enumeration.hasMoreElements()) {
            folders.add(enumeration.nextElement());
        }
        settings.saveFolders(folders);
    }

    void addLocationButtonpressed(){
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(settingsFrame) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            if (!settingsFrame.hasMovieLocation(newFolder.getAbsolutePath())) {
                settingsFrame.addMovieLocation(newFolder.getAbsolutePath());
                selectedFile = newFolder;
                needRescan = true;
            }
        } else {
            LOGGER.debug("No Selection ");
        }
    }

    void deleteLocationPressed() {
        settingsFrame.removeMovieLocation(settingsFrame.getSelectedLocation());
        /*
        Set<String> folders = new LinkedHashSet<String>();
        for(int i=0; i<locationsList.getModel().getSize(); i++) {
            folders.add(locationsList.getModel().getElementAt(i).toString());
        }
        settings.saveFolders(folders);*/
    }


    private void loadMovieLocations() {
        settingsFrame.setMovieLocations(settings.loadFolders());
    }



    private void setSettingsValues() {
        settings.setRenameTitles(settingsFrame.isRenameTitlesSelected());
        settings.setSaveAlbumArt(settingsFrame.isSaveCoverArtSelected());



        for (Map.Entry<MovieService, Boolean> entry : settingsFrame.getServiceSelection().entrySet()) {
            settings.setServiceEnabled(entry.getKey(), entry.getValue());
        }
        MovieService item = settingsFrame.getSelectedPreferredSite();
        settings.setPreferredService(item);
    }

}
