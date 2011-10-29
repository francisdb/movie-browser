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

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.Services;
import eu.somatik.moviebrowser.config.Settings;

/**
 *
 * @author francisdb
 */
public class SettingsDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialogController.class);

    private static final Set<String> DEFAULT_SERVICES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                Services.GOOGLE, Services.IMDB, Services.TOMATOES, Services.MOVIEWEB, Services.FLIXSTER)));

    private final Settings settings;
    private final MovieBrowser movieBrowser;
    private final SettingsDialog settingsFrame;

    private boolean needRescan;
    private File selectedFile;

    // TODO get rid of this
    private final MainFrame mainFrame;

    public SettingsDialogController(Settings settings, MovieBrowser movieBrowser, SettingsDialog settingsFrame, MainFrame mainFrame) {
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
        loadSettingsValues();
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


    private void loadSettingsValues() {
        Map<String, Boolean> values = new HashMap<String, Boolean>();
        for (String service : settingsFrame.getServiceSelection().keySet()) {
            // TODO set default values for each service
            values.put(service, settings.isServiceEnabled(service, DEFAULT_SERVICES.contains(service)));
        }
        settingsFrame.setServiceSelection(values);
        settingsFrame.setSaveCoverArtSelected(settings.getSaveAlbumArt());
    }

    private void setSettingsValues() {
        settings.setRenameTitles(settingsFrame.isRenameTitlesSelected());
        settings.setSaveAlbumArt(settingsFrame.isSaveCoverArtSelected());
        for (Map.Entry<String, Boolean> entry : settingsFrame.getServiceSelection().entrySet()) {
            settings.setServiceEnabled(entry.getKey(), entry.getValue());
        }
        MovieService item = settingsFrame.getSelectedPreferredSite();
        settings.setPreferredService(item);
    }

}
