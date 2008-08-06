package eu.somatik.moviebrowser.service.flixter;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.TextExtractor;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.AbstractJerichoParser;
import eu.somatik.moviebrowser.tools.ElementOnlyTextExtractor;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class FlixterParser extends AbstractJerichoParser{

    private static final Logger LOGGER = LoggerFactory.getLogger(FlixterParser.class);
    
    @Override
    public void parse(Source source, Movie movie) {
        List<?> divElements = source.findAllElements(HTMLElementName.TH);
        for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
            Element thElement = (Element) i.next();
            TextExtractor extractor = new ElementOnlyTextExtractor(thElement.getContent());
            String content = extractor.toString().trim();
            if (content.equals("All Flixster")) {
                Element next = source.findNextElement(thElement.getEnd());
                List<?> childs = next.getChildElements();
                if (childs.size() > 0) {
                    Element imgElment = (Element) childs.get(0);
                    // <img src="/static/images/rating/3.0.png"   score="3.0 Stars" />
                    String score = imgElment.getAttributeValue("alt");
                    score = score.replaceAll("Stars", "");
                    if (score.length() > 0) {
                        try {
                            float theScore = Float.valueOf(score).floatValue() * 20;
                            int intScore = Math.round(theScore);
                            movie.setFlixterScore(intScore);
                        } catch (NumberFormatException ex) {
                            LOGGER.error("Could not parse " + score + " to Float", ex);
                        }
                    }
                }
            } 
//            else if (content.equals("Female")) {
//                // TODO use?
//                
//            } 
//            // ...
        }
    }

}
