/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service.google;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
import java.io.IOException;
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
public class GoogleInfoFetcher implements MovieInfoFetcher{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleInfoFetcher.class);
    
    private final Parser googleParser;
    private final HttpSourceLoader httpLoader;

    @Inject
    public GoogleInfoFetcher(final @Google Parser googleParser, HttpSourceLoader httpLoader) {
        this.googleParser = googleParser;
        this.httpLoader = httpLoader;
    }
    
    

    @Override
    public void fetch(Movie movie) {
        try {
            String movieParam = URLEncoder.encode(movie.getTitle(), "utf-8");
            Source source = httpLoader.load("http://www.google.com/movies?q="+movieParam);
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
                // /movies/reviews?cid=b939f27b219eb36f&fq=Pulp+Fiction&hl=en
                if (url != null && url.startsWith("/movies/reviews?cid=")) {
                    movieUrl = "http://www.google.com" + url;
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                }
            }
            if (movieUrl == null) {
                throw new IOException("Movie not found on Google: "+movie.getTitle());
            }
            source = httpLoader.load(movieUrl);
            googleParser.parse(source, movie);
        } catch (IOException ex) {
            LOGGER.error("Loading from Google failed", ex);
        }
    }

}
