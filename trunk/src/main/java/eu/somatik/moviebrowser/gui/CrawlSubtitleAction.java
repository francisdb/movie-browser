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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 * This action opens the SubtitleCrawlerFrame if a video file is found in the directory.
 */
class CrawlSubtitleAction extends AbstractAction {

    private final Component parent;
    private final MovieInfo info;
    private final MovieBrowser browser;

    public CrawlSubtitleAction(final MovieInfo info, final MovieBrowser browser, final Component parent) {
        super("Subtitle Crawler", browser.getIconLoader().loadIcon("images/16/subtitles.png"));
        this.browser = browser;
        this.parent = parent;
        this.info = info;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Set<String> files = new HashSet<String>();
        for (FileGroup fg : info.getMovie().getGroups()) {
            for (MovieLocation location : fg.getLocations()) {
                // FIXME do we realy need to crawl the folder again?
                File locationFile = new File(location.getPath());
                files.add(locationFile.getName());
            }
        }
        files.add(info.getMovie().getTitle());

        SubtitleCrawlerFrame subtitleCrawler = new SubtitleCrawlerFrame(files, info, browser.getSubtitlesLoader(), browser.getIconLoader());
        subtitleCrawler.setLocationRelativeTo(parent);
        subtitleCrawler.setVisible(true);
    }

}
