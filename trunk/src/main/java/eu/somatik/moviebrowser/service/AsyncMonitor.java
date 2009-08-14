package eu.somatik.moviebrowser.service;

public interface AsyncMonitor {

    public void start();
    
    public void step(String text);
    
    public void finish();
    
    public boolean isCanceled();
    
}
