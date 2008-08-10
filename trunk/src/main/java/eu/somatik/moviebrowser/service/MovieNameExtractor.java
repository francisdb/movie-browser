package eu.somatik.moviebrowser.service;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieNameExtractor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieNameExtractor.class);

    private static final String TO_REMOVE[] = {
        ".limited.dvdrip",
        ".extended.dvdrip",
        ".dvdrip",
        ".samplefix",
        ".dvdivx",
        ".dvdivx4",
        ".dvdivx5",
        ".divx",
        ".xvid",
        ".limited",
        ".internal",
        ".proper",
        ".dc",
        ".ac3",
        ".unrated",
        ".stv",
        ".dutch", // keep this one?
        ".limited",
        ".nfofix",
        ".subpack",
        ".subfix",
        ".syncfix",
        ".cd1",
        ".cd2",
        ".screener",
        ".dvd",
        ".direcors.cut",
        ".repack"//".ws"        
    };
    private final MovieFileFilter filter;

    public MovieNameExtractor() {
        this.filter = new MovieFileFilter(false);
    }

    public String removeCrap(File file) {
        String movieName;
        if (file.isDirectory()) {
            movieName = file.getName().toLowerCase();
            for (String bad : TO_REMOVE) {
                movieName = movieName.replaceAll(bad, "");
            }

            Calendar calendar = new GregorianCalendar();
            int thisYear = calendar.get(Calendar.YEAR);

            //TODO recup the movie year!

            for (int i = 1800; i < thisYear; i++) {
                movieName = movieName.replaceAll(Integer.toString(i), "");
            }
            int dashPos = movieName.lastIndexOf('-');
            if (dashPos != -1) {
                movieName = movieName.substring(0, movieName.lastIndexOf('-'));
            }
        } else {
            movieName = filter.clearMovieExtension(file);
        }
        movieName = movieName.replaceAll("\\.", " ");
        movieName = movieName.trim();
        LOGGER.debug(movieName);
        return movieName;
    }
}
