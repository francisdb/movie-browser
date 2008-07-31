/*
 * MainFrame.java
 *
 * Created on January 24, 2007, 10:47 PM
 */
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.*;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.service.MovieFinder;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.swing.JTable;
import javax.swing.JPopupMenu;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  francisdb
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private File selectedFile;
    private final MovieBrowser browser;
    private final ImageCache imageCache;

    private JButton imdbButton;
    private JButton tomatoesButton;
    private JButton moviewebButton;
    private JButton omdbButton;
    
    /** 
     * Creates new form MainFrame
     * @param browser
     * @param imageCache 
     */
    @Inject
    public MainFrame(final MovieBrowser browser, final ImageCache imageCache) {
        this.browser = browser;
        this.imageCache = imageCache;
        this.setIconImage(loadIcon("images/32/video-x-generic.png").getImage());
        this.setPreferredSize(new Dimension(1000, 600));
        initComponents();
        setLocationRelativeTo(null);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setModel(new MovieInfoTableModel());
        setColumnWidths();
        addIcons();
       
        
        this.setVisible(true);
    }
    
    private void setColumnWidths(){
        movieTable.getColumn(MovieInfoTableModel.STATUS_COLUMN_NAME).setCellRenderer(new StatusCellRenderer());
        movieTable.getColumn(MovieInfoTableModel.STATUS_COLUMN_NAME).setPreferredWidth(16);
        movieTable.getColumn(MovieInfoTableModel.MOVIE_COLUMN_NAME).setPreferredWidth(150);
    }
    
    private void addIcons() {
        buttonPanel.setLayout(new FlowLayout());
       
        imdbButton = new JButton(loadIcon("images/16/imdb.png"));
        imdbButton.setToolTipText("Open on imdb website");
        imdbButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(imdbButton);
            }
        });
        buttonPanel.add(imdbButton);
        tomatoesButton = new JButton(loadIcon("images/16/rottentomatoes.png"));
        tomatoesButton.setToolTipText("Open on rottentomatoes website");
        tomatoesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(tomatoesButton);
            }
        });
        buttonPanel.add(tomatoesButton);
        moviewebButton = new JButton(loadIcon("images/16/movieweb.png"));
        moviewebButton.setToolTipText("Open on movieweb website");
        moviewebButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(moviewebButton);
            }
        });
        buttonPanel.add(moviewebButton);
        omdbButton = new JButton(loadIcon("images/16/omdb.png"));
        omdbButton.setToolTipText("Open on omdb website");
        omdbButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(omdbButton);
            }
        });
        buttonPanel.add(omdbButton);
    }

    private void openLinkFor(JButton button) {
        String link = button.getActionCommand();
        if (link != null && link.length() > 0) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (URISyntaxException ex) {
                LOGGER.error("Failed launching default browser for " + link, ex);
            } catch (IOException ex) {
                LOGGER.error("Failed launching default browser for " + link, ex);
            }
        }else{
            JOptionPane.showMessageDialog(this, "No movie selected or no link found", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Makes the frame ready for use
     */
    public void load() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                browser.getMovieFinder().stop();
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
                        File sample = browser.getFileSystemScanner().findSample(info.getDirectory());
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
        movieTableScrollPane = new javax.swing.JScrollPane();
        movieTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        movieHeader = new org.jdesktop.swingx.JXHeader();
        buttonPanel = new javax.swing.JPanel();
        loadProgressBar = new javax.swing.JProgressBar();
        infoLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        movieMenu = new javax.swing.JMenu();
        importMenuItem = new javax.swing.JMenuItem();
        clearListMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        clearCacheMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Movie browser");
        setName("mainFrame"); // NOI18N

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(530);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);

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
        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                movieTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                movieTableMouseReleased(evt);
            }
        });
        movieTableScrollPane.setViewportView(movieTable);

        jSplitPane1.setLeftComponent(movieTableScrollPane);

        jScrollPane2.setViewportView(infoTextPane);

        movieHeader.setDescription("");
        movieHeader.setTitle("");
        movieHeader.setToolTipText("Movie info");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(movieHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(movieHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
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

        clearListMenuItem.setMnemonic('C');
        clearListMenuItem.setText("Clear List");
        clearListMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearListMenuItemActionPerformed(evt);
            }
        });
        movieMenu.add(clearListMenuItem);

        jMenuBar1.add(movieMenu);

        toolsMenu.setText("Tools");

        clearCacheMenuItem.setText("Clear Cache");
        clearCacheMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCacheMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(clearCacheMenuItem);

        jMenuBar1.add(toolsMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLabel)
                    .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            Settings.addFolder(newFolder);
            this.selectedFile = newFolder;
            fillTable();
        } else {
            LOGGER.debug("No Selection ");
        }
}//GEN-LAST:event_importMenuItemActionPerformed

private void clearListMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearListMenuItemActionPerformed
    clearTableList();
}//GEN-LAST:event_clearListMenuItemActionPerformed

    /**
     * Clear the table list, this does not clear the cache.
     */
    private void clearTableList() {
        MovieInfoTableModel model = (MovieInfoTableModel) movieTable.getModel();
        model.clear();
    }

    /**
     * Clear cache data, including images and persistence DB. 
     * @param evt
     */
private void clearCacheMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCacheMenuItemActionPerformed
//    //Clear the image folder
//    File imagesDir = new File(Settings.getImageCacheDir().getName());
//    System.out.println(imagesDir.getAbsolutePath());
//    imagesDir.delete();
//       
//    //Clear the persistence db. For testing I tried to pass the selected row but
//    //still get detached error - for more info see commented code in MovieCache class.
//    MovieInfoTableModel model = (MovieInfoTableModel)movieTable.getModel();
//    MovieInfo info = (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
//    //System.out.println(info.getDirectory());
//    MovieCache movies = new MovieCache();
//    movies.removeFromList(info.getMovie());
//    
//    //clear the table values
//    clearTableList();
}//GEN-LAST:event_clearCacheMenuItemActionPerformed

    /**
     * This method is the movie tables right click mouse event action listner. 
     * It identifies the row (and column, might be useful for other right click options
     * added later) the mouse clicks on and selcts that row by storing the row and column 
     * values in int variables row and column. 
     * @param evt
     */
private void movieTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieTableMouseReleased
    showPopup(evt);
}//GEN-LAST:event_movieTableMouseReleased

private void movieTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieTableMousePressed
    // needed for linux gtk look and feel
    showPopup(evt);
}//GEN-LAST:event_movieTableMousePressed

    private void showPopup(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            JTable source = (JTable) evt.getSource();
            int row = source.rowAtPoint(evt.getPoint());
            int column = source.columnAtPoint(evt.getPoint());
            source.changeSelection(row, column, false, false);

            JPopupMenu popup = new JPopupMenu();
            popup.add(new TrailerAction());
            popup.add(new WatchSampleAction());
            popup.add(new CrawlSubtitleAction());
            popup.add(new EditAction());
            LOGGER.info("Showing popup");
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    
    private void fillTable() {
        infoLabel.setText("Scanning folders...");
        loadProgressBar.setIndeterminate(true);
        final Set<String> folders = Settings.loadFolders();
        new SwingWorker<List<MovieInfo>, Void>() {
            @Override
            protected List<MovieInfo> doInBackground() throws Exception {
                return browser.getFolderScanner().scan(folders);
            }

            @Override
            protected void done() {
                try {
                    List<MovieInfo> movies = get();
                    MovieInfoTableModel model = (MovieInfoTableModel) movieTable.getModel();
                    model.clear();
                    model.addAll(movies);
                    infoLabel.setText(model.getRowCount() + " movies found, loading info...");
                    loadMovies(movies);
                } catch (InterruptedException ex) {
                    LOGGER.error("Loading interrupted", ex);
                    loadProgressBar.setIndeterminate(false);
                } catch (ExecutionException ex) {
                    LOGGER.error("Loading failed", ex.getCause());
                    loadProgressBar.setIndeterminate(false);
                }
            }
        }.execute();
    }

    private void loadMovies(final List<MovieInfo> infos) {
        loadProgressBar.setIndeterminate(true);
        new SwingWorker<MovieInfo, Void>() {

            @Override
            protected MovieInfo doInBackground() throws Exception {
                browser.getMovieFinder().start();
                browser.getMovieFinder().loadMovies(infos);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                    ex.getCause().printStackTrace();
                } finally {
                    loadProgressBar.setIndeterminate(false);
                    infoLabel.setText("All movie info loaded.");
                }
            }
        }.execute();

    }
    
    private void setImage(MovieInfo info){
        if(info.getImage() == null){
            movieHeader.setIcon(null);
        }else{
            movieHeader.setIcon(new ImageIcon(info.getImage()));
        }
    }
    
    private void showMovie(final MovieInfo info){
        // TODO need better image cache, if loading takes a lot of time the
        // image might be shown after a new movie was selected
        if(info.getImage() == null){
            imageCache.loadImg(info);
            if(info.getImage() == null){
                new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        imageCache.saveImgToCache(info.getMovie());
                        imageCache.loadImg(info);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            setImage(info);
                        } catch (InterruptedException ex) {
                            LOGGER.error("Worker interrupted", ex);
                        } catch (ExecutionException ex) {
                            LOGGER.error("Worker failed", ex.getCause());
                        }
                    }
                }.execute();
            }else{
                setImage(info);
            }
        }else{
            setImage(info);
        }
        if(info.getMovie().getTitle() == null){
            movieHeader.setTitle(info.getDirectory().getName());
        }else{
            movieHeader.setTitle(info.getMovie().getTitle());
        }
        movieHeader.setDescription(info.getMovie().getPlot());
        imdbButton.setActionCommand(MovieFinder.generateImdbUrl(info.getMovie()));
        tomatoesButton.setActionCommand(MovieFinder.generateTomatoesUrl(info.getMovie()));
        // TODO save and use these links
        moviewebButton.setActionCommand("");
        omdbButton.setActionCommand("");
        
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
        builder.append("IMDB ").append(info.getMovie().getImdbScore()).append(" ").append(info.getMovie().getVotes()).append("\n");
        builder.append("TOMATO ").append(info.getMovie().getTomatoScore()).append("\n");
        builder.append("MovieWeb ").append(info.getMovie().getMovieWebScore()).append("\n");
        builder.append(info.getMovie().getPlot());
        infoTextPane.setText(builder.toString());
        infoTextPane.setCaretPosition(0);
    }
        
    
    private MovieInfo getSelectedMovie(){
        return (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
    }
    
    /**
     * This action lets you edit a record
     * @param evt
     */
    private class EditAction extends AbstractAction {

        public EditAction() {
            super("Edit", loadIcon("images/16/edit.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            EditMovieFrame editMovieFrame = new EditMovieFrame(getSelectedMovie(), browser.getImdbSearch(), browser.getMovieFinder());
            editMovieFrame.setLocationRelativeTo(movieTableScrollPane);
            editMovieFrame.setVisible(true);
            
        }
    }
    
    
     /**
     * This action  tries to play the sample video if any. 
     * TODO Code duplicated here, from movieTable.addMouseListener in Load() method. 
     * @param evt
     */
    private class TrailerAction extends AbstractAction {

        public TrailerAction() {
            super("IMDB Trailer", loadIcon("images/16/video-x-generic.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String url = imdbButton.getActionCommand() + "trailers";
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (URISyntaxException ex) {
                LOGGER.error("Failed launching default browser for " + url, ex);
            } catch (IOException ex) {
                LOGGER.error("Failed launching default browser for " + url, ex);
            }
        }
    }

    /**
     * This action opens the sample if a sample is found
     * @param evt
     */
    private class  WatchSampleAction extends AbstractAction{
        public WatchSampleAction() {
            super("Watch Sample", loadIcon("images/16/video-display.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            File sample = browser.getFileSystemScanner().findSample(info.getDirectory());
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
    
    /**
     * This action opens the SubtitleCrawlerFrame if a video file is found in the directory. 
     */
    private class CrawlSubtitleAction extends AbstractAction {
        public CrawlSubtitleAction() {
            super("Subtitle Crawler", loadIcon("images/16/subtitles.png"));
        }
        
        @Override 
        public void actionPerformed(ActionEvent e) {
            //TO DO:
            //JOptionPane.showMessageDialog(MainFrame.this, "Subtitle Crawler Coming Soon.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            
            MovieInfo info = getSelectedMovie(); 
            File dir = info.getDirectory();
            for(File file:dir.listFiles()) {
                if(file.isDirectory()) {
                    File child = file;
                    for(File file2:child.listFiles()) {
                        if(file2.isFile()) {
                            checkFileType(file2);
                        }
                    }
                }
                else {
                    if(file.isFile()) {
                        checkFileType(file);
                    }
                }
            }            
        }
    }
    
    /**
     * Checks what type of file is found and if video file is found, calls openSubCrawler
     * @param fileName
     */
    private void checkFileType(File file) {
        String fileType = file.getName().substring((file.getName().length()-4), file.getName().length());
        String fileName = file.getName().substring(0,(file.getName().length()-4));
        if(fileName.endsWith(".")) {
            fileName = fileName.substring(0, (fileName.length()-1));
        }
        
        System.out.println(fileType);
        System.out.println(fileName);
        if(fileType.equals(".avi")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals(".mpg")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals(".mp4")) {
            openSubCrawler(fileName);
        }        
        else if(fileType.equals("divx")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals(".mkv")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals("xvid")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals("mpeg")) {
            openSubCrawler(fileName);
        }
        else if(fileType.equals("m4v")) {
            openSubCrawler(fileName);
        }
    }
    
    /**
     * Loads SubtitleCrawlerFrame
     * @param fileName
     */
    public void openSubCrawler(String fileName) {
        System.out.println("Testing");
        SubtitleCrawlerFrame subtitleCrawler = new SubtitleCrawlerFrame(fileName);
        subtitleCrawler.setLocationRelativeTo(movieTableScrollPane);
        subtitleCrawler.setVisible(true);
    }
    
    /**
     * Loads an icon from the specified filename
     * @param fileName
     * @return the loaded ImageIcon
     */
    private static final ImageIcon loadIcon(String fileName) {
        ImageIcon icon = null;
        URL resource = MainFrame.class.getClassLoader().getResource(fileName);
        if (resource != null) {
            icon = new ImageIcon(resource);
        } else {
            LOGGER.error("Icon does not exist: " + fileName);
        }
        return icon;
    }
    
    private static final class StatusCellRenderer extends DefaultTableCellRenderer{

        private final Icon defaultIcon = loadIcon("images/16/bullet_black.png");
        private final Icon loadedIcon = loadIcon("images/16/bullet_green.png");
        private final Icon loadingIcon = loadIcon("images/16/bullet_orange.png");
        //private final Icon failedIcon = loadIcon("images/16/bullet_red.png");
        
        
        public StatusCellRenderer() {
            setIcon(defaultIcon);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            MovieStatus movieStatus = (MovieStatus) value;
            switch(movieStatus){
                case NEW:
                    setIcon(defaultIcon);
                    break;
                case CACHED:
                    setIcon(loadedIcon);
                    break;
                case LOADED:
                    setIcon(loadedIcon);
                    break;
                case LOADING_IMDB:
                    setIcon(loadingIcon);
                    break;
                case LOADING_IMG:
                    setIcon(loadingIcon);
                    break;
                case LOADING_TOMATOES:
                    setIcon(loadingIcon);
                    break;
            }
            setText(null);
            return this;
        }
        
        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JMenuItem clearCacheMenuItem;
    private javax.swing.JMenuItem clearListMenuItem;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar loadProgressBar;
    private org.jdesktop.swingx.JXHeader movieHeader;
    private javax.swing.JMenu movieMenu;
    private javax.swing.JTable movieTable;
    private javax.swing.JScrollPane movieTableScrollPane;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
    
}
