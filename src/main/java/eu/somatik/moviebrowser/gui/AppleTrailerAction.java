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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 * This action tries to show the apple trailer site
 * 
 * @author zsombor
 */
class AppleTrailerAction extends AbstractAction {

    final MainFrame mainFrame;
    final MovieBrowser browser;

    public AppleTrailerAction(MainFrame main, MovieBrowser browser) {
        super("Apple Trailer", browser.getIconLoader().loadIcon("images/16/apple.png"));
        this.mainFrame = main;
        this.browser = browser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MovieInfo info = mainFrame.getSelectedMovie();
        if (info!=null) {
            String url = browser.getTrailers().findTrailerUrl(info.getMovie().getTitle(), null);
            if (url == null) {
                JOptionPane.showMessageDialog(mainFrame, "Could not find a trailer on www.apple.com");
            } else {
                browser.openUrl(url);
            }
        }
    }
}
