/*
 * MovieInfoPanel.java
 *
 * Created on August 1, 2008, 3:10 PM
 */
package eu.somatik.moviebrowser.gui;

import com.flicklib.domain.MovieService;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.Genre;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.service.InfoHandler;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
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
    private final InfoHandler infoHandler;
    /**
     * Make generic version for all buttons
     * @deprecated
     */
    @Deprecated
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
     * @param infoHandler 
     */
    public MovieInfoPanel(final ImageCache imageCache, final IconLoader iconLoader, final InfoHandler infoHandler) {
        this.imageCache = imageCache;
        this.iconLoader = iconLoader;
        this.infoHandler = infoHandler;
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

        // TODO if loading takes a lot of time the, image might be shown after a new movie was selected
        Image image = imageCache.loadImg(info);
        if (image == null) {
            new ImageLoadingWorker().execute();
        } else {
            updateImage(image);
        }


        StorableMovie movie = info.getMovieFile().getMovie();
        if (movie == null) {
            movieHeader.setTitle("");
            movieHeader.setDescription("");
            infoTextPane.setText("");
            updateButton(imdbButton, null);
            updateButton(tomatoesButton, null);
            updateButton(moviewebButton, null);
            updateButton(omdbButton, null);
            updateButton(googleButton, null);
            updateButton(flixterButton, null);
        } else {
            if (movie.getTitle() == null) {
                movieHeader.setTitle(info.getDirectory().getName());
            } else {
                movieHeader.setTitle(movie.getTitle());
            }
            movieHeader.setDescription(movie.getPlot());

            // TODO make generified button bar
            updateButton(imdbButton, infoHandler.url(info, MovieService.IMDB));
            updateButton(tomatoesButton, infoHandler.url(info, MovieService.TOMATOES));
            updateButton(moviewebButton, infoHandler.url(info, MovieService.MOVIEWEB));
            updateButton(omdbButton, infoHandler.url(info, MovieService.OMDB));
            updateButton(googleButton, infoHandler.url(info, MovieService.GOOGLE));
            updateButton(flixterButton, infoHandler.url(info, MovieService.FLIXSTER));

            StringBuilder builder = new StringBuilder("<html>");

            builder.append("<h2>").append(movie.getTitle()).append("</h2>");
            boolean first = true;
            builder.append("<strong>Director</strong> ").append(movie.getDirector()).append("<br/>");
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
            builder.append("<br/>");
            builder.append("<strong>IMDB</strong> ").append(info(info, MovieService.IMDB)).append("<br/>");
            builder.append("<strong>Tomato</strong> ").append(info(info, MovieService.TOMATOES)).append("<br/>");
            builder.append("<strong>MovieWeb</strong> ").append(info(info, MovieService.MOVIEWEB)).append("<br/>");
            builder.append("<strong>Goolge</strong> ").append(info(info, MovieService.GOOGLE)).append("<br/>");
            //builder.append("<strong>OMDB</strong> ").append(info(info, MovieService.OMDB)).append("<br/>");
            builder.append("<strong>Flixter</strong> ").append(info(info, MovieService.FLIXSTER)).append("<br/>");
            builder.append("<br/>");
            builder.append(movie.getPlot());
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
            builder.append(" ");
            builder.append(votes);
            builder.append(" votes");
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
