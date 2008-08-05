package eu.somatik.moviebrowser.service;

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
            source = slurp(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return source;
    }

    public String slurp(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}
