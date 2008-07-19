package eu.somatik.moviebrowser.module;

import com.google.inject.AbstractModule;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.MovieCacheImpl;
import eu.somatik.moviebrowser.scanner.FileSystemScanner;
import eu.somatik.moviebrowser.scanner.FileSystemScannerImpl;
import eu.somatik.moviebrowser.service.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.fetcher.MovieInfoFetcher;
import eu.somatik.moviebrowser.service.MovieNameExtractor;
import eu.somatik.moviebrowser.service.fetcher.MovieWebInfoFetcher;
import eu.somatik.moviebrowser.service.SimpleFolderScanner;
import eu.somatik.moviebrowser.service.fetcher.TomatoesInfoFetcher;
import eu.somatik.moviebrowser.service.parser.ImdbParser;
import eu.somatik.moviebrowser.service.parser.MovieWebParser;
import eu.somatik.moviebrowser.service.parser.Parser;
import eu.somatik.moviebrowser.service.parser.TomatoesParser;

/**
 * Guice configuration module
 * @author fdb
 */
public class MovieBrowserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MovieFinder.class);
        bind(MovieNameExtractor.class);
        
        bind(MovieCache.class).to(MovieCacheImpl.class);
        bind(FolderScanner.class).to(SimpleFolderScanner.class);
        bind(FileSystemScanner.class).to(FileSystemScannerImpl.class);

        bind(Parser.class).annotatedWith(MovieWeb.class).to(MovieWebParser.class);
        bind(Parser.class).annotatedWith(Imdb.class).to(ImdbParser.class);
        bind(Parser.class).annotatedWith(RottenTomatoes.class).to(TomatoesParser.class);
        
        bind(MovieInfoFetcher.class).annotatedWith(MovieWeb.class).to(MovieWebInfoFetcher.class);
        bind(MovieInfoFetcher.class).annotatedWith(RottenTomatoes.class).to(TomatoesInfoFetcher.class);

    }
}
