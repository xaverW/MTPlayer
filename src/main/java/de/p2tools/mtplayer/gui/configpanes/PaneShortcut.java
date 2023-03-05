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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.MTShortcut;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.tools.log.PLog;
import de.p2tools.p2lib.tools.shortcut.PShortcut;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.Collection;

public class PaneShortcut {
    private boolean released = true; // damits beim ersten Mal schon passt
    private String newShortcutValue = "";
    private final TextArea txtLongDescription = new TextArea();
    private final TableView<PShortcut> tableView = new TableView<>();

    private final PDialog pDialog;

    public PaneShortcut(PDialog pDialog) {
        this.pDialog = pDialog;
    }

    public void close() {
    }

    public void makeShortcut(Collection<TitledPane> result) {
        final Button btnHelp = PButton.helpButton(pDialog.getStage(), "Tastenkürzel ändern",
                HelpText.SHORTCUT);

        SplitPane splitPane = new SplitPane();

        initTable(tableView);

        txtLongDescription.setMinHeight(ProgConst.MIN_TEXTAREA_HEIGHT_LOW);
        txtLongDescription.setPrefHeight(ProgConst.MIN_TEXTAREA_HEIGHT_LOW);
        txtLongDescription.setEditable(false);
        txtLongDescription.setWrapText(true);
        txtLongDescription.setPrefRowCount(2);

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(tableView, Boolean.TRUE);
        SplitPane.setResizableWithParent(txtLongDescription, Boolean.FALSE);
        splitPane.getItems().addAll(tableView, txtLongDescription);

        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setMaxHeight(Double.MAX_VALUE);
        hBox.getChildren().addAll(splitPane, btnHelp);
        HBox.setHgrow(splitPane, Priority.ALWAYS);

        TitledPane tpShortcut = new TitledPane("Tastenkürzel", hBox);
        result.add(tpShortcut);
    }

    private void initTable(TableView<PShortcut> tableView) {
        final TableColumn<PShortcut, String> descriptionColumn = new TableColumn<>("Beschreibung");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<PShortcut, String> actShortcutColumn = new TableColumn<>("Tastenkürzel");
        actShortcutColumn.setCellValueFactory(new PropertyValueFactory<>("actShortcut"));
        actShortcutColumn.getStyleClass().add("alignCenter");

        final TableColumn<PShortcut, String> changeColumn = new TableColumn<>("");
        changeColumn.getStyleClass().add("alignCenter");
        changeColumn.setCellFactory(cellFactoryChange);

        final TableColumn<PShortcut, String> resetColumn = new TableColumn<>("");
        resetColumn.getStyleClass().add("alignCenter");
        resetColumn.setCellFactory(cellFactoryReset);

        final TableColumn<PShortcut, String> orgShortcutColumn = new TableColumn<>("Original");
        orgShortcutColumn.setCellValueFactory(new PropertyValueFactory<>("orgShortcut"));
        orgShortcutColumn.getStyleClass().add("alignCenter");

        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        descriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        actShortcutColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));
        changeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));
        resetColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));
        orgShortcutColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));

        tableView.getColumns().addAll(descriptionColumn, actShortcutColumn, changeColumn, resetColumn, orgShortcutColumn);
        tableView.setItems(MTShortcut.getShortcutList());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActReplaceData));

    }

    private void setActReplaceData() {
        PShortcut pShortcutAct = tableView.getSelectionModel().getSelectedItem();
        txtLongDescription.setDisable(pShortcutAct == null);
        if (pShortcutAct != null) {
            txtLongDescription.setText(pShortcutAct.getLongDescription());
        } else {
            txtLongDescription.setText("");
        }
    }

    private Callback<TableColumn<PShortcut, String>, TableCell<PShortcut, String>> cellFactoryChange
            = (final TableColumn<PShortcut, String> param) -> {

        final TableCell<PShortcut, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                PShortcut pShortcut = getTableView().getItems().get(getIndex());
                newShortcutValue = pShortcut.getActShortcut();

                final Button btnChange = new Button("Ändern");
                btnChange.setTooltip(new Tooltip("Button klicken und dann das neue Tastenkürzel eingeben"));
                btnChange.setOnAction(a -> getTableView().getSelectionModel().select(getIndex()));
                btnChange.addEventFilter(KeyEvent.KEY_RELEASED, ke -> {
                    released = true;
                    if (newShortcutValue.isEmpty()) {
                        PLog.sysLog("Shortcut: nicht ändern");
                        return;
                    }

                    //neu setzen
                    PLog.sysLog("Shortcut: " + pShortcut.getDescription() + " ändern von: " + pShortcut.getActShortcut() + " nach: " + newShortcutValue);
                    pShortcut.setActShortcut(newShortcutValue);

                    //Prüfen auf Doppelte
                    if (MTShortcut.checkDoubleShortcutList()) {
                        PAlert.showErrorAlert("Tastenkürzel", "das angegebene Tastenkürzel " +
                                "wird zweimal verwendet.");
                    }
                });
                btnChange.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
                    if (released) {
                        released = false;
                        newShortcutValue = "";
                    }

                    if (newShortcutValue.isEmpty() &&
                            !ke.getCode().equals(KeyCode.ALT) &&
                            !ke.getCode().equals(KeyCode.ALT_GRAPH) &&
                            !ke.getCode().equals(KeyCode.CONTROL) &&
                            !ke.getCode().equals(KeyCode.META) &&
                            !ke.getCode().equals(KeyCode.SHIFT) &&
                            !ke.getCode().equals(KeyCode.WINDOWS)) {
                        // dann ist ein neuer Versuch und muss ein Steuerzeichen enthalten
                        newShortcutValue = "";

                    } else {
                        if (newShortcutValue.isEmpty()) {
                            newShortcutValue = ke.getCode().getName();
                        } else {
                            newShortcutValue = newShortcutValue + "+" + ke.getCode().getName();
                        }
                    }
                    ke.consume();
                });

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));
                hbox.getChildren().addAll(btnChange);
                setGraphic(hbox);
            }
        };

        return cell;
    };

    private Callback<TableColumn<PShortcut, String>, TableCell<PShortcut, String>> cellFactoryReset
            = (final TableColumn<PShortcut, String> param) -> {

        final TableCell<PShortcut, String> cell = new TableCell<PShortcut, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                PShortcut pShortcut = getTableView().getItems().get(getIndex());

                final Button btnResete = new Button("Zurücksetzen");
                btnResete.setTooltip(new Tooltip("Ein Klick setzt wieder das Original Tastenkürzel"));
                btnResete.setOnAction(a -> {
                    getTableView().getSelectionModel().select(getIndex());
                    pShortcut.resetShortcut();
                });

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));
                hbox.getChildren().add(btnResete);
                setGraphic(hbox);
            }
        };

        return cell;
    };
}
