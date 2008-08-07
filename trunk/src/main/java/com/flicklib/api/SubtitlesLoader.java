package com.flicklib.api;

import com.flicklib.domain.Subtitle;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public interface SubtitlesLoader {

    Set<Subtitle> search(String localFileName, String imdbId) throws IOException;

}
