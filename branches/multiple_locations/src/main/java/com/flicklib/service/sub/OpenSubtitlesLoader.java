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
package com.flicklib.service.sub;

import com.flicklib.api.SubtitlesLoader;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.google.inject.Inject;
import com.flicklib.domain.Subtitle;
import com.flicklib.service.SourceLoader;
import com.flicklib.tools.Param;
import com.flicklib.tools.ElementOnlyTextExtractor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * http://www.opensubtitles.org
 * @author francisdb
 */
public class OpenSubtitlesLoader implements SubtitlesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesLoader.class);
    private static final String SITE = "http://www.opensubtitles.org";
    private final SourceLoader sourceLoader;

    /**
     * Constructs a new OpenSubtitlesLoader
     * @param sourceLoader
     */
    @Inject
    public OpenSubtitlesLoader(final SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
    }

    @Override
    public Set<Subtitle> search(String localFileName, String imdbId) throws IOException {
        String url = searchUrl(localFileName);
        int carryOn = 1;
        String source = sourceLoader.load(url);
        Source jerichoSource = new Source(source);
        jerichoSource.fullSequentialParse();
        Set<Subtitle> results = new HashSet<Subtitle>();

        Element titleElement = (Element) jerichoSource.findAllElements(HTMLElementName.TITLE).get(0);
        String title = titleElement.getContent().getTextExtractor().toString();
        if (title.contains("(results)")) {
            //first check if the results page contains no results. 
            List<?> divElements = jerichoSource.findAllElements(HTMLElementName.DIV);
            Iterator<?> j = divElements.iterator();
            while(j.hasNext() && carryOn==1) {
                Element divElement  = (Element) j.next();
                if(divElement.getTextExtractor().toString().contains("No results found")) {
                    carryOn = 0;
                }
                else {
                    carryOn = 1;
                }
            }
            
            //if the results page does contain results then load first link
            if(carryOn!=0) {
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
                
                //Get links for other pages.
                List<String> pages = new ArrayList<String>();
                pages.addAll(getPageLinks(jerichoSource));
                Iterator<?> k = pages.iterator();
                while(k.hasNext()) {
                    String link = (String) k.next();
                    source = sourceLoader.load(SITE + link);
                    jerichoSource = new Source(source);
                    jerichoSource.fullSequentialParse();
                    results.addAll(loadSubtitlesPage(jerichoSource));
                }
            }
            
        } else {
            // direct hit
            results = loadSubtitlesPage(jerichoSource);
            
            //Get links for other pages.
            Set<String> pages = new HashSet<String>();
            pages.addAll(getPageLinks(jerichoSource));
            for(String link:pages){
                source = sourceLoader.load(SITE + link);
                jerichoSource = new Source(source);
                jerichoSource.fullSequentialParse();
                results.addAll(loadSubtitlesPage(jerichoSource));
            }
        }
        
        return results;
    }
    
    /**
     * This method retrieves the links for all pages other than the first page. 
     * @param URL
     * @return
     */
    private Set<String> getPageLinks(Source source) {
        Set<String> links = new HashSet<String>();
        List<?> linksElements = source.findAllElements(HTMLElementName.A);
        Iterator<?> i;
        i = linksElements.iterator();
        
        while (i.hasNext()) {
            Element linkElement = (Element) i.next();
            String href = linkElement.getAttributeValue("href");
            if(!href.isEmpty() && href.contains("/offset-")) {
                LOGGER.info(linkElement.getTextExtractor().toString());
                links.add(href);
            }
        }
        return links;
    }

    private Set<Subtitle> loadSubtitlesPage(Source jerichoSource) {
        Set<Subtitle> results = new HashSet<Subtitle>();

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
                        fileName = extractor.toString()+" "+fileName;
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
        String encoded = Param.encode(title);
        return SITE + "/en/search2/sublanguageid-all/moviename-" + encoded;
    }

    
}
