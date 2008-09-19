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
package eu.somatik.moviebrowser.service;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks if a file has a know video extension
 * 
 * @author francisdb
 */
public class MovieFileFilter implements FileFilter {

	public static final Set<String> VIDEO_EXTENSIONS = new HashSet<String>();
	public static final Set<String> VIDEO_EXT_EXTENSIONS = new HashSet<String>();

	static {
		Collections.addAll(VIDEO_EXTENSIONS, "avi", "mpg", "mpeg", "divx",
				"mkv", "xvid", "m4v", "mov", "flv", "iso");
		VIDEO_EXT_EXTENSIONS.addAll(VIDEO_EXTENSIONS);
		Collections.addAll(VIDEO_EXT_EXTENSIONS, "srt", "nfo", "sub", "idx");
		
	}

	private boolean acceptFolders;

	public MovieFileFilter(boolean acceptFolders) {
		this.acceptFolders = acceptFolders;
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return acceptFolders;
		} else {
			String name = file.getName();
			int lastDotPos = name.lastIndexOf('.');
			if (lastDotPos != -1 && lastDotPos != 0
					&& lastDotPos < name.length() - 1) {
				String ext = name.substring(lastDotPos + 1).toLowerCase();
				if (VIDEO_EXTENSIONS.contains(ext)) {
					return true;
				}
			}

		}
		return false;
	}
}
