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

package de.mtplayer.mtp.gui.startDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.Icons;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class DownPathPane {
    StringProperty pathProp = ProgConfig.START_DIALOG_DOWNLOAD_PATH.getStringProperty();


    public TitledPane makePath() {

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Zielverzeichnis", gridPane);

        Text text = new Text("Pfad für die Downloads auswählen");
        text.setStyle("-fx-font-weight: bold");
        gridPane.add(text, 0, 0);

        TextField txtPath = new TextField();
        txtPath.textProperty().bindBidirectional(pathProp);
        gridPane.add(txtPath, 0, 1);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(StartDialogController.stage, txtPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        gridPane.add(btnFile, 1, 1);

        final Button btnHelp = new PButton().helpButton(StartDialogController.stage,
                "Zielverzeichnis",
                "Hier kann das Verzeichnis angegeben werden, " +
                        "in dem die Downloads gespeichert werden.");
        gridPane.add(btnHelp, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        return tpConfig;
    }


}
