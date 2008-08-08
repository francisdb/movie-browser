package com.flicklib.service.movie.movieweb;

import com.flicklib.tools.ElementOnlyTextExtractor;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.TextExtractor;
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
public class MovieWebParser extends AbstractJerichoParser{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieWebParser.class);

    @Override
    public void parse(Source source, MoviePage movieSite) {
         List<?> divElements = source.findAllElements(HTMLElementName.DIV);
        for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
            Element divElement = (Element) i.next();
            TextExtractor extractor = new ElementOnlyTextExtractor(divElement.getContent());
            String content = extractor.toString();
            if (content.startsWith("MovieWeb Users:")) {
                List childs = divElement.getChildElements();
                if (childs.size() > 0) {
                    String score = ((Element) childs.get(0)).getContent().getTextExtractor().toString().trim();
                    if (score.length() > 0) {
                        try {
                            float theScore = Float.valueOf(score).floatValue() * 20;
                            int intScore = Math.round(theScore);
                            movieSite.setScore(intScore);
                        } catch (NumberFormatException ex) {
                            LOGGER.error("Could not parse " + score + " to Float", ex);
                        }
                    }
                }
            } else if (content.startsWith("The Critics:")) {
                List childs = divElement.getChildElements();
                if (childs.size() > 0) {
                    String score = ((Element) childs.get(0)).getContent().getTextExtractor().toString();
                    LOGGER.debug("Critics score: " + score);
                // TODO use?
                }
            }
        }
    }

}
