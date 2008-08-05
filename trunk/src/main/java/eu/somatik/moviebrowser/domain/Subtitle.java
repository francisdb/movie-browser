package eu.somatik.moviebrowser.domain;

/**
 *
 * @author francisdb
 */
public class Subtitle {

    private String fileName;
    private String language;
    private String noCd;
    private String type;
    private String subSource;

    public Subtitle() {
        fileName = "";
        language = "";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNoCd() {
        return noCd;
    }

    public void setNoCd(String noCd) {
        this.noCd = noCd;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
