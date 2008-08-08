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
    public void parse(Source source, MoviePage movieSite) {
        Movie movie = movieSite.getMovie();
        Element titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
        String titleYear = titleElement.getContent().getTextExtractor().toString();

        if (titleYear.endsWith(")")) {
            int index = titleYear.lastIndexOf("(");
            String year = titleYear.substring(index + 1, titleYear.length() - 1);
            // get rid of the /I in for example "1998/I"
            int slashIndex = year.indexOf('/');
            if(slashIndex != -1){
                year = year.substring(0, slashIndex);
            }
            try {
                movie.setYear(Integer.valueOf(year));
            } catch (NumberFormatException ex) {
                LOGGER.error("Could not parse '" + year + "' to integer", ex);
            }
            titleYear = titleYear.substring(0, index-1);
        }
        movie.setTitle(titleYear);

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
                if(!genre.toLowerCase().contains("imdb")){
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
                if(!rating.contains("awaiting")){
                    // to percentage
                    rating = rating.replace("/10", "");
                    try {
                        int theScore = Math.round(Float.valueOf(rating).floatValue() * 10);
                        movieSite.setScore(theScore);
                    } catch (NumberFormatException ex) {
                        LOGGER.error("Could not parse rating '" + rating + "' to Float", ex);
                    }
                    next = source.findNextElement(next.getEndTag().getEnd());
                    String votes = next.getContent().getTextExtractor().toString();

                    votes = votes.replaceAll("\\(", "");
                    votes = votes.replaceAll("votes\\)", "");
                    votes = votes.replaceAll(",", "");
                    votes = votes.trim();
                    try{
                        movieSite.setVotes(Integer.valueOf(votes));
                    }catch(NumberFormatException ex){
                        LOGGER.error("Could not parse the votes '"+votes+"' to Integer", ex);
                    }
                }
            }
        }

        linkElements = source.findAllElements(HTMLElementName.H5);
        String hText;
        for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
            Element hElement = (Element) i.next();
            hText = hElement.getContent().getTextExtractor().toString();
            if (hText.contains("Plot Outline")) {
                int end = hElement.getEnd();
                movie.setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            } else if (hText.contains("Plot:")) {
                int end = hElement.getEnd();
                movie.setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            } else if (hText.contains("Runtime")) {
                int end = hElement.getEnd();
                EndTag next = source.findNextEndTag(end);
                //System.out.println(next);
                String runtime = source.subSequence(end, next.getBegin()).toString().trim();
                movie.setRuntime(parseRuntime(runtime));
            } else if (hText.contains("Director")) {
                int end = hElement.getEnd();
                Element aElement = source.findNextElement(end);
                movie.setDirector(aElement.getContent().getTextExtractor().toString());
            }
        }

        if (movie.getTitle() == null) {
            //System.out.println(source.toString());
            movie.setPlot("Not found");
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
