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


package de.p2tools.mtplayer.gui.mediaConfig;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.mediaDb.MediaCollectionData;
import de.p2tools.mtplayer.controller.mediaDb.MediaDataWorker;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import javafx.beans.binding.Bindings;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PanePath {

    private final TableView<MediaCollectionData> tableView = new TableView<>();
    private final boolean external;
    private final TextField txtPath = new TextField();
    private final TextField txtCollectionName = new TextField();
    private final Button btnAdd = new Button("");

    private final ProgData progData;
    private final Stage stage;

    public PanePath(Stage stage, boolean external) {
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

    public void close() {
        btnAdd.disableProperty().unbind();
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
        pathColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        final TableColumn<MediaCollectionData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        countColumn.getStyleClass().add("alignCenterRightPadding_25");

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
        HBox hBox = new HBox(10);

        final Button btnHelp = PButton.helpButton(stage,
                external ? "Externe Mediensammlungen verwalten" : "Interne Mediensammlungen verwalten",
                external ? HelpText.EXTERN_MEDIA_COLLECTION : HelpText.INTERN_MEDIA_COLLECTION);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Die markierte Sammlung wird gelöscht."));
        btnDel.setGraphic(new ProgIcons().ICON_BUTTON_REMOVE);
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems())
                .or(progData.mediaDataList.searchingProperty()));
        btnDel.setOnAction(a -> delete());

        if (external) {
            Button btnUpdate = new Button("");
            btnUpdate.setTooltip(new Tooltip("Die markierte Sammlung wird neu eingelesen."));
            btnUpdate.setGraphic(new ProgIcons().ICON_BUTTON_UPDATE);
            btnUpdate.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems())
                    .or(progData.mediaDataList.searchingProperty()));

            btnUpdate.setOnAction(a -> {
                update();
            });
            hBox.getChildren().addAll(btnUpdate);
        }

        hBox.getChildren().addAll(btnDel, PGuiTools.getHBoxGrower(), btnHelp);
        vBox.getChildren().addAll(hBox);
    }

    private void update() {
        MediaCollectionData mediaCollectionData = tableView.getSelectionModel().getSelectedItem();

        File file = new File(mediaCollectionData.getPath());
        if (!file.exists()) {
            PAlert.showErrorAlert("Pfad existiert nicht!", "Der Pfad der Sammlung:" + P2LibConst.LINE_SEPARATOR +
                    mediaCollectionData.getPath() + P2LibConst.LINE_SEPARATOR +
                    "existiert nicht. Die Sammlung kann nicht eingelesen werden");
            return;
        }

        MediaDataWorker.updateExternalCollection(mediaCollectionData);
    }

    private void delete() {
        final List<MediaCollectionData> sels = new ArrayList<>();
        sels.addAll(tableView.getSelectionModel().getSelectedItems());

        if (sels.isEmpty()) {
            PAlert.showInfoNoSelection();
            return;
        }

        List<Long> idList = new ArrayList<>();
        sels.stream().forEach(mediaPathData -> {
            idList.add(mediaPathData.getId());
        });
        MediaDataWorker.removeMediaCollection(idList);

        tableView.getSelectionModel().clearSelection();
    }

    private void makeGrid(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        txtCollectionName.setText(progData.mediaCollectionDataList.getNextMediaCollectionName(external));

        final Button btnPath = new Button("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Einlesen einer neuen Sammlung auswählen."));
        btnPath.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnPath.setOnAction(event -> {
            PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
            if (txtCollectionName.getText().isEmpty()) {
                txtCollectionName.setText(txtPath.getText());
            }
        });

        btnAdd.setGraphic(new ProgIcons().ICON_BUTTON_ADD);
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

        header = "Sammlung: " + txtPath.getText();
        if (!external && progData.mediaCollectionDataList.getMediaCollectionData(txtPath.getText(), external) != null) {
            text = "Eine Sammlung mit dem **Pfad** existiert bereits.";
            PAlert.showErrorAlert(stage, "Sammlung hinzufügen", header, text);
            return;
        }

        if (!PFileUtils.fileIsDirectoryExist(txtPath.getText())) {
            PAlert.showErrorAlert(stage, header, "Das angegebene Verzeichnis existiert nicht.");
            return;
        }

        mediaCollectionData = progData.mediaCollectionDataList.addNewMediaCollectionData(txtPath.getText(), txtCollectionName.getText(), external);
        if (external) {
            MediaDataWorker.createExternalCollection(mediaCollectionData);
        }

        txtCollectionName.setText(progData.mediaCollectionDataList.getNextMediaCollectionName(external));

        tableView.getSelectionModel().clearSelection();
        tableView.getSelectionModel().select(mediaCollectionData);
        tableView.scrollTo(mediaCollectionData);
    }

}
