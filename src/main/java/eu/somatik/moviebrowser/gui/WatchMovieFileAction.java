package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

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
                            if (file.getType()==FileType.VIDEO_CONTENT) {
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
