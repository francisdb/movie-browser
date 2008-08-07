package com.flicklib.service.movie.movieweb;

import com.flicklib.api.MovieInfoFetcher;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.domain.Movie;
import com.flicklib.service.HttpSourceLoader;
import com.flicklib.api.Parser;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MovieSite;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
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
    private final HttpSourceLoader httpLoader;

    /**
     * Creates a new MovieWebInfoFetcher
     * @param movieWebInfoParser
     * @param httpLoader 
     */
    @Inject
    public MovieWebInfoFetcher(final @MovieWeb Parser movieWebInfoParser, final HttpSourceLoader httpLoader) {
        this.movieWebInfoParser = movieWebInfoParser;
        this.httpLoader = httpLoader;
    }

    @Override
    public MovieSite fetch(Movie movie) {
        MovieSite site = new MovieSite();
        site.setMovie(movie);
        site.setService(MovieService.MOVIEWEB);
        site.setTime(new Date());
        try {
            String source = httpLoader.load(createMovieWebSearchUrl(movie));
            Source jerichoSource = new Source(source);
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            jerichoSource.fullSequentialParse();

            //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
            //System.out.println(titleElement.getContent().extractText());

            // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>

            String movieUrl = null;
            List<?> aElements = jerichoSource.findAllElements(HTMLElementName.A);
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
                throw new IOException("Movie not found on MovieWeb: "+movie.getTitle());
            }
            site.getMovie().setMoviewebUrl(movieUrl);
            site.setUrl(movieUrl);
            source = httpLoader.load(movieUrl);
            movieWebInfoParser.parse(source, site);
        } catch (IOException ex) {
            LOGGER.error("Loading from MovieWeb failed", ex);
        }
        return site;
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
