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

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.data.P2ColorData;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.tools.P2ColorFactory;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Collection;

public class PaneColor {
    private final Stage stage;
    private final P2ToggleSwitch tglDarkTheme = new P2ToggleSwitch("Dunkles Erscheinungsbild der Programmoberfläche");
    private final P2ToggleSwitch tglBlackWhiteIcon = new P2ToggleSwitch("Schwarz-Weiße Icons");

    public PaneColor(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglDarkTheme.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME);
    }

    public void makeColor(Collection<TitledPane> result) {
        tglDarkTheme.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME);
        final Button btnHelpTheme = P2Button.helpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME);
        tglBlackWhiteIcon.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACK_WHITE_ICON);
        final Button btnHelpIcon = P2Button.helpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.BLACK_WHITE_ICON);

        TableView<P2ColorData> tableViewFont = new TableView<>();
        initTableColor(tableViewFont);
        tableViewFont.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewFont.setItems(ProgColorList.getColorListFront());

        TableView<P2ColorData> tableViewBackground = new TableView<>();
        initTableColor(tableViewBackground);
        tableViewBackground.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewBackground.setItems(ProgColorList.getColorListBackground());

        Button button = new Button("Alle _Farben zurücksetzen");
        button.setOnAction(event -> {
            ProgColorList.resetAllColor();
        });

        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        gridPane.add(tglDarkTheme, 0, row);
        gridPane.add(btnHelpTheme, 1, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);

        gridPane.add(tglBlackWhiteIcon, 0, ++row);
        gridPane.add(btnHelpIcon, 1, row);
        GridPane.setHalignment(btnHelpIcon, HPos.RIGHT);


        gridPane.add(new Label("Schriftfarben"), 0, ++row, 2, 1);
        gridPane.add(tableViewFont, 0, ++row, 2, 1);

        ++row;
        gridPane.add(new Label("Hintergrundfarben"), 0, row, 2, 1);
        gridPane.add(tableViewBackground, 0, ++row, 2, 1);

        gridPane.add(button, 0, ++row, 2, 1);
        GridPane.setHalignment(button, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());

        TitledPane tpColor = new TitledPane("Farben", gridPane);
        result.add(tpColor);
    }

    private void initTableColor(TableView<P2ColorData> tableView) {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> {
            P2TableFactory.refreshTable(tableView);
            tableView.refresh();
        });

        final TableColumn<P2ColorData, String> textColumn = new TableColumn<>("Beschreibung");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<P2ColorData, String> changeColumn = new TableColumn<>("Farbe");
        changeColumn.setCellFactory(cellFactoryChange);
        changeColumn.getStyleClass().add("alignCenter");

        final TableColumn<P2ColorData, String> resetColumn = new TableColumn<>("Reset");
        resetColumn.setCellFactory(cellFactoryReset);
        resetColumn.getStyleClass().add("alignCenter");

        final TableColumn<P2ColorData, Color> colorColumn = new TableColumn<>("Farbe");
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(cellFactoryColor);
        colorColumn.getStyleClass().add("alignCenter");

        final TableColumn<P2ColorData, Color> colorOrgColumn = new TableColumn<>("Original");
        colorOrgColumn.setCellValueFactory(new PropertyValueFactory<>("resetColor"));
        colorOrgColumn.setCellFactory(cellFactoryColorReset);
        colorOrgColumn.getStyleClass().add("alignCenter");

        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(textColumn, changeColumn, colorColumn, colorOrgColumn, resetColumn);
    }

    private Callback<TableColumn<P2ColorData, String>, TableCell<P2ColorData, String>> cellFactoryChange
            = (final TableColumn<P2ColorData, String> param) -> {

        final TableCell<P2ColorData, String> cell = new TableCell<P2ColorData, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                P2ColorData pColorData = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final ColorPicker colorPicker = new ColorPicker();
                colorPicker.getStyleClass().add("split-button");

                colorPicker.setValue(pColorData.getColor());
                colorPicker.setOnAction(a -> {
                    Color color = colorPicker.getValue();
                    pColorData.setColor(color);
                });
                hbox.getChildren().addAll(colorPicker);
                setGraphic(hbox);
            }
        };
        return cell;
    };

    private Callback<TableColumn<P2ColorData, String>, TableCell<P2ColorData, String>> cellFactoryReset
            = (final TableColumn<P2ColorData, String> param) -> {

        final TableCell<P2ColorData, String> cell = new TableCell<P2ColorData, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                P2ColorData pColorData = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button button = new Button("Reset");
                button.setOnAction(a -> {
                    pColorData.resetColor();
                });

                hbox.getChildren().add(button);
                setGraphic(hbox);
            }
        };
        return cell;
    };

    private Callback<TableColumn<P2ColorData, Color>, TableCell<P2ColorData, Color>> cellFactoryColor
            = (final TableColumn<P2ColorData, Color> param) -> {

        final TableCell<P2ColorData, Color> cell = new TableCell<P2ColorData, Color>() {


            @Override
            public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                P2ColorData pColorData = getTableView().getItems().get(getIndex());
                setStyle("-fx-background-color:" + pColorData.getColorSelectedToWeb());
            }

        };
        return cell;
    };

    private Callback<TableColumn<P2ColorData, Color>, TableCell<P2ColorData, Color>> cellFactoryColorReset
            = (final TableColumn<P2ColorData, Color> param) -> {

        final TableCell<P2ColorData, Color> cell = new TableCell<P2ColorData, Color>() {

            @Override
            public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                P2ColorData pColorData = getTableView().getItems().get(getIndex());
                setStyle("-fx-background-color:" + P2ColorFactory.getColorToWeb(pColorData.getResetColor()));
            }

        };
        return cell;
    };
}
