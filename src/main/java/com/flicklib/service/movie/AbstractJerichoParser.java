package com.flicklib.service.movie;

import au.id.jericho.lib.html.Source;
import com.flicklib.api.Parser;
import com.flicklib.domain.MoviePage;

/**
 *
 * @author francisdb
 */
public abstract class AbstractJerichoParser implements Parser{

    @Override
    public void parse(String pageSource, MoviePage movieSite) {
        Source source = new Source(pageSource);
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        parse(source, movieSite);
    }

    public abstract void parse(Source source, MoviePage movieSite);
}
