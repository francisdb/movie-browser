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
package com.flicklib.service.movie.imdb;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.EndTag;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;

import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.movie.AbstractJerichoParser;
import com.flicklib.tools.ElementOnlyTextExtractor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author francisdb
 */
@Singleton
public class ImdbParser extends AbstractJerichoParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbParser.class);

    @Inject
    public ImdbParser() {
    }

    @Override
    public void parse(final String html, Source source, MoviePage movieSite) {

        ImdbParserRegex regexParser = new ImdbParserRegex(html);

        Movie movie = movieSite.getMovie();
        movie.setType(regexParser.getType());
        Element titleHeader = (Element) source.findAllElements(HTMLElementName.H1).get(0);
        String title = new ElementOnlyTextExtractor(titleHeader.getContent()).toString();
        title = ImdbParserRegex.cleanTitle(title);
        movie.setTitle(title);

        List<?> yearLinks = titleHeader.findAllElements(HTMLElementName.A);
        if (yearLinks.size() > 0) {
            Element yearLink = (Element) yearLinks.get(0);
            String year = yearLink.getContent().getTextExtractor().toString();
            try {
                movie.setYear(Integer.valueOf(year));
            } catch (NumberFormatException ex) {
                LOGGER.error("Could not parse year '" + year + "' to integer", ex);
            }
        }

        List<?> linkElements = source.findAllElements(HTMLElementName.A);
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element linkElement = (Element) i.next();
            if ("poster".equals(linkElement.getAttributeValue("name"))) {
                // A element can contain other tags so need to extract the text from it:
                List<?> imgs = linkElement.getContent().findAllElements(HTMLElementName.IMG);
                Element img = (Element) imgs.get(0);
                String imgUrl = img.getAttributeValue("src");
                movieSite.setImgUrl(imgUrl);
            }
            String href = linkElement.getAttributeValue("href");
            if (href != null && href.contains("/Sections/Genres/")) {
                String genre = linkElement.getContent().getTextExtractor().toString();
                // TODO find a better way to parse these out, make sure it are only the movie genres
                if (!genre.toLowerCase().contains("imdb")) {
                    movie.addGenre(linkElement.getContent().getTextExtractor().toString());
                }
            }
            if (href != null && href.contains("/Sections/Languages/")) {
                movie.addLanguage(linkElement.getContent().getTextExtractor().toString());
            }

        }

        linkElements = source.findAllElements(HTMLElementName.B);
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element bElement = (Element) i.next();
            if (bElement.getContent().getTextExtractor().toString().contains("User Rating:")) {
                Element next = source.findNextElement(bElement.getEndTag().getEnd());
                String rating = next.getContent().getTextExtractor().toString();
                // skip (awaiting 5 votes)
                if (!rating.contains("awaiting")) {
                    parseRatingString(movieSite, rating);
                    next = source.findNextElement(next.getEndTag().getEnd());
                    parseVotes(movieSite, next);
                }
            }
        }

        linkElements = source.findAllElements(HTMLElementName.H5);
        String hText;
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element hElement = (Element) i.next();
            hText = hElement.getContent().getTextExtractor().toString();
            int end = hElement.getEnd();
            if (hText.contains("Plot Outline")) {
                movie.setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            } else if (hText.contains("Plot:")) {
                movie.setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            } else if (hText.contains("Runtime")) {
                EndTag next = source.findNextEndTag(end);
                //System.out.println(next);
                String runtime = source.subSequence(end, next.getBegin()).toString().trim();
                movie.setRuntime(parseRuntime(runtime));
            } else if (hText.contains("Director")) {
                Element aElement = source.findNextElement(end);
                movie.setDirector(aElement.getContent().getTextExtractor().toString());
            } else if (hText.contains("User Rating")) {
                Element aElement = source.findNextElement(end);
                List<Element> boldOnes = aElement.findAllElements(HTMLElementName.B);
                if (boldOnes.size()>0) {
                    Element element = boldOnes.get(0);
                    String rating = element.getTextExtractor().toString();
                    if (!rating.contains("awaiting")) {
                        parseRatingString(movieSite, rating);
                        Element next = source.findNextElement(element.getEndTag().getEnd());
                        parseVotes(movieSite, next);
                    }
                }
            } /*else if (hText.contains("Genre")) {
                
            }*/
            		
        }

        if (movie.getTitle() == null) {
            //System.out.println(source.toString());
            movie.setPlot("Not found");
        }

    }

    private void parseVotes(MoviePage movieSite, Element element) {
        String votes = element.getContent().getTextExtractor().toString();

        votes = votes.replaceAll("\\(", "");
        votes = votes.replaceAll("votes(\\))*", "");
        votes = votes.replaceAll(",", "");
        votes = votes.trim();
        try {
            movieSite.setVotes(Integer.valueOf(votes));
        } catch (NumberFormatException ex) {
            LOGGER.error("Could not parse the votes '" + votes + "' to Integer", ex);
        }
    }

    private void parseRatingString(MoviePage movieSite, String rating) {
        // to percentage
        rating = rating.replace("/10", "");
        try {
            int theScore = Math.round(Float.valueOf(rating).floatValue() * 10);
            movieSite.setScore(theScore);
        } catch (NumberFormatException ex) {
            LOGGER.error("Could not parse rating '" + rating + "' to Float", ex);
        }
    }

    private Integer parseRuntime(String runtimeString) {
        String runtime = runtimeString.substring(0, runtimeString.indexOf("min")).trim();
        int colonIndex = runtime.indexOf(":");
        if (colonIndex != -1) {
            runtime = runtime.substring(colonIndex + 1);
        }

        return Integer.valueOf(runtime);
    }
}
