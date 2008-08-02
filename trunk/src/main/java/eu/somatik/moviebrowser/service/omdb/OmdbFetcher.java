package eu.somatik.moviebrowser.service.omdb;

import eu.somatik.moviebrowser.api.MovieInfoFetcher;
import eu.somatik.moviebrowser.domain.Movie;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.restlet.data.Response;
import org.restlet.resource.Representation;

/**
 *
 * @author francisdb
 */
public class OmdbFetcher implements MovieInfoFetcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieInfoFetcher.class);

    @Override
    public void fetch(Movie movie) {
        // Outputting the content of a Web page
        // Prepare the request
        Request request = new Request(Method.GET, "http://www.omdb-beta.org/search/movies?query=test");
        Preference<MediaType> preference2 = new Preference<MediaType>(MediaType.APPLICATION_XML);
        Preference<MediaType> preference3 = new Preference<MediaType>(MediaType.TEXT_XML);
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
        } catch (IOException ex) {
            LOGGER.error("Could not load rest",ex);
        }

    }

}
