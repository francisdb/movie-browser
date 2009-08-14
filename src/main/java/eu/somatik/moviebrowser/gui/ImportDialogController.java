/*
 * This file is part of Movie Browser.
 *
 * Copyright (C) Movie Browser Team
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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.MovieSearchResult;
import com.flicklib.domain.MovieService;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import eu.somatik.moviebrowser.service.DuplicateFinder;
import eu.somatik.moviebrowser.service.MovieVisitor;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author zsombor
 */
public class ImportDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDialogController.class);
    
    private final ImportDialog dialog;
    private final File scanDirectory;
    private final MovieBrowser browser;
    private final MovieInfoTableModel tableModel;

    private final Map<MovieInfo,MovieSearchResult> selectedResults;
    private final Map<MovieInfo,MovieService> lastServices;
    private final Map<MovieInfo,List<? extends MovieSearchResult>> lastSearchResults;

    private FileScanMonitor fileScanMonitor; 
    private List<MovieInfo> movies;
    private MovieInfo currentMovieInfo;

    public ImportDialogController(MovieBrowser browser, ImportDialog dialog, File selectedFile, MovieInfoTableModel model) {
        this.dialog = dialog;
        this.dialog.setController(this);
        this.dialog.setMovieService(browser.getSettings().getPreferredService());
        this.scanDirectory = selectedFile;
        this.browser = browser;
        this.tableModel = model;

        this.selectedResults = new HashMap<MovieInfo,MovieSearchResult>();
        this.lastServices = new HashMap<MovieInfo,MovieService>();
        this.lastSearchResults= new HashMap<MovieInfo,List<? extends MovieSearchResult>>();
    }

    public void startImporting(final Component componentToCenterOn) {
        fileScanMonitor = new FileScanMonitor(componentToCenterOn, "Importing from " + scanDirectory.getAbsolutePath());
        
        new SwingWorker<List<MovieInfo>, Void>() {

            @Override
            protected List<MovieInfo> doInBackground() throws Exception {
                LOGGER.info("scanning in " + scanDirectory.getAbsolutePath());
                List<MovieInfo> list = browser.getFolderScanner().scan(Collections.singleton(scanDirectory.getAbsolutePath()), fileScanMonitor);
                return new DuplicateFinder(browser.getMovieCache()).filter(list);
            }

            @Override
            protected void done() {
                try {
                    movies = get();

                    if (movies.size() > 0) {
                        dialog.setImportFolderPath(scanDirectory.getAbsolutePath());
                        dialog.setFolderLabel(scanDirectory.getName());
                        initDialogWithMovieInfo(0);
                        dialog.setLocationRelativeTo(componentToCenterOn);
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

    void suggestedTitlesDoubleClicked(MovieSearchResult selectedMovie) {
        String url = selectedMovie.getUrl();
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        }
    }

    private void initDialogWithMovieInfo(int pos) {
        dialog.setEnableNextButton(pos < movies.size() -1);
        dialog.setEnablePrevButton(0 < pos);

        if (0<=pos && pos<movies.size()) {
            currentMovieInfo = movies.get(pos);
            StorableMovie mv = currentMovieInfo.getMovie();
    
            dialog.clearMovieSuggestions();
            dialog.setMovieTitle(mv.getTitle());
            FileGroup fg = mv.getUniqueFileGroup();
            String path = fg.getDirectoryPath();
            if (path.length()>scanDirectory.getAbsolutePath().length() + 1) {
                dialog.setPathToMovie(path.substring(scanDirectory.getAbsolutePath().length() + 1));
            } else {
                dialog.setPathToMovie("[scanned folder]");
            }
    
            dialog.setRelatedFiles(fg.getFiles());
    
    
            List<? extends MovieSearchResult> lastResult = this.lastSearchResults.get(currentMovieInfo);
            MovieSearchResult lastSelection = this.selectedResults.get(currentMovieInfo);
            dialog.setMovieSuggestions(lastResult);
            dialog.setSelectedMovie(lastSelection);
    
            MovieService lastService = this.lastServices.get(currentMovieInfo);
            if (lastService!=null) {
                dialog.setSelectedMovieService(lastService);
            }
        } else {
            if (movies.size()==0) {
                cancelPressed();
                return;
            }
            currentMovieInfo = null;
            dialog.clearMovieSuggestions();
            dialog.setMovieTitle("");
            dialog.setRelatedFiles(Collections.EMPTY_SET);
            dialog.setSelectedMovie(null);
            dialog.setPathToMovie("");
        }
    }


    void selectionChanged() {
        if (currentMovieInfo != null) {
            storeValues();
        }
        dialog.setProgressBar(selectedResults.size(), movies.size());
    }

    void cancelPressed() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    void nextButtonPressed() {
        int pos = Math.min(movies.indexOf(currentMovieInfo) + 1, movies.size() -1);
        initDialogWithMovieInfo(pos);
    }

    void prevButtonPressed() {
        int pos = movies.indexOf(currentMovieInfo) - 1;
        initDialogWithMovieInfo(pos);
    }

    void okButtonPressed() {
        final String label = dialog.getFolderLabel();
        
        MovieVisitor visitor = new MovieVisitor() {
            @Override
            public void visitLocation(FileGroup fileGroup, MovieLocation location) {
                location.setLabel(label);
            }
        };
        for (MovieInfo info : selectedResults.keySet()) {
            MovieSearchResult result = selectedResults.get(info);
            
            // set the site-id into the MovieInfo object, and call the regular check/retrieval methods.
            StorableMovieSite movieSite = info.getMovie().getMovieSiteInfoOrCreate(result.getService());
            movieSite.setIdForSite(result.getIdForSite());
            
            visitor.startVisit(info.getMovie());
            
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



    void searchPressed() {
        dialog.setEnableSearch(false);
        MovieService service = dialog.getSelectedMovieService();
        final MovieInfoFetcher fetcher = browser.getFetcherFactory().get(service);
        final String title = dialog.getMovieTitle().trim();

        final MovieInfo info = currentMovieInfo;
        lastServices.put(currentMovieInfo, service);

        SwingWorker<List<? extends MovieSearchResult>, Void> worker =
                new SwingWorker<List<? extends MovieSearchResult>, Void>() {

                    @Override
                    public List<? extends MovieSearchResult> doInBackground() throws Exception {
                        return fetcher.search(title);
                    }

                    @Override
                    public void done() {
                        try {
                            List<? extends MovieSearchResult> result = get();
                            lastSearchResults.put(info, result);
                            dialog.setMovieSuggestions(result);
                        } catch (InterruptedException ex) {
                        	LOGGER.error("Get request intterrupted: ", ex);
                            dialog.setMovieSuggestions(null);
                            dialog.showFetchingInformationErrorMessage(ex.getCause().getMessage());
                        } catch (ExecutionException ex) {
                        	LOGGER.error("Get request failed: ", ex.getCause());
                            dialog.setMovieSuggestions(null);
                            dialog.showFetchingInformationErrorMessage(ex.getCause().getMessage());
                        } finally {
                            dialog.setEnableSearch(true);
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
