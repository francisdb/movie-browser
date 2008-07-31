/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.service.parser;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class TomatoesParser implements Parser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TomatoesParser.class);

    @Override
    public void parse(Source source, Movie movie) {
        List<?> divElements = source.findAllElements(HTMLElementName.DIV);
        for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
            Element divElement = (Element) i.next();
            String id = divElement.getAttributeValue("id");
            if (id != null && "bubble_allCritics".equals(id)) {
                String userRating = divElement.getContent().getTextExtractor().toString().trim();
                if (!"".equals(userRating)) {
                    userRating = userRating.replace("%", "");
                    try {
                        movie.setTomatoScore(Integer.valueOf(userRating));
                    } catch (NumberFormatException ex) {
                        LOGGER.error("Could not parse " + userRating + " to Integer", ex);
                    }
                }
            }
        }

    }
}