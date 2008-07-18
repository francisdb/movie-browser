/*
 * EditMovieFrame.java
 *
 * Created on 17 July 2008, 22:21
 */

package eu.somatik.moviebrowser;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.net.URLEncoder;
        
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.Element;

/**
 *
 * @author  rug
 */
public class EditMovieFrame extends javax.swing.JFrame {

    /** Creates new form EditMovieFrame */
    public EditMovieFrame(String searchkey) {
        initComponents();
        searchTextField.setText(searchkey);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchTextField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsList = new javax.swing.JList();
        updateButton = new javax.swing.JButton();
        statusProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Edit Movie");

        searchLabel.setText("Look for:");

        searchButton.setText("Find");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resultsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(resultsList);

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        statusProgressBar.setForeground(new java.awt.Color(255, 153, 51));
        statusProgressBar.setString("");
        statusProgressBar.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateButton)
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(searchLabel)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    try {
        getResults();
    } 
    catch (Exception ex) {
        System.out.println(ex);
    }
}//GEN-LAST:event_searchButtonActionPerformed

private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
    // TODO add your handling code here:
    JOptionPane.showMessageDialog(EditMovieFrame.this, "Not implemented");
}//GEN-LAST:event_updateButtonActionPerformed

 private Source getRequest(String searchkey) throws Exception {

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://www.imdb.com/find?q=" + URLEncoder.encode(searchTextField.getText(), "UTF-8"));
        statusProgressBar.setIndeterminate(true);
        client.executeMethod(method);
        
        Source source = null;
        source = new Source(method.getResponseBodyAsString());
        //source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        return source;
    }
 
 private void getResults() throws Exception {
        statusProgressBar.setString("Searching...");
        DefaultListModel listModel = new DefaultListModel();
        
        Source source = null;
        source = getRequest(searchTextField.getText());
        System.out.println(source);
        Element titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
        
        if (titleElement.getContent().getTextExtractor().toString().contains("IMDb Search")) {
            List<?> linkElements = source.findAllElements(HTMLElementName.A);
            for (Iterator<?> i = linkElements.iterator(); i.hasNext();) {
                Element linkElement = (Element) i.next();
                String href = linkElement.getAttributeValue("href");
                System.out.println(href);
                if (href != null && href.startsWith("/title/tt")) {
                    String name = linkElement.getContent().getTextExtractor().toString();
                    int questionMarkIndex = href.indexOf('?');
                    if (questionMarkIndex != -1) {
                        href = href.substring(0, questionMarkIndex);
                    }
                    listModel.addElement(name + " ~ " + "http://www.imdb.com" + href);
                    titleElement = (Element) source.findAllElements(HTMLElementName.TITLE).get(0);
                }
            }

        }
        resultsList.setModel(listModel);
        statusProgressBar.setIndeterminate(false);
        if(listModel.isEmpty())
            statusProgressBar.setString("No Results Found");
        else
            statusProgressBar.setString("");
 }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList resultsList;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JProgressBar statusProgressBar;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

}
