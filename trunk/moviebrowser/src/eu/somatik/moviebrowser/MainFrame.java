/*
 * MainFrame.java
 *
 * Created on January 24, 2007, 10:47 PM
 */

package eu.somatik.moviebrowser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  francisdb
 */
public class MainFrame extends javax.swing.JFrame {
    
    private File selectedFile;
    
    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setModel(new MovieInfoTableModel());
        movieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    MovieInfo info = (MovieInfo)movieTable.getValueAt(movieTable.getSelectedRow(), MovieInfoTableModel.MOVIE_COL);
                    if(info.getUrl() == null){
                        loadMovie(info);
                    }else{
                        showMovie(info);
                    }
                }
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        movieTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        imdbHyperlink = new org.jdesktop.swingx.JXHyperlink();
        tomatoesHyperlink = new org.jdesktop.swingx.JXHyperlink();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        movieHeader = new org.jdesktop.swingx.JXHeader();
        loadProgressBar = new javax.swing.JProgressBar();
        infoLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        MovieMenu = new javax.swing.JMenu();
        ImportMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Somatik.be movie browser");
        setName("mainFrame");

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setResizeWeight(1.0);

        movieTable.setAutoCreateRowSorter(true);
        movieTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Movie", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        movieTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        movieTable.setFillsViewportHeight(true);
        movieTable.setGridColor(java.awt.SystemColor.controlHighlight);
        jScrollPane3.setViewportView(movieTable);

        jSplitPane1.setLeftComponent(jScrollPane3);

        imdbHyperlink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imdbHyperlinkActionPerformed(evt);
            }
        });

        tomatoesHyperlink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomatoesHyperlinkActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(infoTextPane);

        movieHeader.setDescription("");
        movieHeader.setTitle("");
        movieHeader.setToolTipText("Movie info");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(movieHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(imdbHyperlink, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(tomatoesHyperlink, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(movieHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imdbHyperlink, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tomatoesHyperlink, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        infoLabel.setText("Ready to load movies");

        MovieMenu.setMnemonic('M');
        MovieMenu.setText("Movie");

        ImportMenuItem.setMnemonic('I');
        ImportMenuItem.setText("Import folder...");
        ImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportMenuItemActionPerformed(evt);
            }
        });
        MovieMenu.add(ImportMenuItem);

        jMenuBar1.add(MovieMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadProgressBar.setIndeterminate(true);
            final File selected = chooser.getSelectedFile();
            this.selectedFile = selected;
            new SwingWorker<List<MovieInfo>,Void>() {
                protected List<MovieInfo> doInBackground() throws Exception {
                    
                    List<MovieInfo> movies = new ArrayList<MovieInfo>();
                    for(File file:selected.listFiles()){
                        if(file.isDirectory()){
                            movies.add(new MovieInfo(file));
                        }
                    }
                    
                    return movies;
                }
                
                protected void done(){
                    try{
                        List<MovieInfo> movies = get();
                        MovieInfoTableModel model = (MovieInfoTableModel)movieTable.getModel();
                        model.addAll(movies);
                        infoLabel.setText(model.getRowCount()+" movies loaded");
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }catch(ExecutionException ex){
                        ex.getCause().printStackTrace();
                    }finally{
                        loadProgressBar.setIndeterminate(false);
                    }
                }
                
            }.execute();
            
        } else {
            System.out.println("No Selection ");
        }
    }//GEN-LAST:event_ImportMenuItemActionPerformed

    private void tomatoesHyperlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomatoesHyperlinkActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(tomatoesHyperlink.getText()));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_tomatoesHyperlinkActionPerformed

    private void imdbHyperlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imdbHyperlinkActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(imdbHyperlink.getText()));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_imdbHyperlinkActionPerformed
        
    private void loadMovie(final MovieInfo info){
        loadProgressBar.setIndeterminate(true);
        
        new SwingWorker<MovieInfo,Void>() {
            protected MovieInfo doInBackground() throws Exception {
                MovieFinder main = new MovieFinder();
                return main.getMovieInfo(info);
            }
            
            protected void done() {
                try{
                    showMovie(get());
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }catch(ExecutionException ex){
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                    ex.getCause().printStackTrace();
                }finally {
                    loadProgressBar.setIndeterminate(false);
                }
            }
            
        }.execute();
        
    }
    
    private void showMovie(MovieInfo info){
        if(info.getImage() == null){
            movieHeader.setIcon(null);
        }else{
            movieHeader.setIcon(new ImageIcon(info.getImage()));
        }
        movieHeader.setTitle(info.getTitle());
        movieHeader.setDescription(info.getPlot());
        imdbHyperlink.setText(MovieFinder.generateImdbUrl(info));
        tomatoesHyperlink.setText(MovieFinder.generateTomatoesUrl(info));
        
        StringBuilder builder = new StringBuilder();

        builder.append(info.getTitle()).append("\n");
        boolean first = true;
        for(String genre:info.getGenres()){
            if(first){
                first = false;
            }else{
                builder.append(", ");                
            }
            builder.append(genre);
        }
        builder.append("\n");
        first = true;
        for(String genre:info.getLanguages()){
            if(first){
                first = false; 
            }else{
                builder.append(", ");
            }
            builder.append(genre);
        }
        builder.append("\n");
        builder.append(info.getRuntime()).append("\n");
        builder.append("IMDB ").append(info.getRating()).append(" ").append(info.getVotes()).append("\n");
        builder.append("CRIT ").append(info.getTomatoesRating()).append(" USR ").append(info.getTomatoesRatingUsers()).append("\n");
        builder.append(info.getPlot());
        infoTextPane.setText(builder.toString());
        infoTextPane.setCaretPosition(0);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ImportMenuItem;
    private javax.swing.JMenu MovieMenu;
    private org.jdesktop.swingx.JXHyperlink imdbHyperlink;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar loadProgressBar;
    private org.jdesktop.swingx.JXHeader movieHeader;
    private javax.swing.JTable movieTable;
    private org.jdesktop.swingx.JXHyperlink tomatoesHyperlink;
    // End of variables declaration//GEN-END:variables
    
}
