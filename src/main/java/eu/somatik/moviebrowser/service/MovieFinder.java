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
import java.io.IOException;
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
import com.flicklib.domain.MoviePage;
import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.FileGroup;
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
    private final MovieCache movieCache;
    private final InfoFetcherFactory fetcherFactory;
    private final Settings settings;
    private final Converter converter = new Converter();
    /**
     * Keeps track of how many tasks are running
     */
    private int runningTasks;
    
    /**
     * as the running tasks property is updated from several threads, it is possible to some concurrent modification get lost, to avoid
     * we need a lock ... 
     */
    private final Object runningTaskLock = new Object();
    

    /**
     * Creates a new instance of MovieFinder
     * @param movieCache
     * @param fetcherFactory
     * @param settings 
     */
    @Inject
    public MovieFinder(
            final MovieCache movieCache,
            final InfoFetcherFactory fetcherFactory,
            final Settings settings) {
        this.movieCache = movieCache;
        this.fetcherFactory = fetcherFactory;
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
        MovieService preferredService = settings.getPreferredService();
        String movieId = movieInfo.siteFor(preferredService).getIdForSite();

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
        
        movie.getMovieSiteInfoOrCreate(preferredService).setIdForSite(movieId);

        movieInfo.setMovie(movieCache.insertOrUpdate(movie));
        loadMovies(list, true);
    }

    /**
     * Loads all movies
     * 
     * @param movies
     * @param async if true the method will return before finisihing the load
     */
    public void loadMovies(List<MovieInfo> movies, boolean async) {
        LOGGER.info("Loading " + movies.size() + " movies");
        if (async) {
            for (MovieInfo info : movies) {
                service.submit(new MainServiceCaller(info));
            }
        } else {
            List<MainServiceCaller> callers = new LinkedList<MainServiceCaller>();
            for (MovieInfo info : movies) {
                callers.add(new MainServiceCaller(info));
            }
    
            try {
                service.invokeAll(callers);
            } catch (InterruptedException ex) {
                LOGGER.error("Movie loader interrupted", ex);
            }
        }
    }
    
    public void incRunningTasks() {
        synchronized (runningTaskLock) {
            runningTasks++;
        }
    }
    public void decRunningTasks() {
        synchronized (runningTaskLock) {
            runningTasks--;
        }
    }
    


    private class MainServiceCaller implements Callable<MovieInfo> {

        private final MovieInfo info;
        private final MovieService preferredService;

        /**
         * Constructs a new ImdbCaller object
         *
         * @param info
         */
        public MainServiceCaller(MovieInfo info) {
            this.info = info;
            this.preferredService = settings.getPreferredService();
            incRunningTasks();
        }

        @Override
        public MovieInfo call() throws Exception {
            synchronized(info) {
                doCall();
            }
            decRunningTasks();
            return info;
        }
        
        private void doCall() throws Exception {
            try {
                LOGGER.info("call "+info.getMovie().getTitle());
                info.setStatus(MovieStatus.LOADING);
                if (info.getMovie().getId() == null) {
                	//info.setMovieFile(movieCache.getOrCreateFile(info.getDirectory().getAbsolutePath()));
                    // TODO load movie
                    FileGroup group = findMovie(info);
                    if (group!=null) {
                        MovieLocation directory = group.getDirectory(info.getDirectory().getAbsolutePath());
                        //directory.setName(info.getDirectory().getName());
                        info.setMovie(movieCache.insertOrUpdate(group.getMovie()));
                    } else {
                        fetchInformation();
                    }
                    info.setStatus(MovieStatus.LOADED);
                } else {
                    if (info.isNeedRefetch()) {
                        getMovieInfoImdb(info, preferredService);
                        
                        info.setMovie(movieCache.insertOrUpdate(info.getMovie()));
                    }
                    info.setStatus(MovieStatus.CACHED);
                }

                for (MovieService service : getEnabledServices()) {
                    if (service!=preferredService) {
                        StorableMovieSite siteInfo = info.siteFor(service);
                        if (siteInfo == null || siteInfo.getScore()==null) {
                            secondaryService.submit(new MovieServiceCaller(service, info));
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Exception while loading/saving movie", ex);
                info.setStatus(MovieStatus.ERROR);
            }
        }

        private void fetchInformation() throws UnknownHostException, Exception {
            StorableMovie movie;
            getMovieInfoImdb(info, preferredService);
            movie = movieCache.findMovieByTitle(info.getMovie().getTitle());
            if (movie == null) {
                info.setMovie(movieCache.insertOrUpdate(info.getMovie()));
   
            } else {
                // the movie is already in the database, but we haven't find this movie by 
                // it's files, so it must be a different version, so we add as a new file
                // group.
                FileGroup newFileGroup = info.getMovie().getUniqueFileGroup();
                MovieLocation directory = newFileGroup.getDirectory(info.getDirectory().getAbsolutePath());
                movie.addFileGroup(newFileGroup);
            	//directory.setName(info.getDirectory().getName());
            	info.setMovie(movieCache.insertOrUpdate(movie));
            }
            info.setNeedRefetch(false);
        }

        private FileGroup findMovie(MovieInfo info) {
            for (StorableMovieFile file : info.getMovie().getUniqueFileGroup().getFiles()) {
                if (file.getType()==FileType.VIDEO_CONTENT) {
                    FileGroup fileGroup = movieCache.findByFile(file.getName(), file.getSize());
                    if (fileGroup!=null) {
                        return fileGroup;
                    }
                }
            }
            return null;
        }
    }

    private class MovieServiceCaller implements Callable<MovieInfo> {

        private final MovieInfo info;
        private final MovieService service;

        /**
         * Constructs a new MovieCaller object
         * @param service
         * @param info
         */
        public MovieServiceCaller(final MovieService service, final MovieInfo info) {
            this.service = service;
            this.info = info;
            incRunningTasks();
        }
        @Override
        public MovieInfo call() throws Exception {
            synchronized(info) {
                doCall();
            }
            decRunningTasks();
            return info;
        }
        
        private void doCall() throws Exception {
            // TODO make a null entry if movie not found? so we can do better reloading
            try {
                LOGGER.trace("Calling fetch on {} for '{}'", service.getClass().getSimpleName(), info.getMovie().getTitle());
                info.setStatus(MovieStatus.LOADING);
                
                fetchMoviePageInfo(info, service);
                
                info.setMovie(movieCache.insertOrUpdate(info.getMovie()));
                info.setStatus(MovieStatus.LOADED);
            } catch (Exception ex) {
                LOGGER.error("Loading '" + info.getMovie().getTitle() + "' on " + service.getName() + " failed", ex);
                info.setStatus(MovieStatus.ERROR);
            }
        }
    }

    /**
     *
     * @param movieInfo
     * @param movieService 
     * @return the MovieInfo
     * @throws java.net.UnknownHostException
     * @throws java.lang.Exception
     */
    private void getMovieInfoImdb(MovieInfo movieInfo, MovieService movieService) throws UnknownHostException, Exception {
        StorableMovie newMovie = movieInfo.getMovie();

        MoviePage moviePage = fetchMoviePageInfo(movieInfo, movieService);
        if (moviePage!=null && moviePage.getIdForSite()!=null) {
            converter.convert(moviePage, newMovie);
        }

        //rename titles, if we have IMDB result
        if (settings.getRenameTitles() && moviePage.getIdForSite()!=null) {
            renameFolderToTitle(movieInfo);
        }
    // todo insert the site?
    }

    
    /**
     * fetch all movie information from the given movie service, and put into the MovieInfo/StorableMovie/StorableMovieSite,
     * and returns the modified StorableMovieSite.
     * 
     * @param info
     * @param service
     * @return the movie page
     * @throws IOException
     */
    protected MoviePage fetchMoviePageInfo(MovieInfo info, MovieService service) throws IOException {
        MoviePage site;
        MovieInfoFetcher fetcher = fetcherFactory.get(service);
        StorableMovieSite storableMovieSite = info.getMovie().getMovieSiteInfo(service);
        if (storableMovieSite==null || storableMovieSite.getIdForSite()==null) {
            site = fetcher.fetch(info.getMovie().getTitle());
            storableMovieSite = info.getMovie().getMovieSiteInfoOrCreate(service);
        } else {
            site = fetcher.getMovieInfo(storableMovieSite.getIdForSite());
        }
        if (site!=null) {
            converter.convert(site, storableMovieSite);
        } else {
            LOGGER.warn("Movie Page not found for "+info.getMovie().getTitle()+" by "+service);
        }
        return site;
    }
    
    public int getRunningTasks() {
        return runningTasks;
    }

    private void renameFolderToTitle(MovieInfo info) {
        renameFolder(info, info.getMovie().getTitle(), false);
    }

    public boolean renameFolder(MovieInfo info, String newName) {
        return renameFolder(info, newName, true);
    }
    
    private boolean renameFolder(MovieInfo info, String newName, boolean store) {
        boolean success;
        boolean needUpdate = false;
        for (MovieLocation location : info.getLocations()) {
            if (location.isFolderRenamingSafe()) {
                File oldLocation = new File(location.getPath());
                File newFileName = new File(oldLocation.getParent(), newName);
                success = FileTools.renameDir(oldLocation, newFileName);
                if (success) {
                    LOGGER.info(oldLocation.getAbsolutePath() + " auto renamed to " + newFileName.getAbsolutePath());
                    location.setPath(newFileName.getAbsolutePath());
                    needUpdate= true;
                } else {
                    LOGGER.error("Error auto renaming " + oldLocation.getAbsolutePath() + " to " + newFileName.getAbsolutePath());
                    return false;
                }
            } else {
                LOGGER.info("Folder renaming is not safe for "+location.getPath());
            }
        }
        if (needUpdate) {
            if (store) {
                info.setMovie(movieCache.insertOrUpdate(info.getMovie()));
            }
            info.triggerUpdate();
        }
        return true;
    }
    
    public List<MovieService> getEnabledServices() {
        List<MovieService> result = new ArrayList<MovieService>();
        for (MovieService ms : MovieService.values()) {
            if (settings.isServiceEnabled(ms.name().toLowerCase(), false)) {
                result.add(ms);
            }
        }
        return result;
    }

    
}
