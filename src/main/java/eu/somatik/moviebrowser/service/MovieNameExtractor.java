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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.somatik.moviebrowser.domain.Language;

/**
 *
 * @author francisdb
 */
public class MovieNameExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieNameExtractor.class);
    private static final String TO_REMOVE[] = {
        ".ws.dvdrip",
        ".fs.dvdrip",
        ".fs.internal",
        ".se.internal",
        ".extended.dvdrip",
        ".real.repack",
        ".real.proper",
        ".real.retail",
        ".limited.dvdrip",
        ".limited.color.fixed",
        ".dvdrip",
        ".samplefix",
        ".dvdivx",
        ".dvdivx4",
        ".dvdivx5",
        ".dvdr",
        ".divx",
        ".xvid",
        ".limited",
        ".internal",
        ".special.edition",
        ".proper",
        ".dc",
        ".ac3",
        ".unrated",
        ".uncut",
        ".stv",
        ".dutch", // keep this one?
        ".hund",
        ".hun",
        ".nfofix",
        ".subpack",
        ".subfix",
        ".syncfix",
        ".cd1",
        ".cd2",
        ".screener",
        ".dvdr",
        ".dvd",
        ".pal",
        ".direcors.cut",
        ".extended.cut",
        ".repack",
        ".disc",
        ".retail"//".ws"        
    };
    
    static class LanguageSuggestion {
        Pattern pattern;
        Language language;
        public LanguageSuggestion(String pattern, Language language) {
            this.pattern = Pattern.compile(pattern);
            this.language = language;
        }
        
        public Language match(String name) {
            if (pattern.matcher(name.toLowerCase()).find()) {
                return language;
            }
            return null;
        }
        
    }

    
    static final LanguageSuggestion[] suggestions = new  LanguageSuggestion[] {
        new LanguageSuggestion("\\.hungarian", Language.HUNGARIAN),
        new LanguageSuggestion("\\.hun\\.", Language.HUNGARIAN),
    };
    
    
    
    public MovieNameExtractor() {
    }

    public String removeCrap(File file) {
        String movieName = file.getName().toLowerCase().replace('_', '.').replace('-','.');
        if (!file.isDirectory()) {
            movieName = clearMovieExtension(movieName);
        }
            //getYear(movieName);
        for (String bad : TO_REMOVE) {
            movieName = movieName.replaceAll(bad, "");
        }
        // should we remove year, also? I don't think so...
        
/*
        Calendar calendar = new GregorianCalendar();
        int thisYear = calendar.get(Calendar.YEAR);

        // TODO recup the movie year!

        for (int i = 1800; i < thisYear; i++) {
            movieName = movieName.replaceAll(Integer.toString(i), "");
        }
        int dashPos = movieName.lastIndexOf('-');
        if (dashPos != -1) {
            movieName = movieName.substring(0, movieName.lastIndexOf('-'));
        }
*/            
        movieName = movieName.replaceAll("\\.", " ");
        movieName = movieName.trim();
        LOGGER.debug(movieName);
        return movieName;
    }

    /**
     * TODO use this for the imdb search?
     * @param str
     */
    private void getYear(final String str) {
        final String regex = "(18|19|20|21)\\d\\d";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        int count = 0;
        while (m.find()) {
            //LOGGER.info("start(): " + m.start());
            //LOGGER.info("end(): " + m.end());
            // if at begin string -> must be part of movie title
            if(m.start() > 0){
                count++;
                LOGGER.info("Match number " + count +": possible year for '" + str + "' = " + m.group());
            }
        }
    }

    public String clearMovieExtension(String name) {
        int lastDotPos = name.lastIndexOf('.');
        if (lastDotPos != -1 && lastDotPos != 0 && lastDotPos < name.length() - 1) {
            String ext = name.substring(lastDotPos + 1).toLowerCase();
            if (MovieFileFilter.VIDEO_EXTENSIONS.contains(ext)) {
                return name.substring(0, lastDotPos);
            }
        }
        return name;
    }

    
    public Language getLanguageSuggestion(String filename) {
        for (LanguageSuggestion s : suggestions) {
            Language l = s.match(filename);
            if (l!=null) {
                return l;
            }
        }
        return Language.ENGLISH;
    }

}
