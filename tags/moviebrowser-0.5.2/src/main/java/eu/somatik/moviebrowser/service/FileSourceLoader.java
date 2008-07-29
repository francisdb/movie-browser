package eu.somatik.moviebrowser.service;

import au.id.jericho.lib.html.Source;
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
    public Source load(String url) throws IOException {
        
        Source source = null;
        InputStream fis = null;
        try{
            fis = FileSourceLoader.class.getClassLoader().getResourceAsStream(url);
            //fis = new FileInputStream(url);
            source = new Source(fis);
            //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
            source.fullSequentialParse();
        }finally{
            if(fis != null){
                fis.close();
            }
        }
        return source;
    }
}
