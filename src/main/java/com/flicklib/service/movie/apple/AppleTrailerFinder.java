package com.flicklib.service.movie.apple;

import com.flicklib.api.TrailerFinder;
import com.flicklib.tools.Param;
import com.flicklib.domain.Movie;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

/**
 *
 * @author francisdb
 */
public class AppleTrailerFinder implements TrailerFinder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppleTrailerFinder.class);
    
    @Override
    public String findTrailerUrl(Movie movie){
        String url = null;
        String query = Param.encode(movie.getTitle()+" site:www.apple.com");
        String queryUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="+query;
        Request request = new Request(Method.GET, queryUrl);
        Preference<MediaType> preference = new Preference<MediaType>(MediaType.APPLICATION_JSON);
        List<Preference<MediaType>> types = new ArrayList<Preference<MediaType>>();
        types.add(preference);
        request.getClientInfo().setAcceptedMediaTypes(types);
        //request.setReferrerRef("http://www.nosite.org");

        Client client = new Client(Protocol.HTTP);
        try {
            Response response = client.handle(request);
            LOGGER.info(response.getStatus().toString());
            Representation entity = response.getEntity();
            StringWriter stringWriter = new StringWriter();
            entity.write(stringWriter);
            JSONObject object = new JSONObject(stringWriter.toString());
            JSONArray results = object.getJSONObject("responseData").getJSONArray("results");
            String unescapedUrl;
            for(int i = 0; i<results.length() && url == null;i++){
                unescapedUrl = results.getJSONObject(i).getString("unescapedUrl");
                if(unescapedUrl.startsWith("http://www.apple.com/trailers")){
                    url = unescapedUrl;
                }
            }
            
        } catch (JSONException ex) {
            LOGGER.error("Could not parse google json rest query: "+queryUrl, ex);
        } catch (IOException ex) {
            LOGGER.error("Could not load rest: "+ queryUrl,ex);
        }
        return url;
    }
    
    
}
