package eu.somatik.moviebrowser.service.google;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieSite;
import eu.somatik.moviebrowser.service.AbstractJerichoParser;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class GoogleParser extends AbstractJerichoParser{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleParser.class);

    @Override
    public void parse(final Source source, final MovieSite movieSite) {
        List<?> nobrElements = source.findAllElements("nobr");
        for (Iterator<?> i = nobrElements.iterator(); i.hasNext();) {
            Element nobrElement = (Element) i.next();
            //3.9&nbsp;/&nbsp;5
            String score = nobrElement.getTextExtractor().toString();
            score = score.replace("&nbsp;", "");
            if(!score.contains("/")){
                LOGGER.error("Could not find score on imdb page: "+score);
            }
            score = score.substring(0, score.indexOf('/'));
            Double doubleScore = Double.valueOf(score);
            doubleScore = doubleScore  * 20.0;
            Integer intScore = (int) Math.round(doubleScore);
            movieSite.getMovie().setGoogleScore(intScore);
            movieSite.setScore(intScore);
        }
    }

}