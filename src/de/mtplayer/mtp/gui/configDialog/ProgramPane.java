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

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.ProgramData;
import de.mtplayer.mtp.controller.data.SetData;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Collection;

public class ProgramPane {
    TableView<ProgramData> tableView = new TableView<>();
    private SetData setData = null;
    private final VBox vBox = new VBox();

    private final GridPane gridPane = new GridPane();
    private final TextField txtName = new TextField();
    private final TextField txtDestName = new TextField();
    private final TextField txtProgPath = new TextField();
    private final TextField txtProgSwitch = new TextField();
    private final TextField txtPraefix = new TextField();
    private final TextField txtSuffix = new TextField();
    private final PToggleSwitch tglRestart = new PToggleSwitch("Restart:");
    private final PToggleSwitch tglDown = new PToggleSwitch("Downloadmanager: ");
    private ProgramData programData = null;

    public void setSetDate(SetData setData) {
        this.setData = setData;
        tableView.setItems(setData.getProgramList());
    }

    public void makeProgs(Collection<TitledPane> result) {
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        initTable();
        addConfigs(vBox);

        TitledPane tpConfig = new TitledPane("Hilfsprogramme", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);
    }

    private void initTable() {
        final TableColumn<ProgramData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<ProgramData, String> destNameColumn = new TableColumn<>("Zieldateiname");
        destNameColumn.setCellValueFactory(new PropertyValueFactory<>("destName"));

        final TableColumn<ProgramData, String> progColumn = new TableColumn<>("Programm");
        progColumn.setCellValueFactory(new PropertyValueFactory<>("progPath"));

        final TableColumn<ProgramData, String> switchColumn = new TableColumn<>("Schalter");
        switchColumn.setCellValueFactory(new PropertyValueFactory<>("progSwitch"));

        final TableColumn<ProgramData, String> praefixColumn = new TableColumn<>("Präfix");
        praefixColumn.setCellValueFactory(new PropertyValueFactory<>("praefix"));

        final TableColumn<ProgramData, String> suffixColumn = new TableColumn<>("Suffix");
        suffixColumn.setCellValueFactory(new PropertyValueFactory<>("suffix"));

        final TableColumn<ProgramData, Boolean> restartColumn = new TableColumn<>("Restart");
        restartColumn.setCellValueFactory(new PropertyValueFactory<>("restart"));
        restartColumn.setCellFactory(CheckBoxTableCell.forTableColumn(restartColumn));
        restartColumn.getStyleClass().add("center");

        final TableColumn<ProgramData, Boolean> downManagerColumn = new TableColumn<>("Downloadmanager");
        downManagerColumn.setCellValueFactory(new PropertyValueFactory<>("downManager"));
        downManagerColumn.setCellFactory(CheckBoxTableCell.forTableColumn(downManagerColumn));
        downManagerColumn.getStyleClass().add("center");

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(nameColumn, destNameColumn, progColumn, switchColumn,
                praefixColumn, suffixColumn, restartColumn, downManagerColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActProgramData));

        Button btnDel = new Button("");
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            final ObservableList<ProgramData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                setData.getProgramList().removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            ProgramData progData = new ProgramData();
            setData.getProgramList().add(progData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(progData);
            tableView.scrollTo(progData);
        });

        Button btnUp = new Button("");
        btnUp.setGraphic(new Icons().ICON_BUTTON_MOVE_UP);
        btnUp.setOnAction(event -> {
            int sel = getSelLine();
            if (sel >= 0) {
                int newSel = setData.getProgramList().auf(sel, true);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button btnDown = new Button("");
        btnDown.setGraphic(new Icons().ICON_BUTTON_MOVE_DOWN);
        btnDown.setOnAction(event -> {
            int sel = getSelLine();
            if (sel >= 0) {
                int newSel = setData.getProgramList().auf(sel, false);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNew, btnDel, btnUp, btnDown);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtProgPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);

        int row = 0;
        gridPane.add(new Label("Name: "), 0, row);
        gridPane.add(txtName, 1, row, 2, 1);
        gridPane.add(new Label("Zieldateiname: "), 0, ++row);
        gridPane.add(txtDestName, 1, row, 2, 1);

        gridPane.add(new Label("Programm: "), 0, ++row);
        gridPane.add(txtProgPath, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(new Label("Schalter: "), 0, ++row);
        gridPane.add(txtProgSwitch, 1, row, 2, 1);

        gridPane.add(new Label("Präfix: "), 0, ++row);
        gridPane.add(txtPraefix, 1, row, 2, 1);
        gridPane.add(new Label("Suffix: "), 0, ++row);
        gridPane.add(txtSuffix, 1, row, 2, 1);
        gridPane.add(tglRestart, 0, ++row, 2, 1);
        gridPane.add(tglDown, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
    }

    private void setActProgramData() {
        ProgramData programDataAct = tableView.getSelectionModel().getSelectedItem();
        if (programDataAct == programData) {
            return;
        }

        if (programData != null) {
            txtName.textProperty().unbindBidirectional(programData.nameProperty());
            txtDestName.textProperty().unbindBidirectional(programData.destNameProperty());
            txtProgPath.textProperty().unbindBidirectional(programData.progPathProperty());
            txtProgSwitch.textProperty().unbindBidirectional(programData.progSwitchProperty());
            txtPraefix.textProperty().unbindBidirectional(programData.praefixProperty());
            txtSuffix.textProperty().unbindBidirectional(programData.suffixProperty());
            tglRestart.selectedProperty().unbindBidirectional(programData.restartProperty());
            tglDown.selectedProperty().unbindBidirectional(programData.downManagerProperty());
        }

        programData = programDataAct;
        gridPane.setDisable(programData == null);
        if (programData != null) {
            txtName.textProperty().bindBidirectional(programData.nameProperty());
            txtDestName.textProperty().bindBidirectional(programData.destNameProperty());
            txtProgPath.textProperty().bindBidirectional(programData.progPathProperty());
            txtProgSwitch.textProperty().bindBidirectional(programData.progSwitchProperty());
            txtPraefix.textProperty().bindBidirectional(programData.praefixProperty());
            txtSuffix.textProperty().bindBidirectional(programData.suffixProperty());
            tglRestart.selectedProperty().bindBidirectional(programData.restartProperty());
            tglDown.selectedProperty().bindBidirectional(programData.downManagerProperty());
        }
    }

    private int getSelLine() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }


//    TableRowExpanderColumn<ProgramData> expander = new TableRowExpanderColumn<>(param -> {
//        VBox vBoxCont = new VBox();
//        vBoxCont.setSpacing(5);
//        vBoxCont.setStyle("-fx-background-color: #E0E0E0;");
//
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(5);
//        gridPane.setVgap(5);
//        gridPane.setPadding(new Insets(10, 10, 10, 10));
//        // todo evtl besser: width gridpane an den "sichtbaren" Teil der Scrollpane binden??
//
//        TextField txtName = new TextField();
//        txtName.textProperty().bindBidirectional(param.getValue().nameProperty());
//        TextField txtDestName = new TextField();
//        txtDestName.textProperty().bindBidirectional(param.getValue().destNameProperty());
//
//        TextField txtProgPath = new TextField();
//        txtProgPath.textProperty().bindBidirectional(param.getValue().progPathProperty());
//
//        TextField txtProgSwitch = new TextField();
//        txtProgSwitch.textProperty().bindBidirectional(param.getValue().progSwitchProperty());
//
//        TextField txtPraefix = new TextField();
//        txtPraefix.textProperty().bindBidirectional(param.getValue().praefixProperty());
//        TextField txtSuffix = new TextField();
//        txtSuffix.textProperty().bindBidirectional(param.getValue().suffixProperty());
//
//        PToggleSwitch tglRestart = new PToggleSwitch("Restart:");
//        tglRestart.selectedProperty().bindBidirectional(param.getValue().restartProperty());
//        PToggleSwitch tglDown = new PToggleSwitch("Downloadmanager: ");
//        tglDown.selectedProperty().bindBidirectional(param.getValue().downManagerProperty());
//
//        final Button btnFile = new Button();
//        btnFile.setOnAction(event -> {
//            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtProgPath);
//        });
//        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
//
//
//        gridPane.add(new Label("Name: "), 0, 0);
//        gridPane.add(txtName, 1, 0, 2, 1);
//        gridPane.add(new Label("Zieldateiname: "), 0, 1);
//        gridPane.add(txtDestName, 1, 1, 2, 1);
//
//        gridPane.add(new Label("Programm: "), 0, 2);
//        gridPane.add(txtProgPath, 1, 2);
//        gridPane.add(btnFile, 2, 2);
//        gridPane.add(new Label("Schalter: "), 0, 3);
//        gridPane.add(txtProgSwitch, 1, 3, 2, 1);
//
//        gridPane.add(new Label("Präfix: "), 0, 4);
//        gridPane.add(txtPraefix, 1, 4, 2, 1);
//        gridPane.add(new Label("Suffix: "), 0, 5);
//        gridPane.add(txtSuffix, 1, 5, 2, 1);
//
//        final ColumnConstraints ccTxt = new ColumnConstraints();
//        ccTxt.setFillWidth(true);
//        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
//        ccTxt.setHgrow(Priority.SOMETIMES);
//        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
//
//        GridPane gp = new GridPane();
//        gp.setHgap(5);
//        gp.setVgap(5);
//        gp.setPadding(new Insets(10, 10, 10, 10));
//
//        tglRestart.setMaxWidth(Double.MAX_VALUE);
//        tglDown.setMaxWidth(Double.MAX_VALUE);
//        gp.add(tglRestart, 0, 0);
//        gp.add(tglDown, 0, 1);
//
//        vBoxCont.getChildren().addAll(gridPane, gp);
//        return vBoxCont;
//    });


}
