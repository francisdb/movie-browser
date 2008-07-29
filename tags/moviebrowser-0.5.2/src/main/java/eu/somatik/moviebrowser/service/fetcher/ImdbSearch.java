package eu.somatik.moviebrowser.service.fetcher;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class ImdbSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbSearch.class);
    private final HttpSourceLoader httpLoader;

    @Inject
    public ImdbSearch(HttpSourceLoader httpLoader) {
        this.httpLoader = httpLoader;
    }

    public List<Movie> getResults(Source source) throws IOException{
        List<Movie> results = new ArrayList<Movie>();
        Element titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (title.contains("IMDb") && title.contains("Search")) {

            List<?> linkElements = source.findAllElements(HTMLElementName.A);
            Element linkElement;
            Movie movie;
            Iterator<?> i = linkElements.iterator();
            Set<String> ids = new HashSet<String>();
            while (i.hasNext()) {
                movie = new Movie();
                linkElement = (Element) i.next();
                String href = linkElement.getAttributeValue("href");
                if (href != null && href.startsWith("/title/tt")) {
                    int questionMarkIndex = href.indexOf('?');
                    if (questionMarkIndex != -1) {
                        href = href.substring(0, questionMarkIndex);
                    }
                    movie.setUrl("http://www.imdb.com" + href);
                    movie.setImdbId(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                    movie.setTitle(linkElement.getTextExtractor().toString());
                    // only add if not allready in the list
                    if (movie.getTitle().length() > 0 && !ids.contains(movie.getImdbId())) {
                        ids.add(movie.getImdbId());
                        results.add(movie);
                    }
                }
            }

        }else{
            
            //Assume it's a perfect result, therefore get first /title/tt link.
            List<?> linkElements = source.findAllElements(HTMLElementName.A);
            Element linkElement;
            Movie movie;
            Iterator<?> i = linkElements.iterator();
            Set<String> ids = new HashSet<String>();
            while (i.hasNext()) {
                movie = new Movie();
                linkElement = (Element) i.next();
                String href = linkElement.getAttributeValue("href");
                if (href != null && href.startsWith("/title/tt")) {
                    int questionMarkIndex = href.indexOf('?');
                    if (questionMarkIndex != -1) {
                        href = href.substring(0, questionMarkIndex);
                    }
                    //href has to be split as href will be in from of /title/tt#######/some-other-dir-like-trailers
                    String[] split = href.split("/");
                    href = "/" + split[1] + "/" + split[2];
                    movie.setUrl("http://www.imdb.com" + href);
                    movie.setImdbId(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                    //set title as the movies title since this is a perfect search result who's HTMLElementName.title will be the movie title.
                    movie.setTitle(title);
                    // only add if not allready in the list
                    if (movie.getTitle().length() > 0 && !ids.contains(movie.getImdbId())) {
                        //Only add to the set and results list if they are empty, i.e. only if the first result, since perfect result assumed. The rest of the identified links will be the IMDB recommendations for the particular perfect result.
                        if(ids.isEmpty() && results.isEmpty()) {
                            ids.add(movie.getImdbId());
                            results.add(movie);
                        }
                    }
                }
            }
        }
        return results;

    }

    public List<Movie> getResults(String search) throws Exception {
        Source source = httpLoader.load(generateImdbSearchUrl(search));
        return getResults(source);
    }

    /**
     * @param title 
     * @return the imdb url
     */
    public String generateImdbTitleSearchUrl(String title) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.imdb.com/Tsearch?title=" + encoded;
    }

    /**
     * @param title 
     * @return the imdb url
     */
    public String generateImdbSearchUrl(String title) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.imdb.com/find?q=" + encoded;
    }
}
