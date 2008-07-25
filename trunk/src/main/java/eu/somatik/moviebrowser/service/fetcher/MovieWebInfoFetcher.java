/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.service.fetcher;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.module.MovieWeb;
import eu.somatik.moviebrowser.service.HttpLoader;
import eu.somatik.moviebrowser.service.parser.Parser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class MovieWebInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieWebInfoFetcher.class);

    private final Parser movieWebInfoParser;
    private final HttpLoader httpLoader;

    /**
     * Creates a new MovieWebInfoFetcher
     * @param movieWebInfoParser
     * @param httpLoader 
     */
    @Inject
    public MovieWebInfoFetcher(final @MovieWeb Parser movieWebInfoParser, final HttpLoader httpLoader) {
        this.movieWebInfoParser = movieWebInfoParser;
        this.httpLoader = httpLoader;
    }

    @Override
    public void fetch(Movie movie) {
        try {
            Source source = httpLoader.fetch(createMovieWebSearchUrl(movie));
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();

            //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
            //System.out.println(titleElement.getContent().extractText());

            // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>

            String movieUrl = null;
            List<?> aElements = source.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = aElements.iterator(); i.hasNext();) {
                Element aElement = (Element) i.next();
                String url = aElement.getAttributeValue("href");
                if (url != null && url.endsWith("summary.php")) {
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    if (movieUrl == null && movieName != null && movieName.trim().length() != 0) {

                        movieUrl = "http://www.movieweb.com" + url;
                        LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                    }
                }
            }
            if (movieUrl == null) {
                throw new IOException("Movie not found on MovieWeb");
            }
            source = httpLoader.fetch(movieUrl);
            movieWebInfoParser.parse(source, movie);
        } catch (IOException ex) {
            LOGGER.error("Loading from MovieWeb failed", ex);
        }
    }

    private String createMovieWebSearchUrl(Movie movie) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(movie.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.movieweb.com/search/?search=" + encoded;
    }

}
