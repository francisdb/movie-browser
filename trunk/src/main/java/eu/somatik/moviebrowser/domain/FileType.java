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
package eu.somatik.moviebrowser.domain;

import eu.somatik.moviebrowser.service.MovieFileFilter;

public enum FileType {
	VIDEO_CONTENT, SUBTITLE, NFO;
	
	
	public static FileType getTypeByExtension(String ext) {
		if ("nfo".equals(ext)) {
			return NFO;
		} else {
			if ("sub".equals(ext) || "srt".equals(ext) || "idx".equals(ext)) {
				return SUBTITLE;
			} else {
				if (MovieFileFilter.VIDEO_EXTENSIONS.contains(ext)) {
					return VIDEO_CONTENT;
				}
			}
		}
		return null;
	}
}
