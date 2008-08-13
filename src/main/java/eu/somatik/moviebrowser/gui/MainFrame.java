/*
 * MainFrame.java
 *
 * Created on January 24, 2007, 10:47 PM
 */
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.*;
import com.google.inject.Inject;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.config.Settings;
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
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTable;
import javax.swing.JPopupMenu;

import eu.somatik.moviebrowser.domain.MovieInfo;
import com.flicklib.service.movie.apple.AppleTrailerFinder;
import com.flicklib.service.movie.imdb.ImdbTrailerFinder;
import com.flicklib.api.TrailerFinder;
import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.MovieFileFilter;
import eu.somatik.moviebrowser.tools.FileTools;
import eu.somatik.moviebrowser.tools.SwingTools;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Check Update Imports Should remove after duplicate code reomved. 
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 *
 * @author  francisdb
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private File selectedFile;
    private final MovieBrowser browser;
    private final IconLoader iconLoader;
    private final Settings settings;
    private final MovieInfoPanel movieInfoPanel;
    private final MovieFileFilter movieFileFilter;

    /** 
     * Creates new form MainFrame
     * @param browser
     * @param imageCache
     * @param iconLoader
     * @param settings
     * @param infoHandler 
     */
    @Inject
    public MainFrame(
            final MovieBrowser browser,
            final ImageCache imageCache,
            final IconLoader iconLoader,
            final Settings settings,
            final InfoHandler infoHandler) {
        this.browser = browser;
        this.iconLoader = iconLoader;
        this.settings = settings;
        this.movieFileFilter = new MovieFileFilter(false);
        this.setIconImage(iconLoader.loadIcon("images/32/video-x-generic.png").getImage());
        this.setPreferredSize(new Dimension(1000, 600));

        initComponents();
        this.movieInfoPanel = new MovieInfoPanel(imageCache, iconLoader, infoHandler);
        jSplitPane1.setRightComponent(movieInfoPanel);
        setLocationRelativeTo(null);
        movieTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setModel(new MovieInfoTableModel(infoHandler));
        setColumnWidths();

        loadLookAndFeels();



        Timer timer = new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int tasks = browser.getMovieFinder().getRunningTasks();
                if (tasks == 0) {
                    loadProgressBar.setIndeterminate(false);
                    loadProgressBar.setString("ready");
                    loadProgressBar.setValue(loadProgressBar.getMaximum());
                } else {
                    loadProgressBar.setIndeterminate(true);
                    loadProgressBar.setString(tasks + " task(s) remaining");
                    loadProgressBar.setToolTipText("The task indicator might fluctuate while data is loaded from imdb because other serverices are only activated after imdb is loaded!");
                }
            }
        });
        timer.start();
    }

    private void setColumnWidths() {
        movieTable.getColumn(MovieInfoTableModel.STATUS_COLUMN_NAME).setCellRenderer(new MovieStatusCellRenderer(iconLoader));
        movieTable.getColumn(MovieInfoTableModel.STATUS_COLUMN_NAME).setPreferredWidth(16);
        movieTable.getColumn(MovieInfoTableModel.MOVIE_COLUMN_NAME).setPreferredWidth(150);
        movieTable.getColumn(MovieInfoTableModel.SCORE_COLUMN_NAME).setCellRenderer(new MovieScoreCellRenderer());
    }

    private void loadLookAndFeels() {
        lookAndFeelMenu.removeAll();
        LookAndFeel current = UIManager.getLookAndFeel();
        ButtonGroup group = new ButtonGroup();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            final String className = info.getClassName();
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(info.getName());
            group.add(item);
            if (current.getClass().getName().equals(className)) {
                item.setSelected(true);
            }
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(className);
                        Map<String, String> prefs = settings.loadPreferences();
                        prefs.put("lookandfeel", className);
                        settings.savePreferences(prefs);
                        SwingUtilities.updateComponentTreeUI(MainFrame.this);
                        item.setSelected(true);
                    } catch (ClassNotFoundException ex) {
                        LOGGER.error("Error setting native LAF", ex);
                    } catch (InstantiationException ex) {
                        LOGGER.error("Error setting native LAF", ex);
                    } catch (IllegalAccessException ex) {
                        LOGGER.error("Error setting native LAF", ex);
                    } catch (UnsupportedLookAndFeelException ex) {
                        LOGGER.error("Error setting native LAF", ex);
                    }
                }
            });
            lookAndFeelMenu.add(item);
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
                    MovieInfo info = getSelectedMovie();
                    movieInfoPanel.setMovieInfo(info);
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
        loadProgressBar = new javax.swing.JProgressBar();
        filterLabel = new javax.swing.JLabel();
        filterText = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        movieMenu = new javax.swing.JMenu();
        importMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        clearCacheMenuItem = new javax.swing.JMenuItem();
        extraMenu = new javax.swing.JMenu();
        lookAndFeelMenu = new javax.swing.JMenu();
        checkUpdatesMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

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

        loadProgressBar.setString("");
        loadProgressBar.setStringPainted(true);

        filterLabel.setText("Filter:");

        filterText.setFont(filterText.getFont().deriveFont(filterText.getFont().getSize()-2f));
        filterText.setToolTipText("Seperate filter words with a space (ex: action adventure thriller)");
        filterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextActionPerformed(evt);
            }
        });
        filterText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTextKeyReleased(evt);
            }
        });

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

        extraMenu.setText("Extra");

        lookAndFeelMenu.setText("Look and feel");
        extraMenu.add(lookAndFeelMenu);

        checkUpdatesMenuItem.setText("Check for Updates");
        checkUpdatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdatesMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(checkUpdatesMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(aboutMenuItem);

        jMenuBar1.add(extraMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 861, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterText, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel)
                    .addComponent(filterText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File newFolder = chooser.getSelectedFile();
            settings.addFolder(newFolder);
            this.selectedFile = newFolder;
            fillTable();
        } else {
            LOGGER.debug("No Selection ");
        }
}//GEN-LAST:event_importMenuItemActionPerformed

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
    final BusyDialog busyDialog = new BusyDialog(this, "test", true);
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

        @Override
        protected Void doInBackground() throws Exception {
            //Clear the image folder
            File imagesDir = settings.getImageCacheDir();
            if (imagesDir.exists()) {
                FileTools.deleteDirectory(imagesDir);
            }
            //clear the table values
            clearTableList();
            browser.getMovieCache().clear();

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                load();
            } catch (InterruptedException ex) {
                LOGGER.error("Delete worker execution interrupted", ex);
            } catch (ExecutionException ex) {
                LOGGER.error("Delete worker execution failed", ex.getCause());
            } finally {
                busyDialog.dispose();
            }
        }
    };
    worker.execute();
    busyDialog.setVisible(true);

}//GEN-LAST:event_clearCacheMenuItemActionPerformed

    private class BusyDialog extends JDialog {

        private JProgressBar progressB;

        public BusyDialog(JFrame relativeTo, String title, boolean modal) {
            super(relativeTo, title, modal);
            setModalityType(ModalityType.APPLICATION_MODAL);
            setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
            setResizable(false);
            setUndecorated(true);
            setLayout(new BorderLayout());
            progressB = new JProgressBar();
            progressB.setIndeterminate(true);
            add(progressB, BorderLayout.CENTER);
            pack();
            this.setLocationRelativeTo(relativeTo);
        }
    }

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

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
    AboutPanel aboutPanel = new AboutPanel(settings);
    JOptionPane.showMessageDialog(this, aboutPanel, "About", JOptionPane.PLAIN_MESSAGE);
}//GEN-LAST:event_aboutMenuItemActionPerformed

private void filterTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextKeyReleased
    final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(movieTable.getModel());
    movieTable.setRowSorter(sorter);

    final String text = filterText.getText().toLowerCase();
    if (text.length() == 0) {
        sorter.setRowFilter(null);
    } else {
        //sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        sorter.setRowFilter(new MovieTableRowFilter(text));
    }
}//GEN-LAST:event_filterTextKeyReleased

private void filterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTextActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_filterTextActionPerformed

private void checkUpdatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesMenuItemActionPerformed
    //TODO: Dupliacte code from AboutPanel
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
    //End of duplicate code from AboutPanel

    //Retrieve Version from web
    String latestVersionInfoURL = "http://movie-browser.googlecode.com/svn/site/latest";
    LOGGER.info("Checking latest version info from: " + latestVersionInfoURL);
    InputStream in = null;
    OutputStream out = new ByteArrayOutputStream();

    try {
        // Set up the streams
        LOGGER.info("Fetcing latest version info from: " + latestVersionInfoURL);
        URL url = new URL(latestVersionInfoURL);   // Create the URL
        URLConnection uc = url.openConnection();
        uc.setDefaultUseCaches(false);
        uc.setUseCaches(false);
        uc.setRequestProperty("Cache-Control", "max-age=0,no-cache");
        uc.setRequestProperty("Pragma", "no-cache");

        try {
            in = uc.getInputStream();
        } catch (FileNotFoundException ex) {
            LOGGER.error("Could not find file: " + latestVersionInfoURL, ex);
        }

        // Read bytes into string
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);

            if (read == -1) {
                break;
            }

            out.write(buffer, 0, read);
        }

        String latestVersion = out.toString();
        if (latestVersion.equals(version)) {
            JOptionPane.showMessageDialog(MainFrame.this, "You have the latest version of Movie Browser.", "Updates", JOptionPane.INFORMATION_MESSAGE);
        } else if (version.contains("SNAPSHOT") || version.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.this, "You have a development version of Movie Browser. The latest stable release available is " + latestVersion + ". \n The latest release can be downloaded from http://movie-browser.googlecode.com", "Updates", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(MainFrame.this, "The latest version available is " + latestVersion + "\n You are running the older version " + version + ". Please visit http://movie-browser.googlecode.com to get the latest version.", "Updates", JOptionPane.INFORMATION_MESSAGE);
        }
    } // On exceptions, print error message and usage message.
    catch (Exception ex) {
        LOGGER.error("Error fetching latest version info from: " + latestVersionInfoURL, ex);
    } finally {  // Always close the streams
        try {
            in.close();
            out.close();
        } catch (Exception e) {
        }
    }
}//GEN-LAST:event_checkUpdatesMenuItemActionPerformed

    private void showPopup(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            JTable source = (JTable) evt.getSource();
            int row = source.rowAtPoint(evt.getPoint());
            int column = source.columnAtPoint(evt.getPoint());
            source.changeSelection(row, column, false, false);

            JPopupMenu popup = new JPopupMenu();

            JMenu trailerMenu = new JMenu("Trailer");
            trailerMenu.setIcon(iconLoader.loadIcon("images/16/video-x-generic.png"));
            trailerMenu.add(new AppleTrailerAction());
            trailerMenu.add(new ImdbTrailerAction());
            popup.add(trailerMenu);

            JMenu watchMenu = new JMenu("Watch");
            watchMenu.setIcon(iconLoader.loadIcon("images/16/video-display.png"));
            watchMenu.add(new WatchMovieFileAction());
            watchMenu.add(new WatchSampleAction());
            popup.add(watchMenu);

            popup.add(new CrawlSubtitleAction());
            popup.add(new EditAction());
            LOGGER.info("Showing popup");
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void fillTable() {
        loadProgressBar.setString("Scanning folders...");
        loadProgressBar.setIndeterminate(true);
        final Set<String> folders = settings.loadFolders();
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
                    SwingTools.packColumns(movieTable, 3);
                    loadProgressBar.setString(model.getRowCount() + " movies found, loading info...");
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
                    LOGGER.error("Movie loader worker interrupted", ex);
                } catch (ExecutionException ex) {
                    LOGGER.error("Loading movies failed", ex);
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                } finally {
                    loadProgressBar.setIndeterminate(false);
                    loadProgressBar.setString("All movie info loaded.");
                }
            }
        }.execute();

    }

    private MovieInfo getSelectedMovie() {
        return (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
    }

    /**
     * This action lets you edit a record
     * @param evt
     */
    private class EditAction extends AbstractAction {

        public EditAction() {
            super("Edit", iconLoader.loadIcon("images/16/edit.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (browser.getMovieFinder().getRunningTasks() == 0) {
                EditMovieFrame editMovieFrame = new EditMovieFrame(getSelectedMovie(), browser.getImdbSearch(), browser.getMovieFinder());
                editMovieFrame.setLocationRelativeTo(movieTableScrollPane);
                editMovieFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Editing cannot be done while movie info is being loaded. \nPlease wait till all movie info is loaded and try again.", "Loading Info", JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + url, ex);
        }
    }

    private void openFile(File file) {
        LOGGER.info("Trying to open " + file.getAbsolutePath());
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            LOGGER.error("Failed launching default browser for " + file.getAbsolutePath(), ex);
        }
    }

    /**
     * This action  tries to show the trailer
     * @param evt
     */
    private class ImdbTrailerAction extends AbstractAction {

        public ImdbTrailerAction() {
            super("IMDB Trailer", iconLoader.loadIcon("images/16/imdb.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            TrailerFinder finder = new ImdbTrailerFinder();
            LOGGER.error("Not implemented");
            String url = finder.findTrailerUrl(info.getMovieFile().getMovie().getTitle(), info.siteFor(MovieService.IMDB).getIdForSite());
            if (url == null) {
                JOptionPane.showMessageDialog(MainFrame.this, "Could not find a trailer on www.imdb.com");
            } else {
                openUrl(url);
            }
        }
    }

    /**
     * This action tries to show the apple trailer site
     * @param evt
     */
    private class AppleTrailerAction extends AbstractAction {

        public AppleTrailerAction() {
            super("Apple Trailer", iconLoader.loadIcon("images/16/apple.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            TrailerFinder finder = new AppleTrailerFinder();
            LOGGER.error("Not implemented");
            String url = finder.findTrailerUrl(info.getMovieFile().getMovie().getTitle(), null);
            if (url == null) {
                JOptionPane.showMessageDialog(MainFrame.this, "Could not find a trailer on www.apple.com");
            } else {
                openUrl(url);
            }
        }
    }

    /**
     * This action opens the sample if a sample is found
     * @param evt
     */
    private class WatchSampleAction extends AbstractAction {

        public WatchSampleAction() {
            super("Sample", iconLoader.loadIcon("images/16/video-display.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            File sample = browser.getFileSystemScanner().findSample(info.getDirectory());
            if (sample != null) {
                openFile(sample);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "No sample found");
            }
        }
    }

    /**
     * This action opens the sample if a sample is found
     * @param evt
     */
    private class WatchMovieFileAction extends AbstractAction {

        public WatchMovieFileAction() {
            super("Video", iconLoader.loadIcon("images/16/video-display.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            File file = info.getDirectory();
            if (file.isDirectory()) {
                File[] movieFiles = file.listFiles(movieFileFilter);
                if (movieFiles.length > 0) {
                    for (File movieFile : movieFiles) {
                        openFile(movieFile);
                    }
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "No video found");
                }
            } else if (movieFileFilter.accept(file)) {
                openFile(file);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "No video found");
            }
        }
    }

    /**
     * This action opens the SubtitleCrawlerFrame if a video file is found in the directory. 
     */
    private class CrawlSubtitleAction extends AbstractAction {

        public CrawlSubtitleAction() {
            super("Subtitle Crawler", iconLoader.loadIcon("images/16/subtitles.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO get this out of here, this sould be somewhere in a logic class and not in the gui
            if (browser.getMovieFinder().getRunningTasks() == 0) {
                List<String> files = new ArrayList<String>();
                MovieInfo info = getSelectedMovie();
                File dir = info.getDirectory();
                String alternateSearchKey = getSelectedMovie().getMovieFile().getMovie().getTitle();
                File child;
                if (!dir.isFile()) {
                    for (File file : dir.listFiles()) {
                        if (file.isDirectory()) {
                            child = file;
                            for (File file2 : child.listFiles()) {
                                if (file2.isFile()) {
                                    if (!file2.getName().contains("sample")) {
                                        if (movieFileFilter.accept(file2)) {
                                            files.add(file2.getName());
                                        }
                                    }
                                }
                            }
                        } else {
                            if (file.isFile()) {
                                if (!file.getName().contains("sample")) {
                                    if (movieFileFilter.accept(file)) {
                                        files.add(file.getName());
                                    }
                                }
                            }
                        }
                    }
                }

                files.add(alternateSearchKey);
                openSubCrawler(files, info.getMovieFile().getMovie());
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Subtitle crawling cannot be done while movie info is being loaded. \nPlease try again after all movie info is loaded.", "Loading Info", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Loads SubtitleCrawlerFrame
     * @param fileName
     */
    private void openSubCrawler(List<String> file, StorableMovie movie) {
        SubtitleCrawlerFrame subtitleCrawler = new SubtitleCrawlerFrame(file, movie, browser.getSubtitlesLoader(), iconLoader);
        subtitleCrawler.setLocationRelativeTo(movieTableScrollPane);
        subtitleCrawler.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem checkUpdatesMenuItem;
    private javax.swing.JMenuItem clearCacheMenuItem;
    private javax.swing.JMenu extraMenu;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterText;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar loadProgressBar;
    private javax.swing.JMenu lookAndFeelMenu;
    private javax.swing.JMenu movieMenu;
    private javax.swing.JTable movieTable;
    private javax.swing.JScrollPane movieTableScrollPane;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
}
