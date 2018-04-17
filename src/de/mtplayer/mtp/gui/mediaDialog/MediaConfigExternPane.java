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
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDb.CreateMediaDb;
import de.mtplayer.mtp.gui.mediaDb.MediaDbDataExtern;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialog.PAlert;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Collection;
import java.util.Date;

public class MediaConfigExternPane {

    private final Daten daten;
    private final TableView<MediaDbDataExtern> tableView = new TableView<>();

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
//        txtPath.textProperty().bindBidirectional(Config.MEDIA_DB_PATH_EXTERN.getStringProperty());

        final TextField txtName = new TextField();
        txtName.setText("Sammlung-" + StringFormatters.FORMATTER_ddMMyyyy.format(new Date())
        );
//        txtName.textProperty().bindBidirectional(Config.MEDIA_DB_NAME_EXTERN.getStringProperty());

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

        final Button btnAdd = new Button("Neue Sammlung hinzufügen");
        btnAdd.disableProperty().bind(txtName.textProperty().isEmpty().or(txtPath.textProperty().isEmpty()));
        GridPane.setHalignment(btnAdd, HPos.RIGHT
        );
        btnAdd.setOnAction(a -> {

            MediaDbDataExtern found = daten.mediaDbList.getExternList().stream()
                    .filter(m -> m.getCollectionName().equals(txtName.getText())).findAny().orElse(null);
            if (found == null ||
                    PAlert.showAlert_yes_no("Sammlung hinzufügen", "Sammlung: " + txtName.getText(),
                            "Eine Sammlung mit dem Namen existiert bereits.\n" +
                                    "Sollen weitere Filme in " +
                                    "die Sammlung integriert werden?").equals(PAlert.BUTTON.YES)) {

                // nur wenn noch nicht läuft
                Thread th = new Thread(new CreateMediaDb(daten.mediaDbList, txtPath.getText(), txtName.getText()));
                th.setName("createMediaDb-EXTERN");
                th.start();
            }
        });

        int row = 0;
        gridPane.add(new Label("Name der Sammlung:"), 0, row);
        gridPane.add(txtName, 1, row);
        gridPane.add(btnHelpPath, 2, row);

        gridPane.add(new Label("Pfad:"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);

        gridPane.add(btnAdd, 1, ++row, 2, 1);


        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        initTable(vBox);
        Button btnDel = new Button("Sammlung entfernen");
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnDel.setOnAction(a -> {
            MediaDbDataExtern md = tableView.getSelectionModel().getSelectedItem();
            daten.mediaDbList.removeCollectionFromMediaDb(md.getCollectionName());
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnDel);

        vBox.getChildren().add(hBox);
        vBox.getChildren().addAll(gridPane);
    }

    private void initTable(VBox vBox) {

        tableView.setMinHeight(Const.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TableColumn<MediaDbDataExtern, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

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
