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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneColorGui {
    private final Stage stage;
    private final TabPane tabPane = new TabPane();
    private final Tab tabDark = new Tab("Dark-Theme");
    private final Tab tabLight = new Tab("Light-Theme");

    private final RadioButton rbDark = new RadioButton("Dark-Theme");
    private final RadioButton rbLight = new RadioButton("Light-Theme");
    private final RadioButton rbIcon1 = new RadioButton("Icon-Theme 1");
    private final RadioButton rbIcon2 = new RadioButton("Icon-Theme 2");
    private final RadioButton rbGui1 = new RadioButton("Gui-Farben 1");
    private final RadioButton rbGui2 = new RadioButton("Gui-Farben 2");

    private final Button btnReset = new Button("Zurücksetzen");

    public PaneColorGui(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        rbDark.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME);
        rbIcon1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_THEME_1);
    }

    public void make(Collection<TitledPane> result) {
        ToggleGroup tgDark = new ToggleGroup();
        rbDark.setToggleGroup(tgDark);
        rbLight.setToggleGroup(tgDark);

        rbDark.selectedProperty().setValue(ProgConfig.SYSTEM_DARK_THEME.getValue());
        rbLight.selectedProperty().setValue(ProgConfig.SYSTEM_DARK_THEME.not().getValue());
        rbDark.selectedProperty().addListener((u, o, n) -> ProgConfig.SYSTEM_DARK_THEME.set(rbDark.isSelected()));
        ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
            rbDark.setSelected(ProgConfig.SYSTEM_DARK_THEME.get());
            rbLight.setSelected(!ProgConfig.SYSTEM_DARK_THEME.get());
        });


        ToggleGroup tg1 = new ToggleGroup();
        rbIcon1.setToggleGroup(tg1);
        rbIcon2.setToggleGroup(tg1);

        rbIcon1.selectedProperty().setValue(ProgConfig.SYSTEM_GUI_THEME_1.getValue());
        rbIcon2.selectedProperty().setValue(ProgConfig.SYSTEM_GUI_THEME_1.not().getValue());
        rbIcon1.selectedProperty().addListener((u, o, n) -> ProgConfig.SYSTEM_GUI_THEME_1.set(rbIcon1.isSelected()));
        ProgConfig.SYSTEM_GUI_THEME_1.addListener((u, o, n) -> {
            rbIcon1.setSelected(ProgConfig.SYSTEM_GUI_THEME_1.get());
            rbIcon2.setSelected(!ProgConfig.SYSTEM_GUI_THEME_1.get());
        });

        final Button btnHelpTheme = PIconFactory.getHelpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME);

        final Button btnHelpIcon = PIconFactory.getHelpButton(stage, "Erscheinungsbild der Icons",
                HelpText.THEME_ICON);

        btnReset.setOnAction(a -> reset());

        VBox vBox = new VBox(P2LibConst.SPACING_VBOX);

        // ====================
        // Dark-Light
        int row = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(rbDark, 0, row);
        gridPane.add(rbLight, 1, row, 2, 1);
        gridPane.add(btnHelpTheme, 3, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);
        gridPane.add(rbIcon1, 0, ++row);
        gridPane.add(rbIcon2, 1, row, 2, 1);
        gridPane.add(btnHelpIcon, 3, row);
        GridPane.setHalignment(btnHelpIcon, HPos.RIGHT);
        vBox.getChildren().addAll(gridPane);


        // ====================
        // Icon-Dark
        row = 0;
        gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(new Label("Dark-Icon-Theme 1"), 1, row);
        HBox hBoxD1 = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_1);
        gridPane.add(hBoxD1, 2, row, 2, 1);
        GridPane.setHalignment(hBoxD1, HPos.RIGHT);

        gridPane.add(new Label("Dark-Icon-Theme 2"), 1, ++row);
        HBox hBoxD2 = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_2);
        gridPane.add(hBoxD2, 2, row, 2, 1);
        GridPane.setHalignment(hBoxD2, HPos.RIGHT);

        gridPane.add(new Label("Dark-Gui-Theme 1"), 1, ++row);
        hBoxD1 = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_1);
        gridPane.add(hBoxD1, 2, row, 2, 1);
        GridPane.setHalignment(hBoxD1, HPos.RIGHT);

        gridPane.add(new Label("Dark-GUI-Theme 2"), 1, ++row);
        hBoxD2 = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_2);
        gridPane.add(hBoxD2, 2, row, 2, 1);
        GridPane.setHalignment(hBoxD2, HPos.RIGHT);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        tabDark.setContent(scrollPane);


        // ====================
        // Icon-Light
        row = 0;
        gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(new Label("Light-Icon-Theme 1"), 1, row);
        HBox hBoxL1 = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1);
        gridPane.add(hBoxL1, 2, row, 2, 1);
        GridPane.setHalignment(hBoxL1, HPos.RIGHT);

        gridPane.add(new Label("Light-Icon-Theme 2"), 1, ++row);
        HBox hBoxL2 = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2);
        gridPane.add(hBoxL2, 2, row, 2, 1);
        GridPane.setHalignment(hBoxL2, HPos.RIGHT);

        gridPane.add(new Label("Light-Gui-Theme 1"), 1, ++row);
        hBoxL1 = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_1);
        gridPane.add(hBoxL1, 2, row, 2, 1);
        GridPane.setHalignment(hBoxL1, HPos.RIGHT);

        gridPane.add(new Label("Light-Gui-Theme 2"), 1, ++row);
        hBoxL2 = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_2);
        gridPane.add(hBoxL2, 2, row, 2, 1);
        GridPane.setHalignment(hBoxL2, HPos.RIGHT);
        gridPane.setMinHeight(gridPane.getPrefHeight());
        scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        tabLight.setContent(scrollPane);


        // ============
        // Button Reset
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getChildren().add(btnReset);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);

        tabPane.getTabs().addAll(tabDark, tabLight);
        tabDark.setClosable(false);
        tabLight.setClosable(false);
        tabPane.setMinHeight(200);

        vBox.getChildren().addAll(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        vBox.getChildren().addAll(hBox);
        TitledPane tpColor = new TitledPane("Farbe Programm", vBox);
        result.add(tpColor);
    }

    private void reset() {
        ProgConfig.SYSTEM_ICON_THEME_DARK_1.setValue(ProgConst.ICON_COLOR_DARK_1);
        ProgConfig.SYSTEM_ICON_THEME_DARK_2.setValue(ProgConst.ICON_COLOR_DARK_2);
        ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.setValue(ProgConst.ICON_COLOR_LIGHT_1);
        ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.setValue(ProgConst.ICON_COLOR_LIGHT_2);

        ProgConfig.SYSTEM_GUI_THEME_DARK_1.setValue(ProgConst.GUI_COLOR_DARK_1);
        ProgConfig.SYSTEM_GUI_THEME_DARK_2.setValue(ProgConst.GUI_COLOR_DARK_2);
        ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.setValue(ProgConst.GUI_COLOR_LIGHT_1);
        ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.setValue(ProgConst.GUI_COLOR_LIGHT_2);
        ProgData.getInstance().pIconWorker.setColor();
    }

    private HBox addColor(StringProperty stringProperty) {
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("split-button");
        colorPicker.setMinHeight(Region.USE_PREF_SIZE);

        stringProperty.addListener((u, o, n) -> {
            colorPicker.setValue(Color.web(stringProperty.get()));
        });
        colorPicker.setValue(Color.web(stringProperty.get()));
        colorPicker.setOnAction(a -> {
            Color color = colorPicker.getValue();
            stringProperty.setValue(color.toString());
            ProgData.getInstance().pIconWorker.setColor();
        });

        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(2));
        hbox.getChildren().addAll(colorPicker);
        return hbox;
    }
}
