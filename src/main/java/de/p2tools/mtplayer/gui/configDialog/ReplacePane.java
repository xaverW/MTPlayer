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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.ReplaceData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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

    private final TextField txtFrom = new TextField();
    private final TextField txtTo = new TextField();
    private final GridPane gridPane = new GridPane();

    private TableView<ReplaceData> tableView = new TableView<>();
    private ObjectProperty<ReplaceData> replaceDateProp = new SimpleObjectProperty<>(null);
    private final PToggleSwitch tglAscii = new PToggleSwitch("nur ASCII-Zeichen erlauben");
    private final PToggleSwitch tglReplace = new PToggleSwitch("Ersetzungstabelle");

    private final Stage stage;


    public ReplacePane(Stage stage) {
        this.stage = stage;
    }

    public void makeReplaceListTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        makeAscii(vBox);
        initTable(vBox);
        addConfigs(vBox);

        TitledPane tpReplace = new TitledPane("Ersetzungstabelle", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpReplace, Priority.ALWAYS);
    }

    public void close() {
        unbindText();
        tglAscii.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_ONLY_ASCII);
        tglReplace.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_REPLACETABLE);
    }

    private void makeAscii(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        vBox.getChildren().add(gridPane);

        tglAscii.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_ONLY_ASCII);
        final Button btnHelpAscii = PButton.helpButton(stage, "Nur ASCII-Zeichen",
                HelpText.DOWNLOAD_ONLY_ASCII);

        tglReplace.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_REPLACETABLE);
        final Button btnHelpReplace = PButton.helpButton(stage, "Ersetzungstabelle",
                HelpText.DOWNLOAD_REPLACELIST);

        gridPane.add(tglAscii, 0, 0);
        gridPane.add(btnHelpAscii, 1, 0);

        gridPane.add(tglReplace, 0, 1);
        gridPane.add(btnHelpReplace, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }


    private void initTable(VBox vBox) {
        final TableColumn<ReplaceData, String> fromColumn = new TableColumn<>("Von");
        fromColumn.setEditable(true);
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));

        final TableColumn<ReplaceData, String> toColumn = new TableColumn<>("Nach");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(fromColumn, toColumn);
        tableView.setItems(ProgData.getInstance().replaceList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActReplaceData));

        tableView.disableProperty().bind(ProgConfig.SYSTEM_USE_REPLACETABLE.not());
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Eintrag löschen"));
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
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
        btnNew.setTooltip(new Tooltip("Einen neuen Eintrag erstellen"));
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            ReplaceData replaceData = new ReplaceData();
            ProgData.getInstance().replaceList.add(replaceData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(replaceData);
            tableView.scrollTo(replaceData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Eintrag nach oben schieben"));
        btnUp.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Eintrag nach unten schieben"));
        btnDown.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnTop = new Button();
        btnTop.setTooltip(new Tooltip("Eintrag an den Anfang verschieben"));
        btnTop.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_TOP.getImageView());
        btnTop.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.top(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnBottom = new Button();
        btnBottom.setTooltip(new Tooltip("Eintrag an das Ende verschieben"));
        btnBottom.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_BOTTOM.getImageView());
        btnBottom.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.top(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnReset = new Button("_Tabelle zurücksetzen");
        btnReset.setTooltip(new Tooltip("Alle Einträge löschen und Standardeinträge wieder herstellen"));
        btnReset.setOnAction(event -> {
            ProgData.getInstance().replaceList.init();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.disableProperty().bind(ProgConfig.SYSTEM_USE_REPLACETABLE.not());
        hBox.getChildren().addAll(btnNew, btnDel, btnTop, btnUp, btnDown, btnBottom, btnReset);
        vBox.getChildren().addAll(hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));

        gridPane.add(new Label("Von: "), 0, 0);
        gridPane.add(txtFrom, 1, 0);
        gridPane.add(new Label("Nach: "), 0, 1);
        gridPane.add(txtTo, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(
                Bindings.createBooleanBinding(() -> replaceDateProp.getValue() == null, replaceDateProp)
                        .or(ProgConfig.SYSTEM_USE_REPLACETABLE.not()));
    }

    private void setActReplaceData() {
        ReplaceData replaceDataAct = tableView.getSelectionModel().getSelectedItem();
        if (replaceDataAct == replaceDateProp.getValue()) {
            return;
        }

        unbindText();
        replaceDateProp.setValue(replaceDataAct);

        if (replaceDateProp.getValue() != null) {
            txtFrom.textProperty().bindBidirectional(replaceDateProp.getValue().fromProperty());
            txtTo.textProperty().bindBidirectional(replaceDateProp.getValue().toProperty());
        }
    }

    private void unbindText() {
        if (replaceDateProp.getValue() != null) {
            txtFrom.textProperty().unbindBidirectional(replaceDateProp.getValue().fromProperty());
            txtTo.textProperty().unbindBidirectional(replaceDateProp.getValue().toProperty());
        }
        txtFrom.setText("");
        txtTo.setText("");
    }
}
