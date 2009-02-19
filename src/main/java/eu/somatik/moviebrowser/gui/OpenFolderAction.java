package eu.somatik.moviebrowser.gui;

import eu.somatik.moviebrowser.MovieBrowser;
import eu.somatik.moviebrowser.domain.MovieInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;

class OpenFolderAction extends AbstractAction {

    private final MovieBrowser browser;
    private final MovieInfo info;

    public OpenFolderAction(final MovieInfo info, final MovieBrowser browser) {
        super("Open folder", UIManager.getIcon("FileView.directoryIcon"));
        // see here for more icons
        this.browser = browser;
        this.info = info;
        this.setEnabled(info.getDirectory() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        browser.openFile(info.getDirectory());
    }
}
