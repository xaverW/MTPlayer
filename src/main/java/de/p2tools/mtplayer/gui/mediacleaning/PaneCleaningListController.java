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

package de.p2tools.mtplayer.gui.mediacleaning;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.mediacleaningdata.MediaCleaningData;
import de.p2tools.mtplayer.controller.mediadb.MediaCleaningFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PGuiTools;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneCleaningListController {

    TableView<MediaCleaningData> tableView = new TableView<>();
    private final ProgData progData;
    private final Stage stage;
    private MediaCleaningData mediaCleaningData;
    private TextField txtClean = new TextField();


    public PaneCleaningListController(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
    }

    public AnchorPane makePane() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));
        makePane(vBox);
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        anchorPane.getChildren().add(vBox);
        return anchorPane;
    }

    private void makePane(VBox vBox) {
        initTable(vBox);
        addButton(vBox);
        addTextField(vBox);
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final TableColumn<MediaCleaningData, String> removeColumn = new TableColumn<>("Entfernen");
        removeColumn.setCellValueFactory(new PropertyValueFactory<>("cleaningData"));
        removeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45));

        final TableColumn<MediaCleaningData, String> utfCodeColumn = new TableColumn<>("UTF-8 Code");
        utfCodeColumn.setCellValueFactory(new PropertyValueFactory<>("codePoint"));
        utfCodeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45));

        tableView.getColumns().addAll(removeColumn, utfCodeColumn);
        SortedList<MediaCleaningData> sortedList = progData.mediaCleaningList.getSortedList();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        tableView.setMaxHeight(Double.MAX_VALUE);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActData());

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void addButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<MediaCleaningData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                progData.mediaCleaningList.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            MediaCleaningData blackData = new MediaCleaningData();
            progData.mediaCleaningList.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        Button btnClear = new Button("_Alle löschen");
        btnClear.setTooltip(new Tooltip("Alle Einträge in der Liste werden gelöscht"));
        btnClear.setOnAction(event -> {
            if (progData.mediaCleaningList.size() > 0) {
                if (!PAlert.showAlertOkCancel(stage, "Liste löschen", "Sollen alle Tabelleneinträge gelöscht werden?",
                        "Die Tabelle wird komplett gelöscht und alle Einträge gehen verloren.")) {
                    return;
                }
            }
            progData.mediaCleaningList.clear();
        });

        Button btnAddStandards = new Button("_Standards einfügen");
        btnAddStandards.setTooltip(new Tooltip("Die Standardeinträge der Liste anfügen"));
        btnAddStandards.setOnAction(event -> {
            MediaCleaningFactory.initMediaCleaningList(progData.mediaCleaningList);
        });

        HBox hBox1 = new HBox(P2LibConst.DIST_EDGE);
        hBox1.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        hBox1.getChildren().addAll(btnNew, btnDel, btnClear, PGuiTools.getHBoxGrower(), btnAddStandards);
        vBox.getChildren().add(hBox1);
    }

    private void addTextField(VBox vBox) {
        HBox hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(txtClean, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Entfernen:"), txtClean);
        vBox.getChildren().add(hBox);
    }

    private void setActData() {
        MediaCleaningData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == mediaCleaningData) {
            return;
        }

        if (mediaCleaningData != null) {
            txtClean.textProperty().unbindBidirectional(mediaCleaningData.cleaningDataProperty());
            txtClean.clear();
        }

        mediaCleaningData = blackDataAct;
        if (mediaCleaningData != null) {
            txtClean.textProperty().bindBidirectional(mediaCleaningData.cleaningDataProperty());
        }
    }
}
