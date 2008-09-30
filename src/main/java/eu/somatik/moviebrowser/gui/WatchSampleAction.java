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
