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

import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ListePsetVorlagen;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
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

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        vBox.getChildren().addAll(createSetList());
        splitPane.getItems().addAll(vBox, scrollPane);
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

    private void makeSetListTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);

        initTable(vBox);

        TitledPane tpSet = new TitledPane("Sets", vBox);
        tpSet.setCollapsible(false);
        result.add(tpSet);
        VBox.setVgrow(tpSet, Priority.ALWAYS);
        tpSet.setMaxHeight(Double.MAX_VALUE);
    }


    private void initTable(VBox vBox) {
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setDataPane.bindProgData(newValue);
        });

        final TableColumn<SetData, String> visibleNameColumn = new TableColumn<>("Name");
        visibleNameColumn.setCellValueFactory(new PropertyValueFactory<>("visibleName"));
        visibleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn()); //todo muss eindeutig sein

        final TableColumn<SetData, Boolean> playColumn = new TableColumn<>("Abspielen");
        playColumn.setCellValueFactory(new PropertyValueFactory<>("play"));
        playColumn.setCellFactory(cellFactoryStart);
        playColumn.getStyleClass().add("center");

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(visibleNameColumn, playColumn);
        tableView.setItems(progData.setDataList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

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

        final Button btnHelp = PButton.helpButton(stage, "Set", HelpTextPset.HELP_PSET);


        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(btnNew, btnDel, btnUp, btnDown);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        HBox hBoxHlp = new HBox(10);
        hBoxHlp.getChildren().addAll(hBox, btnHelp);

        vBox.getChildren().addAll(hBoxHlp, btnDup, btnNewSet, btnCheck);
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

    private Callback<TableColumn<SetData, Boolean>, TableCell<SetData, Boolean>> cellFactoryStart
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
}