package com.flicklib.service.movie.tomatoes;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.flicklib.domain.MoviePage;
import com.google.inject.Singleton;
import com.flicklib.service.movie.AbstractJerichoParser;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class TomatoesParser extends AbstractJerichoParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TomatoesParser.class);

    @Override
    public void parse(final String html, Source source, MoviePage movieSite) {
        List<?> divElements = source.findAllElements(HTMLElementName.DIV);
        for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
            Element divElement = (Element) i.next();
            String id = divElement.getAttributeValue("id");
            if (id != null && "bubble_allCritics".equals(id)) {
                String userRating = divElement.getContent().getTextExtractor().toString().trim();
                if (!"".equals(userRating)) {
                    userRating = userRating.replace("%", "");
                    try {
                        int score = Integer.valueOf(userRating);
                        movieSite.setScore(score);
                    } catch (NumberFormatException ex) {
                        LOGGER.error("Could not parse " + userRating + " to Integer", ex);
                    }
                }
            }
        }

    }
}
