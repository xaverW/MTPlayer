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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Collection;

public class PaneDownload {

    private final P2ToggleSwitch tglFinished = new P2ToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final P2ToggleSwitch tglError = new P2ToggleSwitch("Bei Downloadfehlern Fehlermeldung anzeigen");
    private final P2ToggleSwitch tglHistory = new P2ToggleSwitch("Nur Downloads in die History eintragen");
    private final Spinner<Integer> spinnerAnz = new Spinner<>(1, 9, 1);
    private final P2ToggleSwitch tglSSL = new P2ToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final P2ToggleSwitch tglBeep = new P2ToggleSwitch("Nach jedem Download einen \"Beep\" ausgeben");
    private final Slider sliderBandwidth = new Slider();
    private final Label lblBandwidth = new Label();
    private final Stage stage;

    public PaneDownload(Stage stage) {
        this.stage = stage;
    }


    public void close() {
        tglFinished.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        tglError.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW);
        tglHistory.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_ONLY_HISTORY);
        tglSSL.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        tglBeep.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_BEEP);
        sliderBandwidth.valueProperty().unbindBidirectional(ProgConfig.DOWNLOAD_MAX_BANDWIDTH_BYTE);
    }

    public void makeDownload(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        TitledPane tpConfig = new TitledPane("Download", gridPane);
        result.add(tpConfig);

        tglFinished.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        final Button btnHelpFinished = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);

        tglError.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW);
        final Button btnHelpError = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_ERROR);

        tglHistory.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_ONLY_HISTORY);
        final Button btnHelpHistory = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_ONLY_HISTORY);

        final Button btnHelpStop = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_STOP);

        final Button btnHelpContinue = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_CONTINUE);

        tglSSL.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        final Button btnHelpSSL = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_SSL_ALWAYS_TRUE);

        tglBeep.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_BEEP);
        final Button btnBeep = new Button("_Test");
        btnBeep.setOnAction(a -> Toolkit.getDefaultToolkit().beep());

        final Button btnBandwidth = PIconFactory.getHelpButton(stage, "Download",
                HelpText.DOWNLOAD_BANDWIDTH);


        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        btnHelpFinished.setPrefWidth(btnBeep.getWidth());
        btnHelpFinished.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnHelpError, HPos.RIGHT);
        btnHelpError.setPrefWidth(btnBeep.getWidth());
        btnHelpError.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnHelpHistory, HPos.RIGHT);
        btnHelpHistory.setPrefWidth(btnBeep.getWidth());
        btnHelpHistory.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnHelpStop, HPos.RIGHT);
        btnHelpStop.setPrefWidth(btnBeep.getWidth());
        btnHelpStop.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnHelpContinue, HPos.RIGHT);
        btnHelpContinue.setPrefWidth(btnBeep.getWidth());
        btnHelpContinue.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnHelpSSL, HPos.RIGHT);
        btnHelpSSL.setPrefWidth(btnBeep.getWidth());
        btnHelpSSL.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHalignment(btnBeep, HPos.RIGHT);

        GridPane.setHalignment(btnBandwidth, HPos.RIGHT);
        btnBandwidth.setPrefWidth(btnBeep.getWidth());
        btnBandwidth.setMaxWidth(Double.MAX_VALUE);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        int row = 0;
        gridPane.add(tglFinished, 0, ++row, 2, 1);
        gridPane.add(btnHelpFinished, 2, row);

        gridPane.add(tglBeep, 0, ++row, 2, 1);
        gridPane.add(btnBeep, 2, row);

        gridPane.add(tglHistory, 0, ++row, 2, 1);
        gridPane.add(btnHelpHistory, 2, row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(tglError, 0, ++row, 2, 1);
        gridPane.add(btnHelpError, 2, row);

        gridPane.add(tglSSL, 0, ++row, 2, 1);
        gridPane.add(btnHelpSSL, 2, row);


        // gleichzeitige Downloads
        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label("Gleichzeitige Downloads"), 0, ++row);
        gridPane.add(btnBandwidth, 2, row, 1, 2);
        gridPane.add(spinnerAnz, 0, ++row);

        // max. Bandbreite
        Label lblText = new Label("Max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));

        HBox hh = new HBox();
        HBox.setHgrow(hh, Priority.ALWAYS);
        HBox h = new HBox();
        h.getChildren().addAll(lblText, hh, lblBandwidth);
        ++row;
        gridPane.add(h, 0, ++row);
        gridPane.add(sliderBandwidth, 0, ++row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        initNumberDownloads();
        DownloadInfosFactory.initBandwidth(sliderBandwidth, lblBandwidth);
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
}
