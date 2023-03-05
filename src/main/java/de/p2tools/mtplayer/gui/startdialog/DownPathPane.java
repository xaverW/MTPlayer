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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DownPathPane {
    private final TextField txtPath = new TextField();
    StringProperty pathProp = ProgConfig.START_DIALOG_DOWNLOAD_PATH;

    private final PDialog pDialog;

    public DownPathPane(PDialog pDialog) {
        this.pDialog = pDialog;
    }

    public void close() {
        txtPath.textProperty().unbindBidirectional(pathProp);
    }

    public TitledPane makePath() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        if (ProgData.debug) {
            //dann einen anderen Downloadpfad
            pathProp.setValue("/tmp/Download");
        }
        txtPath.textProperty().bindBidirectional(pathProp);
        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.DirChooser(pDialog.getStage(), txtPath);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen"));

        final Button btnHelp = PButton.helpButton(pDialog.getStage(),
                "Zielverzeichnis",
                "Hier kann das Verzeichnis angegeben werden, " +
                        "in dem die Downloads gespeichert werden.");

        int row = 0;
        gridPane.add(new Label("Pfad:"), 0, row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnHelp, 3, row);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());

        TitledPane tpConfig = new TitledPane("Pfad für die Downloads", gridPane);
        return tpConfig;
    }
}
