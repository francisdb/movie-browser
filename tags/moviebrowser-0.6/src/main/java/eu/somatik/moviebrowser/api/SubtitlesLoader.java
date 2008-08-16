/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.api;

import eu.somatik.moviebrowser.domain.Subtitle;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author francisdb
 */
public interface SubtitlesLoader {

    Set<Subtitle> getOpenSubsResults(String localFileName) throws IOException;

}