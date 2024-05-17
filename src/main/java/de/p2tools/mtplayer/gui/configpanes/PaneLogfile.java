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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.tools.log.P2Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneLogfile {

    private final BooleanProperty logfileChanged = new SimpleBooleanProperty(false);
    private final P2ToggleSwitch tglEnableLog = new P2ToggleSwitch("Ein Logfile anlegen:");
    private final Label lblStandardLogDir = new Label("");
    private TextField txtLogFile;

    private final Stage stage;

    public PaneLogfile(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglEnableLog.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOG_ON);
        txtLogFile.textProperty().unbindBidirectional(ProgConfig.SYSTEM_LOG_DIR);
    }

    public void makeLogfile(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        TitledPane tpConfig = new TitledPane("Logfile", gridPane);
        result.add(tpConfig);

        tglEnableLog.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOG_ON);
        tglEnableLog.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (newValue) {
                P2Logger.setFileHandler(ProgInfos.getLogDirectory_String());
            } else {
                P2Logger.removeFileHandler();
            }
        }));

        final Button btnHelp = P2Button.helpButton(stage, "Logfile", HelpText.LOGFILE);

        txtLogFile = new TextField();
        txtLogFile.textProperty().bindBidirectional(ProgConfig.SYSTEM_LOG_DIR);

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Einen Ordner für das Logfile auswählen"));
        btnFile.setOnAction(event -> {
            P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtLogFile);
        });
        btnFile.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

        final Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAN.getImageView());
        btnClear.setTooltip(new Tooltip("Pfad löschen"));
        btnClear.setOnAction(event -> {
            txtLogFile.setText("");
        });

        final Button btnChange = new Button("_Pfad zum Logfile jetzt schon ändern");
        btnChange.setTooltip(new Tooltip("Den geänderten Pfad für das Logfile\n" +
                "jetzt schon verwenden, \n\n" +
                "ansonsten wird er erst beim nächsten\n" +
                "Programmstart verwendet"));
        btnChange.setOnAction(event -> {
            P2Logger.setFileHandler(ProgInfos.getLogDirectory_String());
            logfileChanged.setValue(false);
        });

        Label lblPath = new Label("Eigener Pfad:");

        lblStandardLogDir.visibleProperty().bind(tglEnableLog.selectedProperty());
        txtLogFile.textProperty().addListener((u, o, n) -> setLabel());
        setLabel();

        int row = 0;
        gridPane.add(tglEnableLog, 0, row, 3, 1);
        gridPane.add(btnHelp, 3, row);
        gridPane.add(lblStandardLogDir, 0, ++row, 4, 1);

        gridPane.add(new Label(""), 0, ++row);

        gridPane.add(lblPath, 0, ++row);
        gridPane.add(txtLogFile, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnClear, 3, row);

        gridPane.add(btnChange, 0, ++row, 4, 1);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        lblPath.disableProperty().bind(tglEnableLog.selectedProperty().not());
        txtLogFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnClear.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnChange.disableProperty().bind(tglEnableLog.selectedProperty().not().or(logfileChanged.not()));

        txtLogFile.textProperty().addListener((observable, oldValue, newValue) -> logfileChanged.setValue(true));
    }

    private void setLabel() {
        if (txtLogFile.getText().isEmpty()) {
            lblStandardLogDir.setText("Standard-Pfad:  " + ProgInfos.getStandardLogDirectory_String());
        } else {
            lblStandardLogDir.setText("");
        }
    }
}
