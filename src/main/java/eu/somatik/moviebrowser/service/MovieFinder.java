/*
 * MovieFinder.java
 *
 * Created on January 20, 2007, 1:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.EndTag;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.scanner.FileSystemScanner;
import eu.somatik.moviebrowser.service.MovieInfoFetcher;
import eu.somatik.moviebrowser.service.MovieWebInfoFetcher;
import eu.somatik.moviebrowser.service.TomatoesInfoFetcher;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieFinder.class);
    private static final String TO_REMOVE[] = {
        ".dvdrip",
        ".samplefix",
        ".dvdivx",
        ".dvdivx4",
        ".dvdivx5",
        ".divx",
        ".xvid",
        ".limited",
        ".internal",
        ".proper",
        ".dc",
        ".ac3",
        ".unrated",
        ".stv",
        ".dutch",
        ".limited",
        ".nfofix"    //".ws"        
    };
    private final ExecutorService service;
    private final ExecutorService secondaryService;
    private MovieCache movieCache;
    private final FileSystemScanner fileSystemScanner;

    /**
     * Creates a new instance of MovieFinder
     */
    public MovieFinder() {
        this.fileSystemScanner = new FileSystemScanner();
        this.service = Executors.newFixedThreadPool(5);
        this.secondaryService = Executors.newFixedThreadPool(5);
    }

    public void init() {
        this.movieCache = new MovieCache();
    }

    /**
     * Stops the finder
     */
    public void stop() {
        movieCache.shutdown();
        service.shutdownNow();
        secondaryService.shutdown();
    }

    /**
     * Loads all movies
     * @param movies
     */
    public void loadMovies(List<MovieInfo> movies) {
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
            MovieInfo loaded;
            if (movie == null || movie.getImdbId() == null) {
                loaded = getMovieInfo(info);
                secondaryService.submit(new TomatoesCaller(info));
                secondaryService.submit(new MovieWebCaller(info));
                movieCache.saveMovie(loaded.getMovie());
            } else {
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
            super(new TomatoesInfoFetcher(), info);
        }
    }

    private class MovieWebCaller extends AbstractMovieCaller {

        /**
         * Constructs a new MovieCaller object
         *
         * @param info
         */
        public MovieWebCaller(MovieInfo info) {
            super(new MovieWebInfoFetcher(), info);
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
            this.fetcher = fetcher;
            this.info = info;
        }

        @Override
        public MovieInfo call() throws Exception {
            info.setStatus(MovieStatus.LOADING_TOMATOES);
            LOGGER.info("Calling fetch on " + fetcher.getClass().getSimpleName());
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
    public MovieInfo getMovieInfo(MovieInfo movieInfo) throws UnknownHostException, Exception {
        movieInfo.setStatus(MovieStatus.LOADING_IMDB);
        LOGGER.info(movieInfo.getDirectory().getAbsolutePath());
        String url = fileSystemScanner.findNfoUrl(movieInfo.getDirectory());
        if (url == null) {
            String title = removeCrap(movieInfo.getDirectory().getName());
            url = generateImdbSearchUrl(title);

        }


        movieInfo.getMovie().setUrl(url);
        movieInfo.getMovie().setImdbId(url.replaceAll("[a-zA-Z:/.+=?]", "").trim());

        Source source = getParsedSource(movieInfo);

        return parseImdbHtml(source, movieInfo);

    }

    private MovieInfo parseImdbHtml(Source source, MovieInfo movieInfo) throws Exception {
        Element titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
        if (titleElement.getContent().getTextExtractor().toString().contains("Title Search")) {
            //find the first link
            movieInfo.getMovie().setUrl(null);
            List<?> linkElements = source.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = linkElements.iterator(); i.hasNext() && movieInfo.getMovie().getUrl() == null;) {
                Element linkElement = (Element) i.next();
                String href = linkElement.getAttributeValue("href");
                if (href != null && href.startsWith("/title/tt")) {
                    int questionMarkIndex = href.indexOf('?');
                    if (questionMarkIndex != -1) {
                        href = href.substring(0, questionMarkIndex);
                    }
                    movieInfo.getMovie().setUrl(href);
                    movieInfo.getMovie().setImdbId(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                    source = getParsedSource(movieInfo);
                    titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
                }
            }

        }
        String titleYear = titleElement.getContent().getTextExtractor().toString();
        
        if(titleYear.endsWith(")")){
            int index = titleYear.lastIndexOf("(");
            String year = titleYear.substring(index+1, titleYear.length()-1);
            try{
                movieInfo.getMovie().setYear(Integer.valueOf(year));
            }catch(NumberFormatException ex){
                LOGGER.error("Could not parse '"+year+"' to integer", ex);
            }
            titleYear = titleYear.substring(0, index);
        }
        movieInfo.getMovie().setTitle(titleYear);
        

        List<?> linkElements = source.findAllElements(HTMLElementName.A);
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element linkElement = (Element) i.next();

            if ("poster".equals(linkElement.getAttributeValue("name"))) {

                // A element can contain other tags so need to extract the text from it:
                List<?> imgs = linkElement.getContent().findAllElements(HTMLElementName.IMG);
                Element img = (Element) imgs.get(0);
                String imgUrl = img.getAttributeValue("src");

                movieInfo.getMovie().setImgUrl(imgUrl);
                ImageCache.saveImgToCache(movieInfo);
            }
            String href = linkElement.getAttributeValue("href");
            if (href != null && href.startsWith("/Sections/Genres/")) {
                Genre genre = movieCache.getOrCreateGenre(linkElement.getContent().getTextExtractor().toString());
                movieInfo.getMovie().addGenre(genre);
            }
            if (href != null && href.startsWith("/Sections/Languages/")) {
                Language language = movieCache.getOrCreateLanguage(linkElement.getContent().getTextExtractor().toString());
                movieInfo.getMovie().addLanguage(language);
            }

        }

        linkElements = source.findAllElements(HTMLElementName.B);
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element bElement = (Element) i.next();
            if (bElement.getContent().getTextExtractor().toString().contains("User Rating:")) {
                Element next = source.findNextElement(bElement.getEndTag().getEnd());
                String rating = next.getContent().getTextExtractor().toString();
                // to percentage
                rating = rating.replace("/10", "");
                try {
                    int theScore = Math.round(Float.valueOf(rating).floatValue() * 10);
                    movieInfo.getMovie().setImdbScore(Integer.valueOf(theScore));
                } catch (NumberFormatException ex) {
                    LOGGER.error("Could not parse " + rating + " to Float", ex);
                }
                next = source.findNextElement(next.getEndTag().getEnd());
                movieInfo.getMovie().setVotes(next.getContent().getTextExtractor().toString());
            }
        }

        linkElements = source.findAllElements(HTMLElementName.H5);
        String hText;
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element hElement = (Element) i.next();
            hText = hElement.getContent().getTextExtractor().toString();
            if (hText.contains("Plot Outline")) {
                int end = hElement.getEnd();
                movieInfo.getMovie().setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            }else if (hText.contains("Plot:")) {
                int end = hElement.getEnd();
                movieInfo.getMovie().setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            }else if (hText.contains("Runtime")) {
                int end = hElement.getEnd();
                EndTag next = source.findNextEndTag(end);
                //System.out.println(next);
                String runtime = source.subSequence(end, next.getBegin()).toString().trim();
                movieInfo.getMovie().setRuntime(parseRuntime(runtime));
            }
        }

        if (movieInfo.getMovie().getTitle() == null) {
            //System.out.println(source.toString());
            movieInfo.getMovie().setPlot("Not found");
        }

        return movieInfo;
    }

    private Integer parseRuntime(String runtimeString) {
        String runtime = runtimeString.substring(0, runtimeString.indexOf("min")).trim();
        int colonIndex = runtime.indexOf(":");
        if (colonIndex != -1) {
            runtime = runtime.substring(colonIndex + 1);
        }

        return Integer.valueOf(runtime);
    }

    /**
     *
     * @param movieInfo
     * @return the parsed source
     * @throws Exception 
     */
    public Source getParsedSource(MovieInfo movieInfo) throws Exception {

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(generateImdbUrl(movieInfo.getMovie()));
        client.executeMethod(method);

//        Session s = new Session();
//        String url = generateImdbUrl(movieInfo);
//        System.out.println("Loading "+url);
//        Response r = s.get(url);
        //System.out.println("HEADERS: " + Arrays.toString(r.getHeaders()));

        //PHPTagTypes.register();
        //PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        //MasonTagTypes.register();
        Source source = null;
        source = new Source(method.getResponseBodyAsString());
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        return source;
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

    /**
     * @param title 
     * @return the imdb url
     */
    public static String generateImdbSearchUrl(String title) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.imdb.com/Tsearch?title=" + encoded;
    }

    private String removeCrap(String name) {
        String movieName = name.toLowerCase();
        for (String bad : TO_REMOVE) {
            movieName = movieName.replaceAll(bad, "");
        }

        Calendar calendar = new GregorianCalendar();
        int thisYear = calendar.get(Calendar.YEAR);

        //TODO recup the movie year!

        for (int i = 1800; i < thisYear; i++) {
            movieName = movieName.replaceAll(Integer.toString(i), "");
        }
        int dashPos = movieName.lastIndexOf('-');
        if (dashPos != -1) {
            movieName = movieName.substring(0, movieName.lastIndexOf('-'));
        }
        movieName = movieName.replaceAll("\\.", " ");
        movieName = movieName.trim();
        return movieName;
    }    //    /**
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
