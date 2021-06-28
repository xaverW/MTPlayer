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

package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ListePsetVorlagen;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;

public class SetPaneController extends AnchorPane {

    private final ProgData progData;

    private final Accordion accordion = new Accordion();
    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox vBox = new VBox();
    private final TableView<SetData> tableView = new TableView<>();
    private final ToggleGroup toggleGroup = new ToggleGroup();

    static int newCounter = 1;
    private SetDataPane setDataPane;
    private Collection<TitledPane> setDataPaneTitle;
    private final Stage stage;
    DoubleProperty split = ProgConfig.CONFIG_DIALOG_SET_DIVIDER.getDoubleProperty();

    public SetPaneController(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();

        splitPane.setOrientation(Orientation.VERTICAL);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        vBox.getChildren().addAll(createSetList());
        splitPane.getItems().addAll(vBox, scrollPane);
        splitPane.getItems().get(0).autosize();
        SplitPane.setResizableWithParent(vBox, Boolean.FALSE);
        getChildren().addAll(splitPane);

        createSetDataPane();
        selectTableFirst();

        accordion.getPanes().addAll(setDataPaneTitle);
        scrollPane.setContent(accordion);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(split);
    }

    public void close() {
        splitPane.getDividers().get(0).positionProperty().unbindBidirectional(split);
    }

    public void selectTableFirst() {
        tableView.getSelectionModel().selectFirst();
    }

    private Collection<TitledPane> createSetList() {
        Collection<TitledPane> result = new ArrayList<>();
        makeSetListTable(result);
        return result;
    }

    private void createSetDataPane() {
        setDataPaneTitle = new ArrayList<>();
        setDataPane = new SetDataPane(stage);
        setDataPane.makeSetPane(setDataPaneTitle);
    }

    double size = 0;
    double div = 0;

    private void makeSetListTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        initTable(vBox);

        TitledPane tpSet = new TitledPane("Sets", vBox);
        tpSet.setCollapsible(true);
        tpSet.expandedProperty().addListener((nn, n, i) -> {
            if (tpSet.isExpanded()) {
                this.vBox.setMaxHeight(Double.MAX_VALUE);
//                this.tableView.setMinHeight(size);
//                this.tableView.setPrefHeight(size);
//                this.tableView.resize(this.tableView.getWidth(), size);
                splitPane.getDividers().get(0).setPosition(div);
            } else {
                div = splitPane.getDividers().get(0).getPosition();
                size = tableView.getHeight();
                this.vBox.setMaxHeight(0);
            }
        });
        result.add(tpSet);
        VBox.setVgrow(tpSet, Priority.ALWAYS);
        tpSet.setMaxHeight(Double.MAX_VALUE);
    }


    private void initTable(VBox vBox) {
        HBox hBoxTable = new HBox(10);
        VBox vBoxButton = new VBox(10);
        HBox hBoxButton = new HBox(10);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(hBoxTable, Priority.ALWAYS);
        vBoxButton.getChildren().add(hBoxButton);
        hBoxTable.getChildren().addAll(tableView, vBoxButton);
        vBox.getChildren().addAll(hBoxTable);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setDataPane.bindProgData(newValue);
        });

        final TableColumn<SetData, String> visibleNameColumn = new TableColumn<>("Name");
        visibleNameColumn.setCellValueFactory(new PropertyValueFactory<>("visibleName"));
        visibleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn()); //todo muss eindeutig sein

        final TableColumn<SetData, Boolean> playColumn = new TableColumn<>("Abspielen");
        playColumn.setCellValueFactory(new PropertyValueFactory<>("play"));
        playColumn.setCellFactory(cellFactoryPlay);
        playColumn.getStyleClass().add("center");

        final TableColumn<SetData, Boolean> saveColumn = new TableColumn<>("Speichern");
        saveColumn.setCellValueFactory(new PropertyValueFactory<>("save"));
        saveColumn.setCellFactory(cellFactorySave);
        saveColumn.getStyleClass().add("center");

        final TableColumn<SetData, Boolean> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("abo"));
        aboColumn.setCellFactory(cellFactoryAbo);
        aboColumn.getStyleClass().add("center");

        final TableColumn<SetData, Boolean> buttonColumn = new TableColumn<>("Button");
        buttonColumn.setCellValueFactory(new PropertyValueFactory<>("button"));
        buttonColumn.setCellFactory(cellFactoryButton);
        buttonColumn.getStyleClass().add("center");

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(visibleNameColumn, playColumn, saveColumn, aboColumn, buttonColumn);
        tableView.setItems(progData.setDataList);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Markiertes Set löschen"));
        btnDel.setGraphic(new ProgIcons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            SetData setData = getSelectedSelData();
            if (setData != null) {
                progData.setDataList.removeSetData(setData);
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Ein neues Set anlegen"));
        btnNew.setGraphic(new ProgIcons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            SetData setData = new SetData("Neu-" + ++newCounter);
            progData.setDataList.addSetData(setData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Markiertes Set nach oben schieben"));
        btnUp.setGraphic(new ProgIcons().ICON_BUTTON_MOVE_UP);
        btnUp.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = progData.setDataList.up(sel, true);
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Markiertes Set nach unten schieben"));
        btnDown.setGraphic(new ProgIcons().ICON_BUTTON_MOVE_DOWN);
        btnDown.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = progData.setDataList.up(sel, false);
                tableView.getSelectionModel().select(newSel);
            }
        });

        final Button btnHelp = PButton.helpButton(stage, "Set", HelpTextPset.HELP_PSET);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(btnNew, btnDel, btnUp, btnDown);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBoxButton.getChildren().addAll(hBox, btnHelp);


        Button btnDup = new Button("_Duplizieren");
        btnDup.setTooltip(new Tooltip("Eine Kopie des markierten Sets erstellen"));
        btnDup.setOnAction(event -> {
            SetData setData = getSelectedSelData();
            if (setData != null) {
                progData.setDataList.addSetData(setData.copy());
            }
        });
        HBox.setHgrow(btnDup, Priority.ALWAYS);
        btnDup.setMaxWidth(Double.MAX_VALUE);

        Button btnNewSet = new Button("Standardsets _anfügen");
        btnNewSet.setTooltip(new Tooltip("Standardsets erstellen und der Liste anfügen"));
        btnNewSet.setOnAction(event -> {
            if (!SetsPrograms.addSetTemplate(ListePsetVorlagen.getStandarset(true /*replaceMuster*/))) {
                PAlert.showErrorAlert("Set importieren", "Set konnten nicht importiert werden!");
            }
        });
        HBox.setHgrow(btnNewSet, Priority.ALWAYS);
        btnNewSet.setMaxWidth(Double.MAX_VALUE);

        Button btnCheck = new Button("_Prüfen");
        btnCheck.setTooltip(new Tooltip("Die angelegten Sets überprüfen"));
        btnCheck.setOnAction(event -> SetsPrograms.checkPrograms(progData));
        HBox.setHgrow(btnCheck, Priority.ALWAYS);
        btnCheck.setMaxWidth(Double.MAX_VALUE);

        vBoxButton.getChildren().addAll(btnDup, btnNewSet, btnCheck);
    }

    private SetData getSelectedSelData() {
        final SetData sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }

    private int getSelectedLine() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }

    private Callback<TableColumn<SetData, Boolean>, TableCell<SetData, Boolean>> cellFactoryPlay
            = (final TableColumn<SetData, Boolean> param) -> {

        final TableCell<SetData, Boolean> cell = new TableCell<SetData, Boolean>() {

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                SetData setData = getTableView().getItems().get(getIndex());

                final RadioButton radioButton = new RadioButton("");
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setSelected(item.booleanValue());

                radioButton.setOnAction(event -> progData.setDataList.setPlay(setData));

                hbox.getChildren().addAll(radioButton);
                setGraphic(hbox);

            }
        };
        return cell;
    };
    private Callback<TableColumn<SetData, Boolean>, TableCell<SetData, Boolean>> cellFactorySave
            = (final TableColumn<SetData, Boolean> param) -> {

        final TableCell<SetData, Boolean> cell = new TableCell<SetData, Boolean>() {

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                SetData setData = getTableView().getItems().get(getIndex());

                final HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final CheckBox chkButton = new CheckBox("");
                chkButton.selectedProperty().bindBidirectional(setData.saveProperty());
                chkButton.selectedProperty().addListener((nn, o, n) -> ProgData.getInstance().setDataList.setListChanged());
                hbox.getChildren().addAll(chkButton);
                setGraphic(hbox);
            }
        };
        return cell;
    };
    private Callback<TableColumn<SetData, Boolean>, TableCell<SetData, Boolean>> cellFactoryButton
            = (final TableColumn<SetData, Boolean> param) -> {

        final TableCell<SetData, Boolean> cell = new TableCell<SetData, Boolean>() {

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                SetData setData = getTableView().getItems().get(getIndex());

                final HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final CheckBox chkButton = new CheckBox("");
                chkButton.selectedProperty().bindBidirectional(setData.buttonProperty());
                chkButton.selectedProperty().addListener((nn, o, n) -> ProgData.getInstance().setDataList.setListChanged());
                hbox.getChildren().addAll(chkButton);
                setGraphic(hbox);
            }
        };
        return cell;
    };
    private Callback<TableColumn<SetData, Boolean>, TableCell<SetData, Boolean>> cellFactoryAbo
            = (final TableColumn<SetData, Boolean> param) -> {

        final TableCell<SetData, Boolean> cell = new TableCell<SetData, Boolean>() {

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                SetData setData = getTableView().getItems().get(getIndex());

                final HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final CheckBox chkButton = new CheckBox("");
                chkButton.selectedProperty().bindBidirectional(setData.aboProperty());
                chkButton.selectedProperty().addListener((nn, o, n) -> ProgData.getInstance().setDataList.setListChanged());
                hbox.getChildren().addAll(chkButton);
                setGraphic(hbox);
            }
        };
        return cell;
    };
}