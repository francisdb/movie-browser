package eu.somatik.moviebrowser.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

class BusyDialog extends JDialog {

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
