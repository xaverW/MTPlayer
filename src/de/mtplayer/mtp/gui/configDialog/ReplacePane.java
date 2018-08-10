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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.ReplaceData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class ReplacePane {

    TableView<ReplaceData> tableView = new TableView<>();

    BooleanProperty propAscii = ProgConfig.SYSTEM_ONLY_ASCII.getBooleanProperty();
    BooleanProperty propReplace = ProgConfig.SYSTEM_USE_REPLACETABLE.getBooleanProperty();
    private final TextField txtFrom = new TextField();
    private final TextField txtTo = new TextField();
    private final GridPane gridPane = new GridPane();
    private ReplaceData replaceData = null;
    private final Stage stage;

    public ReplacePane(Stage stage) {
        this.stage = stage;
    }

    public void makeReplaceListTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeAscii(vBox);
        initTable(vBox);
        addConfigs(vBox);

        TitledPane tpReplace = new TitledPane("Ersetzungstabelle", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpReplace, Priority.ALWAYS);
    }


    private void makeAscii(VBox vBox) {

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        vBox.getChildren().add(gridPane);

        final PToggleSwitch tglAscii = new PToggleSwitch("nur ASCII-Zeichen erlauben");
        tglAscii.selectedProperty().bindBidirectional(propAscii);

        final Button btnHelpAscii = new PButton().helpButton(stage, "Nur ASCII-Zeichen",
                HelpText.DOWNLOAD_ONLY_ASCII);

        final PToggleSwitch tglReplace = new PToggleSwitch("Ersetzungstabelle");
        tglReplace.selectedProperty().bindBidirectional(propReplace);

        final Button btnHelpReplace = new PButton().helpButton(stage, "Ersetzungstabelle",
                HelpText.DOWNLOAD_REPLACELIST);


        gridPane.add(tglAscii, 0, 0);
        GridPane.setHalignment(btnHelpAscii, HPos.RIGHT);
        gridPane.add(btnHelpAscii, 2, 0);

        gridPane.add(tglReplace, 0, 1);
        GridPane.setHalignment(btnHelpReplace, HPos.RIGHT);
        gridPane.add(btnHelpReplace, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
    }


    private void initTable(VBox vBox) {
        final TableColumn<ReplaceData, String> fromColumn = new TableColumn<>("Von");
        fromColumn.setEditable(true);
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));

        final TableColumn<ReplaceData, String> toColumn = new TableColumn<>("Nach");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(fromColumn, toColumn);
        tableView.setItems(ProgData.getInstance().replaceList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActReplaceData));

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

        Button btnDel = new Button("");
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            final ObservableList<ReplaceData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                ProgData.getInstance().replaceList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            ReplaceData replaceData = new ReplaceData();
            ProgData.getInstance().replaceList.add(replaceData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(replaceData);
            tableView.scrollTo(replaceData);
        });

        Button up = new Button("");
        up.setGraphic(new Icons().ICON_BUTTON_MOVE_UP);
        up.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button down = new Button("");
        down.setGraphic(new Icons().ICON_BUTTON_MOVE_DOWN);
        down.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button reset = new Button("Tabelle zurücksetzen");
        reset.setOnAction(event -> {
            ProgData.getInstance().replaceList.init();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNew, btnDel, up, down, reset);
        vBox.getChildren().addAll(hBox);

    }

    private void addConfigs(VBox vBox) {
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        gridPane.add(new Label("Von: "), 0, 0);
        gridPane.add(txtFrom, 1, 0);
        gridPane.add(new Label("Nach: "), 0, 1);
        gridPane.add(txtTo, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
    }

    private void setActReplaceData() {
        ReplaceData replaceDataAct = tableView.getSelectionModel().getSelectedItem();
        if (replaceDataAct == replaceData) {
            return;
        }

        if (replaceData != null) {
            txtFrom.textProperty().unbindBidirectional(replaceData.fromProperty());
            txtTo.textProperty().unbindBidirectional(replaceData.toProperty());
        }

        replaceData = replaceDataAct;
        gridPane.setDisable(replaceData == null);
        if (replaceData != null) {
            txtFrom.textProperty().bindBidirectional(replaceData.fromProperty());
            txtTo.textProperty().bindBidirectional(replaceData.toProperty());
        }
    }

}
