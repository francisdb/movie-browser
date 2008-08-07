package com.flicklib.module;

import com.flicklib.api.SubtitlesLoader;
import com.flicklib.service.HttpSourceLoader;
import com.flicklib.service.SourceLoader;
import com.flicklib.service.sub.OpenSubtitlesLoader;
import com.google.inject.AbstractModule;

/**
 *
 * @author francisdb
 */
public class FlicklibModule extends AbstractModule{

    @Override
    protected void configure() {
         bind(SourceLoader.class).to(HttpSourceLoader.class);
        
         bind(SubtitlesLoader.class).to(OpenSubtitlesLoader.class);
    }

}
