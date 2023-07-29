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

package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.worker.ImportStandardSet;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.PButton;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;

public class PaneSetList extends VBox {

    private final TableView<SetData> tableView = new TableView<>();
    static int newCounter = 1;
    private final Stage stage;
    private final ProgData progData;
    private final ObjectProperty<SetData> setDataObjectProperty;

    public PaneSetList(Stage stage, ObjectProperty<SetData> setDataObjectProperty) {
        this.stage = stage;
        this.setDataObjectProperty = setDataObjectProperty;
        this.progData = ProgData.getInstance();

        make();
    }

    public void close() {
        progData.setDataList.getUndoList().clear();
        progData.setDataList.forEach(setData -> setData.getProgramList().getUndoList().clear());
    }

    public Optional<SetData> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        }
        if (show) {
            PAlert.showInfoNoSelection();
        }
        return Optional.empty();
    }

    private void make() {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);

        initTable(vBox);
        initButton(vBox);

        TitledPane tpSet = new TitledPane("Sets", vBox);
        tpSet.setMaxHeight(Double.MAX_VALUE);
        tpSet.setCollapsible(false);
        VBox.setVgrow(tpSet, Priority.ALWAYS);
        getChildren().addAll(tpSet);
    }

    private void initTable(VBox vBox) {
        vBox.getChildren().addAll(tableView);

        final TableColumn<SetData, String> visibleNameColumn = new TableColumn<>("Name");
        visibleNameColumn.setCellValueFactory(new PropertyValueFactory<>("visibleName"));
        visibleNameColumn.setCellFactory(cellFactoryName);

        tableView.getColumns().addAll(visibleNameColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(progData.setDataList);
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<SetData> optionalSetData = getSel(false);
                SetData setData = optionalSetData.orElse(null);
                ContextMenu contextMenu = new SetDataTableContextMenu(progData).getContextMenu(setData);
                tableView.setContextMenu(contextMenu);
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((u, o, n) ->
                setDataObjectProperty.setValue(n));
        progData.setDataList.listChangedProperty().addListener((u, o, n) -> tableView.refresh());
        setDataObjectProperty.setValue(tableView.getSelectionModel().getSelectedItem());
        tableView.getSelectionModel().selectFirst();
    }

    private void initButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Markiertes Set löschen"));
        btnDel.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            SetData setData = getSelectedSelData();
            if (setData != null) {
                progData.setDataList.addDataToUndoList(setData);
                progData.setDataList.removeSetData(setData);
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Ein neues Set anlegen"));
        btnNew.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            SetData setData = new SetData("Neu-" + ++newCounter);
            progData.setDataList.addSetData(setData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Markiertes Set nach oben schieben"));
        btnUp.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = progData.setDataList.up(sel, true);
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Markiertes Set nach unten schieben"));
        btnDown.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = progData.setDataList.up(sel, false);
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button btnDup = new Button("_Duplizieren");
        btnDup.setTooltip(new Tooltip("Eine Kopie des markierten Sets erstellen"));
        btnDup.setOnAction(event -> {
            SetData setData = getSelectedSelData();
            if (setData != null) {
                progData.setDataList.addSetData(setData.getCopy());
            }
        });
        btnDup.setMaxWidth(Double.MAX_VALUE);

        Button btnNewSet = new Button("Standardsets _anfügen");
        btnNewSet.setTooltip(new Tooltip("Standardsets erstellen und der Liste anfügen"));
        btnNewSet.setOnAction(event -> {
            if (!ImportStandardSet.getStandardSet()) {
                PAlert.showErrorAlert("Set importieren", "Set konnten nicht importiert werden!");
            }
        });
        btnNewSet.setMaxWidth(Double.MAX_VALUE);

        Button btnCheck = new Button("_Prüfen");
        btnCheck.setTooltip(new Tooltip("Die angelegten Sets überprüfen"));
        btnCheck.setOnAction(event -> SetFactory.checkPrograms(progData));
        btnCheck.setMaxWidth(Double.MAX_VALUE);

        final Button btnHelp = PButton.helpButton(stage, "Set", HelpTextPset.HELP_PSET);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnNew, btnDel, P2GuiTools.getHBoxGrower(), btnUp, btnDown);

        HBox hBoxHelp = new HBox();
        hBoxHelp.setAlignment(Pos.CENTER_RIGHT);
        hBoxHelp.getChildren().add(btnHelp);

        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(hBoxButton, btnDup, btnNewSet, btnCheck, P2GuiTools.getVBoxGrower(), hBoxHelp);
        VBox.setVgrow(vb, Priority.ALWAYS);
        vBox.getChildren().addAll(vb);
    }

    private int getSelectedLine() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }

    private SetData getSelectedSelData() {
        final SetData sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }

    private Callback<TableColumn<SetData, String>, TableCell<SetData, String>> cellFactoryName
            = (final TableColumn<SetData, String> param) -> {

        final TableCell<SetData, String> cell = new TableCell<SetData, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                SetData setData = getTableView().getItems().get(getIndex());
                Label lbl = new Label(setData.getVisibleName());
                if (setData.isPlay()) {
                    lbl.getStyleClass().add("markSetPlay");
                } else {
                    lbl.getStyleClass().removeAll("markSetPlay");
                }
                setGraphic(lbl);
            }
        };
        return cell;
    };
}