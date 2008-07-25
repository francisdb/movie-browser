package eu.somatik.moviebrowser.service.fetcher;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpLoader;
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
    private final HttpLoader httpLoader;

    @Inject
    public ImdbSearch(HttpLoader httpLoader) {
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
            throw new IOException("Expected search page but found: "+title);
        }
        return results;

    }

    public List<Movie> getResults(String search) throws Exception {
        Source source = httpLoader.fetch(generateImdbSearchUrl(search));
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
