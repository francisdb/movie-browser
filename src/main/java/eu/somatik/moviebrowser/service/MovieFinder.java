/*
 * MovieFinder.java
 *
 * Created on January 20, 2007, 1:51 PM
 *
 */
package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.cache.MovieCacheImpl;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.service.imdb.Imdb;
import eu.somatik.moviebrowser.service.movieweb.MovieWeb;
import eu.somatik.moviebrowser.service.tomatoes.RottenTomatoes;
import eu.somatik.moviebrowser.service.imdb.ImdbSearch;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.service.flixter.Flixter;
import eu.somatik.moviebrowser.service.google.Google;
import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieFinder.class);
    private final ExecutorService service;
    private final ExecutorService secondaryService;
    private final FileSystemScanner fileSystemScanner;
    private final MovieInfoFetcher movieWebInfoFetcher;
    private final MovieInfoFetcher tomatoesInfoFetcher;
    private final MovieInfoFetcher googleInfoFetcher;
    private final MovieInfoFetcher flixterInfoFetcher;
    private final MovieNameExtractor movieNameExtractor;
    private final MovieCacheImpl movieCache;
    private final Parser imdbParser;
    private final ImdbSearch imdbSearch;
    private final HttpSourceLoader httpLoader;

    /**
     * Creates a new instance of MovieFinder
     * @param movieWebInfoFetcher
     * @param tomatoesInfoFetcher
     * @param googleInfoFetcher 
     * @param flixterInfoFetcher 
     * @param movieCache
     * @param fileSystemScanner
     * @param movieNameExtractor
     * @param imdbParser
     * @param imdbSearch
     * @param httpLoader
     */
    @Inject
    public MovieFinder(
            final @MovieWeb MovieInfoFetcher movieWebInfoFetcher,
            final @RottenTomatoes MovieInfoFetcher tomatoesInfoFetcher,
            final @Google MovieInfoFetcher googleInfoFetcher,
            final @Flixter MovieInfoFetcher flixterInfoFetcher,
            final MovieCacheImpl movieCache,
            final FileSystemScanner fileSystemScanner,
            final MovieNameExtractor movieNameExtractor,
            final @Imdb Parser imdbParser,
            final ImdbSearch imdbSearch,
            final HttpSourceLoader httpLoader) {
        this.movieWebInfoFetcher = movieWebInfoFetcher;
        this.tomatoesInfoFetcher = tomatoesInfoFetcher;
        this.googleInfoFetcher = googleInfoFetcher;
        this.flixterInfoFetcher = flixterInfoFetcher;
        this.movieCache = movieCache;
        this.fileSystemScanner = fileSystemScanner;
        this.movieNameExtractor = movieNameExtractor;
        this.imdbParser = imdbParser;
        this.imdbSearch = imdbSearch;
        this.httpLoader = httpLoader;
        
        this.service = Executors.newFixedThreadPool(5);
        this.secondaryService = Executors.newFixedThreadPool(5);
    }

    /**
     * Stops the finder
     */
    public void stop() {
        movieCache.shutdown();
        service.shutdownNow();
        secondaryService.shutdownNow();
    }

    public void start(){
        if (!movieCache.isStarted()) {
            movieCache.startup();
        }
    }
     
    public void reloadMovie(MovieInfo movieInfo){
        List<MovieInfo> list = new ArrayList<MovieInfo>();
        list.add(movieInfo);
        movieInfo.setImage(null);
        movieCache.removeMovie(movieInfo.getMovie());
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
            Movie movie = movieCache.find(info.getMovie().getPath());
            MovieInfo loaded = null;
            if (movie == null || movie.getImdbId() == null) {
                try{
                    LOGGER.info("Fetching data for "+info.getMovie().getPath());
                    info.setStatus(MovieStatus.LOADING_IMDB);
                    loaded = getMovieInfo(info);
                    secondaryService.submit(new TomatoesCaller(loaded));
                    secondaryService.submit(new MovieWebCaller(loaded));
                    secondaryService.submit(new GoogleCaller(loaded));
                    secondaryService.submit(new FlixterCaller(loaded));
                    movieCache.saveMovie(loaded.getMovie());
                }catch(Exception ex){
                    LOGGER.error("Exception while loading/saving movie", ex);
                }
            } else {
                LOGGER.info("Loading cached data for "+info.getMovie().getPath());
                info.setStatus(MovieStatus.CACHED);
                info.setMovie(movie);
                loaded = info;
            }
            return loaded;
        }
    }

    private class TomatoesCaller extends AbstractMovieCaller {

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public TomatoesCaller(MovieInfo info) {
            super(tomatoesInfoFetcher, info);
        }
    }

    private class MovieWebCaller extends AbstractMovieCaller {

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public MovieWebCaller(MovieInfo info) {
            super(movieWebInfoFetcher, info);
        }
    }
    
    private class GoogleCaller extends AbstractMovieCaller {

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public GoogleCaller(MovieInfo info) {
            super(googleInfoFetcher, info);
        }
    }
    
    private class FlixterCaller extends AbstractMovieCaller {

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public FlixterCaller(MovieInfo info) {
            super(flixterInfoFetcher, info);
        }
    }

    private abstract class AbstractMovieCaller implements Callable<MovieInfo> {

        private final MovieInfoFetcher fetcher;
        private final MovieInfo info;

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public AbstractMovieCaller(final MovieInfoFetcher fetcher, final MovieInfo info) {
            LOGGER.info("New caller for " + fetcher.getClass().getSimpleName());
            this.fetcher = fetcher;
            this.info = info;
        }

        @Override
        public MovieInfo call() throws Exception {
            LOGGER.info("Calling fetch on " + fetcher.getClass().getSimpleName());
            info.setStatus(MovieStatus.LOADING_TOMATOES);
            fetcher.fetch(info.getMovie());
            movieCache.saveMovie(info.getMovie());
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
    private MovieInfo getMovieInfo(MovieInfo movieInfo) throws UnknownHostException, Exception {
        movieInfo.setStatus(MovieStatus.LOADING_IMDB);
        String url = null;
        if(movieInfo.getMovie().getImdbId() != null){
            url = generateImdbUrl(movieInfo.getMovie());
        }
        if (url == null) {
            url = fileSystemScanner.findNfoUrl(movieInfo.getDirectory());
        }
        if (url == null) {
            String title = movieNameExtractor.removeCrap(movieInfo.getDirectory().getName());
            url = imdbSearch.generateImdbTitleSearchUrl(title);

        }

        movieInfo.getMovie().setUrl(url);
        movieInfo.getMovie().setImdbId(url.replaceAll("[a-zA-Z:/.+=?]", "").trim());

        String source = httpLoader.load(generateImdbUrl(movieInfo.getMovie()));

        return parseImdbHtml(source, movieInfo);

    }

    private MovieInfo parseImdbHtml(String source, MovieInfo movieInfo) throws Exception {
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        if (titleElement.getContent().getTextExtractor().toString().contains("Title Search")) {
            List<Movie> movies = imdbSearch.parseResults(source);
            if(movies.size() == 0){
                throw new IOException("No movies found");
            }else{
                //use the first link
                Movie firstMovie = movies.get(0);
                movieInfo.getMovie().setImdbId(firstMovie.getImdbId());
                source = httpLoader.load(generateImdbUrl(firstMovie));
            }
        }
        imdbParser.parse(source, movieInfo.getMovie());
        return movieInfo;
    }


    /**
     *
     * @param movie 
     * @return the tomatoes url
     */
    public static String generateTomatoesUrl(Movie movie) {
        return "http://www.rottentomatoes.com/alias?type=imdbid&s=" + movie.getImdbId();
    }

    /**
     * @param movie 
     * @return the imdb url
     */
    public static String generateImdbUrl(Movie movie) {
        String id = movie.getImdbId();
        if ("".equals(id)) {
            return movie.getUrl();
        } else {
            return "http://www.imdb.com/title/tt" + id + "/";
        }
    }


    
    
    
    
    //    /**
    //     * Test class for the apache htpclient
    //     */
    //    public void httpclient(){
    //        // initialize the POST method
    //        GetMethod get = new GetMethod("http://www.imdb.com/Tsearch?title=idiocracy");
    //        System.out.println(get.getQueryString());
    //
    //        // execute the POST
    //        HttpClient client = new HttpClient();
    //
    //        try{
    //            int status = client.executeMethod(get);
    //            String response = get.getResponseBodyAsString();
    //            get.releaseConnection();
    //            System.out.println(response);
    //        }catch(IOException ex){
    //            ex.printStackTrace();
    //        }
    //    }
    //    /**
    //     * Runs JTidy on the source string, to produce the dest string.
    //     */
    //    private static String tidy(String source) {
    //        try {
    //            org.w3c.tidy.Tidy tidy = new org.w3c.tidy.Tidy();
    //            tidy.setXHTML(true);
    //            tidy.setShowWarnings(false);
    //            tidy.setSmartIndent(true);
    //            ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
    //            ByteArrayOutputStream out = new ByteArrayOutputStream();
    //            tidy.parse(in, out);
    //            in.close();
    //            return out.toString();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return source;
    //        }
    //    }
    //    public void testSwingX() throws Exception{
    //        Session s = new Session();
    //        Response r = s.get("http://www.imdb.com/search");
    //        Form form = Forms.getFormByIndex(r,1);
    //        System.out.println("FORM "+form.getMethod() + "(" + form.getAction() + ")");
    //        if(form != null){
    //            form.getInput("s").setValue("tt");
    //            form.getInput("q").setValue("idiocracy");
    //            for(Input input:form.getInputs()){
    //                System.out.println(input.getName()+":"+input.getValue());
    //            }
    //
    //
    //            r = Forms.submit(form,s);
    //            System.out.println(r.getBody());
    //        }
    //    }
    //    public void testDom() throws Exception{
    //
    //        Session s = new Session();
    //        Response r = s.get("http://www.imdb.com/Tsearch?title=idiocracy");
    //        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    //        String tidyHtml = tidy(r.getBody());
    //        System.out.println(tidyHtml);
    //        ByteArrayInputStream in = new ByteArrayInputStream(tidyHtml.getBytes());
    //        Document doc = builder.parse(in);
    //        in.close();
    //
    //        XPathFactory factory = XPathFactory.newInstance();
    //        XPath xpath = factory.newXPath();
    //        XPathExpression e = XPathUtils.compile("//form[2]");
    //        Node foundNode = (Node)e.evaluate(doc, XPathConstants.NODE);
    //        String href = xpath.evaluate("@action", foundNode);
    //        String method = xpath.evaluate("@method", foundNode);
    //        System.out.println("FORM "+method + "(" + href + ")");
    //    }
}
