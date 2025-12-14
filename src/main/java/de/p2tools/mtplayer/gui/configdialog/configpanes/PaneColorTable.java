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
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptable.P2TableFactory;
import de.p2tools.p2lib.tools.P2ColorFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Collection;

public class PaneColorTable {
    private final Stage stage;
    private final RadioButton rbDark = new RadioButton("Farben für das Dark-Them");
    private final RadioButton rbLight = new RadioButton("Farben für das Light-Them");
    private final TableView<P2ColorData> tableViewFont = new TableView<>();
    private final TableView<P2ColorData> tableViewBackground = new TableView<>();

    public PaneColorTable(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        rbDark.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_THEME_DARK);
        rbLight.selectedProperty().unbind();
    }

    public void make(Collection<TitledPane> result) {
        ToggleGroup tg = new ToggleGroup();
        rbDark.setToggleGroup(tg);
        rbLight.setToggleGroup(tg);
        rbDark.selectedProperty().setValue(ProgConfig.SYSTEM_THEME_DARK.getValue());
        rbLight.selectedProperty().setValue(ProgConfig.SYSTEM_THEME_DARK.not().getValue());
        rbDark.selectedProperty().addListener((u, o, n) -> ProgConfig.SYSTEM_THEME_DARK.set(rbDark.isSelected()));
        ProgConfig.SYSTEM_THEME_DARK.addListener((u, o, n) -> {
            rbDark.setSelected(ProgConfig.SYSTEM_THEME_DARK.get());
            rbLight.setSelected(!ProgConfig.SYSTEM_THEME_DARK.get());
        });

        final Button btnHelpTheme = PIconFactory.getHelpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME_TABLE);

        initTableColor(tableViewFont);
        tableViewFont.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewFont.setItems(ProgColorList.getColorListFront());

        initTableColor(tableViewBackground);
        tableViewBackground.setPrefHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableViewBackground.setItems(ProgColorList.getColorListBackground());

        Button btnReset = new Button("Alle _Farben zurücksetzen");
        btnReset.setOnAction(event -> {
            ProgColorList.resetAllColor();
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_REFRESH_TABLE);
        });

        ProgConfig.SYSTEM_THEME_DARK.addListener((u, o, n) -> {
            ProgColorList.setColorTheme();
            P2TableFactory.refreshTable(tableViewFont);
            P2TableFactory.refreshTable(tableViewBackground);
        });

        VBox vBox = new VBox(P2LibConst.SPACING_VBOX);

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(rbDark, P2GuiTools.getHDistance(10), rbLight,
                P2GuiTools.getHBoxGrower(), btnReset, btnHelpTheme);
        vBox.getChildren().add(hBox);

        vBox.getChildren().add(new Label("Schriftfarben"));
        vBox.getChildren().add(tableViewFont);
        vBox.getChildren().add(new Label("Hintergrundfarben"));
        vBox.getChildren().add(tableViewBackground);

        TitledPane tpColor = new TitledPane("Farbe Tabellen-Zeilen", vBox);
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
