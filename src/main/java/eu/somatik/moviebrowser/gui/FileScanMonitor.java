package eu.somatik.moviebrowser.gui;

import java.awt.Component;

import javax.swing.ProgressMonitor;

import eu.somatik.moviebrowser.service.AsyncMonitor;

/**
 * This class encapsulates a dialog with a progress bar, which can be cancelled, for a long running processes.
 * 
 * @author zsombor
 *
 */
class FileScanMonitor implements AsyncMonitor {
    
    private final static int STEP = 10;

    private int position;
    int max;
    ProgressMonitor monitor;

    FileScanMonitor(Component parentComponent, String startMessage) {
        this.position = 0;
        this.max = STEP;
        this.monitor = new ProgressMonitor(parentComponent, startMessage, "", 0, max);
    }

    @Override
    public void finish() {
        monitor.setProgress(monitor.getMaximum());
    }

    @Override
    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    @Override
    public void start() {
        monitor.setProgress(0);
        monitor.setNote("start.");
    }

    @Override
    public void step(String text) {
        position ++;
        if (max<=position) {
            max += STEP;
            monitor.setMaximum(max);
        }
        monitor.setNote(text);
        monitor.setProgress(position);
    }

}
