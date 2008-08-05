package eu.somatik.moviebrowser.service.subs;

import eu.somatik.moviebrowser.api.SubtitlesLoader;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.domain.Subtitle;
import eu.somatik.moviebrowser.service.SourceLoader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class OpenSubtitlesLoader implements SubtitlesLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesLoader.class);
    
    private final SourceLoader sourceLoader;

    @Inject
    public OpenSubtitlesLoader(final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
    }
    
    
    @Override
    public Set<Subtitle> getOpenSubsResults(String localFileName) throws IOException {
        String encodedFileName = generateUrl(localFileName);
        String url = "http://www.opensubtitles.org/en/search2/?moviename=" + encodedFileName + "&sublanguageid=all";
        LOGGER.info("url = "+url);
        String source = sourceLoader.load(url);
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        Set<Subtitle> results = new HashSet<Subtitle>();
        
        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (!title.contains("download divx subtitles from the biggest open")) {
            List<?> tableElements = jerichoSource.findAllElements(HTMLElementName.TD);
            Element tableElement;
            Iterator<?> j = tableElements.iterator();
            Subtitle sub;
            while (j.hasNext()) {
                sub = new Subtitle();
                sub.setSubSource("OpenSubtitles.org");
                tableElement = (Element) j.next();

                //System.out.println(tableElement.getTextExtractor().toString());
                String newList = tableElement.getChildElements().toString();
                Source newSource = new Source(newList);
                List<?> linkElements = newSource.findAllElements(HTMLElementName.A);
                Element linkElement;
                Iterator<?> i = linkElements.iterator();

                while (i.hasNext()) {
                    linkElement = (Element) i.next();
                    String href = linkElement.getAttributeValue("href");
                    if (href != null && href.startsWith("/en/download/sub/")) {
                        sub.setFileName("http://www.opensubtitles.org" + href);
                    } else if (href != null && href.startsWith("/en/search/")) {
                        String split[] = href.split("/");
                        sub.setLanguage(split[4]);
                    }
                    System.out.println(tableElement.getTextExtractor().toString());
                    sub.setType("sub/srt");
                    sub.setNoCd("N/A");
                    results.add(sub);
                }
            }

        }

        return results;
    }
    
        
   
    /**
     * @param fileName 
     * @return the imdb url
     */
    private String generateUrl(String fileName) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return encoded;
    }

}
