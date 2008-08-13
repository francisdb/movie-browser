package eu.somatik.moviebrowser.tools;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class FileTools {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileTools.class);

    private FileTools() {
    }


    /**
     * Deletes a directory and all its subdirectories
     * @param path
     * @return
     */
    public static boolean deleteDirectory(File path) {
        LOGGER.debug("Deleting recursively: "+path.getAbsolutePath());
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    boolean deleted = files[i].delete();
                    if(!deleted){
                        LOGGER.error("Could not delete: "+files[i].getAbsolutePath());
                    }
                }
            }
        }
        return (path.delete());
    }
}
