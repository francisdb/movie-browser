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

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.SourceLoader;
import com.flicklib.tools.ElementOnlyTextExtractor;
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

    public List<MoviePage> parseResults(String source) throws IOException{
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        List<MoviePage> results = new ArrayList<MoviePage>();
        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (title.contains("IMDb") && title.contains("Search")) {
            LOGGER.info("Search results returned");
            List<?> tableElements = jerichoSource.findAllElements(HTMLElementName.TD);
            Element tableElement;
            Iterator<?> j = tableElements.iterator();
            while(j.hasNext()) {
                tableElement = (Element) j.next();
                String tdContents = tableElement.getTextExtractor().toString();
                List<?> linkElements = tableElement.findAllElements(HTMLElementName.A);
                Element linkElement;
                MoviePage movieSite;
                Movie movie;
                Iterator<?> i = linkElements.iterator();
                Set<String> ids = new HashSet<String>();
                while ((i.hasNext()) && (!tableElement.getTextExtractor().toString().startsWith("Media from")) && (!tableElement.getTextExtractor().toString().startsWith(" ")) && (!tableElement.getTextExtractor().toString().endsWith("Update your search preferences.")) && (!tableElement.getTextExtractor().toString().endsWith("...)"))) {
                    movieSite = new MoviePage();
                    movieSite.setService(MovieService.IMDB);
                    movie = new Movie();
                    movieSite.setMovie(movie);
                    linkElement = (Element) i.next();
                    String href = linkElement.getAttributeValue("href");
                    if (href != null && href.startsWith("/title/tt")) {
                        int questionMarkIndex = href.indexOf('?');
                        if (questionMarkIndex != -1) {
                            href = href.substring(0, questionMarkIndex);
                        }
                        movieSite.setUrl("http://www.imdb.com" + href);
                        movieSite.setIdForSite(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                        movie.setType(ImdbParserRegex.getType(tdContents, false));
                        title = linkElement.getTextExtractor().toString();
                        title = ImdbParserRegex.cleanTitle(title);
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
                        if (movie.getTitle().length() > 0 && !ids.contains(movieSite.getIdForSite())) {
                            ids.add(movieSite.getIdForSite());
                            results.add(movieSite);
                        }
                    }
                }
            }
            
        }else{
            LOGGER.info("Exact match returned");
            MoviePage result = new MoviePage();
            
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
                    MoviePage movieSite = new MoviePage();
                    movieSite.setService(MovieService.IMDB);
                    Movie movie = new Movie();
                    movieSite.setMovie(movie);
                    movieSite.setUrl("http://www.imdb.com" + href);
                    movieSite.setIdForSite(href.replaceAll("[a-zA-Z:/.+=?]", "").trim());
                    //set title as the movies title since this is a perfect search result who's HTMLElementName.title will be the movie title.
                    
                    movie.setTitle(title);
                    // only add if not allready in the list
                    if (movie.getTitle().length() > 0 && !ids.contains(movieSite.getIdForSite())) {
                        //Only add to the set and results list if they are empty, i.e. only if the first result, since perfect result assumed. The rest of the identified links will be the IMDB recommendations for the particular perfect result.
                        if(ids.isEmpty() && results.isEmpty()) {
                            ids.add(movieSite.getIdForSite());
                            result = movieSite;
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
     * Performs a search on imdb
     * @param search
     * @return the results found as List of MoviePage
     * @throws java.io.IOException
     */
    public List<MoviePage> getResults(String search) throws IOException {
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
        // global search is without s=tt
        return "http://www.imdb.com/find?q="+encoded+";s=tt;site=aka";
    }
}
