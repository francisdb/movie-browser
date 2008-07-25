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
import eu.somatik.moviebrowser.module.MovieBrowserModule;
import eu.somatik.moviebrowser.service.scanner.FileSystemScanner;
import eu.somatik.moviebrowser.service.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.fetcher.ImdbSearch;
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

    /** 
     * Creates a new instance of MovieBrowser
     * @param finder
     * @param folderScanner
     * @param fileSystemScanner
     * @param imdbSearch 
     */
    @Inject
    public MovieBrowser(
            final MovieFinder finder, 
            final FolderScanner folderScanner, 
            final FileSystemScanner fileSystemScanner,
            final ImdbSearch imdbSearch) {
        this.movieFinder = finder;
        this.folderScanner = folderScanner;
        this.fileSystemScanner = fileSystemScanner;
        this.imdbSearch = imdbSearch;
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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
    
    private void configureExceptionhandling(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                LOGGER.error("Uncaught exception in thread " + thread.getName(), ex);
            }
        });

    }

    private void start() {
        configureLogging();
        configureExceptionhandling();
        configurelookAndFeel();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame(MovieBrowser.this);
                mainFrame.setVisible(true);
                mainFrame.load();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Injector injector = Guice.createInjector(new MovieBrowserModule());
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
    
    
}
