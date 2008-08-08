package eu.somatik.moviebrowser.module;

import com.google.inject.AbstractModule;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.cache.ImageCacheImpl;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.JPAMovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.config.SettingsImpl;
import eu.somatik.moviebrowser.gui.IconLoader;
import eu.somatik.moviebrowser.api.FileSystemScanner;
import eu.somatik.moviebrowser.service.FileSystemScannerImpl;
import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.MovieNameExtractor;
import eu.somatik.moviebrowser.service.SimpleFolderScanner;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.InfoHandlerImpl;

/**
 * Guice configuration module
 * @author fdb
 */
public class MovieBrowserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MovieFinder.class);
        bind(MovieNameExtractor.class);
        bind(IconLoader.class);
        
        bind(MovieCache.class).to(JPAMovieCache.class);
        bind(ImageCache.class).to(ImageCacheImpl.class);
        bind(FolderScanner.class).to(SimpleFolderScanner.class);
        bind(FileSystemScanner.class).to(FileSystemScannerImpl.class);
        bind(Settings.class).to(SettingsImpl.class);
        bind(InfoHandler.class).to(InfoHandlerImpl.class);

       



    }
}
