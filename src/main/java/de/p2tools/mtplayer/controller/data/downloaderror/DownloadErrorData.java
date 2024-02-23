package de.p2tools.mtplayer.controller.data.downloaderror;

public class DownloadErrorData {
    private String title;
    private String error;
    private String url;
    private String file;

    public DownloadErrorData(String title, String url, String file, String error) {
        this.title = title;
        this.url = url;
        this.file = file;
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
