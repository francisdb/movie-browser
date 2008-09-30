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
