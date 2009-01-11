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

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.ui.ContentProvider;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  francisdb
 */
public class MovieInfoPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieInfoPanel.class);
    private final IconLoader iconLoader;
    private final ImageCache imageCache;
    private final InfoHandler infoHandler;
    private final List<MovieService> services;
    private final Map<MovieService, JButton> siteButtons;
    private final MovieFileTreeTableModel fileTree;
    private ContentProvider provider;
    private MovieInfo info;
    private ResourceBundle bundle;


    /** Creates new form MovieInfoPanel
     * @param imageCache 
     * @param iconLoader
     * @param infoHandler 
     */
    public MovieInfoPanel(
            final ImageCache imageCache,
            final IconLoader iconLoader,
            final InfoHandler infoHandler,
            final ContentProvider provider,
            final Settings settings) {
        this.imageCache = imageCache;
        this.iconLoader = iconLoader;
        this.infoHandler = infoHandler;
        // TODO get the services from the settings
        this.services = settings.getEnabledServices();
        this.siteButtons = new HashMap<MovieService, JButton>();
        this.fileTree = new MovieFileTreeTableModel();
        this.provider = provider;
        this.bundle = ResourceBundle.getBundle("eu/somatik/moviebrowser/gui/Bundle"); // NOI18N

        initComponents();
        addIcons();
        infoTextPane.setContentType("text/html");
    }

    /**
     * Set the data the Panel should show
     * @param movieInfo
     */
    public void setMovieInfo(final MovieInfo movieInfo) {
        this.info = movieInfo;
        update();
    }

    private void updateButton(final JButton button, final String url) {
        if (url != null && url.trim().length() > 0) {
            button.setActionCommand(url);
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }
    
    public void setContentProvider(ContentProvider provider) {
        this.provider = provider;
        update();
    }

    private void update() {


        if (info == null || info.getMovie() == null) {
            movieHeader.setTitle("");
            movieHeader.setDescription("");
            infoTextPane.setText("");
            for (MovieService service : services) {
                updateButton(siteButtons.get(service), null);
            }
            fileTree.setMovie(null);
            updateImage(null);
        } else {
            StorableMovie movie = info.getMovie();
            // TODO if loading takes a lot of time the, image might be shown after a new movie was selected
            Image image = imageCache.loadImg(info);
            if (image == null) {
                new ImageLoadingWorker().execute();
            } else {
                updateImage(image);
            }
            fileTree.setMovie(movie);
            // to expand ratings...
            movieFileTreeTable.expandRow(1);
            String title = provider.getTitle(info);
            if (title == null) {
                movieHeader.setTitle(info.getDirectory().getName());
            } else {
                movieHeader.setTitle(title);
            }
            String plot = provider.getPlot(info);
            if (plot!=null) {
                movieHeader.setDescription(plot.substring(0, Math.min(plot.length(), 256)));
            } else {
                movieHeader.setDescription("");
            }
            
            for (MovieService service : services) {
                updateButton(siteButtons.get(service), infoHandler.url(info, service));
            }

            StringBuilder builder = new StringBuilder("<html>");

            builder.append("<h2>").append(title).append("</h2>");
            boolean first = true;
            String type = movie.getType() == null ? "" : movie.getType().getName();
            builder.append("<strong>"+bundle.getString("MovieInfoPanel.panel.type")+"</strong> ").append(type).append("<br/>");
            builder.append("<strong>"+bundle.getString("MovieInfoPanel.panel.director")+"</strong> ").append(movie.getDirector()).append("<br/>");
            builder.append("<strong>"+bundle.getString("MovieInfoPanel.panel.genres")+"</strong> ");
            for (Genre genre : movie.getGenres()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(genre);
            }
            builder.append("<br/>");
            builder.append("<strong>"+bundle.getString("MovieInfoPanel.panel.languages")+"</strong> ");
            first = true;
            for (Language language : movie.getLanguages()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(language);
            }
            builder.append("<br/>");
            if (movie.getRuntime()!=null) {
                builder.append("<strong>"+bundle.getString("MovieInfoPanel.panel.runtime")+"</strong> ").append(movie.getRuntime()).append(" min<br/>");
                builder.append("<br/>");
            }
            
            for (MovieService s : services) {
                builder.append("<strong>").append(s.getName()).append("</strong> ").append(info(info, s)).append("<br/>");
            }
            builder.append("<br/>");
            builder.append(plot);
            builder.append("</html>");
            infoTextPane.setText(builder.toString());
        }

        infoTextPane.setCaretPosition(0);
    }

    private String scoreString(Integer score) {
        String result = "N/A";
        if (score != null) {
            result = score.toString() + "%";
        }
        return result;
    }

    private CharSequence info(MovieInfo info, MovieService service) {
        StringBuilder builder = new StringBuilder();
        builder.append(scoreString(infoHandler.score(info, service)));
        Integer votes = infoHandler.votes(info, service);
        if (votes != null) {
            builder.append(' ');
            builder.append(MessageFormat.format(
                    bundle.getString("MovieInfoPanel.panel.votes"), votes));
        }
        return builder;
    }

    private void updateImage(Image image) {
        if (image == null) {
            movieHeader.setIcon(null);
        } else {
            movieHeader.setIcon(new ImageIcon(image));
        }
    }

    private JButton createSiteButton(MovieService service) {
        final JButton serviceButton = new JButton(iconLoader.iconFor(service));
        serviceButton.setEnabled(false);
        //serviceButton.setToolTipText("Open " + service.getName() + " page for this movie");
        serviceButton.setToolTipText(
                MessageFormat.format(
                    bundle.getString("MovieInfoPanel.serviceButton.tooltip"), 
                    service.getName()));
        serviceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(serviceButton);
            }
        });
        return serviceButton;
    }

    private void addIcons() {
        buttonPanel.setLayout(new FlowLayout());
        JButton button;
        for (MovieService service : services) {
            button = createSiteButton(service);
            siteButtons.put(service, button);
            buttonPanel.add(button);
        }
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
        } else {
            JOptionPane.showMessageDialog(this, "No movie selected or no link found", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class ImageLoadingWorker extends SwingWorker<Image, Void> {

        @Override
        protected Image doInBackground() throws Exception {
            imageCache.saveImgToCache(info);
            Image image = imageCache.loadImg(info);
            return image;
        }

        @Override
        protected void done() {
            try {
                updateImage(get());
            } catch (InterruptedException ex) {
                LOGGER.error("Worker interrupted", ex);
            } catch (ExecutionException ex) {
                LOGGER.error("Worker failed", ex.getCause());
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

        movieHeader = new org.jdesktop.swingx.JXHeader();
        buttonPanel = new javax.swing.JPanel();
        infoTabbedPane = new javax.swing.JTabbedPane();
        infoScrollPane = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        movieFileScrollPane = new javax.swing.JScrollPane();
        movieFileTreeTable = new org.jdesktop.swingx.JXTreeTable(fileTree);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("eu/somatik/moviebrowser/gui/Bundle"); // NOI18N
        movieHeader.setDescription(bundle.getString("MovieInfoPanel.movieHeader.description")); // NOI18N
        movieHeader.setTitle(bundle.getString("MovieInfoPanel.movieHeader.title")); // NOI18N
        movieHeader.setToolTipText(bundle.getString("MovieInfoPanel.movieHeader.toolTipText")); // NOI18N

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        infoTextPane.setEditable(false);
        infoTextPane.setBackground(new java.awt.Color(254, 254, 254));
        infoScrollPane.setViewportView(infoTextPane);

        infoTabbedPane.addTab(bundle.getString("MovieInfoPanel.infoScrollPane.TabConstraints.tabTitle"), infoScrollPane); // NOI18N

        movieFileTreeTable.setRootVisible(true);
        movieFileTreeTable.setShowHorizontalLines(true);
        movieFileScrollPane.setViewportView(movieFileTreeTable);

        infoTabbedPane.addTab(bundle.getString("MovieInfoPanel.movieFileScrollPane.TabConstraints.tabTitle"), movieFileScrollPane); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(movieHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
            .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(infoTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(movieHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JTabbedPane infoTabbedPane;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JScrollPane movieFileScrollPane;
    private org.jdesktop.swingx.JXTreeTable movieFileTreeTable;
    private org.jdesktop.swingx.JXHeader movieHeader;
    // End of variables declaration//GEN-END:variables
}
