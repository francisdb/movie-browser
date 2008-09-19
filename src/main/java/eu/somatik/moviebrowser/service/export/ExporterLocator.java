/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service.export;

import java.util.Iterator;

/**
 *
 * @author francisdb
 */
public interface ExporterLocator {

    Exporter get(String name);

    Iterator<String> list();

    void register(Exporter exporter);

}
