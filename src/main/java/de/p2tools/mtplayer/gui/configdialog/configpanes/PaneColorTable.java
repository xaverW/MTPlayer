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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.colordata.P2ColorData;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptable.P2TableFactory;
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

public class PaneColorTable {
    private final Stage stage;
    private final P2ToggleSwitch tglDarkTheme = new P2ToggleSwitch("Dunkles Erscheinungsbild der Programmoberfläche");
    private final TableView<P2ColorData> tableViewFont = new TableView<>();
    private final TableView<P2ColorData> tableViewBackground = new TableView<>();

    public PaneColorTable(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglDarkTheme.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_COLOR_THEME_DARK);
    }

    public void make(Collection<TitledPane> result) {
        tglDarkTheme.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_COLOR_THEME_DARK);
        final Button btnHelpTheme = PIconFactory.getHelpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME);
        final Button btnHelpIcon = PIconFactory.getHelpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.BLACK_WHITE_ICON);

        initTableColor(tableViewFont);
        tableViewFont.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewFont.setItems(ProgColorList.getColorListFront());

        initTableColor(tableViewBackground);
        tableViewBackground.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewBackground.setItems(ProgColorList.getColorListBackground());

        ProgConfig.SYSTEM_COLOR_THEME_DARK.addListener((u, o, n) -> {
            ProgColorList.setColorTheme();
            P2TableFactory.refreshTable(tableViewFont);
            P2TableFactory.refreshTable(tableViewBackground);
        });

        Button button = new Button("Alle _Farben zurücksetzen");
        button.setOnAction(event -> {
            ProgColorList.resetAllColor();
//            PListener.notify(PListener.EVENT_REFRESH_TABLE, PaneColor.class.getSimpleName());
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_REFRESH_TABLE);
        });

        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        gridPane.add(tglDarkTheme, 0, row);
        gridPane.add(btnHelpTheme, 1, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);

        gridPane.add(new Label("Schriftfarben"), 0, ++row, 2, 1);
        gridPane.add(tableViewFont, 0, ++row, 2, 1);

        ++row;
        gridPane.add(new Label("Hintergrundfarben"), 0, row, 2, 1);
        gridPane.add(tableViewBackground, 0, ++row, 2, 1);

        gridPane.add(button, 0, ++row, 2, 1);
        GridPane.setHalignment(button, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        TitledPane tpColor = new TitledPane("Farbe Tabellen-Zeilen", gridPane);
        result.add(tpColor);
    }

    private void initTableColor(TableView<P2ColorData> tableView) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

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
        colorOrgColumn.setCellFactory(cellFactoryResetColor);
        colorOrgColumn.getStyleClass().add("alignCenter");

        tableView.getColumns().addAll(textColumn, changeColumn, colorColumn, colorOrgColumn, resetColumn);
    }

    private final Callback<TableColumn<P2ColorData, String>, TableCell<P2ColorData, String>> cellFactoryChange
            = (final TableColumn<P2ColorData, String> param) -> new TableCell<>() {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            P2ColorData pColorData = getTableView().getItems().get(getIndex());
            if (pColorData == null) {
                setGraphic(null);
                setText(null);
                return;
            }

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
//                PListener.notify(PListener.EVENT_REFRESH_TABLE, PaneColor.class.getSimpleName());
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_REFRESH_TABLE);
            });
            hbox.getChildren().addAll(colorPicker);
            setGraphic(hbox);
        }
    };

    private final Callback<TableColumn<P2ColorData, String>, TableCell<P2ColorData, String>> cellFactoryReset
            = (final TableColumn<P2ColorData, String> param) -> new TableCell<>() {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            P2ColorData pColorData = getTableView().getItems().get(getIndex());
            if (pColorData == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            final HBox hbox = new HBox();
            hbox.setSpacing(5);
            hbox.setAlignment(Pos.CENTER);
            hbox.setPadding(new Insets(0, 2, 0, 2));

            final Button button = new Button("Reset");
            button.setOnAction(a -> {
                pColorData.resetColor();
//                PListener.notify(PListener.EVENT_REFRESH_TABLE, PaneColor.class.getSimpleName());
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_REFRESH_TABLE);
            });

            hbox.getChildren().add(button);
            setGraphic(hbox);
        }
    };

    private final Callback<TableColumn<P2ColorData, Color>, TableCell<P2ColorData, Color>> cellFactoryColor
            = (final TableColumn<P2ColorData, Color> param) -> new TableCell<>() {

        @Override
        public void updateItem(Color item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            P2ColorData pColorData = getTableView().getItems().get(getIndex());
            if (pColorData == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            Button btn = new Button("      ");
            btn.setStyle("-fx-background-color: " + pColorData.getColorSelectedToWeb());
            setGraphic(btn);
        }
    };

    private final Callback<TableColumn<P2ColorData, Color>, TableCell<P2ColorData, Color>> cellFactoryResetColor
            = (final TableColumn<P2ColorData, Color> param) -> {

        final TableCell<P2ColorData, Color> cell = new TableCell<>() {

            @Override
            public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                P2ColorData pColorData = getTableView().getItems().get(getIndex());
                if (pColorData == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Button btn = new Button("      ");
                btn.setStyle("-fx-background-color:" + P2ColorFactory.getColorToWeb(pColorData.getResetColor()));
                setGraphic(btn);
            }
        };
        return cell;
    };
}
