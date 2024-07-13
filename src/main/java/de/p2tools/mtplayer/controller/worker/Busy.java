package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.ProgIcons;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Busy {

    public enum BUSY_SRC {
        GUI, ABO_DIALOG, PANE_BLACKLIST
    }

    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final BooleanProperty stopProp = new SimpleBooleanProperty(false);
    private final BooleanProperty stopBtn = new SimpleBooleanProperty(true);
    private final StringProperty text = new SimpleStringProperty();
    private BUSY_SRC enumBusy = null;

    public Busy() {
    }

    public void busyOn(BUSY_SRC b, String text, double value, boolean stopBtn) {
        enumBusy = b;
        busy.set(true);
        textProperty().set(text);
        progress.set(value);
        stopProp.set(false);
        this.stopBtn.set(stopBtn);
    }

    public void busyOnFx(BUSY_SRC b, String text, double value, boolean stopBtn) {
        Platform.runLater(() -> {
            enumBusy = b;
            busy.set(true);
            textProperty().set(text);
            progress.set(value);
            stopProp.set(false);
            this.stopBtn.set(stopBtn);
        });
    }

    public void busyOffFx() {
        Platform.runLater(() -> {
            busy.set(false);
            textProperty().set("");
            progress.set(0.0);
            stopProp.set(false);
        });
    }

    public HBox getBusyHbox(BUSY_SRC busySrc) {
        final HBox busyHBox = new HBox(10);
        final Label busyLbl = new Label("");
        final ProgressBar busyProgressBar = new ProgressBar();
        final Button busyBtnStop = new Button();

        // busy
        busyBtnStop.setGraphic(ProgIcons.ICON_BUTTON_WORKER_STOP.getImageView());
        busyBtnStop.getStyleClass().add("buttonVeryLow");
        busyBtnStop.setOnAction(a -> stopPropProperty().set(true));
        busyBtnStop.visibleProperty().bind(stopBtn);
        busyBtnStop.managedProperty().bind(stopBtn);

        busyProgressBar.progressProperty().bind(progressProperty());
        busyProgressBar.setMaxWidth(Double.MAX_VALUE);
        busyLbl.textProperty().bind(text);
        busyHBox.setVisible(false);
        busyHBox.setManaged(false);

        busyProperty().addListener((u, o, n) -> {
            if (!n || enumBusy.equals(busySrc)) {
                // Einschalten nur wenns stimmt
                busyHBox.setVisible(n);
                busyHBox.setManaged(n);
            }
        });

        busyHBox.getChildren().addAll(busyLbl, busyProgressBar, busyBtnStop);
        HBox.setHgrow(busyProgressBar, Priority.ALWAYS);

        return busyHBox;
    }

    public boolean isBusy() {
        return busy.get();
    }

    public BooleanProperty busyProperty() {
        return busy;
    }

    public void setProgress(double p) {
        Platform.runLater(() -> {
            this.progress.set(p);
        });
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setStopProp(boolean s) {
        Platform.runLater(() -> {
            this.stopProp.set(s);
        });
    }

    public boolean isStopProp() {
        return stopProp.get();
    }

    public BooleanProperty stopPropProperty() {
        return stopProp;
    }

    public void setStopBtn(boolean s) {
        Platform.runLater(() -> {
            stopBtn.set(s);
        });
    }

    public boolean isStopBtn() {
        return stopBtn.get();
    }

    public BooleanProperty stopBtnProperty() {
        return stopBtn;
    }

    public void setText(String t) {
        Platform.runLater(() -> {
            this.text.set(t);
        });
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }
}
