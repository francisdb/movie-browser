/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.gui;

import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.MovieSearchResult;
import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zsombor
 */
public class ImportDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDialogController.class);
    ImportDialog dialog;
    File scanDirectory;
    MovieBrowser browser;
    List<MovieInfo> movies;
    MovieInfo currentMovieInfo;
    Map<MovieInfo,MovieSearchResult> selectedResults = new HashMap<MovieInfo,MovieSearchResult>();
    Map<MovieInfo,MovieService> lastService = new HashMap<MovieInfo,MovieService>();
    Map<MovieInfo,List<MovieSearchResult>> lastSearchResults= new HashMap<MovieInfo,List<MovieSearchResult>>();

    MovieInfoTableModel tableModel;

    public ImportDialogController(MovieBrowser browser, ImportDialog dialog, File selectedFile, MovieInfoTableModel model) {
        this.dialog = dialog;
        this.dialog.setController(this);
        this.scanDirectory = selectedFile;
        this.browser = browser;
        this.tableModel = model;
    }

    public void startImporting() {
        new SwingWorker<List<MovieInfo>, Void>() {

            @Override
            protected List<MovieInfo> doInBackground() throws Exception {
                LOGGER.info("scanning in " + scanDirectory.getAbsolutePath());
                return browser.getFolderScanner().scan(Collections.singleton(scanDirectory.getAbsolutePath()));
            }

            @Override
            protected void done() {
                try {
                    movies = get();

                    if (movies.size() > 0) {
                        dialog.setImportFolderPath(scanDirectory.getAbsolutePath());
                        dialog.setFolderLabel(scanDirectory.getName());
                        initDialogWithMovieInfo(0);
                        dialog.setVisible(true);
                    } else {
                        dialog.showMessageDialog("Unable to locate any movies in " + scanDirectory.getAbsolutePath(), "No movies found!");
                    }
                } catch (InterruptedException ex) {
                    LOGGER.error("Loading interrupted", ex);
                } catch (ExecutionException ex) {
                    LOGGER.error("Loading failed", ex.getCause());
                }
            }
        }.execute();

    }

    private void initDialogWithMovieInfo(int pos) {
        currentMovieInfo = movies.get(pos);
        dialog.clearMovieSuggestions();
        StorableMovie mv = currentMovieInfo.getMovie();
        dialog.setMovieTitle(mv.getTitle());
        FileGroup fg = mv.getUniqueFileGroup();
        String path = fg.getDirectoryPath();
        if (path.length()>scanDirectory.getAbsolutePath().length() + 1) {
            dialog.setPathToMovie(path.substring(scanDirectory.getAbsolutePath().length() + 1));
        } else {
            dialog.setPathToMovie("");
        }
        dialog.setRelatedFiles(fg.getFiles());


        dialog.setEnableNextButton(pos < movies.size() - 1);
        dialog.setEnablePrevButton(0 < pos);

        List<MovieSearchResult> lastResult = this.lastSearchResults.get(currentMovieInfo);
        MovieSearchResult lastSelection = this.selectedResults.get(currentMovieInfo);
        dialog.setMovieSuggestions(lastResult);
        dialog.setSelectedMovie(lastSelection);

        MovieService lastService = this.lastService.get(currentMovieInfo);
        if (lastService!=null) {
            dialog.setSelectedMovieService(lastService);
        }

        dialog.setProgressBar(selectedResults.size(), movies.size());
    }

    void cancelPressed() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    void nextButtonPressed() {
        int pos = 0;
        if (currentMovieInfo != null) {
            storeValues();
            pos = Math.min(movies.indexOf(currentMovieInfo) + 1, movies.size() -1);
        }
        initDialogWithMovieInfo(pos);
    }

    void okButtonPressed() {
        for (MovieInfo info : selectedResults.keySet()) {
            MovieSearchResult result = selectedResults.get(info);
            
            // set the site-id into the MovieInfo object, and call the regular check/retrieval methods.
            StorableMovieSite movieSite = info.getMovie().getMovieSiteInfoOrCreate(result.getService());
            movieSite.setIdForSite(result.getIdForSite());
            
            browser.getMovieFinder().loadMovie(info, result.getService());
        }
        tableModel.addAll(selectedResults.keySet());
        
        
        dialog.setVisible(false);
        dialog.dispose();
    }

    void removeButtonPressed() {
        if (currentMovieInfo != null) {
            int pos = movies.indexOf(currentMovieInfo);
            movies.remove(pos);
            if (pos == movies.size()) {
                pos--;
            }
            initDialogWithMovieInfo(pos);
        }
    }

    void prevButtonPressed() {
        int pos = 0;
        if (currentMovieInfo != null) {
            storeValues();
            pos = movies.indexOf(currentMovieInfo) - 1;
        }
        initDialogWithMovieInfo(pos);
    }

    void searchPressed() {
        dialog.setEnableSearch(false);
        MovieService service = dialog.getSelectedMovieService();
        final MovieInfoFetcher fetcher = browser.getFetcherFactory().get(service);
        final String title = dialog.getMovieTitle().trim();

        final MovieInfo info = currentMovieInfo;
        lastService.put(currentMovieInfo, service);

        SwingWorker<List<? extends MovieSearchResult>, Void> worker =
                new SwingWorker<List<? extends MovieSearchResult>, Void>() {

                    @Override
                    public List<? extends MovieSearchResult> doInBackground() throws Exception {
                        return fetcher.search(title);
                    }

                    @Override
                    public void done() {
                        try {
                            List<MovieSearchResult> result = (List<MovieSearchResult>)get();
                            lastSearchResults.put(info, result);
                            dialog.setMovieSuggestions(result);
                            dialog.setEnableSearch(true);
                        } catch (InterruptedException ex) {
                            LOGGER.error("Get request intterrupted: ", ex);
                        } catch (ExecutionException ex) {
                            LOGGER.error("Get request failed: ", ex.getCause());
                        }
                    }
                };
        worker.execute();
    }

    private void storeValues() {
        currentMovieInfo.getMovie().setTitle(dialog.getMovieTitle());
        MovieSearchResult movie = dialog.getSelectedMovie();
        if (movie!=null) {
            selectedResults.put(currentMovieInfo, movie);
        } else {
            selectedResults.remove(currentMovieInfo);
        }
    }
}
