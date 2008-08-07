package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.tools.IOTools;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author francisdb
 */
public class FileSourceLoader implements SourceLoader {

    /**
     * Loads a page source file from the class path
     * @param url
     * @return
     * @throws java.io.IOException
     */
    @Override
    public String load(String url) throws IOException {
        String source = null;
        InputStream fis = null;
        try {
            fis = FileSourceLoader.class.getClassLoader().getResourceAsStream(url);
            source = IOTools.inputSreamToString(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return source;
    }
}
