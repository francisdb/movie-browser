package eu.somatik.moviebrowser.service.tomatoes;

import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.HttpSourceLoader;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.api.Parser;
import java.io.IOException;

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
    private final HttpSourceLoader httpLoader;

    @Inject
    public TomatoesInfoFetcher(final @RottenTomatoes Parser tomatoesParser, final HttpSourceLoader httpLoader) {
        this.httpLoader = httpLoader;
        this.tomatoesParser = tomatoesParser;
    }

    @Override
    public void fetch(Movie movie) {
        if (!"".equals(movie.getImdbId())) {
            try {
                Source source = httpLoader.load(MovieFinder.generateTomatoesUrl(movie));
                //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
                source.fullSequentialParse();

                //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
                //System.out.println(titleElement.getContent().extractText());

                // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>

                tomatoesParser.parse(source, movie);
            } catch (IOException ex) {
                LOGGER.error("Loading from rotten tomatoes failed", ex);
            }
        }
    }
}
