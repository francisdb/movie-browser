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
