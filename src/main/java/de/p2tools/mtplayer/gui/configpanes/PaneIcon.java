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

import de.p2tools.mtplayer.controller.ProgStartAfterGui;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.icons.GetIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneIcon {

    private final PToggleSwitch tglOwnProgIcon = new PToggleSwitch("Ein eigenes Programmicon anzeigen");
    private final TextField txtProgIconPath = new TextField();
    private final PToggleSwitch tglTray = new PToggleSwitch("Programm im System Tray anzeigen");
    private final PToggleSwitch tglOwnTrayIcon = new PToggleSwitch("Ein eigenes Icon anzeigen");
    private final TextField txtTrayIconPath = new TextField();

    private final Stage stage;

    public PaneIcon(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglOwnProgIcon.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON);
        txtProgIconPath.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROGRAM_ICON_PATH);
        tglTray.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_TRAY);
        tglOwnTrayIcon.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_TRAY_USE_OWN_ICON);
        txtTrayIconPath.textProperty().unbindBidirectional(ProgConfig.SYSTEM_TRAY_ICON_PATH);
    }

    public void makeIcon(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();

        TitledPane tpConfig = new TitledPane("Programm Icons", gridPane);
        result.add(tpConfig);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());

        int row = 0;
        row = addProgIcon(gridPane, row);
        gridPane.add(new Label(), 0, ++row);
        addTryIcon(gridPane, ++row);
    }

    private int addProgIcon(GridPane gridPane, int row) {
        tglOwnProgIcon.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON);
        final Button btnHelpProgramIcon = PButton.helpButton(stage, "Eigenes Bild als Programmicon anzeigen",
                HelpText.PROGRAM_ICON);
        GridPane.setHalignment(btnHelpProgramIcon, HPos.RIGHT);

        final Button btnProgIconFile = new Button();
        btnProgIconFile.setTooltip(new Tooltip("Eine Programmicon auswählen"));
        btnProgIconFile.setOnAction(event -> {
            String s = PDirFileChooser.FileChooserSelect(ProgData.getInstance().primaryStage, "", "");
            if (!s.isEmpty()) {
                //evtl. Abbruch des FileChooser
                txtProgIconPath.setText(s);
            }
        });
        btnProgIconFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        txtProgIconPath.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROGRAM_ICON_PATH);
        tglOwnProgIcon.selectedProperty().addListener((v, o, n) -> {
            ProgStartAfterGui.setProgramIcon();
            GetIcon.addWindowP2Icon(stage, tglOwnProgIcon.isSelected() ? txtProgIconPath.getText() : "");
        });
        txtProgIconPath.textProperty().addListener((v, o, n) -> {
            ProgStartAfterGui.setProgramIcon();
            GetIcon.addWindowP2Icon(stage, tglOwnProgIcon.isSelected() ? txtProgIconPath.getText() : "");
        });

        btnProgIconFile.disableProperty().bind(tglOwnProgIcon.selectedProperty().not());
        txtProgIconPath.disableProperty().bind(tglOwnProgIcon.selectedProperty().not());

        Label lblIcon = new Label("Datei (png, jpg):");
        lblIcon.disableProperty().bind(tglOwnProgIcon.selectedProperty().not());

        gridPane.add(tglOwnProgIcon, 0, row, 2, 1);
        gridPane.add(btnHelpProgramIcon, 2, row);

        gridPane.add(lblIcon, 0, ++row);
        gridPane.add(txtProgIconPath, 1, row);
        gridPane.add(btnProgIconFile, 2, row);

        return row;
    }

    private int addTryIcon(GridPane gridPane, int row) {
        tglTray.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_TRAY);
        final Button btnHelpTray = PButton.helpButton(stage, "Programm im System Tray anzeigen",
                HelpText.TRAY);
        GridPane.setHalignment(btnHelpTray, HPos.RIGHT);

        tglOwnTrayIcon.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_TRAY_USE_OWN_ICON);
        tglOwnTrayIcon.disableProperty().bind(tglTray.selectedProperty().not());
        final Button btnHelpTrayOwnIcon = PButton.helpButton(stage, "Eigenes Bild im Tray anzeigen",
                HelpText.TRAY_OWN_ICON);
        GridPane.setHalignment(btnHelpTrayOwnIcon, HPos.RIGHT);
        btnHelpTrayOwnIcon.disableProperty().bind(tglTray.selectedProperty().not());


        final Button btnTrayFile = new Button();
        btnTrayFile.setTooltip(new Tooltip("Eine Datei für das Icon auswählen"));
        btnTrayFile.setOnAction(event -> {
            String s = PDirFileChooser.FileChooserSelect(ProgData.getInstance().primaryStage, "", "");
            if (!s.isEmpty()) {
                //evtl. Abbruch des FileChooser
                txtTrayIconPath.setText(s);
            }
        });
        btnTrayFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnTrayFile.disableProperty().bind(tglOwnTrayIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));

        txtTrayIconPath.textProperty().bindBidirectional(ProgConfig.SYSTEM_TRAY_ICON_PATH);
        txtTrayIconPath.disableProperty().bind(tglOwnTrayIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));

        Label lblFile = new Label("Datei (png, jpg):");
        lblFile.disableProperty().bind(tglOwnTrayIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));

        gridPane.add(tglTray, 0, row, 2, 1);
        gridPane.add(btnHelpTray, 2, row);

        gridPane.add(tglOwnTrayIcon, 0, ++row, 2, 1);
        gridPane.add(btnHelpTrayOwnIcon, 2, row);

        gridPane.add(lblFile, 0, ++row);
        gridPane.add(txtTrayIconPath, 1, row);
        gridPane.add(btnTrayFile, 2, row);

        return row;
    }
}
