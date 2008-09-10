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
package com.flicklib.service.movie.google;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.HttpSourceLoader;
import com.flicklib.tools.Param;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class GoogleInfoFetcher implements MovieInfoFetcher{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleInfoFetcher.class);
    
    private final Parser googleParser;
    private final HttpSourceLoader httpLoader;

    /**
     * Constructs a new GoogleInfoFetcher
     * @param googleParser
     * @param httpLoader
     */
    @Inject
    public GoogleInfoFetcher(final @Google Parser googleParser, HttpSourceLoader httpLoader) {
        this.googleParser = googleParser;
        this.httpLoader = httpLoader;
    }
    
    

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.GOOGLE);
        try {
            String params = Param.paramString("q", movie.getTitle());
            String sourceString = httpLoader.load("http://www.google.com/movies"+params);
            Source source = new Source(sourceString);
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();

            //Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
            //System.out.println(titleElement.getContent().extractText());

            // <div id="bubble_allCritics" class="percentBubble" style="display:none;">     57%    </div>

            String movieUrl = null;
            List<?> aElements = source.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = aElements.iterator(); i.hasNext() && movieUrl == null;) {
                Element aElement = (Element) i.next();
                String url = aElement.getAttributeValue("href");
                // /movies/reviews?cid=b939f27b219eb36f&fq=Pulp+Fiction&hl=en
                if (url != null && url.startsWith("/movies/reviews?cid=")) {
                    movieUrl = "http://www.google.com" + url;
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                }
            }
            if (movieUrl == null) {
                throw new IOException("Movie not found on Google: "+movie.getTitle());
            }
            site.setUrl(movieUrl);
            sourceString = httpLoader.load(movieUrl);
            googleParser.parse(sourceString, site);
        } catch (IOException ex) {
            LOGGER.error("Loading from Google failed", ex);
        }
        return site;
    }

}
