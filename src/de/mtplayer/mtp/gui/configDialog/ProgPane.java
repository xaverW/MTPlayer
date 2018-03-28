/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.util.Collection;

public class ProgPane {
    TableView<ProgData> tableView = new TableView<>();
    private SetData setData = null;
    private final VBox vBox = new VBox();

    public void setSetDate(SetData setData) {
        this.setData = setData;
        tableView.setItems(setData.getProgList());
    }

    public void makeProgs(Collection<TitledPane> result) {
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        TitledPane tpConfig = new TitledPane("Hilfsprogramme", vBox);
        result.add(tpConfig);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);
        tpConfig.setMaxHeight(Double.MAX_VALUE);

        initTable();
    }

    private void initTable() {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(Region.USE_PREF_SIZE);

        final TableColumn<ProgData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<ProgData, String> destNameColumn = new TableColumn<>("Zieldateiname");
        destNameColumn.setCellValueFactory(new PropertyValueFactory<>("destName"));

        final TableColumn<ProgData, String> progColumn = new TableColumn<>("Programm");
        progColumn.setCellValueFactory(new PropertyValueFactory<>("progPath"));

        final TableColumn<ProgData, String> switchColumn = new TableColumn<>("Schalter");
        switchColumn.setCellValueFactory(new PropertyValueFactory<>("progSwitch"));

        final TableColumn<ProgData, String> praefixColumn = new TableColumn<>("Präfix");
        praefixColumn.setCellValueFactory(new PropertyValueFactory<>("praefix"));

        final TableColumn<ProgData, String> suffixColumn = new TableColumn<>("Suffix");
        suffixColumn.setCellValueFactory(new PropertyValueFactory<>("suffix"));

        final TableColumn<ProgData, Boolean> restartColumn = new TableColumn<>("Restart");
        restartColumn.setCellValueFactory(new PropertyValueFactory<>("restart"));
        restartColumn.setCellFactory(CheckBoxTableCell.forTableColumn(restartColumn));
        restartColumn.getStyleClass().add("center");

        final TableColumn<ProgData, Boolean> downManagerColumn = new TableColumn<>("Downloadmanager");
        downManagerColumn.setCellValueFactory(new PropertyValueFactory<>("downManager"));
        downManagerColumn.setCellFactory(CheckBoxTableCell.forTableColumn(downManagerColumn));
        downManagerColumn.getStyleClass().add("center");

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(expander, nameColumn, destNameColumn, progColumn, switchColumn,
                praefixColumn, suffixColumn, restartColumn, downManagerColumn);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

        Button del = new Button("");
        del.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        del.setOnAction(event -> {
            final ObservableList<ProgData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                setData.getProgList().removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button neu = new Button("");
        neu.setGraphic(new Icons().ICON_BUTTON_ADD);
        neu.setOnAction(event -> {
            ProgData progData = new ProgData();
            setData.getProgList().add(progData);
            tableView.getSelectionModel().select(progData);
            tableView.scrollTo(progData);
        });

        Button up = new Button("");
        up.setGraphic(new Icons().ICON_BUTTON_MOVE_UP);
        up.setOnAction(event -> {
            int sel = getSelLine();
            if (sel >= 0) {
                int newSel = setData.getProgList().auf(sel, true);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });

        Button down = new Button("");
        down.setGraphic(new Icons().ICON_BUTTON_MOVE_DOWN);
        down.setOnAction(event -> {
            int sel = getSelLine();
            if (sel >= 0) {
                int newSel = setData.getProgList().auf(sel, false);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newSel);
            }
        });
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(neu, del, up, down);
        vBox.getChildren().addAll(hBox);

    }

    private int getSelLine() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            new MTAlert().showInfoNoSelection();
        }
        return sel;
    }


    TableRowExpanderColumn<ProgData> expander = new TableRowExpanderColumn<>(param -> {
        VBox vBoxCont = new VBox();
        vBoxCont.setSpacing(5);
        vBoxCont.setStyle("-fx-background-color: #E0E0E0;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        // todo evtl besser: width gridpane an den "sichtbaren" Teil der Scrollpane binden??

        TextField txtName = new TextField();
        txtName.textProperty().bindBidirectional(param.getValue().nameProperty());
        TextField txtDestName = new TextField();
        txtDestName.textProperty().bindBidirectional(param.getValue().destNameProperty());

        TextField txtProgPath = new TextField();
        txtProgPath.textProperty().bindBidirectional(param.getValue().progPathProperty());

        TextField txtProgSwitch = new TextField();
        txtProgSwitch.textProperty().bindBidirectional(param.getValue().progSwitchProperty());

        TextField txtPraefix = new TextField();
        txtPraefix.textProperty().bindBidirectional(param.getValue().praefixProperty());
        TextField txtSuffix = new TextField();
        txtSuffix.textProperty().bindBidirectional(param.getValue().suffixProperty());

        ToggleSwitch tglRestart = new ToggleSwitch("Restart:");
        tglRestart.selectedProperty().bindBidirectional(param.getValue().restartProperty());
        ToggleSwitch tglDown = new ToggleSwitch("Downloadmanager: ");
        tglDown.selectedProperty().bindBidirectional(param.getValue().downManagerProperty());

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(Daten.getInstance().primaryStage, txtProgPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);


        gridPane.add(new Label("Name: "), 0, 0);
        gridPane.add(txtName, 1, 0, 2, 1);
        gridPane.add(new Label("Zieldateiname: "), 0, 1);
        gridPane.add(txtDestName, 1, 1, 2, 1);

        gridPane.add(new Label("Programm: "), 0, 2);
        gridPane.add(txtProgPath, 1, 2);
        gridPane.add(btnFile, 2, 2);
        gridPane.add(new Label("Schalter: "), 0, 3);
        gridPane.add(txtProgSwitch, 1, 3, 2, 1);

        gridPane.add(new Label("Präfix: "), 0, 4);
        gridPane.add(txtPraefix, 1, 4, 2, 1);
        gridPane.add(new Label("Suffix: "), 0, 5);
        gridPane.add(txtSuffix, 1, 5, 2, 1);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.SOMETIMES);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setPadding(new Insets(10, 10, 10, 10));

        tglRestart.setMaxWidth(Double.MAX_VALUE);
        tglDown.setMaxWidth(Double.MAX_VALUE);
        gp.add(tglRestart, 0, 0);
        gp.add(tglDown, 0, 1);

        vBoxCont.getChildren().addAll(gridPane, gp);
        return vBoxCont;
    });


}
