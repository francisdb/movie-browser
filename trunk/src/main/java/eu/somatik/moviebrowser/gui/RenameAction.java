package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action renames a movie folder.
 */
class RenameAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameAction.class);

    private final Component parent;
    private final MovieInfo info;
    private final MovieBrowser browser;

    public RenameAction(final MovieInfo info, final MovieBrowser browser, final Component parent) {
        super("Rename folder", browser.getIconLoader().loadIcon("images/16/film_edit.png"));
        this.info = info;
        this.browser = browser;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO CLEAN UP
        File oldFile = info.getDirectory();
        String newName = (String) JOptionPane.showInputDialog(parent, "Enter the new title for " + oldFile.getName(), "Renaming " + oldFile.getName(), JOptionPane.PLAIN_MESSAGE, null, null, oldFile.getName());
        if (newName != null) {
            boolean success = browser.getMovieFinder().renameFolder(info, newName);
            if (!success) {
                LOGGER.error("Error renaming movie directory " + oldFile + " to " + newName);
                JOptionPane.showMessageDialog(parent, "Error renaming movie folder " + oldFile.getName() + ". You cannot have two movie folders with the same name and \\ / : * ? \" < > | characters are not allowed by the Operating System for folder naming.", "Error Renaming", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane dialog = new JOptionPane("Would you like Movie Browser to search and cache movie information to match the new movie name?\nSay No if the information in cache is correct for " + newName + ".", JOptionPane.QUESTION_MESSAGE);
                Object[] options = new String[]{"Yes", "No"};
                dialog.setOptions(options);
                JDialog dialogWindow = dialog.createDialog(parent, "Find Information");
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
