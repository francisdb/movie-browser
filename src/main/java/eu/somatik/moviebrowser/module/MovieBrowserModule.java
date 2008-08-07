package eu.somatik.moviebrowser.module;

import com.flicklib.service.movie.tomatoes.RottenTomatoes;
import com.flicklib.service.movie.movieweb.MovieWeb;
import com.flicklib.service.movie.imdb.Imdb;
import com.google.inject.AbstractModule;
import com.flicklib.api.InfoFetcherFactory;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.cache.ImageCacheImpl;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.JPAMovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.config.SettingsImpl;
import eu.somatik.moviebrowser.gui.IconLoader;
import eu.somatik.moviebrowser.service.FileSystemScanner;
import eu.somatik.moviebrowser.service.FileSystemScannerImpl;
import eu.somatik.moviebrowser.service.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import com.flicklib.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.service.MovieNameExtractor;
import com.flicklib.service.movie.movieweb.MovieWebInfoFetcher;
import eu.somatik.moviebrowser.service.SimpleFolderScanner;
import com.flicklib.service.movie.tomatoes.TomatoesInfoFetcher;
import com.flicklib.service.movie.imdb.ImdbParser;
import com.flicklib.service.movie.movieweb.MovieWebParser;
import com.flicklib.api.Parser;
import com.flicklib.service.movie.InfoFetcherFactoryImpl;
import com.flicklib.service.movie.flixter.Flixter;
import com.flicklib.service.movie.flixter.FlixterInfoFetcher;
import com.flicklib.service.movie.flixter.FlixterParser;
import com.flicklib.service.movie.google.Google;
import com.flicklib.service.movie.google.GoogleInfoFetcher;
import com.flicklib.service.movie.google.GoogleParser;
import com.flicklib.service.movie.imdb.ImdbInfoFetcher;
import com.flicklib.service.movie.omdb.Omdb;
import com.flicklib.service.movie.omdb.OmdbFetcher;
import com.flicklib.service.movie.tomatoes.TomatoesParser;

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

       

        bind(InfoFetcherFactory.class).to(InfoFetcherFactoryImpl.class);
        
        bind(Parser.class).annotatedWith(MovieWeb.class).to(MovieWebParser.class);
        bind(Parser.class).annotatedWith(Imdb.class).to(ImdbParser.class);
        bind(Parser.class).annotatedWith(RottenTomatoes.class).to(TomatoesParser.class);
        bind(Parser.class).annotatedWith(Google.class).to(GoogleParser.class);
        bind(Parser.class).annotatedWith(Flixter.class).to(FlixterParser.class);

        bind(MovieInfoFetcher.class).annotatedWith(Imdb.class).to(ImdbInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(MovieWeb.class).to(MovieWebInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(RottenTomatoes.class).to(TomatoesInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Google.class).to(GoogleInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Flixter.class).to(FlixterInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(Omdb.class).to(OmdbFetcher.class);

    }
}
