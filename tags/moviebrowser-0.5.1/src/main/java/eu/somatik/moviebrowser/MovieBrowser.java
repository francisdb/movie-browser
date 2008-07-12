/*
 * MovieBrowser.java
 *
 * Created on May 6, 2007, 10:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
public class MovieBrowser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieBrowser.class);

    /** Creates a new instance of MovieBrowser */
    private MovieBrowser() {
        // nothing here
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    LOGGER.error("Error setting native LAF", ex);
                } catch (InstantiationException ex) {
                    LOGGER.error("Error setting native LAF", ex);
                } catch (IllegalAccessException ex) {
                    LOGGER.error("Error setting native LAF", ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    LOGGER.error("Error setting native LAF", ex);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                         MainFrame mainFrame = new MainFrame();
                         mainFrame.setVisible(true);
                         mainFrame.load();
                    }
                });
               
            }
        });
    }
}
