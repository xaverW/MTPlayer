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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.setdata.ProgramData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneSetProgram {
    TableView<ProgramData> tableView = new TableView<>();

    private final GridPane gridPane = new GridPane();
    private final TextField txtName = new TextField();
    private final TextField txtDestName = new TextField();
    private final TextField txtProgPath = new TextField();
    private final TextField txtProgSwitch = new TextField();
    private final TextField txtPrefix = new TextField();
    private final TextField txtSuffix = new TextField();
    private final PToggleSwitch tglRestart = new PToggleSwitch("Restart:");
    private final PToggleSwitch tglDown = new PToggleSwitch("Downloadmanager: ");
    private ProgramData programData = null;
    private final Stage stage;
    private SetData setData = null;
    private final ObjectProperty<SetData> setDataObjectProperty;

    public PaneSetProgram(Stage stage, ObjectProperty<SetData> setDataObjectProperty) {
        this.stage = stage;
        this.setDataObjectProperty = setDataObjectProperty;
    }

    public void close() {
        unbindActTableData();
    }

    public void makePane(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        initTable(vBox);
        initButton(vBox);
        addConfigs(vBox);

        TitledPane tpConfig = new TitledPane("Hilfsprogramme", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        setDataObjectProperty.addListener((u, o, n) -> {
            tpConfig.setDisable(setDataObjectProperty.getValue() == null);
            bindProgData();
        });
        tpConfig.setDisable(setDataObjectProperty.getValue() == null);
        bindProgData();
    }

    private void initTable(VBox vBox) {
        final TableColumn<ProgramData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<ProgramData, String> destNameColumn = new TableColumn<>("Zieldateiname");
        destNameColumn.setCellValueFactory(new PropertyValueFactory<>("destName"));

        final TableColumn<ProgramData, String> progColumn = new TableColumn<>("Programm");
        progColumn.setCellValueFactory(new PropertyValueFactory<>("progPath"));

        final TableColumn<ProgramData, String> switchColumn = new TableColumn<>("Schalter");
        switchColumn.setCellValueFactory(new PropertyValueFactory<>("progSwitch"));

        final TableColumn<ProgramData, String> prefixColumn = new TableColumn<>("Präfix");
        prefixColumn.setCellValueFactory(new PropertyValueFactory<>("prefix"));

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

        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT_LOW);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableView.getColumns().addAll(nameColumn, destNameColumn, progColumn, switchColumn,
                prefixColumn, suffixColumn, restartColumn, downManagerColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::bindActTableData));
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
        miUndo.setOnAction(a -> setDataObjectProperty.getValue().getProgramList().undoData());
        miUndo.setDisable(setDataObjectProperty.getValue().getProgramList().getUndoList().isEmpty());
        contextMenu.getItems().addAll(miUndo);
        return contextMenu;
    }

    private void initButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<ProgramData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                setDataObjectProperty.getValue().getProgramList().addDataToUndoList(sels);
                setDataObjectProperty.getValue().getProgramList().removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            ProgramData progData = new ProgramData();
            setDataObjectProperty.getValue().getProgramList().add(progData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(progData);
            tableView.scrollTo(progData);
        });

        Button btnUp = new Button("");
        btnUp.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = setDataObjectProperty.getValue().getProgramList().moveUp(sel, true);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button btnDown = new Button("");
        btnDown.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = setDataObjectProperty.getValue().getProgramList().moveUp(sel, false);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });

        final Button btnHelpProg = PButton.helpButton(stage, "Hilfsprogramme",
                HelpTextPset.PSET_FILE_HELP_PROG);

        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(btnNew, btnDel, PGuiTools.getVDistance(P2LibConst.DIST_BUTTON_BLOCK),
                btnUp, btnDown, PGuiTools.getHBoxGrower(), btnHelpProg);
        vBox.getChildren().addAll(hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtProgPath));
        btnFile.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Ein Programm zum verarbeiten der URL auswählen"));

        final Button btnHelpDest = PButton.helpButton(stage, "Zieldateiname",
                HelpTextPset.PSET_FILE_NAME);
        final Button btnHelpSwitch = PButton.helpButton(stage, "Programmschalter",
                HelpTextPset.PSET_SWITCH);

        int row = 0;
        gridPane.add(new Label("Name: "), 0, row);
        gridPane.add(txtName, 1, row, 3, 1);
        gridPane.add(new Label("Zieldateiname: "), 0, ++row);
        gridPane.add(txtDestName, 1, row, 3, 1);
        gridPane.add(btnHelpDest, 4, row);

        gridPane.add(new Label("Programm: "), 0, ++row);
        gridPane.add(txtProgPath, 1, row, 3, 1);
        gridPane.add(btnFile, 4, row);
        gridPane.add(new Label("Schalter: "), 0, ++row);
        gridPane.add(txtProgSwitch, 1, row, 3, 1);
        gridPane.add(btnHelpSwitch, 4, row);

        gridPane.add(new Label("Präfix: "), 0, ++row);
        gridPane.add(txtPrefix, 1, row);
        gridPane.add(new Label("Suffix: "), 2, row);
        gridPane.add(txtSuffix, 3, row);

        gridPane.add(tglRestart, 0, ++row, 2, 1);
        gridPane.add(tglDown, 2, row, 2, 1);

        gridPane.getColumnConstraints().addAll(
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize()
        );

        gridPane.setDisable(true);
        vBox.getChildren().add(gridPane);
    }

    private int getSelectedLine() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            PAlert.showInfoNoSelection();
        }
        return sel;
    }

    private void bindActTableData() {
        ProgramData programDataAct = tableView.getSelectionModel().getSelectedItem();
        if (programDataAct == programData) {
            return;
        }

        unbindActTableData();

        programData = programDataAct;
        gridPane.setDisable(programData == null);
        if (programData != null) {
            txtName.textProperty().bindBidirectional(programData.nameProperty());
            txtDestName.textProperty().bindBidirectional(programData.destNameProperty());
            txtProgPath.textProperty().bindBidirectional(programData.progPathProperty());
            txtProgSwitch.textProperty().bindBidirectional(programData.progSwitchProperty());
            txtPrefix.textProperty().bindBidirectional(programData.prefixProperty());
            txtSuffix.textProperty().bindBidirectional(programData.suffixProperty());
            tglRestart.selectedProperty().bindBidirectional(programData.restartProperty());
            tglDown.selectedProperty().bindBidirectional(programData.downManagerProperty());
        }
    }

    private void unbindActTableData() {
        if (programData != null) {
            txtName.textProperty().unbindBidirectional(programData.nameProperty());
            txtDestName.textProperty().unbindBidirectional(programData.destNameProperty());
            txtProgPath.textProperty().unbindBidirectional(programData.progPathProperty());
            txtProgSwitch.textProperty().unbindBidirectional(programData.progSwitchProperty());
            txtPrefix.textProperty().unbindBidirectional(programData.prefixProperty());
            txtSuffix.textProperty().unbindBidirectional(programData.suffixProperty());

            txtName.setText("");
            txtDestName.setText("");
            txtProgPath.setText("");
            txtProgSwitch.setText("");
            txtPrefix.setText("");
            txtSuffix.setText("");

            tglRestart.selectedProperty().unbindBidirectional(programData.restartProperty());
            tglDown.selectedProperty().unbindBidirectional(programData.downManagerProperty());
        }
    }

    private void bindProgData() {
        unBindProgData();
        setData = setDataObjectProperty.getValue();
        if (setData != null) {
            tableView.setItems(setDataObjectProperty.getValue().getProgramList());
            if (tableView.getItems().size() > 0) {
                tableView.getSelectionModel().select(0);
            }
        }
    }

    private void unBindProgData() {
        tableView.setItems(null);
        setData = null;
    }
}
