package com.flicklib.service.subs;

import com.google.inject.Inject;
import com.flicklib.api.SubtitlesLoader;
import com.flicklib.domain.Subtitle;
import com.flicklib.service.SourceLoader;
import java.io.IOException;
import java.util.Set;


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
    public Set<Subtitle> search(String localFileName, String imdbId) throws IOException {
        // http://www.ondertitel.com/?type=&trefwoord=the+dark+knight&p=zoek
        
        throw new IOException("not implemented");
    }

    
}
