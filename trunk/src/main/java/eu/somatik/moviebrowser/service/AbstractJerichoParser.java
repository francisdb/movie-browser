/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Source;
import eu.somatik.moviebrowser.api.Parser;
import eu.somatik.moviebrowser.domain.MovieSite;

/**
 *
 * @author francisdb
 */
public abstract class AbstractJerichoParser implements Parser{

    @Override
    public void parse(String pageSource, MovieSite movieSite) {
        Source source = new Source(pageSource);
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        parse(source, movieSite);
    }

    public abstract void parse(Source source, MovieSite movieSite);
}
