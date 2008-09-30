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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.api.TrailerFinder;
import com.flicklib.domain.MovieService;
import com.flicklib.service.movie.apple.AppleTrailerFinder;
import com.flicklib.service.movie.imdb.ImdbTrailerFinder;
import com.google.inject.Inject;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.MovieFileFilter;
import eu.somatik.moviebrowser.tools.FileTools;
import eu.somatik.moviebrowser.tools.SwingTools;
import eu.somatik.moviebrowser.service.export.Exporter;
import eu.somatik.moviebrowser.service.export.ExporterLocator;

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
    private final ExporterLocator exporterLocator;

    /** 
     * Creates new form MainFrame
     * @param browser
     * @param imageCache
     * @param iconLoader
     * @param settings
     * @param infoHandler
     * @param exporterLocator 
     */
    @Inject
    public MainFrame(
            final MovieBrowser browser,
            final ImageCache imageCache,
            final IconLoader iconLoader,
            final Settings settings,
            final InfoHandler infoHandler,
            final ExporterLocator exporterLocator) {
        this.browser = browser;
        this.iconLoader = iconLoader;
        this.settings = settings;
        this.exporterLocator = exporterLocator;
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
                    loadProgressBar.setString("Ready");
                    loadProgressBar.setValue(loadProgressBar.getMaximum());
                    loadProgressBar.setToolTipText("All tasks finished");
                } else {
                    loadProgressBar.setIndeterminate(true);
                    loadProgressBar.setString(tasks + " task(s) remaining");
                    loadProgressBar.setToolTipText("The task indicator might fluctuate while data is loaded from imdb because other serverices are only activated after imdb is loaded!");
                }
            }
        });
        timer.start();
    }

    MovieFileFilter getMovieFileFilter() {
        return movieFileFilter;
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
                        settings.setLookAndFeelClassName(className);
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
    public void setupListeners() {
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
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    //MovieInfo info = (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
                    MovieInfo info = getSelectedMovie();
                    try {
                        Desktop.getDesktop().open(info.getDirectory());
                    } catch (IOException ex) {
                        LOGGER.error("Could not open dir " + info.getDirectory(), ex);
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
        jMenuItem1 = new javax.swing.JMenuItem();
        clearCacheMenuItem = new javax.swing.JMenuItem();
        rescanMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
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

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");
        toolsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolsMenuActionPerformed(evt);
            }
        });

        jMenuItem1.setText("Generate HTML Movie Catalog");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        toolsMenu.add(jMenuItem1);

        clearCacheMenuItem.setText("Clear Cache");
        clearCacheMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCacheMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(clearCacheMenuItem);

        rescanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        rescanMenuItem.setText("Rescan Folders");
        rescanMenuItem.setToolTipText("Rescan the file system for movies");
        rescanMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanFolders(evt);
            }
        });
        toolsMenu.add(rescanMenuItem);

        settingsMenuItem.setMnemonic('S');
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(settingsMenuItem);

        jMenuBar1.add(toolsMenu);

        extraMenu.setMnemonic('E');
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
                        .addComponent(filterText, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                        .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel)
                    .addComponent(filterText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
            scanFolders();
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
    int val = JOptionPane.showConfirmDialog(this, "Are you sure you wish to remove all cached movie data?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (val == JOptionPane.YES_OPTION) {
        clearCache();
    }
}//GEN-LAST:event_clearCacheMenuItemActionPerformed

    private void clearCache() {
        //final BusyDialog busyDialog = new BusyDialog(this, true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                //Clear the image folder
                File imagesDir = settings.getImageCacheDir();
                if (imagesDir.exists()) {
                    FileTools.deleteDirectory(imagesDir);
                }
                //clear the table values
                browser.getMovieCache().clear();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException ex) {
                    LOGGER.error("Delete worker execution interrupted", ex);
                } catch (ExecutionException ex) {
                    LOGGER.error("Delete worker execution failed", ex.getCause());
                }
                //finally {
                //    busyDialog.dispose();
                //}
                clearTableList();
                scanFolders();
            }
        };
        worker.execute();
        // This causes threading problems !
        // busyDialog.setVisible(true);
    }

    private class BusyDialog extends JDialog {

        private JProgressBar progressB;

        public BusyDialog(JFrame relativeTo, boolean modal) {
            super(relativeTo, "Working...", modal);
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
    showRightClickMenu(evt);
}//GEN-LAST:event_movieTableMouseReleased

private void movieTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieTableMousePressed
    // needed for linux gtk look and feel
    showRightClickMenu(evt);
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
    String latestVersion = settings.getLatestApplicationVersion();
    if (latestVersion == null) {
        JOptionPane.showMessageDialog(MainFrame.this, "Could not contact the update server!", "Update check failed", JOptionPane.WARNING_MESSAGE);
    } else {
        String version = settings.getApplicationVersion();
        if (latestVersion.equals(version)) {
            JOptionPane.showMessageDialog(MainFrame.this, "You have the latest version of Movie Browser.", "Updates", JOptionPane.INFORMATION_MESSAGE);
        } else if (version == null || version.contains("SNAPSHOT")) {
            String msg = "You have a development version of Movie Browser. The latest stable release available is " + latestVersion + ".\nOpening our website where you can download the new version...";
            browser.openUrl("http://code.google.com/p/movie-browser/downloads/list");
            JOptionPane.showMessageDialog(MainFrame.this, msg, "Updates", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String msg = "The latest version available is " + latestVersion + "\nYou are running the older version " + version + ".\nOpening our website where you can download the new version...";
            browser.openUrl("http://code.google.com/p/movie-browser/downloads/list");
            JOptionPane.showMessageDialog(MainFrame.this, msg, "Updates", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}//GEN-LAST:event_checkUpdatesMenuItemActionPerformed

private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
    SettingsFrame settingsFrame = new SettingsFrame(settings, this);
    settingsFrame.setLocationRelativeTo(movieTableScrollPane);
    settingsFrame.setVisible(true);
}//GEN-LAST:event_settingsMenuItemActionPerformed

private void scanFolders(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanFolders
    scanFolders();
}//GEN-LAST:event_scanFolders

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        String title = JOptionPane.showInputDialog(MainFrame.this, "Enter a title for this movie catalog (ex. John's Movie Library):", "Title", JOptionPane.QUESTION_MESSAGE);
        if(title.isEmpty()) {
            title = "My Movies";
        }
        JFileChooser chooser = new JFileChooser(selectedFile);
        chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            if(saveFile != null){

                // TODO what if the file exists
                // TODO what if the file is a folder

                // TODO show list to user to choose from or add items to menu dynamically
                // see exporterLocator.list()
                Exporter exporter = exporterLocator.get("html");

                try {
                    exporter.exportToFile(title, (MovieInfoTableModel) movieTable.getModel(), saveFile);
                } catch (IOException ex) {
                    LOGGER.error("Chould not export to "+saveFile, ex);
                    JOptionPane.showMessageDialog(this, "Error exporting to "+saveFile.getAbsolutePath(),"Error exporting", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            //FIXME we arrive here if the file exists
            LOGGER.debug("Location to create HTML catalog not selected.");
        }    
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void toolsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolsMenuActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_toolsMenuActionPerformed


    private void showRightClickMenu(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            JTable source = (JTable) evt.getSource();
            int row = source.rowAtPoint(evt.getPoint());
            int column = source.columnAtPoint(evt.getPoint());
            source.changeSelection(row, column, false, false);

            JPopupMenu popup = new JPopupMenu();

            JMenu trailerMenu = new JMenu("Trailer");
            trailerMenu.setIcon(iconLoader.loadIcon("images/16/video-x-generic.png"));
            trailerMenu.add(new AppleTrailerAction(this, browser));
            trailerMenu.add(new ImdbTrailerAction(this, browser));
            popup.add(trailerMenu);

            JMenu watchMenu = new JMenu("Watch");
            watchMenu.setIcon(iconLoader.loadIcon("images/16/video-display.png"));
            watchMenu.add(new WatchMovieFileAction(this, browser));
            watchMenu.add(new WatchSampleAction(this));
            popup.add(watchMenu);

            popup.add(new CrawlSubtitleAction());
            popup.add(new EditAction());
            popup.add(new RenameAction());
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    public void loadMoviesFromDatabase() {
    	new SwingWorker<List<StorableMovie>, Void>() { 
    		@Override
    		protected List<StorableMovie> doInBackground() throws Exception {
    			return browser.getMovieCache().list();
    		}
    		
    		
    		@Override
    		protected void done() {
    			List<StorableMovie> list;
				try {
					list = get();
					List<MovieInfo> infos = new ArrayList<MovieInfo>(list.size());
					
					MovieInfoTableModel model = (MovieInfoTableModel) movieTable.getModel();
					model.clear();
					for (StorableMovie s : list) {
						infos.add(new MovieInfo(s));
					}
					model.addAll(infos);
					SwingTools.packColumns(movieTable, 3);
				} catch (InterruptedException e) {
                    LOGGER.error("Loading interrupted", e);
                } catch (ExecutionException ex) {
                    LOGGER.error("Loading failed", ex.getCause());
				}
    		}
    	}.execute();
    }
    
    public void scanFolders() {
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
                    String message = ex.getMessage();
                    message += "\nTry tools - clear cache in the menu to fix this problem!";
                    JOptionPane.showMessageDialog(MainFrame.this, message, ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                } finally {
                    loadProgressBar.setIndeterminate(false);
                    loadProgressBar.setString("All movie info loaded.");
                }
            }
        }.execute();

    }

    protected MovieInfo getSelectedMovie() {
        int selected = movieTable.getRowSorter().convertRowIndexToModel(movieTable.getSelectedRow());
        return ((MovieInfoTableModel)movieTable.getModel()).getMovieInfo(selected);
        //return (MovieInfo) movieTable.getValueAt(movieTable.getSelectedRow(), movieTable.convertColumnIndexToView(MovieInfoTableModel.MOVIE_COL));
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
                String alternateSearchKey = info.getMovie().getTitle();
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
                openSubCrawler(files, info);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Subtitle crawling cannot be done while movie info is being loaded. \nPlease try again after all movie info is loaded.", "Loading Info", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Loads SubtitleCrawlerFrame
     * @param fileName
     */
    private void openSubCrawler(List<String> file, MovieInfo movie) {
        SubtitleCrawlerFrame subtitleCrawler = new SubtitleCrawlerFrame(file, movie, browser.getSubtitlesLoader(), iconLoader);
        subtitleCrawler.setLocationRelativeTo(movieTableScrollPane);
        subtitleCrawler.setVisible(true);
    }

    /**
     * This action renames a movie folder. 
     */
    private class RenameAction extends AbstractAction {

        public RenameAction() {
            super("Rename", iconLoader.loadIcon("images/16/film_edit.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MovieInfo info = getSelectedMovie();
            File oldFile = info.getDirectory();
            String newName = (String) JOptionPane.showInputDialog(MainFrame.this, "Enter the new title for " + oldFile.getName(), "Renaming " + oldFile.getName(), JOptionPane.PLAIN_MESSAGE, null, null, oldFile.getName());
            if (newName != null) {
            	boolean success = browser.getMovieFinder().renameFolder(info, newName);

                if (!success) {
                    LOGGER.error("Error renaming movie directory " + oldFile + " to " + newName);
                    JOptionPane.showMessageDialog(MainFrame.this, "Error renaming movie folder " + oldFile.getName() + ". You cannot have two movie folders with the same name and \\ / : * ? \" < > | characters are not allowed by the Operating System for folder naming.", "Error Renaming", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane dialog = new JOptionPane("Would you like Movie Browser to search and cache movie information to match the new movie name?\nSay No if the information in cache is correct for " + newName + ".", JOptionPane.QUESTION_MESSAGE);
                    Object[] options = new String[]{"Yes, please :)", "No thanks"};
                    dialog.setOptions(options);
                    JDialog dialogWindow = dialog.createDialog(new JFrame(), "Find Information");
                    dialogWindow.setVisible(true);
                    Object obj = dialog.getValue();
                    int result = -1;
                    for (int i = 0; i < options.length; i++) {
                        if (options[i].equals(obj)) {
                            result = i;
                        }
                    }
                    LOGGER.debug(String.valueOf(result));
                    if (result == 0) {
                        // unlink the file from the movie
                        //info.getMovieFile().setMovie(null);
                        //browser.getMovieCache().update(info.getMovieFile());
                        // request reload
                        browser.getMovieFinder().reloadMovie(info);
                    }
                }
            }
        }
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar loadProgressBar;
    private javax.swing.JMenu lookAndFeelMenu;
    private javax.swing.JMenu movieMenu;
    private javax.swing.JTable movieTable;
    private javax.swing.JScrollPane movieTableScrollPane;
    private javax.swing.JMenuItem rescanMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
}
