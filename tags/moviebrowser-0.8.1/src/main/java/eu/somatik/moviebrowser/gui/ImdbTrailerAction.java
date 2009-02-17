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

import com.flicklib.api.TrailerFinder;
import com.flicklib.domain.MovieService;
import com.flicklib.service.movie.imdb.ImdbTrailerFinder;
import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * This action  tries to show the trailer
 * @param evt
 */
class ImdbTrailerAction extends AbstractAction {

    MainFrame mainFrame;
    MovieBrowser browser;

    @Override
    public void actionPerformed(ActionEvent e) {
        MovieInfo info = mainFrame.getSelectedMovie();
        if (info!=null) {
            TrailerFinder finder = new ImdbTrailerFinder();
            String url = finder.findTrailerUrl(info.getMovie().getTitle(), info.siteFor(MovieService.IMDB).getIdForSite());
            if (url == null) {
                JOptionPane.showMessageDialog(mainFrame, "Could not find a trailer on www.imdb.com");
            } else {
                browser.openUrl(url);
            }
        }
    }

    public ImdbTrailerAction(MainFrame mainFrame, MovieBrowser browser) {
        super("IMDB Trailer", browser.getIconLoader().loadIcon("images/16/imdb.png"));
        this.mainFrame = mainFrame;
        this.browser = browser;
    }
}
