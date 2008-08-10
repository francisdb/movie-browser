/*
 * MovieBrowser.java
 *
 * Created on May 6, 2007, 10:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser;

import eu.somatik.moviebrowser.gui.MainFrame;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
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
import eu.somatik.moviebrowser.gui.debug.CheckThreadViolationRepaintManager;
import eu.somatik.moviebrowser.gui.debug.EventDispatchThreadHangMonitor;
import eu.somatik.moviebrowser.service.InfoHandler;
import java.lang.Thread.UncaughtExceptionHandler;
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
            final InfoHandler infoHandler) {
        this.movieFinder = finder;
        this.folderScanner = folderScanner;
        this.fileSystemScanner = fileSystemScanner;
        this.imdbSearch = imdbSearch;
        this.imageCache = imageCache;
        this.iconLoader = iconLoader;
        this.settings = settings;
        this.subtitlesLoader = subtitlesLoader;
        this.infoHandler = infoHandler;
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
    
    private void configureEdtCheckingRepaintManager(){
        RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(true));
    }
    
    private void configureExceptionhandling(){
        Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
        EventDispatchThreadHangMonitor.initMonitoring();
    }

    private void start() {
        configureLogging();
        configureExceptionhandling();
        configurelookAndFeel();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                configureEdtCheckingRepaintManager();
                MainFrame mainFrame = new MainFrame(MovieBrowser.this, imageCache, iconLoader, settings, infoHandler);
                mainFrame.setVisible(true);
                mainFrame.load();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Injector injector = Guice.createInjector(new MovieBrowserModule(), new FlicklibModule());
        MovieBrowser browser = injector.getInstance(MovieBrowser.class);
        browser.start();
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
    
    

    private static class LoggingUncaughtExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            LOGGER.error("Uncaught exception in thread " + thread.getName(), ex);
        }
    }
    
    
}
