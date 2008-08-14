package com.flicklib.service.sub;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.SubtitlesLoader;
import com.flicklib.domain.Subtitle;
import com.flicklib.service.HttpSourceLoader;

/**
 *
 * @author francisdb
 */
public class OpenSubtitlesLoaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesLoaderTest.class);

    /**
     * Test of search method, of class OpenSubtitlesLoader.
     * @throws Exception 
     */
    @Test
    @Ignore
    public void testSearch() throws Exception {
        SubtitlesLoader loader = new OpenSubtitlesLoader(new HttpSourceLoader(null));
        Set<Subtitle> result = loader.search("The Science of Sleep", null);
        assertTrue(result.size() > 0);
        for(Subtitle sub:result){
            LOGGER.info(sub.getFileName());
        }
        result = loader.search("The.Science.of.Sleep.LIMITED.DVDRip.XViD.-iMBT.avi", null);
        assertTrue(result.size() > 0);
        for(Subtitle sub:result){
            LOGGER.info(sub.getFileName());
        }
    }

}