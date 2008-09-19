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
package eu.somatik.moviebrowser.service;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.api.FileSystemScanner;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import eu.somatik.moviebrowser.tools.FileTools;

/**
 *
 * @author francisdb
 */
@Singleton
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
    private final Settings settings;
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
     * @param settings 
     */
    @Inject
    public MovieFinder(
            final MovieCache movieCache,
            final FileSystemScanner fileSystemScanner,
            final MovieNameExtractor movieNameExtractor,
            final InfoFetcherFactory fetcherFactory,
            final InfoHandler infoHandler,
            final Settings settings) {
        this.movieCache = movieCache;
        this.fileSystemScanner = fileSystemScanner;
        this.movieNameExtractor = movieNameExtractor;
        this.fetcherFactory = fetcherFactory;
        this.infoHandler = infoHandler;
        this.settings = settings;

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
        
        //movieCache.remove(movieInfo.getMovieFile());
        // TODO make sure the movie is not linked to an other file
        StorableMovie movie = movieInfo.getMovie();
        /*if(movie != null){
            movieCache.remove(movieInfo.getMovie());
        }*/

        for (StorableMovieSite sms : movie.getSiteInfo()) {
            sms.setScore(null);
            sms.setVotes(null);
            sms.setIdForSite(null);
        }
        
        movie.getMovieSiteInfoOrCreate(MovieService.IMDB).setIdForSite(imdbId);

        movieCache.insertOrUpdate(movie);
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
            MovieService[] extraServices = new MovieService[]{MovieService.TOMATOES, MovieService.MOVIEWEB, MovieService.GOOGLE, MovieService.FLIXSTER};
            try {
                info.setStatus(MovieStatus.LOADING);
                if (info.getMovie().getId() == null) {
                	//info.setMovieFile(movieCache.getOrCreateFile(info.getDirectory().getAbsolutePath()));
                    // TODO load movie
                    StorableMovie movie = findMovie(info);
                    if (movie!=null) {
                        MovieLocation directory = movie.getDirectory(info.getDirectory().getAbsolutePath());
                        //directory.setName(info.getDirectory().getName());
                        movieCache.insertOrUpdate(movie);
                        info.setMovie(movie);
                    } else {
                        fetchInformations();
                    }
                    info.setStatus(MovieStatus.LOADED);
                } else {
                    if (info.isNeedRefetch()) {
                        getMovieInfoImdb(info);
                        
                        movieCache.insertOrUpdate(info.getMovie());
                    }
                    info.setStatus(MovieStatus.CACHED);
                }
                for (MovieService service : extraServices) {
                    if (info.siteFor(service) == null) {
                        secondaryService.submit(new MovieServiceCaller(service, info));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Exception while loading/saving movie", ex);
                info.setStatus(MovieStatus.ERROR);
            } finally {
                runningTasks--;
            }


            return info;
        }

        private void fetchInformations() throws UnknownHostException, Exception {
            StorableMovie movie;
            getMovieInfoImdb(info);
            movie = movieCache.findMovieByTitle(info.getMovie().getTitle());
            if (movie == null) {
                movieCache.insertOrUpdate(info.getMovie());
                //movieCache.update(info.getMovieFile());
   
                // TODO only do if not available
                // FIXME how come this is possible?
                StorableMovieSite movieSite = info.siteFor(MovieService.IMDB);
                if(movieSite.getId() == 0){
                    movieCache.insert(movieSite);
                }
            } else {
                MovieLocation directory = movie.getDirectory(info.getDirectory().getAbsolutePath());
            	//directory.setName(info.getDirectory().getName());
            	movieCache.insertOrUpdate(movie);
            	info.setMovie(movie);
            }
            info.setNeedRefetch(false);
        }

        private StorableMovie findMovie(MovieInfo info) {
            for (StorableMovieFile file : info.getMovie().getFiles()) {
                if (file.getType()==FileType.VIDEO_CONTENT) {
                    StorableMovie movie = movieCache.findByFile(file.getName(), file.getSize());
                    if (movie!=null) {
                        return movie;
                    }
                }
            }
            return null;
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
                LOGGER.trace("Calling fetch on {} for '{}'", fetcher.getClass().getSimpleName(), info.getMovie().getTitle());
                info.setStatus(MovieStatus.LOADING);
                Movie movie = new Movie();
                converter.convert(info.getMovie(), movie);
                String id = null;
                if (service == MovieService.TOMATOES) {
                    id = infoHandler.id(info, MovieService.IMDB);
                }
                MoviePage site = fetcher.fetch(movie, id);
                StorableMovieSite storableMovieSite = info.getMovie().getMovieSiteInfoOrCreate(service);
                converter.convert(site, storableMovieSite);
                // TODO check if not fetched by some other duplicate before inserting
                
                movieCache.insertOrUpdate(info.getMovie());
                info.setStatus(MovieStatus.LOADED);
            } catch (Exception ex) {
                LOGGER.error("Loading '" + info.getMovie().getTitle() + "' on " + service.getName() + " failed", ex);
                info.setStatus(MovieStatus.ERROR);
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
        StorableMovie newMovie = movieInfo.getMovie();

        if (url == null) {
            LOGGER.info("Finding NFO for " + movieInfo.getDirectory());
            url = fileSystemScanner.findNfoImdbUrl(movieInfo.getDirectory());
        }

        // TODO find a way to pass the imdb url
        // movieInfo.getMovieFile().getMovie().setImdbUrl(url);
        Movie movie = new Movie();
        converter.convert(newMovie, movie);
        MoviePage site = fetcherFactory.get(MovieService.IMDB).fetch(movie, infoHandler.id(movieInfo, MovieService.IMDB));
        converter.convert(movie, newMovie);
        StorableMovieSite storableMovieSite = newMovie.getMovieSiteInfoOrCreate(site.getService());
        converter.convert(site, storableMovieSite);

        //rename titles
        if (settings.getRenameTitles()) {
            renameFolderToTitle(movieInfo);
        }
    // todo insert the site?
    }

    public int getRunningTasks() {
        return runningTasks;
    }

    public void renameFolderToTitle(MovieInfo info) {
		renameFolder(info, info.getMovie().getTitle());
	}

	public boolean renameFolder(MovieInfo info, String newName) {
        boolean success;

        File oldFile = info.getDirectory();
        File newFile = new File(oldFile.getParent(), newName);
        success = FileTools.renameDir(oldFile, newFile);
        if (success) {
            LOGGER.info(oldFile.getAbsolutePath() + " auto renamed to " + newFile.getAbsolutePath());
            // update the path in the db
            info.setDirectory(newFile);
            MovieLocation directory = info.getMovie().getDirectory(oldFile.getAbsolutePath());
            directory.setPath(newFile.getAbsolutePath());
            //info.getMovieFile().setPath(newFile.getAbsolutePath());
            movieCache.update(directory);
            info.triggerUpdate();
            return true;
        } else {
            LOGGER.error("Error auto renaming " + oldFile.getAbsolutePath() + " to " + newFile.getAbsolutePath());
            return false;
        }
    }
}
