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

import de.mtplayer.mLib.tools.MLC;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Collection;

public class ColorPane {

    public void makeColor(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        TableView<MLC> tableView = new TableView<>();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        initTableColor(tableView);

        Button button = new Button("Alle Farben zurücksetzen");
        button.setOnAction(event -> {
            ProgData.mTColor.reset();
            Listener.notify(Listener.EREIGNIS_GUI_COLOR_CHANGED, ColorPane.class.getSimpleName());
        });

        HBox hBox = new HBox();
        hBox.getChildren().add(button);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().addAll(tableView, hBox);

        TitledPane tpColor = new TitledPane("Farben", vBox);
        result.add(tpColor);
    }

    private void initTableColor(TableView<MLC> tableView) {

        final TableColumn<MLC, String> textColumn = new TableColumn<>("Beschreibung");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        final TableColumn<MLC, String> changeColumn = new TableColumn<>("Farbe");
        changeColumn.setCellFactory(cellFactoryChange);
        changeColumn.getStyleClass().add("center");

        final TableColumn<MLC, String> resetColumn = new TableColumn<>("Reset");
        resetColumn.setCellFactory(cellFactoryReset);
        resetColumn.getStyleClass().add("center");

        final TableColumn<MLC, Color> colorColumn = new TableColumn<>("Farbe");
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(cellFactoryColor);

        final TableColumn<MLC, Color> colorOrgColumn = new TableColumn<>("Original");
        colorOrgColumn.setCellValueFactory(new PropertyValueFactory<>("colorReset"));
        colorOrgColumn.setCellFactory(cellFactoryColorReset);

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(textColumn, changeColumn, colorColumn, colorOrgColumn, resetColumn);
        tableView.setItems(MTColor.getColorList());
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
                    ProgData.mTColor.save();
                    Listener.notify(Listener.EREIGNIS_GUI_COLOR_CHANGED, ColorPane.class.getSimpleName());
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
                    ProgData.mTColor.save();
                    Listener.notify(Listener.EREIGNIS_GUI_COLOR_CHANGED, ColorPane.class.getSimpleName());
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
                setStyle("-fx-background-color:" + MLC.getColorToWeb(MLC.getColorReset()));
            }

        };

        return cell;
    };

}
