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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Collection;

public class PaneProgs {

    private final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
    private final PToggleSwitch tglSearchBeta = new PToggleSwitch("auch nach neuen Vorabversionen suchen");
    private final CheckBox chkDaily = new CheckBox("Zwischenschritte (Dailys) mit einbeziehen");
    private final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen:");
    private final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten:");
    private final PToggleSwitch tglSmallFilm = new PToggleSwitch("In der Tabelle \"Film\" nur kleine Button anzeigen:");
    private final PToggleSwitch tglSmallDownload = new PToggleSwitch("In der Tabelle \"Download\" nur kleine Button anzeigen:");
    private final PToggleSwitch tglTipOfDay = new PToggleSwitch("Tip des Tages anzeigen");
    private final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:");
    private TextField txtFileManager;
    private TextField txtFileManagerVideo;
    private TextField txtFileManagerWeb;

    private final PDialog pDialog;

    public PaneProgs(PDialog pDialog) {
        this.pDialog = pDialog;
    }

    public void close() {
        tglSearchAbo.selectedProperty().unbindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        tglStartDownload.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
        tglSmallFilm.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        tglSmallDownload.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        tglTipOfDay.selectedProperty().unbindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        tglEnableLog.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOG_ON);
        txtFileManager.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_DIR);
        txtFileManagerVideo.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_PLAY_FILME);
        txtFileManagerWeb.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_URL);
        tglSearch.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_ACT);
        tglSearchBeta.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_BETA);
        chkDaily.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY);
    }

    public void makeProg(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);

        addFilemanager(gridPane, 0);
        addVideoPlayer(gridPane, 1);
        addWebbrowser(gridPane, 2);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void addFilemanager(GridPane gridPane, int row) {
        txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_DIR);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Dateimanager manuell auswählen"));

        final Button btnHelp = PButton.helpButton(pDialog.getStage(), "Dateimanager", HelpText.FILEMANAGER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManager, btnFile, btnHelp);
        HBox.setHgrow(txtFileManager, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Dateimanager zum Öffnen des Downloadordners"), hBox);
        gridPane.add(vBox, 0, row);
    }

    private void addVideoPlayer(GridPane gridPane, int row) {
        txtFileManagerVideo = new TextField();
        txtFileManagerVideo.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_PLAY_FILME);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManagerVideo);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der gespeicherten Filme auswählen"));

        final Button btnHelp = PButton.helpButton(pDialog.getStage(), "Videoplayer", HelpText.VIDEOPLAYER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManagerVideo, btnFile, btnHelp);
        HBox.setHgrow(txtFileManagerVideo, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Videoplayer zum Abspielen gespeicherter Filme"), hBox);
        gridPane.add(vBox, 0, row);
    }

    private void addWebbrowser(GridPane gridPane, int row) {
        txtFileManagerWeb = new TextField();
        txtFileManagerWeb.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_URL);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManagerWeb);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Webbrowser zum Öffnen von URLs auswählen"));

        final Button btnHelp = PButton.helpButton(pDialog.getStage(), "Webbrowser", HelpText.WEBBROWSER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManagerWeb, btnFile, btnHelp);
        HBox.setHgrow(txtFileManagerWeb, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Webbrowser zum Öffnen von URLs"), hBox);
        gridPane.add(vBox, 0, row);
    }
}
