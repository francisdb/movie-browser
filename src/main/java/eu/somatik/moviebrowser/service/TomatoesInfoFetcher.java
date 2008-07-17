package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.domain.Movie;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fdb
 */
public class TomatoesInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomatoesInfoFetcher.class);

    @Override
    public void fetch(Movie movie) {
        if (!"".equals(movie.getImdbId())) {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(MovieFinder.generateTomatoesUrl(movie));
            try {
                client.executeMethod(method);
                Source source = new Source(method.getResponseBodyAsString());
                //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
                source.fullSequentialParse();

                //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
                //System.out.println(titleElement.getContent().extractText());

                // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>


                List<?> divElements = source.findAllElements(HTMLElementName.DIV);
                for (Iterator<?> i = divElements.iterator(); i.hasNext();) {
                    Element divElement = (Element) i.next();
                    String id = divElement.getAttributeValue("id");
                    if (id != null && "bubble_allCritics".equals(id)) {
                        String userRating = divElement.getContent().getTextExtractor().toString().trim();
                        if (!"".equals(userRating)) {
                            userRating = userRating.replace("%", "");
                            try{
                                movie.setTomatometer(Integer.valueOf(userRating));
                            }catch(NumberFormatException ex){
                                LOGGER.error("Could not parse "+userRating+" to Integer", ex);
                            }
                        }
                    }
                }


            } catch (IOException ex) {
                LOGGER.error("Loading from rotten tomatoes failed", ex);
            } finally {
                // Release the connection.
                if(method != null){
                    method.releaseConnection();
                }
            }
        }
    }
}
