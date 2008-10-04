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
package eu.somatik.moviebrowser;

import eu.somatik.moviebrowser.gui.MainFrame;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.flicklib.api.InfoFetcherFactory;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.flicklib.api.SubtitlesLoader;
import com.flicklib.module.FlicklibModule;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.gui.IconLoader;
import eu.somatik.moviebrowser.module.MovieBrowserModule;
import eu.somatik.moviebrowser.api.FileSystemScanner;
import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import com.flicklib.service.movie.imdb.ImdbSearch;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.gui.debug.CheckThreadViolationRepaintManager;
import eu.somatik.moviebrowser.gui.debug.EventDispatchThreadHangMonitor;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.export.ExporterLocator;
import eu.somatik.moviebrowser.service.export.ExporterModule;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieBrowser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieBrowser.class);
    private final MovieFinder movieFinder;
    private final FolderScanner folderScanner;
    private final FileSystemScanner fileSystemScanner;
    private final ImdbSearch imdbSearch;
    private final ImageCache imageCache;
    private final IconLoader iconLoader;
    private final Settings settings;
    private final SubtitlesLoader subtitlesLoader;
    private final InfoHandler infoHandler;
    private final MovieCache movieCache;
    private final ExporterLocator exporterLocator;
    private final InfoFetcherFactory fetcherFactory;
    
    /** 
     * Creates a new instance of MovieBrowser
     * @param finder
     * @param folderScanner
     * @param fileSystemScanner
     * @param imdbSearch
     * @param imageCache
     * @param iconLoader
     * @param settings
     * @param subtitlesLoader
     * @param infoHandler
     * @param movieCache
     * @param exporterLocator 
     */
    @Inject
    public MovieBrowser(
            final MovieFinder finder,
            final FolderScanner folderScanner,
            final FileSystemScanner fileSystemScanner,
            final ImdbSearch imdbSearch,
            final ImageCache imageCache,
            final IconLoader iconLoader,
            final Settings settings,
            final SubtitlesLoader subtitlesLoader,
            final InfoHandler infoHandler,
            final MovieCache movieCache,
            final ExporterLocator exporterLocator,
            final InfoFetcherFactory fetcherFactory) {
        this.movieFinder = finder;
        this.folderScanner = folderScanner;
        this.fileSystemScanner = fileSystemScanner;
        this.imdbSearch = imdbSearch;
        this.imageCache = imageCache;
        this.iconLoader = iconLoader;
        this.settings = settings;
        this.subtitlesLoader = subtitlesLoader;
        this.infoHandler = infoHandler;
        this.movieCache = movieCache;
        this.exporterLocator = exporterLocator;
        this.fetcherFactory = fetcherFactory;
    }

    private void configureLogging() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.shutdownAndReset();
            configurator.doConfigure(MovieBrowser.class.getClassLoader().getResource("logback.xml"));
        } catch (JoranException je) {
            StatusPrinter.print(lc);
        }

    }

    private void configurelookAndFeel() {
        try {
            UIManager.setLookAndFeel(settings.loadPreferences().get("lookandfeel"));
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Error setting native LAF", ex);
        } catch (InstantiationException ex) {
            LOGGER.error("Error setting native LAF", ex);
        } catch (IllegalAccessException ex) {
            LOGGER.error("Error setting native LAF", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            LOGGER.error("Error setting native LAF", ex);
        }
    }

    private void configureEdtCheckingRepaintManager() {
        EventDispatchThreadHangMonitor.initMonitoring();
        RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(true));
    }

    private void configureExceptionhandling() {
        Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
    }

    private void start() {
        configureLogging();
        configureExceptionhandling();
        configurelookAndFeel();

        if (settings.isDebugMode()) {
            configureEdtCheckingRepaintManager();
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                MainFrame mainFrame = new MainFrame(MovieBrowser.this, imageCache, iconLoader, settings, infoHandler, exporterLocator);
                mainFrame.setupListeners();
                mainFrame.setVisible(true);
                mainFrame.loadMoviesFromDatabase();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Injector injector = Guice.createInjector(new MovieBrowserModule(), new FlicklibModule(), new ExporterModule());
        MovieBrowser browser = injector.getInstance(MovieBrowser.class);
        browser.start();
    }

    public IconLoader getIconLoader() {
        return iconLoader;
    }
    
    public MovieFinder getMovieFinder() {
        return movieFinder;
    }

    public FolderScanner getFolderScanner() {
        return folderScanner;
    }

    public FileSystemScanner getFileSystemScanner() {
        return fileSystemScanner;
    }

    public ImdbSearch getImdbSearch() {
        return imdbSearch;
    }

    public SubtitlesLoader getSubtitlesLoader() {
        return subtitlesLoader;
    }

    public MovieCache getMovieCache() {
        return movieCache;
    }

    public InfoFetcherFactory getFetcherFactory() {
        return fetcherFactory;
    }
    
    public void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        }
    }

    public void openFile(File file) {
        LOGGER.info("Trying to open " + file.getAbsolutePath());
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + file.getAbsolutePath(), ex);
        }
    }


    private static class LoggingUncaughtExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            LOGGER.error("Uncaught exception in thread " + thread.getName(), ex);
        }
    }
}
