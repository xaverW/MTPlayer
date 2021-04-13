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
import de.p2tools.mtplayer.controller.data.MLC;
import de.p2tools.mtplayer.controller.data.MTColor;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.PColorFactory;
import javafx.beans.property.BooleanProperty;
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

public class ColorPane {
    private final Stage stage;
    BooleanProperty propDarkTheme = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty();
    private final PToggleSwitch tglDarkTheme = new PToggleSwitch("Dunkles Erscheinungsbild der Programmoberfläche");

    public ColorPane(Stage stage) {
        this.stage = stage;
    }

    public void makeColor(Collection<TitledPane> result) {
        tglDarkTheme.selectedProperty().bindBidirectional(propDarkTheme);
        final Button btnHelpTheme = PButton.helpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME);

        TableView<MLC> tableViewFont = new TableView<>();
        initTableColor(tableViewFont);
        tableViewFont.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewFont.setItems(MTColor.getColorListFont());

        TableView<MLC> tableViewBackground = new TableView<>();
        initTableColor(tableViewBackground);
        tableViewBackground.setItems(MTColor.getColorListBackground());

        Button button = new Button("Alle _Farben zurücksetzen");
        button.setOnAction(event -> {
            ProgData.getInstance().mTColor.resetAllColors();
        });

        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10));

        gridPane.add(tglDarkTheme, 0, row);
        gridPane.add(btnHelpTheme, 1, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);

        gridPane.add(new Label("Schriftfarben"), 0, ++row, 2, 1);
        gridPane.add(tableViewFont, 0, ++row, 2, 1);
        ++row;
        gridPane.add(new Label("Hintergrundfarben"), 0, ++row, 2, 1);
        gridPane.add(tableViewBackground, 0, ++row, 2, 1);

        gridPane.add(button, 0, ++row, 2, 1);
        GridPane.setHalignment(button, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        TitledPane tpColor = new TitledPane("Farben", gridPane);
        result.add(tpColor);
    }

    public void close() {
        tglDarkTheme.selectedProperty().unbindBidirectional(propDarkTheme);
    }

    private void initTableColor(TableView<MLC> tableView) {
        final TableColumn<MLC, String> textColumn = new TableColumn<>("Beschreibung");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<MLC, String> changeColumn = new TableColumn<>("Farbe");
        changeColumn.setCellFactory(cellFactoryChange);
        changeColumn.getStyleClass().add("alignCenter");

        final TableColumn<MLC, String> resetColumn = new TableColumn<>("Reset");
        resetColumn.setCellFactory(cellFactoryReset);
        resetColumn.getStyleClass().add("alignCenter");

        final TableColumn<MLC, Color> colorColumn = new TableColumn<>("Farbe");
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(cellFactoryColor);
        colorColumn.getStyleClass().add("alignCenter");

        final TableColumn<MLC, Color> colorOrgColumn = new TableColumn<>("Original");
        colorOrgColumn.setCellValueFactory(new PropertyValueFactory<>("resetColor"));
        colorOrgColumn.setCellFactory(cellFactoryColorReset);
        colorOrgColumn.getStyleClass().add("alignCenter");

        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(textColumn, changeColumn, colorColumn, colorOrgColumn, resetColumn);
    }

    private Callback<TableColumn<MLC, String>, TableCell<MLC, String>> cellFactoryChange
            = (final TableColumn<MLC, String> param) -> {

        final TableCell<MLC, String> cell = new TableCell<MLC, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                MLC MLC = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final ColorPicker colorPicker = new ColorPicker();
                colorPicker.getStyleClass().add("split-button");

                colorPicker.setValue(MLC.getColor());
                colorPicker.setOnAction(a -> {
                    Color fxColor = colorPicker.getValue();
                    MLC.setColor(fxColor);
                });
                hbox.getChildren().addAll(colorPicker);
                setGraphic(hbox);
            }
        };
        return cell;
    };

    private Callback<TableColumn<MLC, String>, TableCell<MLC, String>> cellFactoryReset
            = (final TableColumn<MLC, String> param) -> {

        final TableCell<MLC, String> cell = new TableCell<MLC, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                MLC MLC = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button button = new Button("Reset");
                button.setOnAction(a -> {
                    MLC.resetColor();
                });

                hbox.getChildren().add(button);
                setGraphic(hbox);
            }
        };
        return cell;
    };

    private Callback<TableColumn<MLC, Color>, TableCell<MLC, Color>> cellFactoryColor
            = (final TableColumn<MLC, Color> param) -> {

        final TableCell<MLC, Color> cell = new TableCell<MLC, Color>() {


            @Override
            public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                MLC MLC = getTableView().getItems().get(getIndex());
                setStyle("-fx-background-color:" + MLC.getColorToWeb());
            }

        };
        return cell;
    };
    private Callback<TableColumn<MLC, Color>, TableCell<MLC, Color>> cellFactoryColorReset
            = (final TableColumn<MLC, Color> param) -> {

        final TableCell<MLC, Color> cell = new TableCell<MLC, Color>() {

            @Override
            public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                MLC MLC = getTableView().getItems().get(getIndex());
                setStyle("-fx-background-color:" + PColorFactory.getColorToWeb(MLC.getResetColor()));
            }

        };
        return cell;
    };

}
