/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.service.export;

import java.io.File;

/**
 *
 * @author francisdb
 */
public interface Exporter {

    /**
     * Performs an export to file
     * @param libName
     * @param index
     */
    void exportToFile(String libName, File index);

}
