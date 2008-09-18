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