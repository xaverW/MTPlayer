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
import de.mtplayer.mtp.controller.mediaDb.MediaCollectionData;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collection;

public class MediaConfigPanePath {

    private final ProgData progData;
    private final TableView<MediaCollectionData> tableView = new TableView<>();
    private final Stage stage;
    private final boolean external;
    private final TextField txtPath = new TextField();
    private final TextField txtCollectionName = new TextField();


    public MediaConfigPanePath(Stage stage, boolean external) {
        this.stage = stage;
        this.external = external;
        this.progData = ProgData.getInstance();
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);

        TitledPane tpConfig = new TitledPane(external ? "Externe Medien" : "Interne Medien", vBox);
        result.add(tpConfig);

        initTable(vBox);
        makeButton(vBox);
        makeGrid(vBox);
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);

        final TableColumn<MediaCollectionData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<MediaCollectionData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaCollectionData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        countColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));

        tableView.getColumns().addAll(nameColumn, pathColumn, countColumn);


        SortedList<MediaCollectionData> sortedList;
        if (external) {
            sortedList = progData.mediaCollectionDataList.getSortedListExternal();
        } else {
            sortedList = progData.mediaCollectionDataList.getSortedListInternal();
        }
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void makeButton(VBox vBox) {
        final Button btnHelp = new PButton().helpButton(stage,
                external ? "Externe Mediensammlungen verwalten" : "Interne Mediensammlungen verwalten",
                external ? HelpText.EXTERN_MEDIA_COLLECTION : HelpText.INTERN_MEDIA_COLLECTION);

        Button btnUpdate = new Button("");
        btnUpdate.setTooltip(new Tooltip("Die markierte Sammlung wird neu eingelesen."));
        btnUpdate.setGraphic(new Icons().ICON_BUTTON_UPDATE);
        btnUpdate.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnUpdate.setOnAction(a -> {
            MediaCollectionData mediaCollectionData = tableView.getSelectionModel().getSelectedItem();

            File file = new File(mediaCollectionData.getPath());
            if (!file.exists()) {
                PAlert.showErrorAlert("Pfad existiert nicht!", "Der Pfad der Sammlung:" + PConst.LINE_SEPARATOR +
                        mediaCollectionData.getPath() + PConst.LINE_SEPARATOR +
                        "existiert nicht. Die Sammlung kann nicht eingelesen werden");
                return;
            }

            progData.mediaDataList.updateExternalCollection(mediaCollectionData);
        });

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Die markierte Sammlung wird gelöscht."));
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        btnDel.setOnAction(a -> delete());

        HBox hBox = new HBox(10);
        if (external) {
            hBox.getChildren().addAll(btnUpdate);
        }
        hBox.getChildren().addAll(btnDel, PGuiTools.getHBoxGrower(), btnHelp);
        vBox.getChildren().addAll(hBox);
    }

    private void delete() {
        final ObservableList<MediaCollectionData> sels = tableView.getSelectionModel().getSelectedItems();
        if (sels == null || sels.isEmpty()) {
            PAlert.showInfoNoSelection();
            return;
        }

        sels.stream().forEach(mediaPathData -> {
            progData.mediaDataList.removeMediaAndCollection(mediaPathData.getId());
        });
        tableView.getSelectionModel().clearSelection();
    }

    private void makeGrid(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setStyle(PConst.CSS_BACKGROUND_COLOR_GREY);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        txtCollectionName.setText(progData.mediaCollectionDataList.getNextCollectionName(external));

        final Button btnPath = new Button("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Einlesen einer neuen Sammlung auswählen."));
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
            if (txtCollectionName.getText().isEmpty()) {
                txtCollectionName.setText(txtPath.getText());
            }
        });

        final Button btnAdd = new Button("");
        btnAdd.setGraphic(new Icons().ICON_BUTTON_ADD);
        if (external) {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt und vom angegebenen Pfad eingelesen."));
        } else {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt."));
        }
        btnAdd.disableProperty().bind(txtPath.textProperty().isEmpty().or(progData.mediaDataList.searchingProperty()));
        btnAdd.setOnAction(a -> add());

        int row = 0;
        if (external) {
            gridPane.add(new Label("Eine neue externe Sammlung hinzufügen:"), 0, row, 2, 1);
        } else {
            gridPane.add(new Label("Eine neue interne Sammlung hinzufügen:"), 0, row, 2, 1);
        }
        gridPane.add(new Label("Name:"), 0, ++row);
        gridPane.add(txtCollectionName, 1, row);
        gridPane.add(new Label("Pfad:"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);
        gridPane.add(btnAdd, 3, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().addAll(gridPane);
    }

    private void add() {
        final MediaCollectionData mediaCollectionData;
        final String header;
        final String text;

        if (!external && null != progData.mediaCollectionDataList.getMediaCollectionData(txtPath.getText(), external)) {
            header = "Sammlung: " + txtPath.getText();
            text = "Eine Sammlung mit dem **Pfad** existiert bereits.";
            PAlert.showErrorAlert("Sammlung hinzufügen", header, text);
            return;
        }

        mediaCollectionData = progData.mediaCollectionDataList.addNewMediaCollectionData(txtPath.getText(), txtCollectionName.getText(), external);

        if (external) {
            progData.mediaDataList.createExternalCollection(mediaCollectionData);
        }

        txtCollectionName.setText(progData.mediaCollectionDataList.getNextCollectionName(external));

        tableView.getSelectionModel().clearSelection();
        tableView.getSelectionModel().select(mediaCollectionData);
        tableView.scrollTo(mediaCollectionData);
    }

}
