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

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * This action opens the sample if a sample is found
 * @param evt
 */
class WatchSampleAction extends AbstractAction {


    final MainFrame mainFrame;
    final MovieBrowser browser;

    public WatchSampleAction(MainFrame mainFrame,MovieBrowser browser) {
        super("Sample", browser.getIconLoader().loadIcon("images/16/video-display.png"));
        this.mainFrame = mainFrame;
        this.browser = browser;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        MovieInfo info = mainFrame.getSelectedMovie();
        if (info!=null) {
            StorableMovie movie = info.getMovie();
            for (FileGroup filegroup : movie.getGroups()) {
                for (MovieLocation location : filegroup.getLocations()) {
                    File sample = browser.getFileSystemScanner().findSample(new File(location.getPath()));
                    if (sample!=null) {
                        browser.openFile(sample);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(mainFrame, "No sample found");
        }
    }
}
