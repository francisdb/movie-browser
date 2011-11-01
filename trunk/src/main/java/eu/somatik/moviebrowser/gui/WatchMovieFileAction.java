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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.flicklib.folderscanner.MovieFileType;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

/**
 * This action opens the sample if a sample is found
 * @param evt
 */
class WatchMovieFileAction extends AbstractAction {

    final MainFrame mainFrame;
    final MovieBrowser browser;

    public WatchMovieFileAction(MainFrame mainFrame,MovieBrowser browser) {
        super("Video", browser.getIconLoader().loadIcon("images/16/video-display.png"));
        this.mainFrame = mainFrame;
        this.browser = browser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MovieInfo info = mainFrame.getSelectedMovie();
        if (info!=null) {
            StorableMovie movie = info.getMovie();
            for (FileGroup fg : movie.getGroups()) {
                for (MovieLocation location : fg.getLocations()) {
                    File directory = new File(location.getPath());
                    if (directory.exists() && directory.isDirectory()) {
                        for (StorableMovieFile file : fg.getFiles()) {
                            if (file.getType()==MovieFileType.VIDEO_CONTENT) {
                                File filePath = new File(directory, file.getName());
                                browser.openFile(filePath);
                                // I think, one file is enough to open.
                                return;
                            }
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(mainFrame, "No video found!");
        }
        
/*        
        File file = info.getDirectory();
        if (file.isDirectory()) {
            File[] movieFiles = file.listFiles(mainFrame.getMovieFileFilter());
            if (movieFiles.length > 0) {
                for (File movieFile : movieFiles) {
                    browser.openFile(movieFile);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "No video found");
            }
        } else if (mainFrame.getMovieFileFilter().accept(file)) {
            browser.openFile(file);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "No video found");
        }*/
    }
}
