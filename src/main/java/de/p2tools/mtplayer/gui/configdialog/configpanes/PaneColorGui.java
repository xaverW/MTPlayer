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
import de.p2tools.p2lib.guitools.P2Text;
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

    private final RadioButton rbDark = new RadioButton("Dark-Theme");
    private final RadioButton rbLight = new RadioButton("Light-Theme");
    private final RadioButton rbIcon1 = new RadioButton("Icon-Theme 1");
    private final RadioButton rbIcon2 = new RadioButton("Icon-Theme 2");

    private final GridPane gridDark1 = new GridPane();
    private final GridPane gridDark2 = new GridPane();
    private final GridPane gridLight1 = new GridPane();
    private final GridPane gridLight2 = new GridPane();
    private final CheckBox chkEmptyDark1 = new CheckBox("Transparent");
    private final CheckBox chkEmptyDark2 = new CheckBox("Transparent");
    private final CheckBox chkEmptyLight1 = new CheckBox("Transparent");
    private final CheckBox chkEmptyLight2 = new CheckBox("Transparent");

    private final VBox vBox = new VBox(P2LibConst.SPACING_VBOX);

    public PaneColorGui(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        rbDark.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME);
        rbIcon1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_THEME_1);

        chkEmptyDark1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1);
        chkEmptyDark2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2);
        chkEmptyLight1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
        chkEmptyLight2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2);
    }

    public void make(Collection<TitledPane> result) {
        makeSelGrid();
        makeGrid();
        TitledPane tpColor = new TitledPane("Farbe Programm", vBox);
        result.add(tpColor);
    }

    private void makeSelGrid() {
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
    }

    private void makeGrid() {
        gridDark1.setHgap(20);
        gridDark1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridDark1.setPadding(new Insets(P2LibConst.PADDING));
        gridDark1.setMinHeight(200);
        gridDark1.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridDark2.setHgap(20);
        gridDark2.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridDark2.setPadding(new Insets(P2LibConst.PADDING));
        gridDark2.setMinHeight(200);
        gridDark2.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());


        gridLight1.setHgap(20);
        gridLight1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridLight1.setPadding(new Insets(P2LibConst.PADDING));
        gridLight1.setMinHeight(200);
        gridLight1.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridLight2.setHgap(20);
        gridLight2.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridLight2.setPadding(new Insets(P2LibConst.PADDING));
        gridLight2.setMinHeight(200);
        gridLight2.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        chkEmptyDark1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1);
        chkEmptyDark2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2);
        chkEmptyLight1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
        chkEmptyLight2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2);

        // ======
        // add Dark1
        Button btnReset = new Button("Zurücksetzen");
        btnReset.setOnAction(a -> reset());

        gridDark1.add(P2Text.getTextBoldUnderline("Dark-Theme 1", "white"), 0, 0);
        gridDark1.add(new Label("Icon-Theme"), 0, 1);
        HBox hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_1, null);
        gridDark1.add(hBox, 2, 1);

        gridDark1.add(new Label("Gui-Theme"), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_1, null);
        gridDark1.add(hBox, 2, 2);

        gridDark1.add(new Label("Background-Theme"), 0, 3);
        gridDark1.add(chkEmptyDark1, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1, chkEmptyDark1);
        gridDark1.add(hBox, 2, 3);

        gridDark1.add(new Label(), 0, 4);
        gridDark1.add(btnReset, 2, 5);
        GridPane.setHalignment(btnReset, HPos.RIGHT);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridDark1);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1));
        scrollPane.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1));
        vBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ======
        // add Dark2
        btnReset = new Button("Zurücksetzen");
        btnReset.setOnAction(a -> reset());

        gridDark2.add(P2Text.getTextBoldUnderline("Dark-Theme 2", "white"), 0, 0);
        gridDark2.add(new Label("Icon-Theme"), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_2, null);
        gridDark2.add(hBox, 2, 1);

        gridDark2.add(new Label("Gui-Theme"), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_2, null);
        gridDark2.add(hBox, 2, 2);

        gridDark2.add(new Label("Background-Theme"), 0, 3);
        gridDark2.add(chkEmptyDark2, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2, chkEmptyDark2);
        gridDark2.add(hBox, 2, 3);

        gridDark2.add(new Label(), 0, 4);
        gridDark2.add(btnReset, 2, 5);
        GridPane.setHalignment(btnReset, HPos.RIGHT);

        scrollPane = new ScrollPane();
        scrollPane.setContent(gridDark2);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        scrollPane.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        vBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ======
        // add Light1
        btnReset = new Button("Zurücksetzen");
        btnReset.setOnAction(a -> reset());

        gridLight1.add(P2Text.getTextBoldUnderline("Light-Theme 1"), 0, 0);
        gridLight1.visibleProperty().bind(rbIcon1.selectedProperty());
        gridLight1.add(new Label("Icon-Theme"), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1, null);
        gridLight1.add(hBox, 2, 1);

        gridLight1.add(new Label("Gui-Theme"), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_1, null);
        gridLight1.add(hBox, 2, 2);

        gridLight1.add(new Label("Background-Theme"), 0, 3);
        gridLight1.add(chkEmptyLight1, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1, chkEmptyLight1);
        gridLight1.add(hBox, 2, 3);

        gridLight1.add(new Label(), 0, 4);
        gridLight1.add(btnReset, 2, 5);
        GridPane.setHalignment(btnReset, HPos.RIGHT);

        scrollPane = new ScrollPane();
        scrollPane.setContent(gridLight1);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1));
        scrollPane.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1));
        vBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ======
        // add Light2
        btnReset = new Button("Zurücksetzen");
        btnReset.setOnAction(a -> reset());

        gridLight2.add(P2Text.getTextBoldUnderline("Light-Theme 2"), 0, 0);
        gridLight2.visibleProperty().bind(rbIcon2.selectedProperty());
        gridLight2.add(new Label("Icon-Theme"), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2, null);
        gridLight2.add(hBox, 2, 1);

        gridLight2.add(new Label("Gui-Theme"), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_2, null);
        gridLight2.add(hBox, 2, 2);

        gridLight2.add(new Label("Background-Theme"), 0, 3);
        gridLight2.add(chkEmptyLight2, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2, chkEmptyLight2);
        gridLight2.add(hBox, 2, 3);

        gridLight2.add(new Label(), 0, 4);
        gridLight2.add(btnReset, 2, 5);
        GridPane.setHalignment(btnReset, HPos.RIGHT);

        scrollPane = new ScrollPane();
        scrollPane.setContent(gridLight2);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        scrollPane.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        vBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }


    private void reset() {
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                // Dark1
                ProgConfig.SYSTEM_ICON_THEME_DARK_1.setValue(ProgConst.ICON_COLOR_DARK_1);
                ProgConfig.SYSTEM_GUI_THEME_DARK_1.setValue(ProgConst.GUI_COLOR_DARK_1);
                ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1.setValue(ProgConst.GUI_BACKGROUND_DARK_1);

            } else {
                // Dark2
                ProgConfig.SYSTEM_ICON_THEME_DARK_2.setValue(ProgConst.ICON_COLOR_DARK_2);
                ProgConfig.SYSTEM_GUI_THEME_DARK_2.setValue(ProgConst.GUI_COLOR_DARK_2);
                ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2.setValue(ProgConst.GUI_BACKGROUND_DARK_2);
            }

        } else {
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                // Light1
                ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.setValue(ProgConst.ICON_COLOR_LIGHT_1);
                ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.setValue(ProgConst.GUI_COLOR_LIGHT_1);
                ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1.setValue(ProgConst.GUI_BACKGROUND_LIGHT_1);

            } else {
                // Light2
                ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.setValue(ProgConst.ICON_COLOR_LIGHT_2);
                ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.setValue(ProgConst.GUI_COLOR_LIGHT_2);
                ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2.setValue(ProgConst.GUI_BACKGROUND_LIGHT_2);
            }
        }
        ProgData.getInstance().colorWorker.setColor();
    }

    private HBox addColor(StringProperty stringProperty, CheckBox checkBox) {
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("split-button");
        colorPicker.setMinHeight(Region.USE_PREF_SIZE);
        if (checkBox != null) {
            colorPicker.disableProperty().bind(checkBox.selectedProperty());
        }
        stringProperty.addListener((u, o, n) -> {
            if (!stringProperty.getValueSafe().isEmpty()) {
                colorPicker.setValue(Color.web(stringProperty.get()));
            }
        });
        colorPicker.setValue(Color.web(stringProperty.get()));
        if (checkBox != null) {
            checkBox.setOnAction(a -> {
                ProgData.getInstance().colorWorker.setColor();
            });
        }
        colorPicker.setOnAction(a -> {
            Color color = colorPicker.getValue();
            stringProperty.setValue(color.toString());
            ProgData.getInstance().colorWorker.setColor();
        });

        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(2));
        hbox.getChildren().addAll(colorPicker);
        return hbox;
    }
}
