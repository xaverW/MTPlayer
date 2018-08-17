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
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.starter.DownloadState;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DownloadContinueDialogController extends PDialogExtra {

    private final VBox vBoxCont;
    private final HBox hBoxOk;

    private HBox hboxTitle = new HBox();
    private Label lblHeader = new Label("Die Filmdatei existiert bereits.");
    private Button btnRestartDownload = new Button("neu Starten");
    private Button btnCancel = new Button("Abbrechen");
    private Button btnContinueDownload = new Button("Weiterführen in XXX");

    private Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private TextField txtFileName = new TextField("");
    private ComboBox<String> cbPath = new ComboBox<>();
    private Label lblSizeFree = new Label("");

    private Button btnPath = new Button("Button");
    private GridPane gridPane = new GridPane();

    private final ProgData progData;
    private final Download download;
    private DownloadState.ContinueDownload result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
    private final boolean directDownload;
    private String oldPathFile;

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND.getInt();

    public DownloadContinueDialogController(ProgData progData, Download download, boolean directDownload) {
        super(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE.getStringProperty(),
                "Download weiterführen", true);

        this.progData = progData;
        this.download = download;
        this.directDownload = directDownload;
        this.oldPathFile = download.getDestPathFile();

        vBoxCont = getVboxCont();
        hBoxOk = getHboxOk();

        init(getvBoxDialog(), true);
    }

    @Override
    public void make() {
        initCont();
        hboxTitle.getStyleClass().add("dialog-title-border");

        lblHeader.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        btnContinueDownload.setVisible(directDownload);
        btnContinueDownload.setManaged(directDownload);

        if (!directDownload && !checkDownload()) {
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

    private void initCont() {
        hboxTitle.setSpacing(10);
        hboxTitle.setAlignment(Pos.CENTER);
        hboxTitle.setPadding(new Insets(10));
        hboxTitle.getChildren().add(lblHeader);

        // Gridpane
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        gridPane.add(new Label("Film:"), 0, row);
        gridPane.add(lblFilmTitle, 1, row, 2, 1);

        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtFileName, 1, row, 2, 1);

        cbPath.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        gridPane.add(cbPath, 1, row);
        gridPane.add(btnPath, 2, row);

        GridPane.setHalignment(lblSizeFree, HPos.RIGHT);
        gridPane.add(lblSizeFree, 1, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBoxCont.setPadding(new Insets(5));
        vBoxCont.setSpacing(20);
        vBoxCont.getChildren().addAll(hboxTitle, gridPane);


        hBoxOk.setAlignment(Pos.BOTTOM_RIGHT);
        hBoxOk.setSpacing(10);
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(new Label("Wie möchten Sie forfahren?"));
        hBoxOk.getChildren().addAll(hBox, btnRestartDownload, btnContinueDownload, btnCancel);
    }

    private boolean checkDownload() {
        return (download.getpSet() != null && download.getFilm() != null);
    }

    private class CountdownAction implements EventHandler {

        @Override
        public void handle(Event event) {
            timeSeconds--;
            if (timeSeconds > 0) {
                if (!directDownload) {
                    btnRestartDownload.setText("neu Starten in " + timeSeconds + " s");
                } else {
                    btnContinueDownload.setText("Weiterführen in " + timeSeconds + " s");
                }
            } else {
                timeline.stop();
                result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
                quit();
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


    private void quit() {
        close();
    }


    private void setButtonText() {
        if (!directDownload) {
            btnRestartDownload.setText("neu Starten");
        } else {
            btnContinueDownload.setText("Weiterführen");
        }
    }

    private void initButton() {
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setText("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnPath.setOnAction(event -> getDestination());

        btnCancel.setOnAction(event -> {
            result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
            quit();
        });

        setButtonText();
        btnRestartDownload.setOnAction(event -> {
            result = DownloadState.ContinueDownload.RESTART_DOWNLOAD;
            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txtFileName.getText());
            quit();
        });
        btnContinueDownload.setOnAction(event -> {
            result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
            quit();
        });
    }

    public boolean isNewName() {
        switch (Functions.getOs()) {
            case LINUX:
                return !oldPathFile.equals(download.getDestPathFile());
            case WIN32:
            case WIN64:
            default:
                return !oldPathFile.equalsIgnoreCase(download.getDestPathFile());
        }
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        final String[] p = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
        cbPath.getItems().addAll(p);

        if (download.getDestPath().isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
        } else {
            cbPath.getSelectionModel().select(download.getDestPath());
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();
            if (newValue != null) {
                btnContinueDownload.setDisable(!download.getDestPath().equals(newValue));
            }

            DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        });
        DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);

        txtFileName.setText(download.getDestFileName());
        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();
            if (newValue != null) {
                btnContinueDownload.setDisable(!download.getDestFileName().equals(newValue));
            }

            if (!txtFileName.getText().equals(FileNameUtils.checkFileName(txtFileName.getText(), false /* pfad */))) {
                txtFileName.setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtFileName.setStyle("");
            }
        });
    }

    private void getDestination() {
        DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }

}
