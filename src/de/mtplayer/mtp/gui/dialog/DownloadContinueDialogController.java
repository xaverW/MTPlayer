/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.FileNameUtils;
import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.starter.DownloadState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class DownloadContinueDialogController extends MTDialog {

    @FXML
    private HBox hboxTitle;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnRestartDownload;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnContinueDownload;

    @FXML
    private Label lblFilmTitle;
    @FXML
    private TextField txtFileName;
    @FXML
    private ComboBox<String> cbPath;
    @FXML
    private Label lblSizeFree;

    @FXML
    private Button btnPath;
    @FXML
    private GridPane gridPane;

    private final Daten daten;
    private final Download download;
    private DownloadState.ContinueDownload result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
    private final boolean direkterDownload;
    private String oldPathFile;

    private Timeline timeline = null;
    private Integer timeSeconds = Config.SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND.getInt();

    public DownloadContinueDialogController(Daten daten, Download download, boolean direkterDownload) {
        super("/de/mtplayer/mtp/gui/dialog/DownloadContinueDialog.fxml",
                Config.DOWNLOAD_DIALOG_CONTINUE_SIZE,
                "Download weiterführen", true);

        this.daten = daten;
        this.download = download;
        this.direkterDownload = direkterDownload;
        this.oldPathFile = download.getZielPfadDatei();

        init(true);

    }

    @Override
    public void make() {
        gridPane.getStyleClass().add("dialog-border");

        hboxTitle.getStyleClass().add("dialog-title-border");

        lblHeader.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitel());

        btnContinueDownload.setVisible(direkterDownload);
        btnContinueDownload.setManaged(direkterDownload);

        if (!direkterDownload && !checkAufrufBauen()) {
            // nur für Downloads mit Programm
            txtFileName.setDisable(true);
            cbPath.setDisable(true);
        }

        initButton();
        initPathAndName();

        //start the countdown...
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new CountdownAction()));
        timeline.playFromStart();

    }

    private boolean checkAufrufBauen() {
        return (download.getpSet() != null && download.getFilm() != null);
    }

    private class CountdownAction implements EventHandler {

        @Override
        public void handle(Event event) {
            timeSeconds--;
            if (timeSeconds > 0) {
                if (!direkterDownload) {
                    btnRestartDownload.setText("neu Starten in " + timeSeconds + " s");
                } else {
                    btnContinueDownload.setText("Weiterführen in " + timeSeconds + " s");
                }
            } else {
                timeline.stop();
                result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
                beenden();
            }
        }
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }

        setButtonText();
    }

    public DownloadState.ContinueDownload getResult() {
        return result;
    }


    private void beenden() {
        close();
    }


    private void setButtonText() {
        if (!direkterDownload) {
            btnRestartDownload.setText("neu Starten");
        } else {
            btnContinueDownload.setText("Weiterführen");
        }
    }

    private void initButton() {
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setText("");
        btnPath.setOnAction(event -> getDestination());

        btnCancel.setOnAction(event -> {
            result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
            beenden();
        });

        btnRestartDownload.setOnAction(event -> {
            result = DownloadState.ContinueDownload.RESTART_DOWNLOAD;
            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txtFileName.getText());
            beenden();
        });
        setButtonText();
        btnContinueDownload.setOnAction(event -> {
            result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
            beenden();
        });
    }

    public boolean isNewName() {
        switch (Functions.getOs()) {
            case LINUX:
                return !oldPathFile.equals(download.getZielPfadDatei());
            case WIN32:
            case WIN64:
            default:
                return !oldPathFile.equalsIgnoreCase(download.getZielPfadDatei());
        }
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        final String[] p = Config.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
        cbPath.getItems().addAll(p);

        if (download.getZielPfad().isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
        } else {
            cbPath.getSelectionModel().select(download.getZielPfad());
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();

            final String s = cbPath.getSelectionModel().getSelectedItem();
            DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        });

        txtFileName.setText(download.getZielDateiname());
        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();

            if (!txtFileName.getText().equals(FileNameUtils.checkDateiname(txtFileName.getText(), false /* pfad */))) {
                txtFileName.setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtFileName.setStyle("");
            }
        });
    }

    private void getDestination() {
        DirFileChooser.DirChooser(Daten.getInstance().primaryStage, cbPath);
    }

}
