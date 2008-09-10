/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
