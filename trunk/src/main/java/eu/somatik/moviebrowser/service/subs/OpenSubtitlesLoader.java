package eu.somatik.moviebrowser.service.subs;

import eu.somatik.moviebrowser.api.SubtitlesLoader;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.domain.Subtitle;
import eu.somatik.moviebrowser.service.SourceLoader;
import eu.somatik.moviebrowser.tools.ElementOnlyTextExtractor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO implement paging loading
 * 
 * http://www.opensubtitles.org
 * @author francisdb
 */
public class OpenSubtitlesLoader implements SubtitlesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesLoader.class);
    private static final String SITE = "http://www.opensubtitles.org";
    private final SourceLoader sourceLoader;

    @Inject
    public OpenSubtitlesLoader(final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
    }

    @Override
    public List<Subtitle> getOpenSubsResults(String localFileName) throws IOException {
        String url = searchUrl(localFileName);
        LOGGER.info("url = " + url);
        String source = sourceLoader.load(url);
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        List<Subtitle> results = new ArrayList<Subtitle>();

        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (title.contains("(results)")) {
            //load first link
            String subsUrl = null;
            List<?> aElements = jerichoSource.findAllElements(HTMLElementName.A);
            for (int i = 0; i < aElements.size() && subsUrl == null; i++) {
                Element aElement = (Element) aElements.get(i);
                if ("bnone".equals(aElement.getAttributeValue("class"))) {
                    subsUrl = SITE + aElement.getAttributeValue("href");
                }
            }

            source = sourceLoader.load(subsUrl);
            jerichoSource = new Source(source);
            jerichoSource.fullSequentialParse();
            results = loadSubtitlesPage(jerichoSource);
        } else {
            // direct hit
            results = loadSubtitlesPage(jerichoSource);
        }

        return results;
    }

    private List<Subtitle> loadSubtitlesPage(Source jerichoSource) {
        List<Subtitle> results = new ArrayList<Subtitle>();

        Element tableElement = (Element) jerichoSource.findAllElements("id", "search_results", false).get(0);

        List<?> trElements = tableElement.findAllElements(HTMLElementName.TR);
        Element trElement;
        Subtitle sub;
        for (Object trObject : trElements) {
            trElement = (Element) trObject;
            String style = trElement.getAttributeValue("style");
            if (!"display:none".equals(style)) {

                sub = new Subtitle();
                sub.setSubSource(SITE);

                List<?> tdElements = trElement.findAllElements(HTMLElementName.TD);
                if (tdElements.size() >= 4) {


                    // TITLE/URL
                    Element titleTd = (Element) tdElements.get(0);
                    Element firstLink = (Element) titleTd.findAllElements(HTMLElementName.A).get(0);
                    String fileName = firstLink.getContent().getTextExtractor().toString();

                    ElementOnlyTextExtractor extractor = new ElementOnlyTextExtractor(titleTd.getContent());
                    String extra = extractor.toString();
                    if(extra.trim().length() > 0){
                        fileName +=" " + extractor.toString();
                    }
                    sub.setFileName(fileName);


                    // LANG
                    Element flagTd = (Element) tdElements.get(1);
                    List<?> divElements = flagTd.findAllElements(HTMLElementName.DIV);
                    Element divElement;
                    Iterator<?> divs = divElements.iterator();
                    while (divs.hasNext()) {
                        divElement = (Element) divs.next();
                        //LOGGER.info(divElement.toString());
                        String cls = divElement.getAttributeValue("class");
                        if (cls != null && cls.startsWith("flag")) {
                            sub.setLanguage(cls.substring(5));
                        }
                    }

                    // CD
                    Element cdTd = (Element) tdElements.get(2);
                    sub.setNoCd(cdTd.getContent().getTextExtractor().toString());

                    // TYPE & URL
                    Element typeTd = (Element) tdElements.get(4);
                    Element span = (Element) typeTd.findAllElements("class", "p", false).get(0);
                    Element link = (Element) typeTd.findAllElements("a").get(0);
                    sub.setType(span.getContent().getTextExtractor().toString());
                    sub.setFileUrl(SITE + link.getAttributeValue("href"));

                    results.add(sub);
                }

            }
        }
        return results;
    }

    private String searchUrl(String title) {
        String encoded = encode(title);
        return SITE + "/en/search2/sublanguageid-all/moviename-" + encoded;
    }

    /**
     * @param fileName 
     * @return the imdb url
     */
    private String encode(String str) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Could not cencode UTF-8", ex);
        }
        return encoded;
    }
}