package com.flicklib.api;

import com.flicklib.domain.Subtitle;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public interface SubtitlesLoader {

    /**
     * Performs a subtitle search
     * @param localFileName
     * @param imdbId
     * @return the Set of Subtitles
     * @throws IOException
     */
    Set<Subtitle> search(String localFileName, String imdbId) throws IOException;

}
