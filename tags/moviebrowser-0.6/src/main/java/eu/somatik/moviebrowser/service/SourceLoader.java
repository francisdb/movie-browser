/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import java.io.IOException;

/**
 *
 * @author francisdb
 */
public interface SourceLoader {

    /**
     * Loads a http request and parses it to a jericho source
     * @param url
     * @return
     * @throws java.io.IOException
     */
    String load(String url) throws IOException;

}
