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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class TrayPane {

    BooleanProperty propTray = ProgConfig.SYSTEM_TRAY;
    BooleanProperty propOwnIcon = ProgConfig.SYSTEM_TRAY_USE_OWN_ICON;
    StringProperty propIcon = ProgConfig.SYSTEM_TRAY_ICON_PATH;

    private final Stage stage;
    private final PToggleSwitch tglTray = new PToggleSwitch("Programm im System Tray anzeigen");
    private final PToggleSwitch tglOwnIcon = new PToggleSwitch("Ein eigenes Icon anzeigen");
    private final TextField txtPath = new TextField();

    public TrayPane(Stage stage) {
        this.stage = stage;
    }

    public void makeTray(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("System Tray", gridPane);
        result.add(tpConfig);

        tglTray.selectedProperty().bindBidirectional(propTray);
        final Button btnHelpTray = PButton.helpButton(stage, "Programm im System Tray anzeigen",
                HelpText.TRAY);
        GridPane.setHalignment(btnHelpTray, HPos.RIGHT);

        tglOwnIcon.selectedProperty().bindBidirectional(propOwnIcon);
        tglOwnIcon.disableProperty().bind(tglTray.selectedProperty().not());
        final Button btnHelpTrayOwnIcon = PButton.helpButton(stage, "Eigenes Bild im Tray anzeigen",
                HelpText.TRAY_OWN_ICON);
        GridPane.setHalignment(btnHelpTrayOwnIcon, HPos.RIGHT);
        btnHelpTrayOwnIcon.disableProperty().bind(tglOwnIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));


        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Einen Ordner für das Logfile auswählen"));
        btnFile.setOnAction(event -> {
            String s = PDirFileChooser.FileChooserSelect(ProgData.getInstance().primaryStage, "", "");
            if (!s.isEmpty()) {
                //evtl. Abbruch des FileChooser
                txtPath.setText(s);
            }
        });
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.disableProperty().bind(tglOwnIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));

        txtPath.textProperty().bindBidirectional(propIcon);
        txtPath.disableProperty().bind(tglOwnIcon.selectedProperty().not().or(tglTray.selectedProperty().not()));

        int row = 0;
        gridPane.add(tglTray, 0, ++row, 2, 1);
        gridPane.add(btnHelpTray, 2, row);

        gridPane.add(tglOwnIcon, 0, ++row, 2, 1);
        gridPane.add(btnHelpTrayOwnIcon, 2, row);

        gridPane.add(new Label("Datei (png, jpg):"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnFile, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }

    public void close() {
    }
}
