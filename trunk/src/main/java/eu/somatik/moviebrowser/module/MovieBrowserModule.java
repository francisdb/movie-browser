package eu.somatik.moviebrowser.module;

import eu.somatik.moviebrowser.service.tomatoes.RottenTomatoes;
import eu.somatik.moviebrowser.service.movieweb.MovieWeb;
import eu.somatik.moviebrowser.service.imdb.Imdb;
import com.google.inject.AbstractModule;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.cache.ImageCacheImpl;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.MovieCacheImpl;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.config.SettingsImpl;
import eu.somatik.moviebrowser.gui.IconLoader;
import eu.somatik.moviebrowser.service.FileSystemScanner;
import eu.somatik.moviebrowser.service.FileSystemScannerImpl;
import eu.somatik.moviebrowser.service.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.service.MovieNameExtractor;
import eu.somatik.moviebrowser.service.movieweb.MovieWebInfoFetcher;
import eu.somatik.moviebrowser.service.SimpleFolderScanner;
import eu.somatik.moviebrowser.service.tomatoes.TomatoesInfoFetcher;
import eu.somatik.moviebrowser.service.imdb.ImdbParser;
import eu.somatik.moviebrowser.service.movieweb.MovieWebParser;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.service.tomatoes.TomatoesParser;

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
        
        bind(MovieCache.class).to(MovieCacheImpl.class);
        bind(ImageCache.class).to(ImageCacheImpl.class);
        bind(FolderScanner.class).to(SimpleFolderScanner.class);
        bind(FileSystemScanner.class).to(FileSystemScannerImpl.class);
        bind(Settings.class).to(SettingsImpl.class);

        bind(Parser.class).annotatedWith(MovieWeb.class).to(MovieWebParser.class);
        bind(Parser.class).annotatedWith(Imdb.class).to(ImdbParser.class);
        bind(Parser.class).annotatedWith(RottenTomatoes.class).to(TomatoesParser.class);
        
        bind(MovieInfoFetcher.class).annotatedWith(MovieWeb.class).to(MovieWebInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(RottenTomatoes.class).to(TomatoesInfoFetcher.class);

    }
}
