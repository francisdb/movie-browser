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
package com.flicklib.service.movie.omdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.MovieInfoFetcher;
import com.flicklib.domain.Movie;
import com.flicklib.domain.MoviePage;
import com.flicklib.domain.MovieService;

/**
 *
 * @author francisdb
 */
public class OmdbFetcher implements MovieInfoFetcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieInfoFetcher.class);

    @Override
    public MoviePage fetch(Movie movie, String id) {
        MoviePage site = new MoviePage();
        site.setMovie(movie);
        site.setService(MovieService.MOVIEWEB);
        // Outputting the content of a Web page
        // Prepare the request
        Request request = new Request(Method.GET, "http://www.omdb-beta.org/search/movies?query=test");
        Preference<MediaType> preference2 = new Preference<MediaType>(MediaType.APPLICATION_XML); // MediaType.TEXT_XML
        List<Preference<MediaType>> types = new ArrayList<Preference<MediaType>>();
        types.add(preference2);
        //types.add(preference3);
        request.getClientInfo().setAcceptedMediaTypes(types);
        request.setReferrerRef("http://www.mysite.org");

        Client client = new Client(Protocol.HTTP);
        try {
            Response response = client.handle(request);
            LOGGER.info(response.getAllowedMethods().toString());
            LOGGER.info(response.getStatus().toString());
            Representation entity = response.getEntity();
            entity.write(System.out);
            // todo set score....
        } catch (IOException ex) {
            LOGGER.error("Could not load rest",ex);
        }
        return site;
    }

}
