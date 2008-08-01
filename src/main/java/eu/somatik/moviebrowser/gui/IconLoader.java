package eu.somatik.moviebrowser.gui;

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

}
