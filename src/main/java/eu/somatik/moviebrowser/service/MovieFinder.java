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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.InfoFetcherFactory;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.MoviePage;
import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.database.MovieDatabase;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.FileType;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieInfo.LoadType;
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

    
    private final ExecutorService mainServicePool;
    private final ExecutorService secondaryServicePool;
    private final MovieDatabase movieDatabase;
    private final InfoFetcherFactory fetcherFactory;
    private final Settings settings;
    private final Converter converter;
    /**
     * Keeps track of how many tasks are running
     */
    private final AtomicInteger runningTasks;


    /**
     * Creates a new instance of MovieFinder
     * @param movieDatabase
     * @param fetcherFactory
     * @param settings 
     */
    @Inject
    public MovieFinder(
            final MovieDatabase movieDatabase,
            final InfoFetcherFactory fetcherFactory,
            final Settings settings) {
        this.movieDatabase = movieDatabase;
        this.fetcherFactory = fetcherFactory;
        this.settings = settings;

        this.runningTasks = new AtomicInteger();
        this.converter = new Converter();
        this.mainServicePool = Executors.newFixedThreadPool(IMDB_POOL_SIZE);
        this.secondaryServicePool = Executors.newFixedThreadPool(OTHERS_POOL_SIZE);

    }

    /**
     * Stops the finder
     */
    public void stop() {
        mainServicePool.shutdownNow();
        secondaryServicePool.shutdownNow();
        movieDatabase.shutdown();
    }

    public void start() {
        if (!movieDatabase.isStarted()) {
            movieDatabase.startup();
        }
    }

    public void reloadMovie(MovieInfo movieInfo) {
        MovieService preferredService = settings.getPreferredService();

        List<MovieInfo> list = new ArrayList<MovieInfo>();
        list.add(movieInfo);

        // TODO put this all in the cache?
        
        //movieDatabase.remove(movieInfo.getMovieFile());
        // TODO make sure the movie is not linked to an other file
        StorableMovie movie = movieInfo.getMovie();
        /*if(movie != null){
            movieDatabase.remove(movieInfo.getMovie());
        }*/

        for (StorableMovieSite sms : movie.getSiteInfo()) {
            sms.setScore(null);
            sms.setVotes(null);
            if (sms.getService() != preferredService) {
                // null out all, but the preferred service id, because the preferred service already know it well.
                sms.setIdForSite(null);
            }
        }
        

        movieInfo.setMovie(movieDatabase.insertOrUpdate(movie));
        loadMovies(list, true);
    }

    public void loadMovie(MovieInfo info, MovieService mservice) {
        mainServicePool.submit(new MainServiceCaller(info, mservice));
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
                mainServicePool.submit(new MainServiceCaller(info));
            }
        } else {
            List<MainServiceCaller> callers = new LinkedList<MainServiceCaller>();
            for (MovieInfo info : movies) {
                callers.add(new MainServiceCaller(info));
            }
    
            try {
                mainServicePool.invokeAll(callers);
            } catch (InterruptedException ex) {
                LOGGER.error("Movie loader interrupted", ex);
            }
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
            this(info, settings.getPreferredService());
        }

        public MainServiceCaller(MovieInfo info, MovieService service) {
            this.info = info;
            this.preferredService = service;
            runningTasks.incrementAndGet();
        }


        @Override
        public MovieInfo call() throws Exception {
            synchronized(info) {
                doCall();
            }
            runningTasks.decrementAndGet();
            return info;
        }
        
        private void doCall() throws Exception {
            try {
                LOGGER.info("call "+info.getMovie().getTitle());
                info.setStatus(MovieStatus.LOADING);
                // set true, if we should call extra services for this movie
                boolean needExtraServiceCheck = false;
                if (info.getMovie().getId() == null) {
                	//info.setMovieFile(movieDatabase.getOrCreateFile(info.getDirectory().getAbsolutePath()));
                    // TODO load movie
                    FileGroup group = findMovie(info);
                    if (group!=null) {
                        group.getDirectory(info.getDirectory().getAbsolutePath());
                        //directory.setName(info.getDirectory().getName());
                        info.setMovie(movieDatabase.insertOrUpdate(group.getMovie()));
                    } else {
                        needExtraServiceCheck = fetchInformation();
                    }
                    info.setStatus(MovieStatus.LOADED);
                } else {
                    if (info.isNeedRefetch()) {
                        if (info.getLoadType() != LoadType.NEW) {
                            fetchMoviePageInfo(info.getMovie(), preferredService);
                            needExtraServiceCheck = true;
                        } else {
                            needExtraServiceCheck = getMovieInfoImdb(info, preferredService);
                        }
                        info.setMovie(movieDatabase.insertOrUpdate(info.getMovie()));
                    }
                    info.setStatus(MovieStatus.CACHED);
                }

                if (needExtraServiceCheck) {
                    checkWithSecondaryServices(info, preferredService);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception while loading/saving movie", ex);
                info.setStatus(MovieStatus.ERROR);
            }
        }

        private boolean fetchInformation() {
            StorableMovie movie;
            boolean checkSucceeded = getMovieInfoImdb(info, preferredService);
            if (checkSucceeded) {
                movie = movieDatabase.findMovieByTitle(info.getMovie().getTitle());
                if (movie == null) {
                    info.setMovie(movieDatabase.insertOrUpdate(info.getMovie()));
       
                } else {
                    // the movie is already in the database, but we haven't find this movie by 
                    // it's files, so it must be a different version, so we add as a new file
                    // group.
                    FileGroup newFileGroup = info.getMovie().getUniqueFileGroup();
                    newFileGroup.getDirectory(info.getDirectory().getAbsolutePath());
                    movie.addFileGroup(newFileGroup);
                	//directory.setName(info.getDirectory().getName());
                	info.setMovie(movieDatabase.insertOrUpdate(movie));
                }
            }
            info.setNeedRefetch(false);
            return checkSucceeded;
        }

        private FileGroup findMovie(MovieInfo info) {
            for (StorableMovieFile file : info.getMovie().getUniqueFileGroup().getFiles()) {
                if (file.getType()==FileType.VIDEO_CONTENT) {
                    FileGroup fileGroup = movieDatabase.findByFile(file.getName(), file.getSize());
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
         * @param mainServicePool
         * @param info
         */
        public MovieServiceCaller(final MovieService service, final MovieInfo info) {
            this.service = service;
            this.info = info;
            runningTasks.incrementAndGet();
        }
        @Override
        public MovieInfo call() throws Exception {
            synchronized(info) {
                doCall();
            }
            runningTasks.decrementAndGet();
            return info;
        }
        
        private void doCall() throws Exception {
            // TODO make a null entry if movie not found? so we can do better reloading
            try {
                LOGGER.trace("Calling fetch on {} for '{}'", service.name(), info.getMovie().getTitle());
                info.setStatus(MovieStatus.LOADING);
                
                fetchMoviePageInfo(info.getMovie(), service);
                
                info.setMovie(movieDatabase.insertOrUpdate(info.getMovie()));
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
     * @throws IOException 
     */
    private boolean getMovieInfoImdb(MovieInfo movieInfo, MovieService movieService) {
        StorableMovie newMovie = movieInfo.getMovie();
        try {
            MoviePage moviePage = fetchMoviePageInfo(movieInfo.getMovie(), movieService);
            if (moviePage!=null && moviePage.getIdForSite()!=null) {
                converter.convert(moviePage, newMovie);
                //rename titles, if we have IMDB result
                if (settings.getRenameTitles() && moviePage.getIdForSite()!=null) {
                    renameFolderToTitle(movieInfo);
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("info fetching from "+movieService+" failed : "+e.getMessage(), e);
            return false;
        }

    // todo insert the site?
    }

    
    /**
     * fetch all movie information from the given movie mainServicePool, and put into the MovieInfo/StorableMovie/StorableMovieSite,
     * and returns the modified StorableMovieSite.
     * 
     * @param info
     * @param service 
     * @return the movie page
     * @throws IOException
     */
    protected MoviePage fetchMoviePageInfo(StorableMovie movie, MovieService service) throws IOException {
        MoviePage moviePage;
        StorableMovieSite storableMovieSite = movie.getMovieSiteInfo(service);
        MovieInfoFetcher fetcher = fetcherFactory.get(service);
        if (storableMovieSite==null || storableMovieSite.getIdForSite()==null) {
            moviePage = fetcher.fetch(movie.getTitle());
            storableMovieSite = movie.getMovieSiteInfoOrCreate(service);
        } else {
            moviePage = fetcher.getMovieInfo(storableMovieSite.getIdForSite());
        }
        if (moviePage!=null) {
            converter.convert(moviePage, storableMovieSite);
        } else {
            LOGGER.warn("Movie Page not found for "+movie.getTitle()+" by "+service);
        }
        return moviePage;
    }
    
    public int getRunningTasks() {
        return runningTasks.get();
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
                info.setMovie(movieDatabase.insertOrUpdate(info.getMovie()));
            }
            info.triggerUpdate();
        }
        return true;
    }

    /**
     * Submit tasks for load informations with services which from is not information presented yet.
     * @param info
     * @param exceptService
     */
    public void checkWithSecondaryServices(MovieInfo info, MovieService exceptService) {
        for (MovieService service : settings.getEnabledServices()) {
            if (service!=exceptService) {
                StorableMovieSite siteInfo = info.siteFor(service);
                if (siteInfo == null || siteInfo.getScore()==null) {
                    secondaryServicePool.submit(new MovieServiceCaller(service, info));
                }
            }
        }
    }

    /**
     * Submit tasks for load informations with services which from is not information presented yet.
     * @param info
     * @param exceptService
     */
    public void checkWithSecondaryServices(List<MovieInfo> info, MovieService exceptService) {
        for (MovieInfo movie : info) {
            checkWithSecondaryServices(movie, exceptService);
        }
    }

    
}
