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
