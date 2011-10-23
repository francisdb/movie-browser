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

import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.domain.MovieService;
import com.google.inject.Singleton;

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
        String fileName = "images/16/"+service.name().toLowerCase()+".png";
        ImageIcon icon = null;
        if(fileName != null){
            icon = loadIcon(fileName);
        }
        return icon;
    }

}
