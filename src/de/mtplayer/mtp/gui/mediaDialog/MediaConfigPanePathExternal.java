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
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.mediaDb.MediaPathData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collection;

public class MediaConfigPanePathExternal {

    private final ProgData progData;
    private final TableView<MediaPathData> tableView = new TableView<>();
    private final Stage stage;
    private final boolean extern;
    private final TextField txtPath = new TextField();
    private final TextField txtCollectionName = new TextField();


    public MediaConfigPanePathExternal(Stage stage, boolean extern) {
        this.stage = stage;
        this.extern = extern;
        this.progData = ProgData.getInstance();
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);

        TitledPane tpConfig = new TitledPane(extern ? "Externe Medien" : "Interne Medien", vBox);
        result.add(tpConfig);

        initTable(vBox);
        makeButton(vBox);
        makeGrid(vBox);
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final TableColumn<MediaPathData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaPathData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        SortedList<MediaPathData> sortedList;
        if (extern) {
            nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
            pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
            countColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));

            tableView.getColumns().addAll(nameColumn, pathColumn, countColumn);
            sortedList = progData.mediaPathDataList.getSortedListExternal();
        } else {
            pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(60.0 / 100));
            countColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));

            tableView.getColumns().addAll(pathColumn, countColumn);
            sortedList = progData.mediaPathDataList.getSortedListInternal();
        }

        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void makeButton(VBox vBox) {
        final Button btnHelp = new PButton().helpButton(stage,
                extern ? "Externe Mediensammlungen verwalten" : "Interne Mediensammlungen verwalten",
                extern ? HelpText.EXTERN_MEDIA_COLLECTION : HelpText.INTERN_MEDIA_COLLECTION);

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

            progData.mediaDataList.updateExternalCollection(mediaPathData);
        });

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Die markierte Sammlung wird gelöscht."));
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnDel.setOnAction(a -> {
            delete();
        });

        HBox hBox = new HBox(10);
        if (extern) {
            hBox.getChildren().addAll(btnUpdate);
        }
        hBox.getChildren().addAll(btnDel, PGuiTools.getHBoxGrower(), btnHelp);
        vBox.getChildren().addAll(hBox);
    }

    private void delete() {
        final ObservableList<MediaPathData> sels = tableView.getSelectionModel().getSelectedItems();
        if (sels == null || sels.isEmpty()) {
            PAlert.showInfoNoSelection();
            return;
        }

        if (extern) {
            sels.stream().forEach(mediaPathData -> {
                // todo geht besser
                progData.mediaDataList.removeExternalCollection(mediaPathData.getCollectionName());
            });
            progData.mediaPathDataList.removeAll(sels);

        } else {
            progData.mediaPathDataList.removeAll(sels);
        }
        tableView.getSelectionModel().clearSelection();
    }

    private void makeGrid(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setStyle(PConst.CSS_BACKGROUND_COLOR_GREY);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        if (extern) {
            txtCollectionName.setText(progData.mediaPathDataList.getNextExternCollectionName());
        }

        final Button btnPath = new Button("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Einlesen einer neuen Sammlung auswählen."));
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
            if (extern && txtCollectionName.getText().isEmpty()) {
                txtCollectionName.setText(txtPath.getText());
            }
        });

        final Button btnAdd = new Button("");
        btnAdd.setGraphic(new Icons().ICON_BUTTON_ADD);
        if (extern) {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt und vom angegebenen Pfad eingelesen."));
            btnAdd.disableProperty().bind(txtCollectionName.textProperty().isEmpty()
                    .or(txtPath.textProperty().isEmpty())
                    .or(progData.mediaDataList.searchingProperty()));
        } else {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt."));
            btnAdd.disableProperty().bind(txtPath.textProperty().isEmpty()
                    .or(progData.mediaDataList.searchingProperty()));
        }
        btnAdd.setOnAction(a -> {
            add();
        });

        int row = 0;

        if (extern) {
            gridPane.add(new Label("Eine neue externe Sammlung hinzufügen:"), 0, row, 2, 1);
            gridPane.add(new Label("Name:"), 0, ++row);
            gridPane.add(txtCollectionName, 1, row);
        } else {
            gridPane.add(new Label("Eine neue interne Sammlung hinzufügen:"), 0, row, 2, 1);
        }

        gridPane.add(new Label("Pfad:"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);
        gridPane.add(btnAdd, 3, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().addAll(gridPane);
    }

    private void add() {
        final MediaPathData mediaPathData;
        final String header;
        final String text;

        if (extern) {
            mediaPathData = progData.mediaPathDataList.addExternalMediaPathData(txtPath.getText(), txtCollectionName.getText());
            header = "Sammlung: " + txtCollectionName.getText();
            text = "Eine Sammlung mit dem **Namen** existiert bereits.";
        } else {
            mediaPathData = progData.mediaPathDataList.addInternalMediaPathData(txtPath.getText());
            header = "Sammlung: " + txtPath.getText();
            text = "Eine Sammlung mit dem **Pfad** existiert bereits.";
        }

        if (mediaPathData == null) {
            PAlert.showErrorAlert("Sammlung hinzufügen", header, text);
            return;
        }

        if (extern) {
            progData.mediaDataList.createExternalCollection(mediaPathData.getPath(), mediaPathData.getCollectionName());
            txtCollectionName.setText(progData.mediaPathDataList.getNextExternCollectionName());
        }

        tableView.getSelectionModel().clearSelection();
        tableView.getSelectionModel().select(mediaPathData);
        tableView.scrollTo(mediaPathData);
    }

}
