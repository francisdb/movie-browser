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
     * Kepps track of how many tasks are running
     */
    private int runningTasks;

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

        runningTasks = 0;
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
        List<ImdbCaller> callers = new LinkedList<ImdbCaller>();
        for (MovieInfo info : movies) {
            callers.add(new ImdbCaller(info));
        }

        //List<Future<MovieInfo>> futures = new LinkedList<Future<MovieInfo>>();
        try {
            // futures = 
            service.invokeAll(callers);
        // TODO check all futures for succes.
//            for( Future<MovieInfo> future:futures){
//                future.
//            }
        } catch (InterruptedException ex) {
            LOGGER.error("Movie loader interrupted", ex);
        }
    }

    private class ImdbCaller implements Callable<MovieInfo> {

        private final MovieInfo info;

        /**
         * Constructs a new ImdbCaller object
         *
         * @param info
         */
        public ImdbCaller(MovieInfo info) {
            this.info = info;
            runningTasks++;
        }

        @Override
        public MovieInfo call() throws Exception {
            try {
                info.setStatus(MovieStatus.LOADING);
                info.setMovieFile(movieCache.getOrCreateFile(info.getDirectory().getAbsolutePath()));
                if (info.getMovieFile().getMovie() == null || info.getMovieFile().getMovie().getId() == 0) {
                    // TODO load movie

                    getMovieInfoImdb(info);
                    StorableMovie movie = movieCache.findMovieByTitle(info.getMovieFile().getMovie().getTitle());
                    if (movie == null) {
                        movieCache.inserOrUpdate(info.getMovieFile().getMovie());
                        movieCache.update(info.getMovieFile());

                        // TODO only do if not available
                        movieCache.insert(info.siteFor(MovieService.IMDB));


                        MovieService[] services = new MovieService[]{MovieService.TOMATOES, MovieService.MOVIEWEB, MovieService.GOOGLE, MovieService.FLIXSTER};
                        // TODO only do if not available
                        for (MovieService service : services) {
                            secondaryService.submit(new MovieServiceCaller(service, info));
                        }
                    } else {
                        info.getMovieFile().setMovie(movie);
                        movieCache.update(info.getMovieFile());
                        loadSites(info);
                        info.setStatus(MovieStatus.LOADED);
                    }
                } else {
                    loadSites(info);
                    info.setStatus(MovieStatus.CACHED);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception while loading/saving movie", ex);
                info.setStatus(MovieStatus.ERROR);
            } finally {
                runningTasks--;
            }


            return info;
        }
    }

    private void loadSites(MovieInfo info) {
        List<StorableMovieSite> sites = movieCache.loadSites(info.getMovieFile().getMovie());
        for (StorableMovieSite site : sites) {
            info.addSite(site);
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
            runningTasks++;
        }

        @Override
        public MovieInfo call() throws Exception {
            // TODO this should update all records with this movie linked to it
            // TODO make a null entry if movie not found? so we can do better reloading
            try {
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
                movieCache.insert(storableMovieSite);
                info.addSite(storableMovieSite);
                info.setStatus(MovieStatus.LOADED);
            } finally {
                runningTasks--;
            }
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
        if (movieInfo.getMovieFile().getMovie().getTitle() == null) {
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

    public int getRunningTasks() {
        return runningTasks;
    }
}
