/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.TextExtractor;
import eu.somatik.moviebrowser.domain.Movie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieWebInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieWebInfoFetcher.class);

    public MovieWebInfoFetcher() {
    }

    @Override
    public void fetch(Movie movie) {
        HttpClient client = new HttpClient();
        HttpMethod method = null;
        try {
            String searchUrl = createMovieWebSearchUrl(movie);
            LOGGER.debug(searchUrl);
            method = new GetMethod(searchUrl);
            client.executeMethod(method);
            Source source = new Source(method.getResponseBodyAsString());
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();

            //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
            //System.out.println(titleElement.getContent().extractText());

            // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>

            String movieUrl = null;
            List<?> aElements = source.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = aElements.iterator(); i.hasNext();) {
                Element aElement = (Element) i.next();
                String url = aElement.getAttributeValue("href");
                if (url != null && url.endsWith("summary.php")) {
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    if (movieUrl == null && movieName != null && movieName.trim().length() != 0) {

                        movieUrl = "http://www.movieweb.com" + url;
                        LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                    }
                }
            }
            method.releaseConnection();
            if (movieUrl == null) {
                throw new IOException("Movie not found on MovieWeb");
            }
            method = new GetMethod(movieUrl);
            client.executeMethod(method);
            source = new Source(method.getResponseBodyAsString());
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();
            List<?> divElements = source.findAllElements(HTMLElementName.DIV);
            for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
                Element divElement = (Element) i.next();
                TextExtractor extractor = new ElementOnlyTextExtractor(divElement.getContent());
                String content = extractor.toString();
                if (content.startsWith("MovieWeb Users:")) {
                    List childs = divElement.getChildElements();
                    if (childs.size() > 0) {
                        String score = ((Element) childs.get(0)).getContent().getTextExtractor().toString().trim();
                        LOGGER.info("User score: " + score);
                        if(score.length() > 0){
                            try {
                                float theScore = Float.valueOf(score).floatValue() * 20;
                                int intScore = Math.round(theScore);
                                movie.setMovieWebStars(intScore);
                            } catch (NumberFormatException ex) {
                                LOGGER.error("Could not parse " + score + " to Float", ex);
                            }
                        }
                    }
                } else if (content.startsWith("The Critics:")) {
                    List childs = divElement.getChildElements();
                    if (childs.size() > 0) {
                        String score = ((Element) childs.get(0)).getContent().getTextExtractor().toString();
                        LOGGER.info("Critics score: " + score);
                    // TODO use?
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Loading from MovieWeb failed", ex);
        } finally {
            // Release the connection.
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    private String createMovieWebSearchUrl(Movie movie) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(movie.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.movieweb.com/search/?search=" + encoded;
    }

    private static class ElementOnlyTextExtractor extends TextExtractor {

        public ElementOnlyTextExtractor(final Segment segment) {
            super(segment);
        }

        @Override
        public boolean excludeElement(StartTag startTag) {
            //LOGGER.debug(startTag.toString());
            return true;
        }
    }
}
