package eu.somatik.moviebrowser.service.fetcher;

import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.module.RottenTomatoes;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.parser.Parser;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fdb
 */
@Singleton
public class TomatoesInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomatoesInfoFetcher.class);

    private Parser tomatoesParser;

    @Inject
    public TomatoesInfoFetcher(final @RottenTomatoes Parser tomatoesParser) {
        this.tomatoesParser = tomatoesParser;
    }

    @Override
    public void fetch(Movie movie) {
        if (!"".equals(movie.getImdbId())) {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(MovieFinder.generateTomatoesUrl(movie));
            try {
                client.executeMethod(method);
                Source source = new Source(method.getResponseBodyAsStream());
                //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
                source.fullSequentialParse();

                //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
                //System.out.println(titleElement.getContent().extractText());

                // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>


                tomatoesParser.parse(source, movie);

            } catch (IOException ex) {
                LOGGER.error("Loading from rotten tomatoes failed", ex);
            } finally {
                // Release the connection.
                if (method != null) {
                    method.releaseConnection();
                }
            }
        }
    }
}
