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
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.config.Settings;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  francisdb
 */
public class AboutPanel extends javax.swing.JPanel implements HyperlinkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutPanel.class);

    /** Creates new form AboutPanel
     * @param settings 
     */
    public AboutPanel(final Settings settings) {
        initComponents();

        StringBuilder builder = new StringBuilder("<html>");
        builder.append("<h2>Movie Browser</h2>");
        String version = settings.getApplicationVersion();
        if(version == null){
            version = "[DEV VERSION]";
        }
        builder.append("<strong>Version</strong> ").append(version).append("<br/>");
        builder.append("<strong>Settings</strong> ").append(settings.getSettingsDir()).append("<br/>");
        builder.append("<strong>Memory total</strong> ").append(mem(Runtime.getRuntime().totalMemory())).append("<br/>");
        builder.append("<strong>Memory free</strong> ").append(mem(Runtime.getRuntime().freeMemory())).append("<br/>");
        builder.append("<strong>Available Processors</strong> ").append(Runtime.getRuntime().availableProcessors()).append("<br/>");
        builder.append("<strong>Site</strong> <a href=\"http://code.google.com/p/movie-browser/\">http://code.google.com/p/movie-browser/</a>");
        builder.append("<p>THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE CREATORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</p>");
        builder.append("</html>");
        aboutTextPane.setContentType("text/html");
        aboutTextPane.setText(builder.toString());
        aboutTextPane.addHyperlinkListener(this);
    }
    
    private String mem(long bytes){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        double mb = bytes / (1024.0 * 1024.0);
        return df.format(mb)+" MB";
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(event.getURL().toURI());
            } catch (IOException ex) {
                LOGGER.error("Could not open hyperlink", ex);
            } catch (URISyntaxException ex) {
                LOGGER.error("Could not open hyperlink", ex);
            }
        }
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

        aboutTextPane.setEditable(false);
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
