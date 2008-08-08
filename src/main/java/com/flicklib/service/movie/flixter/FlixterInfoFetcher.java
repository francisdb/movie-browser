package com.flicklib.service.movie.flixter;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.SourceLoader;
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
public class FlixterInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlixterInfoFetcher.class);
    private final SourceLoader sourceLoader;
    private final Parser parser;

    @Inject
    public FlixterInfoFetcher(final @Flixter Parser parser, final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
        this.parser = parser;
    }

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.FLIXSTER);
        try {
            String source = sourceLoader.load(createFlixterSearchUrl(movie));
            Source jerichoSource = new Source(source);
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            jerichoSource.fullSequentialParse();

            // <a onmouseover="mB(event, 770678072);" title=" The X-Files: I Want to Believe (The X Files 2)" href="/movie/the-x-files-i-want-to-believe-the-x-files-2"  >
            // The X-Files: I Want to Believe (The X Files 2)
            // </a>

            String movieUrl = null;
            List<?> aElements = jerichoSource.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = aElements.iterator(); i.hasNext();) {
                Element aElement = (Element) i.next();
                String url = aElement.getAttributeValue("href");
                if (url != null && url.startsWith("/movie/")) {
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    if (movieUrl == null && movieName != null && movieName.trim().length() != 0) {

                        movieUrl = "http://www.flixster.com" + url;
                        LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                    }
                }
            }
            if (movieUrl == null) {
                throw new IOException("Movie not found on Flixter: " + movie.getTitle());
            }
            site.setUrl(movieUrl);
            source = sourceLoader.load(movieUrl);
            parser.parse(source, site);
        } catch (IOException ex) {
            LOGGER.error("Loading from Flixter failed", ex);
        }
        return site;
    }

    private String createFlixterSearchUrl(Movie movie) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(movie.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.flixster.com/movies.do?movieAction=doMovieSearch&x=0&y=0&search=" + encoded;
    }
}
