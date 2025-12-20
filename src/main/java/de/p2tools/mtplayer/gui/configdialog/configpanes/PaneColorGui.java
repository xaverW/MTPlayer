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
import de.p2tools.p2lib.guitools.P2GuiTools;
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

    private final RadioButton rbDark = new RadioButton("Dunkel");
    private final RadioButton rbLight = new RadioButton("Hell");
    private final RadioButton rbIcon1 = new RadioButton("Gui-Farben 1");
    private final RadioButton rbIcon2 = new RadioButton("Gui-Farben 2");

    private final GridPane gridDark1 = new GridPane();
    private final GridPane gridDark2 = new GridPane();
    private final GridPane gridLight1 = new GridPane();
    private final GridPane gridLight2 = new GridPane();
    private final CheckBox chkBackgroundEmptyDark1 = new CheckBox("Transparent");
    private final CheckBox chkBackgroundEmptyDark2 = new CheckBox("Transparent");
    private final CheckBox chkBackgroundEmptyLight1 = new CheckBox("Transparent");
    private final CheckBox chkBackgroundEmptyLight2 = new CheckBox("Transparent");

    private final CheckBox chkTitleBarEmptyDark1 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarEmptyDark2 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarEmptyLight1 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarEmptyLight2 = new CheckBox("Transparent");

    private final CheckBox chkTitleBarSelEmptyDark1 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarSelEmptyDark2 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarSelEmptyLight1 = new CheckBox("Transparent");
    private final CheckBox chkTitleBarSelEmptyLight2 = new CheckBox("Transparent");

    private final String ICONS = "Icons";
    private final String GUI = "Programmfarbe";
    private final String BACKGROUND = "Hintergrund";
    private final String TITLE_BAR = "Schalter Titelzeile";
    private final String TITLE_BAR_SEL = "Schalter Titelzeile, ausgewählt";

    private final String DARK_1 = "Dunkel, Gui-Farben 1";
    private final String DARK_2 = "Dunkel, Gui-Farben 2";
    private final String LIGHT_1 = "Hell, Gui-Farben 1";
    private final String LIGHT_2 = "Hell, Gui-Farben 2";


    private final Button btnReset = new Button("Zurücksetzen");
    private final Button btnResetAll = new Button("Alles Zurücksetzen");
    private Button btnHelp;

    private final VBox vBox = new VBox(P2LibConst.SPACING_VBOX);

    public PaneColorGui(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        rbDark.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME);
        rbIcon1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_THEME_1);

        chkBackgroundEmptyDark1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1);
        chkBackgroundEmptyDark2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2);
        chkBackgroundEmptyLight1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
        chkBackgroundEmptyLight2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2);

        chkTitleBarEmptyDark1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_1);
        chkTitleBarEmptyDark2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_2);
        chkTitleBarEmptyLight1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_1);
        chkTitleBarEmptyLight2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_2);

        chkTitleBarSelEmptyDark1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1);
        chkTitleBarSelEmptyDark2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2);
        chkTitleBarSelEmptyLight1.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1);
        chkTitleBarSelEmptyLight2.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2);
    }

    public void make(Collection<TitledPane> result) {
        btnHelp = PIconFactory.getHelpButton(stage, "Zurücksetzen", "\"Zurücksetzen\" sets die " +
                "aktuellen Einstellungen zurücksetzen\n\n" +
                "\"Alles zurücksetzen\" löscht alle Einstellungen.");
        btnReset.setOnAction(a -> reset());
        btnResetAll.setOnAction(a -> resetAll());
        makeSelGrid();
        makeGrid();
        makeReset();
        TitledPane tpColor = new TitledPane("Farbe Programm", vBox);
        result.add(tpColor);
    }

    private void reset() {
        if (ProgConfig.SYSTEM_DARK_THEME.get() && ProgConfig.SYSTEM_GUI_THEME_1.get()) {
            resetDark1();
        } else if (ProgConfig.SYSTEM_DARK_THEME.get() && !ProgConfig.SYSTEM_GUI_THEME_1.get()) {
            resetDark2();
        } else if (!ProgConfig.SYSTEM_DARK_THEME.get() && ProgConfig.SYSTEM_GUI_THEME_1.get()) {
            resetLight1();
        } else {
            resetLight2();
        }
    }

    private void makeReset() {
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(P2GuiTools.getHBoxGrower(), btnResetAll, btnReset, btnHelp);
        vBox.getChildren().add(hBox);
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
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(rbDark, 0, row);
        gridPane.add(rbLight, 1, row);
        gridPane.add(btnHelpTheme, 3, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);
        gridPane.add(rbIcon1, 0, ++row);
        gridPane.add(rbIcon2, 1, row);
        gridPane.add(btnHelpIcon, 3, row);

        gridPane.getStyleClass().add("pBorder-2");
        vBox.getChildren().addAll(gridPane);
    }

    private void makeGrid() {
        gridDark1.setHgap(20);
        gridDark1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridDark1.setPadding(new Insets(P2LibConst.PADDING));
        gridDark1.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridDark2.setHgap(20);
        gridDark2.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridDark2.setPadding(new Insets(P2LibConst.PADDING));
        gridDark2.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridLight1.setHgap(20);
        gridLight1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridLight1.setPadding(new Insets(P2LibConst.PADDING));
        gridLight1.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        gridLight2.setHgap(20);
        gridLight2.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridLight2.setPadding(new Insets(P2LibConst.PADDING));
        gridLight2.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight(),
                P2GridConstraints.getCcPrefSize());

        chkBackgroundEmptyDark1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1);
        chkBackgroundEmptyDark2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2);
        chkBackgroundEmptyLight1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
        chkBackgroundEmptyLight2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2);

        chkTitleBarEmptyDark1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_1);
        chkTitleBarEmptyDark2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_2);
        chkTitleBarEmptyLight1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_1);
        chkTitleBarEmptyLight2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_2);

        chkTitleBarSelEmptyDark1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1);
        chkTitleBarSelEmptyDark2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2);
        chkTitleBarSelEmptyLight1.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1);
        chkTitleBarSelEmptyLight2.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2);

        VBox innerVbox = new VBox();
        innerVbox.getStyleClass().add("pBorder-1");

        // ======
        // add Dark1
        gridDark1.add(P2Text.getTextBoldUnderline(DARK_1, "white"), 0, 0);
        gridDark1.add(new Label(ICONS), 0, 1);
        HBox hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_1, null);
        gridDark1.add(hBox, 2, 1);

        gridDark1.add(new Label(GUI), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_1, null);
        gridDark1.add(hBox, 2, 2);

        gridDark1.add(new Label(BACKGROUND), 0, 3);
        gridDark1.add(chkBackgroundEmptyDark1, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1, chkBackgroundEmptyDark1);
        gridDark1.add(hBox, 2, 3);

        gridDark1.add(new Label(TITLE_BAR), 0, 4);
        gridDark1.add(chkTitleBarEmptyDark1, 1, 4);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_1, chkTitleBarEmptyDark1);
        gridDark1.add(hBox, 2, 4);

        gridDark1.add(new Label(TITLE_BAR_SEL), 0, 5);
        gridDark1.add(chkTitleBarSelEmptyDark1, 1, 5);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_1, chkTitleBarSelEmptyDark1);
        gridDark1.add(hBox, 2, 5);

        innerVbox.getChildren().add(gridDark1);
        VBox.setVgrow(gridDark1, Priority.ALWAYS);
        gridDark1.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1));
        gridDark1.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1));


        // ======
        // add Dark2
        gridDark2.add(P2Text.getTextBoldUnderline(DARK_2, "white"), 0, 0);
        gridDark2.add(new Label(ICONS), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_DARK_2, null);
        gridDark2.add(hBox, 2, 1);

        gridDark2.add(new Label(GUI), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_DARK_2, null);
        gridDark2.add(hBox, 2, 2);

        gridDark2.add(new Label(BACKGROUND), 0, 3);
        gridDark2.add(chkBackgroundEmptyDark2, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2, chkBackgroundEmptyDark2);
        gridDark2.add(hBox, 2, 3);

        gridDark2.add(new Label(TITLE_BAR), 0, 4);
        gridDark2.add(chkTitleBarEmptyDark2, 1, 4);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_2, chkTitleBarEmptyDark2);
        gridDark2.add(hBox, 2, 4);

        gridDark2.add(new Label(TITLE_BAR_SEL), 0, 5);
        gridDark2.add(chkTitleBarSelEmptyDark2, 1, 5);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_2, chkTitleBarSelEmptyDark2);
        gridDark2.add(hBox, 2, 5);

        gridDark2.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        gridDark2.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        innerVbox.getChildren().add(gridDark2);
        VBox.setVgrow(gridDark2, Priority.ALWAYS);


        // ======
        // add Light1
        gridLight1.add(P2Text.getTextBoldUnderline(LIGHT_1), 0, 0);
        gridLight1.visibleProperty().bind(rbIcon1.selectedProperty());
        gridLight1.add(new Label(ICONS), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1, null);
        gridLight1.add(hBox, 2, 1);

        gridLight1.add(new Label(GUI), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_1, null);
        gridLight1.add(hBox, 2, 2);

        gridLight1.add(new Label(BACKGROUND), 0, 3);
        gridLight1.add(chkBackgroundEmptyLight1, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1, chkBackgroundEmptyLight1);
        gridLight1.add(hBox, 2, 3);

        gridLight1.add(new Label(TITLE_BAR), 0, 4);
        gridLight1.add(chkTitleBarEmptyLight1, 1, 4);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_1, chkTitleBarEmptyLight1);
        gridLight1.add(hBox, 2, 4);

        gridLight1.add(new Label(TITLE_BAR_SEL), 0, 5);
        gridLight1.add(chkTitleBarSelEmptyLight1, 1, 5);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_1, chkTitleBarSelEmptyLight1);
        gridLight1.add(hBox, 2, 5);

        gridLight1.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1));
        gridLight1.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1));
        innerVbox.getChildren().add(gridLight1);
        VBox.setVgrow(gridLight1, Priority.ALWAYS);


        // ======
        // add Light2
        gridLight2.add(P2Text.getTextBoldUnderline(LIGHT_2), 0, 0);
        gridLight2.visibleProperty().bind(rbIcon2.selectedProperty());
        gridLight2.add(new Label(ICONS), 0, 1);
        hBox = addColor(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2, null);
        gridLight2.add(hBox, 2, 1);

        gridLight2.add(new Label(GUI), 0, 2);
        hBox = addColor(ProgConfig.SYSTEM_GUI_THEME_LIGHT_2, null);
        gridLight2.add(hBox, 2, 2);

        gridLight2.add(new Label(BACKGROUND), 0, 3);
        gridLight2.add(chkBackgroundEmptyLight2, 1, 3);
        hBox = addColor(ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2, chkBackgroundEmptyLight2);
        gridLight2.add(hBox, 2, 3);

        gridLight2.add(new Label(TITLE_BAR), 0, 4);
        gridLight2.add(chkTitleBarEmptyLight2, 1, 4);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_2, chkTitleBarEmptyLight2);
        gridLight2.add(hBox, 2, 4);

        gridLight2.add(new Label(TITLE_BAR_SEL), 0, 5);
        gridLight2.add(chkTitleBarSelEmptyLight2, 1, 5);
        hBox = addColor(ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_2, chkTitleBarSelEmptyLight2);
        gridLight2.add(hBox, 2, 5);

        gridLight2.visibleProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        gridLight2.managedProperty().bind(ProgConfig.SYSTEM_DARK_THEME.not().and(ProgConfig.SYSTEM_GUI_THEME_1.not()));
        innerVbox.getChildren().add(gridLight2);
        VBox.setVgrow(gridLight2, Priority.ALWAYS);

        vBox.getChildren().add(innerVbox);
    }

    private void resetAll() {
        rD1();
        rD2();
        rL1();
        rL2();
        ProgData.getInstance().colorWorker.setColor();
    }

    private void resetDark1() {
        rD1();
        ProgData.getInstance().colorWorker.setColor();
    }

    private void rD1() {
        ProgConfig.SYSTEM_ICON_THEME_DARK_1.setValue(ProgConst.ICON_COLOR_DARK_1);
        ProgConfig.SYSTEM_GUI_THEME_DARK_1.setValue(ProgConst.GUI_COLOR_DARK_1);
        ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1.setValue(ProgConst.GUI_BACKGROUND_DARK_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_1.setValue(ProgConst.GUI_TITLE_BAR_DARK_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_1.setValue(ProgConst.GUI_TITLE_BAR_SEL_DARK_1);

        ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1.set(ProgConst.GUI_BACKGROUND_TRANSPARENT_DARK_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_1.set(ProgConst.GUI_TITLE_BAR_TRANSPARENT_DARK_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1.set(ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1);
    }

    private void resetDark2() {
        rD2();
        ProgData.getInstance().colorWorker.setColor();
    }

    private void rD2() {
        ProgConfig.SYSTEM_ICON_THEME_DARK_2.setValue(ProgConst.ICON_COLOR_DARK_2);
        ProgConfig.SYSTEM_GUI_THEME_DARK_2.setValue(ProgConst.GUI_COLOR_DARK_2);
        ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2.setValue(ProgConst.GUI_BACKGROUND_DARK_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_2.setValue(ProgConst.GUI_TITLE_BAR_DARK_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_2.setValue(ProgConst.GUI_TITLE_BAR_SEL_DARK_2);

        ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2.set(ProgConst.GUI_BACKGROUND_TRANSPARENT_DARK_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_2.set(ProgConst.GUI_TITLE_BAR_TRANSPARENT_DARK_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2.set(ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2);
    }

    private void resetLight1() {
        rL1();
        ProgData.getInstance().colorWorker.setColor();
    }

    private void rL1() {
        ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.setValue(ProgConst.ICON_COLOR_LIGHT_1);
        ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.setValue(ProgConst.GUI_COLOR_LIGHT_1);
        ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1.setValue(ProgConst.GUI_BACKGROUND_LIGHT_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_1.setValue(ProgConst.GUI_TITLE_BAR_LIGHT_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_1.setValue(ProgConst.GUI_TITLE_BAR_SEL_LIGHT_1);

        ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1.set(ProgConst.GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_1.set(ProgConst.GUI_TITLE_BAR_TRANSPARENT_LIGHT_1);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1.set(ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1);
    }

    private void resetLight2() {
        rL2();
        ProgData.getInstance().colorWorker.setColor();
    }

    private void rL2() {
        ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.setValue(ProgConst.ICON_COLOR_LIGHT_2);
        ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.setValue(ProgConst.GUI_COLOR_LIGHT_2);
        ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2.setValue(ProgConst.GUI_BACKGROUND_LIGHT_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_2.setValue(ProgConst.GUI_TITLE_BAR_LIGHT_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_2.setValue(ProgConst.GUI_TITLE_BAR_SEL_LIGHT_2);

        ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2.set(ProgConst.GUI_BACKGROUND_TRANSPARENT_LIGHT_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_2.set(ProgConst.GUI_TITLE_BAR_TRANSPARENT_LIGHT_2);
        ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2.set(ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2);
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
