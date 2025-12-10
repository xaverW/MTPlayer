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
import de.p2tools.mtplayer.controller.data.propose.ProposeData;
import de.p2tools.mtplayer.controller.data.propose.ProposeFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneProposeList {

    private final ProgData progData;
    private final Stage stage;

    private final TableView<ProposeData> tableList = new TableView<>();
    private final FilteredList<ProposeData> filteredList;
    private final SortedList<ProposeData> sortedList;

    public PaneProposeList(ProgData progData, Stage stage) {
        this.progData = progData;
        this.stage = stage;

        this.filteredList = new FilteredList<>(progData.proposeList, p -> true);
        this.sortedList = new SortedList<>(filteredList);
    }

    public AnchorPane makePane() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        initTableList(vBox);
        initUnderTable(vBox);

        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        anchorPane.getChildren().add(vBox);
        return anchorPane;
    }

    public void close() {
    }

    private void initTableList(VBox vBox) {
        tableList.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableList.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableList.setEditable(true);

        final TableColumn<ProposeData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<ProposeData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        nameColumn.prefWidthProperty().bind(tableList.widthProperty().multiply(60.0 / 100));
        countColumn.prefWidthProperty().bind(tableList.widthProperty().multiply(30.0 / 100));

        tableList.getColumns().addAll(nameColumn, countColumn);
        tableList.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            setTableSel(dataNew);
        });
        sortedList.comparatorProperty().bind(tableList.comparatorProperty());
        tableList.setItems(sortedList);

        vBox.getChildren().addAll(tableList);
        VBox.setVgrow(tableList, Priority.ALWAYS);
    }

    private void initUnderTable(VBox vBox) {
        Button btnGenerate = new Button("Liste erstellen");
        btnGenerate.setOnAction(a -> ProposeFactory.generateProposeList());

        Label lblCountList = new Label();
        lblCountList.textProperty().bind(progData.proposeList.sizeProperty().asString());

        // Gridpane
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        int row = 0;
        gridPane.add(btnGenerate, 1, row);
        gridPane.add(lblCountList, 2, row);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().addAll(gridPane);
    }

    private void setTableSel(ProposeData proposeData) {
        if (proposeData != null) {
        } else {
        }
    }
}
