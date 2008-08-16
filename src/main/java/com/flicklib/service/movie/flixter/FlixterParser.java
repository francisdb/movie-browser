/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.flicklib.service.movie.flixter;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.TextExtractor;
import com.flicklib.domain.MoviePage;
import com.google.inject.Singleton;
import com.flicklib.service.movie.AbstractJerichoParser;
import com.flicklib.tools.ElementOnlyTextExtractor;
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
    public void parse(String html, Source source, MoviePage movieSite) {
        // <a  title=" The X-Files: I Want to Believe (The X Files 2)" href="/movie/the-x-files-i-want-to-believe-the-x-files-2"  class="headerLink" >
        List<?> aElements = source.findAllElements(HTMLElementName.A);
        for (Iterator<?> i = aElements.iterator(); i.hasNext();) {
            Element aElement = (Element) i.next();
            if("headerLink".equals(aElement.getAttributeValue("class"))){
                movieSite.getMovie().setTitle(aElement.getContent().getTextExtractor().toString().trim());
            }
        }
        
        List<?> divElements = source.findAllElements(HTMLElementName.TH);
        for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
            Element thElement = (Element) i.next();
            TextExtractor extractor = new ElementOnlyTextExtractor(thElement.getContent());
            String content = extractor.toString().trim();
            if (content.equals("All Flixster")) {
                Element next = source.findNextElement(thElement.getEnd());
                String votes = new ElementOnlyTextExtractor(next.getContent()).toString().trim();
                votes = votes.replaceAll("\\(", "").replaceAll("\\)", "");
                movieSite.setVotes(Integer.valueOf(votes));
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
                            movieSite.setScore(intScore);
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
