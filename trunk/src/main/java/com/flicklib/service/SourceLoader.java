package com.flicklib.service;

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
