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
import de.p2tools.mtplayer.controller.starter.DownloadState;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Collection;

public class PaneDownload {

    private final PToggleSwitch tglFinished = new PToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final PToggleSwitch tglError = new PToggleSwitch("Bei Downloadfehler Fehlermeldung anzeigen");

    private final ToggleGroup groupStop = new ToggleGroup();
    private final RadioButton rbStopAsk = new RadioButton("Vorher fragen");
    private final RadioButton rbStopDelete = new RadioButton("Download und angefangene Dateien immer löschen");
    private final RadioButton rbStopNothing = new RadioButton("Download immer löschen, keine Dateien löschen");

    private final ToggleGroup group = new ToggleGroup();
    private final RadioButton rbAsk = new RadioButton("Vorher fragen");
    private final RadioButton rbContinue = new RadioButton("Immer weiterführen");
    private final RadioButton rbRestart = new RadioButton("Immer neu starten");

    private final PToggleSwitch tglOne = new PToggleSwitch("Nur ein Download pro Downloadserver");
    private final PToggleSwitch tglSSL = new PToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final PToggleSwitch tglBeep = new PToggleSwitch("Nach jedem Download einen \"Beep\" ausgeben");

    private final Stage stage;

    public PaneDownload(Stage stage) {
        this.stage = stage;
        initRadio();
    }

    public void close() {
        tglFinished.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        tglError.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_ERROR_MSG);
        tglOne.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER);
        tglSSL.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        tglBeep.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_BEEP);
    }

    public void makeDownload(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        TitledPane tpConfig = new TitledPane("Download", gridPane);
        result.add(tpConfig);

        tglFinished.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        final Button btnHelpFinished = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);

        tglError.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_ERROR_MSG);
        final Button btnHelpError = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ERROR);

        final Button btnHelpStop = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_STOP);

        final Button btnHelpContinue = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_CONTINUE);

        tglOne.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER);
        final Button btnHelpOne = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ONE_SERVER);

        tglSSL.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        final Button btnHelpSSL = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_SSL_ALWAYS_TRUE);

        tglBeep.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_BEEP);
        final Button btnBeep = new Button("_Test");
        btnBeep.setOnAction(a -> Toolkit.getDefaultToolkit().beep());

        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpError, HPos.RIGHT);
        GridPane.setHalignment(btnHelpStop, HPos.RIGHT);
        GridPane.setHalignment(btnHelpContinue, HPos.RIGHT);
        GridPane.setHalignment(btnHelpOne, HPos.RIGHT);
        GridPane.setHalignment(btnHelpSSL, HPos.RIGHT);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        int row = 0;
        VBox vBox = new VBox(5);
        HBox hBox = new HBox(20);
        vBox.getChildren().addAll(new Label("Beim Abbrechen oder Löschen von Downloads:"));
        hBox.getChildren().addAll(new Label("            "), rbStopAsk);
        vBox.getChildren().addAll(hBox);
        hBox = new HBox(20);
        hBox.getChildren().addAll(new Label("            "), rbStopDelete, rbStopNothing);
        vBox.getChildren().addAll(hBox);

        gridPane.add(vBox, 0, row);
        gridPane.add(btnHelpStop, 1, row, 1, 2);
        GridPane.setValignment(btnHelpStop, VPos.TOP);

        ++row;
        vBox = new VBox(5);
        hBox = new HBox(20);
        vBox.getChildren().addAll(new Label("Beim Neustart bereits angefangener Downloads:"));
        hBox.getChildren().addAll(new Label("            "), rbAsk);
        vBox.getChildren().addAll(hBox);
        hBox = new HBox(20);
        hBox.getChildren().addAll(new Label("            "), rbContinue, rbRestart);
        vBox.getChildren().addAll(hBox);

        gridPane.add(vBox, 0, ++row);
        gridPane.add(btnHelpContinue, 1, row, 1, 2);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(tglFinished, 0, ++row);
        gridPane.add(btnHelpFinished, 1, row);
        GridPane.setValignment(btnHelpFinished, VPos.TOP);

        gridPane.add(tglError, 0, ++row);
        gridPane.add(btnHelpError, 1, row);

        gridPane.add(tglOne, 0, ++row);
        gridPane.add(btnHelpOne, 1, row);

        gridPane.add(tglSSL, 0, ++row);
        gridPane.add(btnHelpSSL, 1, row);

        gridPane.add(tglBeep, 0, ++row);
        gridPane.add(btnBeep, 1, row);
        GridPane.setHalignment(btnBeep, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void initRadio() {
        rbStopAsk.setToggleGroup(groupStop);
        rbStopDelete.setToggleGroup(groupStop);
        rbStopNothing.setToggleGroup(groupStop);

        rbAsk.setToggleGroup(group);
        rbContinue.setToggleGroup(group);
        rbRestart.setToggleGroup(group);

        setRadio();
        ProgConfig.DOWNLOAD_CONTINUE.addListener((v, o, n) -> setRadio());
        ProgConfig.DOWNLOAD_STOP.addListener((v, o, n) -> setRadio());

        rbStopAsk.setOnAction(a -> ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__ASK));
        rbStopDelete.setOnAction(a -> ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__DELETE_FILE));
        rbStopNothing.setOnAction(a -> ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__DO_NOT_DELETE));
        rbAsk.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__ASK));
        rbContinue.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__CONTINUE));
        rbRestart.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__RESTART));
    }

    private void setRadio() {
        switch (ProgConfig.DOWNLOAD_STOP.getValue()) {
            case DownloadState.DOWNLOAD_STOP__DELETE_FILE:
                rbStopDelete.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_STOP__DO_NOT_DELETE:
                rbStopNothing.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_STOP__ASK:
            default:
                rbStopAsk.setSelected(true);
                break;
        }
        switch (ProgConfig.DOWNLOAD_CONTINUE.getValue()) {
            case DownloadState.DOWNLOAD_RESTART__CONTINUE:
                rbContinue.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_RESTART__RESTART:
                rbRestart.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_RESTART__ASK:
            default:
                rbAsk.setSelected(true);
                break;
        }
    }
}