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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.api.Parser;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MovieService;
import com.flicklib.domain.MoviePage;
import com.flicklib.service.SourceLoader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class FlixterInfoFetcher implements MovieInfoFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlixterInfoFetcher.class);
    private final SourceLoader sourceLoader;
    private final Parser parser;

    /**
     * Constructs a new FlixterInfoFetcher
     * @param parser
     * @param sourceLoader
     */
    @Inject
    public FlixterInfoFetcher(final @Flixster Parser parser, final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
        this.parser = parser;
    }

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.FLIXSTER);
        try {
            String source = sourceLoader.load(createFlixterSearchUrl(movie));
            Source jerichoSource = new Source(source);
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            jerichoSource.fullSequentialParse();

            // <a onmouseover="mB(event, 770678072);" title=" The X-Files: I Want to Believe (The X Files 2)" href="/movie/the-x-files-i-want-to-believe-the-x-files-2"  >
            // The X-Files: I Want to Believe (The X Files 2)
            // </a>

            String movieUrl = null;
            List<?> aElements = jerichoSource.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = aElements.iterator(); i.hasNext();) {
                Element aElement = (Element) i.next();
                String url = aElement.getAttributeValue("href");
                if (url != null && url.startsWith("/movie/")) {
                    String movieName = aElement.getContent().getTextExtractor().toString();
                    if (movieUrl == null && movieName != null && movieName.trim().length() != 0) {

                        movieUrl = "http://www.flixster.com" + url;
                        LOGGER.info("taking first result: " + movieName + " -> " + movieUrl);
                    }
                }
            }
            if (movieUrl == null) {
               LOGGER.warn("Movie not found on Flixter: " + movie.getTitle());
            }else{
                site.setUrl(movieUrl);
                source = sourceLoader.load(movieUrl);
                parser.parse(source, site);
            }
        } catch (IOException ex) {
            LOGGER.error("Loading from Flixter failed", ex);
        }
        return site;
    }

    private String createFlixterSearchUrl(Movie movie) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(movie.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return "http://www.flixster.com/movies.do?movieAction=doMovieSearch&x=0&y=0&search=" + encoded;
    }
}
