package de.p2tools.mtplayer.controller.data.downloaderror;

public class DownloadErrorData {
    private String title;
    private String error;

    public DownloadErrorData(String title, String error) {
        this.title = title;
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
