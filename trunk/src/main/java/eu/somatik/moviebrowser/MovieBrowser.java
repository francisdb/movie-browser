/*
 * MovieBrowser.java
 *
 * Created on May 6, 2007, 10:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser;

/**
 *
 * @author francisdb
 */
public class MovieBrowser {
    
    /** Creates a new instance of MovieBrowser */
    private MovieBrowser() {
    }
    
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                try {
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch(Exception e) {
//                    System.out.println("Error setting native LAF: " + e);
//                }
                new MainFrame().setVisible(true);
            }
        });
    }
    
}
