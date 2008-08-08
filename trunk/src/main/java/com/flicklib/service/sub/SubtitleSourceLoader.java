package com.flicklib.service.sub;

import com.flicklib.api.SubtitlesLoader;
import com.flicklib.domain.Subtitle;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public class SubtitleSourceLoader implements SubtitlesLoader {

    @Override
    public Set<Subtitle> search(String localFileName, String imdbId) throws IOException {
        Set<Subtitle> subs = new HashSet<Subtitle>();
        subs.add(makeSubtitlesourceEntry(localFileName, imdbId));
        return subs;
    }

    /**
     * Generates a dummy entry that links to the site
     * @param localFileName 
     * @param imdbId 
     * @return the Subtitle entry
     * @throws java.io.IOException
     */
    public Subtitle makeSubtitlesourceEntry(String localFileName, String imdbId) throws IOException {
        Subtitle sub = new Subtitle();
        sub.setFileName(localFileName);
        String url = "http://www.subtitlesource.org/title/tt" + imdbId;
        sub.setFileUrl(url);
        sub.setSubSource("http://www.subtitlesource.org");
        sub.setLanguage("Various");
        sub.setNoCd("N/A");
        sub.setType("N/A");
        return sub;
    }
}
