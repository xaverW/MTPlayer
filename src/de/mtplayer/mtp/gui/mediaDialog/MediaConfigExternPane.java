/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Collection;

public class MediaConfigExternPane {

    private final Daten daten;

    public MediaConfigExternPane() {
        this.daten = Daten.getInstance();
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        TitledPane tpConfig = new TitledPane("Externe Medien", vBox);
        result.add(tpConfig);

        makeGet(vBox);
    }

    private void makeGet(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        final TextField txtPath = new TextField();
        txtPath.textProperty().bindBidirectional(Config.MEDIA_DB_PATH_EXTERN.getStringProperty());

        final TextField txtName = new TextField();
        txtName.textProperty().bindBidirectional(Config.MEDIA_DB_NAME_EXTERN.getStringProperty());

        final Button btnPath = new Button("");
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setOnAction(event -> {
            DirFileChooser.DirChooser(Daten.getInstance().primaryStage, txtPath);
            if (txtName.getText().isEmpty()) {
                txtName.setText(txtPath.getText());
            }
        });

        final Button btnHelpPath = new Button("");
        btnHelpPath.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpPath.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpPath.setOnAction(a -> new MTAlert().showHelpAlert("Abos automatisch suchen",
                HelpText.ABOS_SOFRT_SUCHEN)); //todo

        final Button btnAdd = new Button("Hinzufügen");

        int row = 0;
        gridPane.add(new Label("Pfad:"), 0, row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);
        gridPane.add(btnHelpPath, 3, row);
        gridPane.add(new Label("Name der Sammlung:"), 0, ++row);
        gridPane.add(txtName, 1, row);
        gridPane.add(btnAdd, 1, ++row);


        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        vBox.getChildren().addAll(gridPane);
    }

}
