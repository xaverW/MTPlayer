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


package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.mediadb.MediaCollectionData;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaneMediaDataPath {

    private final TableView<MediaCollectionData> tableView = new TableView<>();
    private final boolean external;
    private final TextField txtCollectionName = new TextField();
    private final TextField txtPath = new TextField();
    private final Button btnPath = new Button("");

    private final ProgData progData;
    private final Stage stage;
    private MediaCollectionData collectionDataOld = null;

    public PaneMediaDataPath(Stage stage, boolean external) {
        this.stage = stage;
        this.external = external;
        this.progData = ProgData.getInstance();
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox(P2LibConst.DIST_VBOX);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        TitledPane tpConfig = new TitledPane(external ? "Externe Medien" : "Interne Medien", vBox);
        result.add(tpConfig);

        initTable(vBox);
        makeButton(vBox);
        makeGrid(vBox);
    }

    public void close() {
        progData.mediaCollectionDataList.getUndoList(true).clear();
        progData.mediaCollectionDataList.getUndoList(false).clear();
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);

        final TableColumn<MediaCollectionData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

        final TableColumn<MediaCollectionData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

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

        txtCollectionName.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        txtPath.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        btnPath.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        tableView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (collectionDataOld != null) {
                txtCollectionName.textProperty().unbindBidirectional(collectionDataOld.collectionNameProperty());
                txtPath.textProperty().unbindBidirectional(collectionDataOld.pathProperty());

                txtCollectionName.setText("");
                txtPath.setText("");
            }
            if (n != null) {
                collectionDataOld = n;
                txtCollectionName.textProperty().bindBidirectional(n.collectionNameProperty());
                txtPath.textProperty().bindBidirectional(n.pathProperty());
            }
        });
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ContextMenu contextMenu = getContextMenu();
                tableView.setContextMenu(contextMenu);
            }
        });

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private ContextMenu getContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> ProgData.getInstance().mediaCollectionDataList.undoData(external));
        miUndo.setDisable(ProgData.getInstance().mediaCollectionDataList.getUndoList(external).isEmpty());
        contextMenu.getItems().addAll(miUndo);
        return contextMenu;
    }

    private void makeButton(VBox vBox) {
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);

        final Button btnHelp = PButton.helpButton(stage,
                external ? "Externe Mediensammlungen verwalten" : "Interne Mediensammlungen verwalten",
                external ? HelpText.EXTERN_MEDIA_COLLECTION : HelpText.INTERN_MEDIA_COLLECTION);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Die markierte Sammlung wird gelöscht."));
        btnDel.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_REMOVE.getImageView());
        btnDel.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems())
                .or(progData.mediaDataList.searchingProperty()));
        btnDel.setOnAction(a -> delete());

        Button btnAdd = new Button("");
        btnAdd.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_ADD.getImageView());
        if (external) {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt und vom angegebenen Pfad eingelesen."));
        } else {
            btnAdd.setTooltip(new Tooltip("Eine neue Sammlung wird angelegt."));
        }
        btnAdd.setOnAction(a -> add());

        Button btnUpdate = new Button("");
        btnUpdate.setTooltip(new Tooltip("Die markierte Sammlung wird neu eingelesen."));
        btnUpdate.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_UPDATE.getImageView());
        btnUpdate.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems())
                .or(progData.mediaDataList.searchingProperty()));
        btnUpdate.setOnAction(a -> {
            update();
        });
        hBox.getChildren().addAll(btnUpdate);

        hBox.getChildren().addAll(btnDel, btnAdd, PGuiTools.getHBoxGrower(), btnHelp);
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
        MediaDataWorker.updateCollection(mediaCollectionData);
    }

    private void delete() {
        final List<MediaCollectionData> sels = new ArrayList<>();
        sels.addAll(tableView.getSelectionModel().getSelectedItems());
        if (sels.isEmpty()) {
            PAlert.showInfoNoSelection();
            return;
        }

        progData.mediaCollectionDataList.addDataToUndoList(sels, external);
        MediaDataWorker.removeMediaCollection(sels);
        tableView.getSelectionModel().clearSelection();
    }

    private void makeGrid(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        btnPath.setTooltip(new Tooltip("Einen Pfad zum Einlesen einer neuen Sammlung auswählen."));
        btnPath.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnPath.setOnAction(event -> {
            PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
            if (txtCollectionName.getText().isEmpty()) {
                txtCollectionName.setText(txtPath.getText());
            }
        });

        int row = 0;
        gridPane.add(new Label("Name:"), 0, row);
        gridPane.add(txtCollectionName, 1, row);
        gridPane.add(new Label("Pfad:"), 0, ++row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnPath, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().addAll(gridPane);
    }

    private void add() {
        final MediaCollectionData mediaCollectionData;
        mediaCollectionData = progData.mediaCollectionDataList.addNewMediaCollectionData(external);
        tableView.getSelectionModel().clearSelection();
        tableView.getSelectionModel().select(mediaCollectionData);
        tableView.scrollTo(mediaCollectionData);
    }
}
