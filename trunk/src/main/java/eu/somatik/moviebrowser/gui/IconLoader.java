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
package eu.somatik.moviebrowser.gui;

import com.flicklib.domain.MovieService;
import com.google.inject.Singleton;
import java.net.URL;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class IconLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IconLoader.class);
    
    /**
     * Loads an icon from the specified filename
     * @param fileName
     * @return the loaded ImageIcon
     */
    public final ImageIcon loadIcon(String fileName) {
        ImageIcon icon = null;
        URL resource = MainFrame.class.getClassLoader().getResource(fileName);
        if (resource != null) {
            icon = new ImageIcon(resource);
        } else {
            LOGGER.error("Icon does not exist: " + fileName);
        }
        return icon;
    }
    
    public ImageIcon iconFor(MovieService service){
        String fileName = null;
        switch(service){
            case FLIXSTER:
                fileName = "images/16/flixter.png";
                break;
            case GOOGLE:
                fileName = "images/16/google.png";
                break;
            case IMDB:
                fileName = "images/16/imdb.png";
                break;
            case MOVIEWEB:
                fileName = "images/16/movieweb.png";
                break;
            case OMDB:
                fileName = "images/16/omdb.png";
                break;
            case TOMATOES:
                fileName = "images/16/rottentomatoes.png";
                break;
            case NETFLIX:
                fileName = "images/16/netflix.png";
                break;
            case PORTHU:
                fileName = "images/16/porthu.png";
                break;
            case CINEBEL:
                fileName = "images/16/cinebel.png";
                break;
            default:
                throw new AssertionError("Uncatched movieservice: "+service);
                
        }
        ImageIcon icon = null;
        if(fileName != null){
            icon = loadIcon(fileName);
        }
        return icon;
    }

}
