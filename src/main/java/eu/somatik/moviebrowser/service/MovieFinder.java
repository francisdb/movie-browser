/*
 * MovieFinder.java
 *
 * Created on January 20, 2007, 1:51 PM
 *
 */
package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.api.FileSystemScanner;
import com.flicklib.api.MovieInfoFetcher;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Inject;
import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.domain.Movie;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieFinder.class);
    
    // TODO make this available in settings somewhere
    private static final int IMDB_POOL_SIZE = 5;
    private static final int OTHERS_POOL_SIZE = 5;
    
    private final ExecutorService service;
    private final ExecutorService secondaryService;
    private final FileSystemScanner fileSystemScanner;
    private final MovieNameExtractor movieNameExtractor;
    private final MovieCache movieCache;
    private final InfoFetcherFactory fetcherFactory;
    private final InfoHandler infoHandler;
    private final Converter converter = new Converter();

    /**
     * Creates a new instance of MovieFinder
     * @param movieCache
     * @param fileSystemScanner
     * @param movieNameExtractor
     * @param fetcherFactory
     * @param infoHandler 
     */
    @Inject
    public MovieFinder(
            final MovieCache movieCache,
            final FileSystemScanner fileSystemScanner,
            final MovieNameExtractor movieNameExtractor,
            final InfoFetcherFactory fetcherFactory,
            final InfoHandler infoHandler) {

        this.movieCache = movieCache;
        this.fileSystemScanner = fileSystemScanner;
        this.movieNameExtractor = movieNameExtractor;
        this.fetcherFactory = fetcherFactory;
        this.infoHandler = infoHandler;

        this.service = Executors.newFixedThreadPool(IMDB_POOL_SIZE);
        this.secondaryService = Executors.newFixedThreadPool(OTHERS_POOL_SIZE);
    }

    /**
     * Stops the finder
     */
    public void stop() {
        movieCache.shutdown();
        service.shutdownNow();
        secondaryService.shutdownNow();
    }

    public void start() {
        if (!movieCache.isStarted()) {
            movieCache.startup();
        }
    }

    public void reloadMovie(MovieInfo movieInfo) {
        String imdbId = movieInfo.siteFor(MovieService.IMDB).getIdForSite();

        List<MovieInfo> list = new ArrayList<MovieInfo>();
        list.add(movieInfo);
        movieInfo.setImage(null);

        // TODO put this all in the cache?
        List<StorableMovieSite> sites = movieCache.loadSites(movieInfo.getMovieFile().getMovie());
        for (StorableMovieSite site : sites) {
            movieCache.remove(site);
        }
        movieCache.remove(movieInfo.getMovieFile());
        // TODO make sure the movie is not linked to an other file
        movieCache.remove(movieInfo.getMovieFile().getMovie());

        StorableMovieSite site = new StorableMovieSite();
        site.setService(MovieService.IMDB);
        site.setIdForSite(imdbId);
        movieInfo.addSite(site);
        loadMovies(list);
    }

    /**
     * Loads all movies
     * @param movies
     */
    public void loadMovies(List<MovieInfo> movies) {
        LOGGER.info("Loading " + movies.size() + " movies");
        List<MovieCaller> callers = new LinkedList<MovieCaller>();
        for (MovieInfo info : movies) {
            callers.add(new MovieCaller(info));
        }

        try {
            service.invokeAll(callers);
        } catch (InterruptedException ex) {
            LOGGER.error("Movie loader interrupted", ex);
        }
    }

    private class MovieCaller implements Callable<MovieInfo> {

        private final MovieInfo info;

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public MovieCaller(MovieInfo info) {
            this.info = info;
        }

        @Override
        public MovieInfo call() throws Exception {
            try {
                info.setStatus(MovieStatus.LOADING);
                info.setMovieFile(movieCache.getOrCreateFile(info.getDirectory().getAbsolutePath()));
                if (info.getMovieFile().getMovie() == null || info.getMovieFile().getMovie().getId() == 0) {
                    // TODO load movie

                    getMovieInfoImdb(info);
                    movieCache.inserOrUpdate(info.getMovieFile().getMovie());
                    movieCache.update(info.getMovieFile());
                    
                    // TODO only do if not available
                    movieCache.insert(info.siteFor(MovieService.IMDB));

                    // TODO FOR EACH SELECTED SERVICE
                    // TODO only do if not available
                    secondaryService.submit(new MovieServiceCaller(MovieService.TOMATOES, info));
                    secondaryService.submit(new MovieServiceCaller(MovieService.MOVIEWEB, info));
                    secondaryService.submit(new MovieServiceCaller(MovieService.GOOGLE, info));
                    secondaryService.submit(new MovieServiceCaller(MovieService.FLIXSTER, info));
                    info.setStatus(MovieStatus.LOADED);
                } else {
                    List<StorableMovieSite> sites = movieCache.loadSites(info.getMovieFile().getMovie());
                    for (StorableMovieSite site : sites) {
                        info.addSite(site);
                    }
                    info.setStatus(MovieStatus.CACHED);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception while loading/saving movie", ex);
                info.setStatus(MovieStatus.ERROR);
            }

//            StorableMovie movie = movieCache.find(info.getMovieFile().getMovie().getPath());
//            MovieInfo loaded = null;
//            if (movie == null || movie.getImdbId() == null) {
//                try{
//                    LOGGER.info("Fetching data for "+info.getMovieFile().getMovie().getPath());
//                    
//                    loaded = getMovieInfoImdb(info);
//                    movieCache.inserOrUpdate(loaded.getMovieFile().getMovie());
//                    // TODO FOR EACH SELECTED SERVICE
//                    secondaryService.submit(new MovieServiceCaller(MovieService.TOMATOES, loaded));
//                    secondaryService.submit(new MovieServiceCaller(MovieService.MOVIEWEB, loaded));
//                    secondaryService.submit(new MovieServiceCaller(MovieService.GOOGLE, loaded));
//                    secondaryService.submit(new MovieServiceCaller(MovieService.FLIXSTER, loaded));
//                }catch(Exception ex){
//                    LOGGER.error("Exception while loading/saving movie", ex);
//                }
//            } else {
//                LOGGER.info("Loading cached data for "+info.getMovieFile().getMovie().getPath());
//                info.setStatus(MovieStatus.CACHED);
//                info.getMovieFile().setMovie(movie);
//                loaded = info;
//            }
            return info;
        }
    }

    private class MovieServiceCaller implements Callable<MovieInfo> {

        private final MovieInfoFetcher fetcher;
        private final MovieInfo info;
        private final MovieService service;

        /**
         * Constructs a new MovieCaller object
         * @param service
         * @param info
         */
        public MovieServiceCaller(final MovieService service, final MovieInfo info) {
            this.service = service;
            this.fetcher = fetcherFactory.get(service);
            this.info = info;
        }

        @Override
        public MovieInfo call() throws Exception {
            LOGGER.info("Calling fetch on " + fetcher.getClass().getSimpleName());
            info.setStatus(MovieStatus.LOADING);
            Movie movie = new Movie();
            converter.convert(info.getMovieFile().getMovie(), movie);
            String id = null;
            if (service == MovieService.TOMATOES) {
                id = infoHandler.id(info, MovieService.IMDB);
            }
            MoviePage site = fetcher.fetch(movie, id);
            StorableMovieSite storableMovieSite = new StorableMovieSite();
            converter.convert(site, storableMovieSite);
            storableMovieSite.setMovie(info.getMovieFile().getMovie());
            // TODO insert site?
            movieCache.insert(storableMovieSite);
            info.addSite(storableMovieSite);
            info.setStatus(MovieStatus.LOADED);
            return info;
        }
    }

    /**
     *
     * @param movieInfo
     * @return the MovieInfo
     * @throws java.net.UnknownHostException
     * @throws java.lang.Exception
     */
    private void getMovieInfoImdb(MovieInfo movieInfo) throws UnknownHostException, Exception {
        String url = infoHandler.url(movieInfo, MovieService.IMDB);
        if (movieInfo.getMovieFile().getMovie() == null) {
            movieInfo.getMovieFile().setMovie(new StorableMovie());
        }
        if(movieInfo.getMovieFile().getMovie().getTitle() == null){
            movieInfo.getMovieFile().getMovie().setTitle(movieNameExtractor.removeCrap(movieInfo.getDirectory()));
        }

        if (url == null) {
            LOGGER.info("Finding NFO for " + movieInfo.getDirectory());
            url = fileSystemScanner.findNfoImdbUrl(movieInfo.getDirectory());
        }

        // TODO find a way to pass the imdb url
        // movieInfo.getMovieFile().getMovie().setImdbUrl(url);
        Movie movie = new Movie();
        converter.convert(movieInfo.getMovieFile().getMovie(), movie);
        MoviePage site = fetcherFactory.get(MovieService.IMDB).fetch(movie, infoHandler.id(movieInfo, MovieService.IMDB));
        converter.convert(movie, movieInfo.getMovieFile().getMovie());
        StorableMovieSite storableMovieSite = new StorableMovieSite();
        converter.convert(site, storableMovieSite);
        storableMovieSite.setMovie(movieInfo.getMovieFile().getMovie());
        movieInfo.addSite(storableMovieSite);
    // todo insert the site?
    }    
}
