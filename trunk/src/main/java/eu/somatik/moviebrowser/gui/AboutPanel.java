/*
 * AboutPanel.java
 *
 * Created on August 3, 2008, 11:10 PM
 */
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.config.Settings;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  francisdb
 */
public class AboutPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutPanel.class);

    /** Creates new form AboutPanel
     * @param settings 
     */
    public AboutPanel(final Settings settings) {
        initComponents();

        StringBuilder builder = new StringBuilder("<html>");
        builder.append("<h2>Movie Browser</h2>");
        builder.append("<strong>Version</strong> ").append(getVersion()).append("<br/>");
        builder.append("<strong>Settings</strong> ").append(settings.getSettingsDir()).append("<br/>");
        builder.append("<strong>Site</strong> <a href=\"http://code.google.com/p/movie-browser/\">http://code.google.com/p/movie-browser/</a>");
        builder.append("<p>THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE CREATORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</p>");
        builder.append("</html>");
        aboutTextPane.setContentType("text/html");
        aboutTextPane.setText(builder.toString());
    }

    private String getVersion() {
        String version = "";
        InputStream is = null;
        try {
            String pom = "META-INF/maven/org.somatik/moviebrowser/pom.properties";
            URL resource = AboutPanel.class.getClassLoader().getResource(pom);
            if (resource == null) {
                throw new IOException("Could not load pom properties: " + pom);
            }
            is = resource.openStream();
            Properties props = new Properties();
            props.load(is);
            version = props.getProperty("version");
        } catch (IOException ex) {
            LOGGER.error("Could not read pom.properties", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close InputStream", ex);
                }
            }
        }
        return version;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutScrollPane = new javax.swing.JScrollPane();
        aboutTextPane = new javax.swing.JTextPane();

        aboutScrollPane.setViewportView(aboutTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane aboutScrollPane;
    private javax.swing.JTextPane aboutTextPane;
    // End of variables declaration//GEN-END:variables
}
