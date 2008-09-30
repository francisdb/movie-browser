package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * This action opens the SubtitleCrawlerFrame if a video file is found in the directory.
 */
class CrawlSubtitleAction extends AbstractAction {

    final MainFrame mainFrame;
    final MovieBrowser browser;

    public CrawlSubtitleAction(MainFrame mainFrame, MovieBrowser browser) {
        super("Subtitle Crawler", browser.getIconLoader().loadIcon("images/16/subtitles.png"));
        this.mainFrame = mainFrame;
        this.browser = browser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (browser.getMovieFinder().getRunningTasks() == 0) {
            List<String> files = new ArrayList<String>();
            MovieInfo info = mainFrame.getSelectedMovie();
            String alternateSearchKey = info.getMovie().getTitle();
            for (FileGroup fg : info.getMovie().getGroups()) {
                for (MovieLocation location : fg.getLocations()) {
                    File dir = new File(location.getPath());
                    if (!dir.isFile()) {
                        findFiles(dir, files);
                    }
                }
            }
            files.add(alternateSearchKey);
            openSubCrawler(files, info);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Subtitle crawling cannot be done while movie info is being loaded. \nPlease try again after all movie info is loaded.", "Loading Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    void findFiles(File dir, List<String> files) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                File child = file;
                for (File file2 : child.listFiles()) {
                    if (file2.isFile()) {
                        if (!file2.getName().toLowerCase().contains("sample")) {
                            if (mainFrame.getMovieFileFilter().accept(file2)) {
                                files.add(file2.getName());
                            }
                        }
                    }
                }
            } else {
                if (file.isFile()) {
                    if (!file.getName().toLowerCase().contains("sample")) {
                        if (mainFrame.getMovieFileFilter().accept(file)) {
                            files.add(file.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Loads SubtitleCrawlerFrame
     * @param fileName
     */
    private void openSubCrawler(List<String> file, MovieInfo movie) {
        SubtitleCrawlerFrame subtitleCrawler = new SubtitleCrawlerFrame(file, movie, browser.getSubtitlesLoader(), browser.getIconLoader());
        subtitleCrawler.setLocationRelativeTo(mainFrame.getMovieTableScrollPane());
        subtitleCrawler.setVisible(true);
    }
}
