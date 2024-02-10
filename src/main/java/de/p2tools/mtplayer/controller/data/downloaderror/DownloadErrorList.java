package de.p2tools.mtplayer.controller.data.downloaderror;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class DownloadErrorList extends SimpleListProperty<DownloadErrorData> {
    public DownloadErrorList() {
        super(FXCollections.observableArrayList());
    }
}
