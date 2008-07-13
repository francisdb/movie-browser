/*
 * MainFrame.java
 *
 * Created on January 24, 2007, 10:47 PM
 */
package eu.somatik.moviebrowser;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.scanner.FileSystemScanner;
import java.awt.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  francisdb
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private File selectedFile;
    private final MovieFinder finder;

    /** Creates new form MainFrame */
    public MainFrame() {
        this.setPreferredSize(new Dimension(1000,600));
        initComponents();
        setLocationRelativeTo(null);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setModel(new MovieInfoTableModel());
        this.setVisible(true);
        finder = new MovieFinder();
    }

    /**
     * Makes the frame ready for use
     */
    public void load() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                finder.stop();
            }
        });
        movieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && movieTable.getSelectedRowCount() == 1) {
                    MovieInfo info = (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
                    showMovie(info);
                }
            }
        });
        movieTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    MovieInfo info = (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        try {
                            Desktop.getDesktop().open(info.getDirectory());
                        } catch (IOException ex) {
                            LOGGER.error("Could not open dir " + info.getDirectory(), ex);
                        }
                    } else {
                        FileSystemScanner scanner = new FileSystemScanner();
                        File sample = scanner.findSample(info.getDirectory());
                        if (sample != null) {
                            try {
                                LOGGER.info("OPENING: " + sample);
                                Desktop.getDesktop().open(sample);
                            } catch (IOException ex) {
                                LOGGER.error("Could not launch default app for " + sample, ex);
                            }
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, "No sample found");
                        }
                    }
                }
            }
        });
        fillTable();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        movieMenu = new javax.swing.JMenu();
        importMenuItem = new javax.swing.JMenuItem();
        ClearListMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Somatik.be movie browser");
        setName("mainFrame"); // NOI18N

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(500);
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        infoLabel.setText("Ready to load movies");

        movieMenu.setMnemonic('M');
        movieMenu.setText("Movies");

        importMenuItem.setMnemonic('I');
        importMenuItem.setText("Import folder...");
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed(evt);
            }
        });
        movieMenu.add(importMenuItem);

        ClearListMenuItem.setMnemonic('C');
        ClearListMenuItem.setText("Clear List");
        ClearListMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearListMenuItemActionPerformed(evt);
            }
        });
        movieMenu.add(ClearListMenuItem);

        jMenuBar1.add(movieMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            addFolder(newFolder);
        } else {
            LOGGER.debug("No Selection ");
        }
}//GEN-LAST:event_importMenuItemActionPerformed

    private void tomatoesHyperlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomatoesHyperlinkActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(tomatoesHyperlink.getText()));
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed launching default browser for " + tomatoesHyperlink.getText(), ex);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + tomatoesHyperlink.getText(), ex);
        }
    }//GEN-LAST:event_tomatoesHyperlinkActionPerformed

    private void imdbHyperlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imdbHyperlinkActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(imdbHyperlink.getText()));
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed launching default browser for " + imdbHyperlink.getText(), ex);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + imdbHyperlink.getText(), ex);
        }
    }//GEN-LAST:event_imdbHyperlinkActionPerformed

private void ClearListMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearListMenuItemActionPerformed
    MovieInfoTableModel model = (MovieInfoTableModel)movieTable.getModel();
    model.clear();
}//GEN-LAST:event_ClearListMenuItemActionPerformed
        
    private void addFolder(File newFolder){
        final Set<String> folders = Settings.loadFolders();
            folders.add(newFolder.getAbsolutePath());
            Settings.saveFolders(folders);
            this.selectedFile = newFolder;
            fillTable();
    }
    
    private void fillTable(){
        infoLabel.setText("Scanning folders...");
        loadProgressBar.setIndeterminate(true);
        final Set<String> folders = Settings.loadFolders();
               new SwingWorker<List<MovieInfo>,Void>() {
                @Override
                protected List<MovieInfo> doInBackground() throws Exception {
                    finder.init();
                    File folder;
                    List<MovieInfo> movies = new ArrayList<MovieInfo>();
                    for(String path:folders){
                        folder = new File(path);
                        if(folder.exists()){
                            for(File file:folder.listFiles()){
                                if(file.isDirectory()){
                                    movies.add(new MovieInfo(file));
                                }
                            }
                        }
                    }
                    
                    return movies;
                }
                
                @Override
		protected void done(){
                    try{
                        List<MovieInfo> movies = get();
                        MovieInfoTableModel model = (MovieInfoTableModel)movieTable.getModel();
                        model.clear();
                        model.addAll(movies);
                        infoLabel.setText(model.getRowCount()+" movies found, loading info...");
                        loadMovies(movies);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                        loadProgressBar.setIndeterminate(false);
                    }catch(ExecutionException ex){
                        ex.getCause().printStackTrace();
                        loadProgressBar.setIndeterminate(false);
                    }
                }
                
            }.execute();
    }
    
    private void loadMovies(final List<MovieInfo> infos){
        loadProgressBar.setIndeterminate(true);
        new SwingWorker<MovieInfo,Void>() {
            @Override
			protected MovieInfo doInBackground() throws Exception {
                finder.loadMovies(infos);
                return null;
            }
            
            @Override
			protected void done() {
                try{
                    get();
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }catch(ExecutionException ex){
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                    ex.getCause().printStackTrace();
                }finally {
                    loadProgressBar.setIndeterminate(false);
                    infoLabel.setText("All movie info loaded.");
                }
            }
            
        }.execute();
        
    }
    
    private void showMovie(MovieInfo info){
        if(info.getImage() == null){
            
            ImageCache.loadImg(info);
        }
        if(info.getImage() == null){
            movieHeader.setIcon(null);
        }else{
            movieHeader.setIcon(new ImageIcon(info.getImage()));
        }
        if(info.getMovie().getTitle() == null){
            movieHeader.setTitle(info.getDirectory().getName());
        }else{
            movieHeader.setTitle(info.getMovie().getTitle());
        }
        movieHeader.setDescription(info.getMovie().getPlot());
        imdbHyperlink.setText(MovieFinder.generateImdbUrl(info.getMovie()));
        tomatoesHyperlink.setText(MovieFinder.generateTomatoesUrl(info.getMovie()));
        
        StringBuilder builder = new StringBuilder();

        builder.append(info.getMovie().getTitle()).append("\n");
        boolean first = true;
        for(Genre genre:info.getMovie().getGenres()){
            if(first){
                first = false;
            }else{
                builder.append(", ");                
            }
            builder.append(genre);
        }
        builder.append("\n");
        first = true;
        for(Language language:info.getMovie().getLanguages()){
            if(first){
                first = false; 
            }else{
                builder.append(", ");
            }
            builder.append(language);
        }
        builder.append("\n");
        builder.append(info.getMovie().getRuntime()).append(" min\n");
        builder.append("IMDB ").append(info.getMovie().getRating()).append(" ").append(info.getMovie().getVotes()).append("\n");
        builder.append("TOMATO ").append(info.getMovie().getTomatometer()).append("\n");
        builder.append("MovieWeb ").append(info.getMovie().getMovieWebStars()).append("\n");
        builder.append(info.getMovie().getPlot());
        infoTextPane.setText(builder.toString());
        infoTextPane.setCaretPosition(0);
    }
        

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ClearListMenuItem;
    private org.jdesktop.swingx.JXHyperlink imdbHyperlink;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar loadProgressBar;
    private org.jdesktop.swingx.JXHeader movieHeader;
    private javax.swing.JMenu movieMenu;
    private javax.swing.JTable movieTable;
    private org.jdesktop.swingx.JXHyperlink tomatoesHyperlink;
    // End of variables declaration//GEN-END:variables
    
}
