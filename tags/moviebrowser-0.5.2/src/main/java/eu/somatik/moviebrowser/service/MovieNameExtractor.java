/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author francisdb
 */
public class MovieNameExtractor {
    
    private static final String TO_REMOVE[] = {
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
        ".dutch",
        ".limited",
        ".nfofix"    //".ws"        
    };
    
    public String removeCrap(String name) {
        String movieName = name.toLowerCase();
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
        movieName = movieName.replaceAll("\\.", " ");
        movieName = movieName.trim();
        return movieName;
    }    

}
