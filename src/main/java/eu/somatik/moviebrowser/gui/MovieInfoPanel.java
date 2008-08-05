/*
 * MovieInfoPanel.java
 *
 * Created on August 1, 2008, 3:10 PM
 */
package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.service.MovieFinder;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private JButton imdbButton;
    private JButton tomatoesButton;
    private JButton moviewebButton;
    private JButton omdbButton;
    private JButton googleButton;
    private JButton flixterButton;
    private MovieInfo info;

    /** Creates new form MovieInfoPanel
     * @param imageCache 
     * @param iconLoader
     */
    public MovieInfoPanel(final ImageCache imageCache, final IconLoader iconLoader) {
        this.imageCache = imageCache;
        this.iconLoader = iconLoader;
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

    private void update() {
        Movie movie = info.getMovie();
        // TODO need better image cache, if loading takes a lot of time the
        // image might be shown after a new movie was selected
        if (info.getImage() == null) {
            imageCache.loadImg(info);
            if (info.getImage() == null) {
                new ImageLoadingWorker().execute();
            } else {
                updateImage(info);
            }
        } else {
            updateImage(info);
        }
        if (movie.getTitle() == null) {
            movieHeader.setTitle(info.getDirectory().getName());
        } else {
            movieHeader.setTitle(movie.getTitle());
        }
        movieHeader.setDescription(movie.getPlot());

        // TODO save imdb url and use links
        updateButton(imdbButton, MovieFinder.generateImdbUrl(movie));
        updateButton(tomatoesButton, movie.getTomatoUrl());
        updateButton(moviewebButton, movie.getMoviewebUrl());
        updateButton(omdbButton, movie.getOmdbUrl());
        updateButton(googleButton, movie.getGoogleUrl());
        updateButton(flixterButton, movie.getFlixterUrl());

        StringBuilder builder = new StringBuilder("<html>");

        builder.append("<h2>").append(movie.getTitle()).append("</h2><br/>");
        boolean first = true;
        builder.append("<strong>Genres</strong> ");
        for (Genre genre : movie.getGenres()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(genre);
        }
        builder.append("<br/>");
        builder.append("<strong>Languages</strong> ");
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
        builder.append("<strong>Runtime</strong> ").append(movie.getRuntime()).append(" min<br/>");
        builder.append("<strong>IMDB</strong> ").append(scoreString(movie.getImdbScore())).append(" ").append(movie.getVotes()).append("<br/>");
        builder.append("<strong>TOMATO</strong> ").append(scoreString(movie.getTomatoScore())).append("<br/>");
        builder.append("<strong>MovieWeb</strong> ").append(scoreString(movie.getMovieWebScore())).append("<br/>");
        builder.append("<strong>Google</strong> ").append(scoreString(movie.getGoogleScore())).append("<br/>");
        //builder.append("<strong>OMDB</strong> ").append(scoreString(movie.get)).append("%<br/>");
        builder.append("<strong>Flixter</strong> ").append(scoreString(movie.getFlixterScore())).append("<br/>");
        builder.append(movie.getPlot());
        builder.append("</html>");
        infoTextPane.setText(builder.toString());
        infoTextPane.setCaretPosition(0);
    }
    
    private String scoreString(Integer score){
        String result = "N/A";
        if(score != null){
            result = score.toString()+"%";
        }
        return result;
    }

    
    private void updateImage(MovieInfo info) {
        if (info.getImage() == null) {
            movieHeader.setIcon(null);
        } else {
            movieHeader.setIcon(new ImageIcon(info.getImage()));
        }
    }

    private void addIcons() {
        buttonPanel.setLayout(new FlowLayout());

        imdbButton = new JButton(iconLoader.loadIcon("images/16/imdb.png"));
        imdbButton.setEnabled(false);
        imdbButton.setToolTipText("Open on imdb website");
        imdbButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(imdbButton);
            }
        });
        buttonPanel.add(imdbButton);
        tomatoesButton = new JButton(iconLoader.loadIcon("images/16/rottentomatoes.png"));
        tomatoesButton.setToolTipText("Open on rottentomatoes website");
        tomatoesButton.setEnabled(false);
        tomatoesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(tomatoesButton);
            }
        });
        buttonPanel.add(tomatoesButton);
        moviewebButton = new JButton(iconLoader.loadIcon("images/16/movieweb.png"));
        moviewebButton.setToolTipText("Open on movieweb website");
        moviewebButton.setEnabled(false);
        moviewebButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(moviewebButton);
            }
        });
        buttonPanel.add(moviewebButton);
        omdbButton = new JButton(iconLoader.loadIcon("images/16/omdb.png"));
        omdbButton.setToolTipText("Open on omdb website");
        omdbButton.setEnabled(false);
        omdbButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(omdbButton);
            }
        });
        buttonPanel.add(omdbButton);
        googleButton = new JButton(iconLoader.loadIcon("images/16/google.png"));
        googleButton.setToolTipText("Open on google movie website");
        googleButton.setEnabled(false);
        googleButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(googleButton);
            }
        });
        buttonPanel.add(googleButton);
        flixterButton = new JButton(iconLoader.loadIcon("images/16/flixter.png"));
        flixterButton.setToolTipText("Open on flixter website");
        flixterButton.setEnabled(false);
        flixterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openLinkFor(flixterButton);
            }
        });
        buttonPanel.add(flixterButton);
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

    private class ImageLoadingWorker extends SwingWorker<Void, Void> {

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
                updateImage(info);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        buttonPanel = new javax.swing.JPanel();

        movieHeader.setDescription("");
        movieHeader.setTitle("");
        movieHeader.setToolTipText("Movie info");

        infoTextPane.setBackground(new java.awt.Color(254, 254, 254));
        infoTextPane.setEditable(false);
        jScrollPane2.setViewportView(infoTextPane);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(movieHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
            .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(movieHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXHeader movieHeader;
    // End of variables declaration//GEN-END:variables
}
