/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.gui;

import com.flicklib.api.TrailerFinder;
import com.flicklib.service.movie.apple.AppleTrailerFinder;
import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * This action tries to show the apple trailer site
 * 
 * @author zsombor
 */
public class AppleTrailerAction extends AbstractAction {

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
            TrailerFinder finder = new AppleTrailerFinder();
            String url = finder.findTrailerUrl(info.getMovie().getTitle(), null);
            if (url == null) {
                JOptionPane.showMessageDialog(mainFrame, "Could not find a trailer on www.apple.com");
            } else {
                browser.openUrl(url);
            }
        }
    }
}
