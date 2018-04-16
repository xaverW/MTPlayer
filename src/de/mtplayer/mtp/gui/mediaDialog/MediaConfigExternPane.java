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
import de.mtplayer.mtp.gui.mediaDb.CreateMediaDb;
import de.mtplayer.mtp.gui.mediaDb.MediaDbDataExtern;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
        btnAdd.setOnAction(a -> {
            // todo
            String[] suffix = Config.MEDIA_DB_SUFFIX.get().split(",");
            for (int i = 0; i < suffix.length; ++i) {
                suffix[i] = suffix[i].toLowerCase();
                if (!suffix[i].isEmpty() && !suffix[i].startsWith(".")) {
                    suffix[i] = '.' + suffix[i];
                }
            }

            Thread th = new Thread(new CreateMediaDb(suffix, txtPath.getText(), daten.mediaDbList));
            th.setName("createMediaDB-EXTERN");
            th.start();
        });

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

        initTable(vBox);
        vBox.getChildren().addAll(gridPane);
    }

    private void initTable(VBox vBox) {

        TableView<MediaDbDataExtern> tableView = new TableView<>();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<MediaDbDataExtern, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaDbDataExtern, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaDbDataExtern, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        tableView.getColumns().addAll(nameColumn, pathColumn, countColumn);

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        countColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(10.0 / 100));

        tableView.setItems(daten.mediaDbList.getExternList());

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

}
