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

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.tools.PShutDown;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneProgs {

    private TextField txtFileManager = new TextField();
    private TextField txtFileManagerVideo = new TextField();
    private TextField txtFileManagerWeb = new TextField();
    private TextField txtSystemCall = new TextField();
    private CheckBox cbxSystemCallOn = new CheckBox("Den Systemaufruf nach der Wartezeit und dem Programmende, aufrufen");

    private final Stage stage;

    public PaneProgs(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        txtFileManager.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_DIR);
        txtFileManagerVideo.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_PLAY_FILME);
        txtFileManagerWeb.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_URL);
        txtSystemCall.textProperty().unbindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL);
        cbxSystemCallOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL_ON);
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
        addSystemCall(gridPane, 3);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void addFilemanager(GridPane gridPane, int row) {
        txtFileManager.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_DIR);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Dateimanager manuell auswählen"));

        final Button btnHelp = PButton.helpButton(stage, "Dateimanager", HelpText.FILEMANAGER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManager, btnFile, btnHelp);
        HBox.setHgrow(txtFileManager, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Dateimanager zum Öffnen des Downloadordners"), hBox);
        gridPane.add(vBox, 0, row);
    }

    private void addVideoPlayer(GridPane gridPane, int row) {
        txtFileManagerVideo.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_PLAY_FILME);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManagerVideo);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der gespeicherten Filme auswählen"));

        final Button btnHelp = PButton.helpButton(stage, "Videoplayer", HelpText.VIDEOPLAYER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManagerVideo, btnFile, btnHelp);
        HBox.setHgrow(txtFileManagerVideo, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Videoplayer zum Abspielen gespeicherter Filme"), hBox);
        gridPane.add(vBox, 0, row);
    }

    private void addWebbrowser(GridPane gridPane, int row) {
        txtFileManagerWeb.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_OPEN_URL);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManagerWeb);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Webbrowser zum Öffnen von URLs auswählen"));

        final Button btnHelp = PButton.helpButton(stage, "Webbrowser", HelpText.WEBBROWSER);

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtFileManagerWeb, btnFile, btnHelp);
        HBox.setHgrow(txtFileManagerWeb, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Webbrowser zum Öffnen von URLs"), hBox);
        gridPane.add(vBox, 0, row);
    }

    private void addSystemCall(GridPane gridPane, int row) {
        txtSystemCall.textProperty().bindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL);
        cbxSystemCallOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL_ON);

        final Button btnHelp = PButton.helpButton(stage, "Webbrowser", HelpText.CONFIG_SHUT_DOWN_CALL);

        Button btnTest = new Button("Testen");
        btnTest.setOnAction(a -> {
            ProgSave.saveAll(); // damit nichts verloren geht
            PShutDown.shutDown(ProgConfig.SYSTEM_SHUT_DOWN_CALL.getValueSafe());
        });

        Button btnStandard = new Button("Standard setzen");
        btnStandard.setOnAction(a -> txtSystemCall.setText(PShutDown.getShutDownCommand()));

        VBox vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(txtSystemCall, btnTest, btnStandard, btnHelp);
        HBox.setHgrow(txtSystemCall, Priority.ALWAYS);
        HBox hBoxOn = new HBox();
        hBoxOn.getChildren().addAll(PGuiTools.getVDistance(20), cbxSystemCallOn);
        vBox.getChildren().addAll(new Label("Systemaufruf nach dem \"Auf Downloads warten\" Dialog"), hBox, hBoxOn);
        gridPane.add(vBox, 0, row);
    }
}
