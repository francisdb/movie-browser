package eu.somatik.moviebrowser.service.subs;

import com.google.inject.Inject;
import eu.somatik.moviebrowser.api.SubtitlesLoader;
import eu.somatik.moviebrowser.domain.Subtitle;
import eu.somatik.moviebrowser.service.SourceLoader;
import java.io.IOException;
import java.util.List;


/**
 *
 * @author francisdb
 */
public class OndertitelComLoader implements SubtitlesLoader{
    
    private final SourceLoader sourceLoader;

    /**
     * http://www.ondertitel.com
     * @param sourceLoader
     */
    @Inject
    public OndertitelComLoader(SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
    }

    
    
    
    

    @Override
    public List<Subtitle> getOpenSubsResults(String localFileName) throws IOException {
        // http://www.ondertitel.com/?type=&trefwoord=the+dark+knight&p=zoek
        
        throw new IOException("not implemented");
    }

    
}
