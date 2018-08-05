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
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.mediaDb.MediaPathData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.SortedList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.io.File;
import java.util.Collection;
import java.util.Date;

public class MediaConfigPanePathExtern {

    private final ProgData progData;
    private final TableView<MediaPathData> tableView = new TableView<>();

    public MediaConfigPanePathExtern() {
        this.progData = ProgData.getInstance();
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
        gridPane.setPadding(new Insets(20, 0, 0, 0));

        final TextField txtPath = new TextField();
        final TextField txtName = new TextField();
        txtName.setText("Sammlung-" + StringFormatters.FORMATTER_ddMMyyyy.format(new Date()));

        final Button btnPath = new Button("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Einlesen einer neuen Sammlung auswählen."));
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
            if (txtName.getText().isEmpty()) {
                txtName.setText(txtPath.getText());
            }
        });

        final Button btnHelp = new PButton().helpButton("Externe Mediensammlungen verwalten",
                HelpText.EXTERN_MEDIA_COLLECTION);

        final Button btnAdd = new Button("");
        btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt und vom angegebenen Pfad eingelesen."));
        btnAdd.setGraphic(new Icons().ICON_BUTTON_ADD);

        btnAdd.disableProperty().bind(txtName.textProperty().isEmpty()
                .or(txtPath.textProperty().isEmpty())
                .or(progData.mediaList.propSearchProperty()));
        btnAdd.setOnAction(a -> {
            if (!progData.mediaPathList.containExternal(txtName.getText()) ||
                    PAlert.showAlert_yes_no("Sammlung hinzufügen", "Sammlung: " + txtName.getText(),
                            "Eine Sammlung mit dem Namen existiert bereits." + PConst.LINE_SEPARATOR +
                                    "Sollen weitere Filme in " +
                                    "die Sammlung integriert werden?").equals(PAlert.BUTTON.YES)) {

                progData.mediaList.createCollection(txtPath.getText(), txtName.getText());
            }
        });

        int row = 0;
        GridPane.setHalignment(btnAdd, HPos.RIGHT);
        gridPane.add(new Label("Name der Sammlung:"), 0, row);
        gridPane.add(txtName, 1, row);
        gridPane.add(btnAdd, 2, row);

        gridPane.add(new Label("Pfad:"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), PColumnConstraints.getCcComputedSizeAndHgrow());

        initTable(vBox);

        Button btnUpdate = new Button("");
        btnUpdate.setTooltip(new Tooltip("Die markierte Sammlung wird neu eingelesen."));
        btnUpdate.setGraphic(new Icons().ICON_BUTTON_UPDATE);
        btnUpdate.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnUpdate.setOnAction(a -> {
            MediaPathData mediaPathData = tableView.getSelectionModel().getSelectedItem();

            File file = new File(mediaPathData.getPath());
            if (!file.exists()) {
                PAlert.showErrorAlert("Pfad existiert nicht!", "Der Pfad der Sammlung:" + PConst.LINE_SEPARATOR +
                        mediaPathData.getPath() + PConst.LINE_SEPARATOR +
                        "existiert nicht. Die Sammlung kann nicht eingelesen werden");
                return;
            }

            progData.mediaList.updateCollection(mediaPathData);
        });

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Die markierte Sammlung wird gelöscht."));
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnDel.setOnAction(a -> {
            MediaPathData md = tableView.getSelectionModel().getSelectedItem();
            progData.mediaList.removeCollection(md);
        });

        HBox hHelp = new HBox();
        hHelp.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hHelp, Priority.ALWAYS);
        hHelp.getChildren().add(btnHelp);

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.getChildren().addAll(btnUpdate, btnDel, hHelp);
        vBox.getChildren().add(hBox);
        vBox.getChildren().addAll(gridPane);
    }

    private void initTable(VBox vBox) {

        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TableColumn<MediaPathData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaPathData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        tableView.getColumns().addAll(nameColumn, pathColumn, countColumn);

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        countColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(10.0 / 100));

        SortedList<MediaPathData> sortedList = progData.mediaPathList.getSortedListExternal();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

}
