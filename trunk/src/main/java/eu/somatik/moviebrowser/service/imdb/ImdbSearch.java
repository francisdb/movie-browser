package eu.somatik.moviebrowser.service.imdb;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.service.SourceLoader;
import eu.somatik.moviebrowser.tools.ElementOnlyTextExtractor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class ImdbSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbSearch.class);
    private final SourceLoader sourceLoader;
    private final Parser imdbParser;

    @Inject
    public ImdbSearch(final SourceLoader sourceLoader, final @Imdb Parser imdbParser) {
        this.sourceLoader = sourceLoader;
        this.imdbParser = imdbParser;
    }

    public List<Movie> parseResults(String source) throws IOException{
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        List<Movie> results = new ArrayList<Movie>();
        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (title.contains("IMDb") && title.contains("Search")) {
            LOGGER.info("Search results returned");
            List<?> tableElements = jerichoSource.findAllElements(HTMLElementName.TD);
            Element tableElement;
            Iterator<?> j = tableElements.iterator();
            while(j.hasNext()) {
                tableElement = (Element) j.next();
                //System.out.println(tableElement.getTextExtractor().toString());
                String newList = tableElement.getChildElements().toString();
                Source newSource = new Source(newList);
                List<?> linkElements = newSource.findAllElements(HTMLElementName.A);
                Element linkElement;
                Movie movie;
                Iterator<?> i = linkElements.iterator();
                Set<String> ids = new HashSet<String>();
                while ((i.hasNext()) && (!tableElement.getTextExtractor().toString().startsWith("Media from")) && (!tableElement.getTextExtractor().toString().startsWith(" ")) && (!tableElement.getTextExtractor().toString().endsWith("Update your search preferences.")) && (!tableElement.getTextExtractor().toString().endsWith("...)"))) {
                    movie = new Movie();
                    linkElement = (Element) i.next();
                    String href = linkElement.getAttributeValue("href");
                    if (href != null && href.startsWith("/title/tt")) {
                        int questionMarkIndex = href.indexOf('?');
                        if (questionMarkIndex != -1) {
                            href = href.substring(0, questionMarkIndex);
                        }
                        movie.setImdbUrl("http://www.imdb.com" + href);
                        movie.setImdbId(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                        movie.setTitle(linkElement.getTextExtractor().toString());
                        ElementOnlyTextExtractor extractor = new ElementOnlyTextExtractor(tableElement.getContent());
                        String titleYear = extractor.toString().trim();
                        // FIXME, duplicate of code in ImdbParser, to merge!
                        if (titleYear.length() > 0 && titleYear.contains(")")) {
                            int start = titleYear.indexOf("(");
                            int end = titleYear.indexOf(")");
                            String year = titleYear.substring(start+1, end);
                            // get rid of the /I in for example "1998/I"
                            int slashIndex = year.indexOf('/');
                            if(slashIndex != -1){
                                year = year.substring(0, slashIndex);
                            }
                            try {
                                movie.setYear(Integer.valueOf(year));
                            } catch (NumberFormatException ex) {
                                LOGGER.warn("Could not parse '" + year + "' to integer");
                            }
                        }
                        

                        // only add if not allready in the list
                        if (movie.getTitle().length() > 0 && !ids.contains(movie.getImdbId())) {
                            ids.add(movie.getImdbId());
                            results.add(movie);
                        }
                    }
                }
            }
            
        }else{
            LOGGER.info("Exact match returned");
            Movie result = new Movie();
            
            // FIXME, there should be a way to know at what url whe ended up (better way to parse imdb id)
            
            //Assume it's a perfect result, therefore get first /title/tt link.
            List<?> linkElements = jerichoSource.findAllElements(HTMLElementName.A);
            Element linkElement;
            Iterator<?> i = linkElements.iterator();
            Set<String> ids = new HashSet<String>();
            while (i.hasNext()) {
                
                linkElement = (Element) i.next();
                String href = linkElement.getAttributeValue("href");
                if (href != null && href.startsWith("/title/tt")) {
                    int questionMarkIndex = href.indexOf('?');
                    if (questionMarkIndex != -1) {
                        href = href.substring(0, questionMarkIndex);
                    }
                    //href has to be split as href will be in from of /title/tt#######/some-other-dir-like-trailers
                    String[] split = href.split("/");
                    href = "/" + split[1] + "/" + split[2];
                    Movie movie = new Movie();
                    movie.setImdbUrl("http://www.imdb.com" + href);
                    movie.setImdbId(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                    //set title as the movies title since this is a perfect search result who's HTMLElementName.title will be the movie title.
                    
                    movie.setTitle(title);
                    // only add if not allready in the list
                    if (movie.getTitle().length() > 0 && !ids.contains(movie.getImdbId())) {
                        //Only add to the set and results list if they are empty, i.e. only if the first result, since perfect result assumed. The rest of the identified links will be the IMDB recommendations for the particular perfect result.
                        if(ids.isEmpty() && results.isEmpty()) {
                            ids.add(movie.getImdbId());
                            result = movie;
                        }
                    }
                }
            }

            
            imdbParser.parse(source, result);
            results.add(result);
        }
        return results;

    }

    /**
     * 
     * @param search
     * @return
     * @throws java.io.IOException
     */
    public List<Movie> getResults(String search) throws IOException {
        String url = generateImdbTitleSearchUrl(search);
        LOGGER.info(url);
        String source = sourceLoader.load(url);
        return parseResults(source);
    }

    /**
     * @param title 
     * @return the imdb url
     */
    public String generateImdbTitleSearchUrl(String title) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.imdb.com/Tsearch?title=" + encoded;
    }

    /**
     * @param title 
     * @return the imdb url
     */
    private String generateImdbSearchUrl(String title) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.imdb.com/find?q=" + encoded;
    }
}
