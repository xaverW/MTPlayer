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
import de.p2tools.mtplayer.controller.tools.MLBandwidthTokenBucket;
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
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.*;
import java.util.Collection;

public class PaneDownload {

    private final PToggleSwitch tglFinished = new PToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final PToggleSwitch tglError = new PToggleSwitch("Bei Downloadfehler Fehlermeldung anzeigen");
    private final PToggleSwitch tglOne = new PToggleSwitch("Nur ein Download pro Downloadserver");
    private final PToggleSwitch tglSSL = new PToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final PToggleSwitch tglBeep = new PToggleSwitch("Nach jedem Download einen \"Beep\" ausgeben");
    private final Spinner<Integer> spinnerAnz = new Spinner<>(1, 9, 1);
    private final Slider sliderBandwidth = new Slider();
    private final Label lblBandwidth = new Label();
    private final Stage stage;

    public PaneDownload(Stage stage) {
        this.stage = stage;
    }


    public void close() {
        tglFinished.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        tglError.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_ERROR_MSG);
        tglOne.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER);
        tglSSL.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        tglBeep.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_BEEP);
        sliderBandwidth.valueProperty().unbindBidirectional(ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE);
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

        final Button btnBandwidth = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_BANDWIDTH);


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
        gridPane.add(tglFinished, 0, ++row, 2, 1);
        gridPane.add(btnHelpFinished, 2, row);
        GridPane.setValignment(btnHelpFinished, VPos.TOP);

        gridPane.add(tglError, 0, ++row, 2, 1);
        gridPane.add(btnHelpError, 2, row);

        gridPane.add(tglOne, 0, ++row, 2, 1);
        gridPane.add(btnHelpOne, 2, row);

        gridPane.add(tglSSL, 0, ++row, 2, 1);
        gridPane.add(btnHelpSSL, 2, row);

        gridPane.add(tglBeep, 0, ++row, 2, 1);
        gridPane.add(btnBeep, 2, row);
        GridPane.setHalignment(btnBeep, HPos.RIGHT);

        // gleichzeitige Downloads
        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label("Gleichzeitige Downloads"), 0, ++row);
        gridPane.add(btnBandwidth, 2, row, 1, 2);
        gridPane.add(spinnerAnz, 0, ++row);

        // max. Bandbreite
        Label lblText = new Label("Max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));

        HBox hh = new HBox();
        HBox.setHgrow(hh, Priority.ALWAYS);
        HBox h = new HBox();
        h.getChildren().addAll(lblText, hh, lblBandwidth);
        ++row;
        gridPane.add(h, 0, ++row);
        gridPane.add(sliderBandwidth, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        initNumberDownloads();
        initBandwidth();
    }

    private void initNumberDownloads() {
        spinnerAnz.valueProperty().addListener((u, o, n) -> {
            ProgConfig.DOWNLOAD_MAX_DOWNLOADS.setValue(spinnerAnz.getValue());
        });
        ProgConfig.DOWNLOAD_MAX_DOWNLOADS.addListener((u, o, n) -> {
            spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
        });
        spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
    }

    private void initBandwidth() {
        sliderBandwidth.setMin(50);
        sliderBandwidth.setMax(MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
        sliderBandwidth.setShowTickLabels(true);
        sliderBandwidth.setMinorTickCount(9);
        sliderBandwidth.setMajorTickUnit(250);
        sliderBandwidth.setBlockIncrement(25);
        sliderBandwidth.setSnapToTicks(true);

        sliderBandwidth.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
                    return "alles";
                }

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        sliderBandwidth.valueProperty().bindBidirectional(ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE);
        setTextBandwidth();

        sliderBandwidth.valueProperty().addListener((obs, oldValue, newValue) -> {
            setTextBandwidth();
        });
    }

    private void setTextBandwidth() {
        int bandwidthKByte;
        String ret;
        bandwidthKByte = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
        if (bandwidthKByte == MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
            ret = "alles";
        } else {
            ret = bandwidthKByte + " kB/s";
        }
        lblBandwidth.setText(ret);
    }
}
