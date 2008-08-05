/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.Movie;

/**
 *
 * @author francisdb
 */
public abstract class AbstractJerichoParser implements Parser{

    @Override
    public void parse(String pageSource, Movie movie) {
        Source source = new Source(pageSource);
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        parse(source, movie);
    }

    public abstract void parse(Source source, Movie movie);
}
