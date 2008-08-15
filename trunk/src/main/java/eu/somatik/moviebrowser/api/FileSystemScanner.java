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
package eu.somatik.moviebrowser.api;

import java.io.File;

/**
 *
 * @author francisdb
 */
public interface FileSystemScanner {

    /**
     * Finds the NFO file and looks for the imdb url inside it
     * @param dir
     * @return the nfo URL or null
     */
    String findNfoImdbUrl(File dir);

    /**
     * Locates parent directory name and returns it
     * @param folder
     * @return the parent dir name or null if not found
     */
    File findParentDirectory(File folder);

    /**
     * Locates she sample file and returns it
     * @param folder
     * @return the sample file or null if not found
     */
    File findSample(File folder);

}
