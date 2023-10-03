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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DownloadContinueDialogController extends PDialogExtra {

    private final Label lblHeader = new Label("Die Filmdatei existiert bereits");
    private final Button btnRestartDownload = new Button("neu _Starten");
    private final Button btnCancel = new Button("_Abbrechen");
    private final Button btnContinueDownload = new Button("_Weiterführen");
    private final CheckBox chkAlways = new CheckBox("Nicht mehr fragen");

    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final TextField txtFileName = new TextField("");
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Label lblSizeFree = new Label("");

    private final Button btnPath = new Button("");
    private final GridPane gridPane = new GridPane();

    private final DownloadData download;
    private ProgConfigAskBeforeDelete.ContinueDownload result = ProgConfigAskBeforeDelete.ContinueDownload.CANCEL;
    private final boolean httpDownload;
    private final String oldPathFile;

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue();

    public DownloadContinueDialogController(StringProperty conf, ProgData progData,
                                            DownloadData download, boolean httpDownload) {
        super(progData.primaryStage, conf, "Download weiterführen",
                true, false, DECO.BORDER_SMALL);

        this.download = download;
        this.httpDownload = httpDownload;
        this.oldPathFile = download.getDestPathFile();

        if (ProgData.autoMode) {
            // dann schaut ja eh keiner zu, also restart des Downloads
            result = ProgConfigAskBeforeDelete.ContinueDownload.RESTART;
            return;
        } else {
            init(true);
        }
    }

    public ProgConfigAskBeforeDelete.ContinueDownload getResult() {
        return result;
    }

    public boolean isNewName() {
        switch (ProgramToolsFactory.getOs()) {
            case LINUX:
                return !oldPathFile.equals(download.getDestPathFile());
            case WIN32:
            case WIN64:
            default:
                return !oldPathFile.equalsIgnoreCase(download.getDestPathFile());
        }
    }

    @Override
    public void make() {
        initCont();

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        btnContinueDownload.setVisible(httpDownload);
        btnContinueDownload.setManaged(httpDownload);

        if (!httpDownload && !checkDownload()) {
            // nur für Downloads mit Programm
            txtFileName.setDisable(true);
            cbPath.setDisable(true);
        }

        initButton();
        initPathAndName();
        handleCountDownAction(); // damit die Dialoggröße stimmt

        //start the countdown...
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> handleCountDownAction()));
        timeline.playFromStart();
    }

    private void initCont() {
        getHBoxTitle().getChildren().add(lblHeader);

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

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        getVBoxCont().setPadding(new Insets(5));
        getVBoxCont().setSpacing(20);
        getVBoxCont().getChildren().addAll(gridPane);

//        getHBoxOverButtons().getChildren().addAll(chkSave);
//        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);

        addCancelButton(btnCancel);
        addOkButton(btnRestartDownload);
        addOkButton(btnContinueDownload);

        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkAlways);
    }

    private void initButton() {
        btnPath.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen"));
        btnPath.setOnAction(event -> getDestination());

        btnCancel.setOnAction(event -> {
            result = ProgConfigAskBeforeDelete.ContinueDownload.CANCEL;
            quit();
        });

        btnRestartDownload.setOnAction(event -> {
            if (chkAlways.isSelected()) {
                ProgConfig.DOWNLOAD_CONTINUE.setValue(ProgConfigAskBeforeDelete.DOWNLOAD_RESTART__RESTART);
            }

            result = ProgConfigAskBeforeDelete.ContinueDownload.RESTART;
            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txtFileName.getText());
            quit();
        });
        btnContinueDownload.setOnAction(event -> {
            if (chkAlways.isSelected()) {
                ProgConfig.DOWNLOAD_CONTINUE.setValue(ProgConfigAskBeforeDelete.DOWNLOAD_RESTART__CONTINUE);
            }

            result = ProgConfigAskBeforeDelete.ContinueDownload.CONTINUE;
            quit();
        });
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        cbPath.getItems().addAll(ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH);

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

            DownloadFactory.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        });
        DownloadFactory.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);

        txtFileName.setText(download.getDestFileName());
        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();
            if (newValue != null) {
                btnContinueDownload.setDisable(!download.getDestFileName().equals(newValue));
            }

            if (!txtFileName.getText().equals(FileNameUtils.checkFileName(txtFileName.getText(), false /* pfad */))) {
                txtFileName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtFileName.setStyle("");
            }
        });
    }

    private void handleCountDownAction() {
        timeSeconds--;
        if (timeSeconds > 0) {
            if (!httpDownload) {
                btnRestartDownload.setText("neu _Starten in " + timeSeconds + " s");
            } else {
                btnContinueDownload.setText("_Weiterführen in " + timeSeconds + " s");
            }
        } else {
            timeline.stop();
            result = ProgConfigAskBeforeDelete.ContinueDownload.CONTINUE;
            quit();
        }
    }

    private boolean checkDownload() {
        return (download.getSetData() != null && download.getFilm() != null);
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }
        setButtonText();
    }

    private void setButtonText() {
        if (!httpDownload) {
            btnRestartDownload.setText("neu _Starten");
        } else {
            btnContinueDownload.setText("_Weiterführen");
        }
    }

    private void quit() {
        timeline.stop();
        close();
    }

    private void getDestination() {
        PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }
}
