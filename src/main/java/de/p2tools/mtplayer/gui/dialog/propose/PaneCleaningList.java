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

package de.p2tools.mtplayer.gui.dialog.propose;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningData;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningDataList;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptable.P2CellCheckBox;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PaneCleaningList {

    TableView<CleaningData> tableView = new TableView<>();
    private final ProgData progData;
    private final Stage stage;
    private CleaningData cleaningData;
    private final TextField txtClean = new TextField();
    private final CheckBox chkExact = new CheckBox();
    public final CleaningDataList cleaningDataList;

    public PaneCleaningList(Stage stage, boolean propose) {
        this.stage = stage;
        progData = ProgData.getInstance();

        if (propose) {
            cleaningDataList = progData.cleaningDataListPropose;
        } else {
            cleaningDataList = progData.cleaningDataListMedia;
        }
    }

    public void close() {
    }

    public AnchorPane makePane() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
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

        final TableColumn<CleaningData, String> removeColumn = new TableColumn<>("Entfernen");
        removeColumn.setCellValueFactory(new PropertyValueFactory<>("cleaningString"));
        removeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        final TableColumn<CleaningData, Boolean> exactColumn = new TableColumn<>("Immer");
        exactColumn.setCellValueFactory(new PropertyValueFactory<>("always"));
        exactColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        exactColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        final TableColumn<CleaningData, String> utfCodeColumn = new TableColumn<>("UTF-8 Code");
        utfCodeColumn.setCellValueFactory(new PropertyValueFactory<>("codePoint"));
        utfCodeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.40));

        tableView.getColumns().addAll(removeColumn, exactColumn, utfCodeColumn);
        tableView.setMaxHeight(Double.MAX_VALUE);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActData());

        SortedList<CleaningData> sortedList = cleaningDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void addButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<CleaningData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                P2Alert.showInfoNoSelection();
            } else {
                cleaningDataList.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIcons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            CleaningData blackData = new CleaningData();
            cleaningDataList.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        Button btnClear = new Button("_Alle löschen");
        btnClear.setTooltip(new Tooltip("Alle Einträge in der Liste werden gelöscht"));
        btnClear.setOnAction(event -> {
            if (cleaningDataList.size() > 0) {
                if (!P2Alert.showAlertOkCancel(stage, "Liste löschen", "Sollen alle Tabelleneinträge gelöscht werden?",
                        "Die Tabelle wird komplett gelöscht und alle Einträge gehen verloren.")) {
                    return;
                }
            }
            cleaningDataList.clear();
        });

        Button btnAddStandards = new Button("_Standards einfügen");
        btnAddStandards.setTooltip(new Tooltip("Die Standardeinträge der Liste anfügen"));
        btnAddStandards.setOnAction(event -> {
            cleaningDataList.initList();
        });

        HBox hBox1 = new HBox(P2LibConst.PADDING);
        hBox1.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        hBox1.getChildren().addAll(btnNew, btnDel, btnClear, P2GuiTools.getHBoxGrower(), btnAddStandards);
        vBox.getChildren().add(hBox1);
    }

    private void addTextField(VBox vBox) {
        // Gridpane
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        int row = 0;
        gridPane.add(new Label("Entfernen:"), 0, row);
        gridPane.add(txtClean, 1, row);

        gridPane.add(new Label("Immer:"), 0, ++row);
        gridPane.add(chkExact, 1, row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().add(gridPane);
    }

    private void setActData() {
        CleaningData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == cleaningData) {
            return;
        }

        if (cleaningData != null) {
            txtClean.textProperty().unbindBidirectional(cleaningData.cleaningStringProperty());
            txtClean.clear();
            chkExact.selectedProperty().unbindBidirectional(cleaningData.alwaysProperty());
            chkExact.setSelected(false);
        }

        cleaningData = blackDataAct;
        if (cleaningData != null) {
            txtClean.textProperty().bindBidirectional(cleaningData.cleaningStringProperty());
            chkExact.selectedProperty().bindBidirectional(cleaningData.alwaysProperty());
        }
    }
}
