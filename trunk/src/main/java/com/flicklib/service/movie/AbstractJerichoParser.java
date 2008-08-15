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
    public final void parse(String html, MoviePage movieSite) {
        Source source = new Source(html);
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        parse(html, source, movieSite);
    }

    /**
     * Parses jericho source to MovieSite
     * @param html
     * @param source
     * @param movieSite
     */
    public abstract void parse(String html, Source source, MoviePage movieSite);
}
