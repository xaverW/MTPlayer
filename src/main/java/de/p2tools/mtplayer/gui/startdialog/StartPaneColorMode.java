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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneColorMode {
    private final Stage stage;
    HBox hBoxColor = new HBox();
    private final P2ToggleSwitch tglDarkTheme = new P2ToggleSwitch("Dunkles Erscheinungsbild der Programmoberfläche");
    private final P2ToggleSwitch tglBlackWhiteIcon = new P2ToggleSwitch("Schwarz-Weiße Icons");
    private final HBox hBoxImage0 = new HBox(); // !weiß / !dark
    private final HBox hBoxImage1 = new HBox(); // weiß / !dark
    private final HBox hBoxImage2 = new HBox(); // !weiß / dark
    private final HBox hBoxImage3 = new HBox(); // weiß / dark

    public StartPaneColorMode(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglDarkTheme.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME_START);
        tglBlackWhiteIcon.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACK_WHITE_ICON_START);
    }

    public TitledPane make() {
        makeImage();
        setHBox();
        VBox vBox = new VBox(10);
        hBoxColor.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Wie soll die Programmoberfläche aussehen?");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        tglDarkTheme.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME_START);
        final Button btnHelpTheme = P2Button.helpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.DARK_THEME);
        tglBlackWhiteIcon.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACK_WHITE_ICON_START);
        final Button btnHelpIcon = P2Button.helpButton(stage, "Erscheinungsbild der Programmoberfläche",
                HelpText.BLACK_WHITE_ICON);

        ProgConfig.SYSTEM_DARK_THEME_START.addListener((u, o, n) -> {
            setHBox();
        });
        ProgConfig.SYSTEM_BLACK_WHITE_ICON_START.addListener((u, o, n) -> {
            setHBox();
        });

        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        gridPane.add(tglDarkTheme, 0, row);
        gridPane.add(btnHelpTheme, 1, row);
        GridPane.setHalignment(btnHelpTheme, HPos.RIGHT);

        gridPane.add(tglBlackWhiteIcon, 0, ++row);
        gridPane.add(btnHelpIcon, 1, row);
        GridPane.setHalignment(btnHelpIcon, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
        vBox.getChildren().add(gridPane);

        final GridPane gridPaneGui = new GridPane();
        gridPaneGui.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPaneGui.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPaneGui.setPadding(new Insets(P2LibConst.PADDING));

        gridPaneGui.add(hBoxImage0, 0, 0);
        gridPaneGui.add(hBoxImage1, 1, 0);
        gridPaneGui.add(hBoxImage2, 0, 1);
        gridPaneGui.add(hBoxImage3, 1, 1);

        gridPaneGui.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());
        hBoxColor.getChildren().add(gridPaneGui);
        vBox.getChildren().add(hBoxColor);

        return new TitledPane("Farbe", vBox);
    }

    private void setHBox() {
        int i;
        if (!ProgConfig.SYSTEM_BLACK_WHITE_ICON_START.get() && !ProgConfig.SYSTEM_DARK_THEME_START.get()) {
            i = 0;
        } else if (ProgConfig.SYSTEM_BLACK_WHITE_ICON_START.get() && !ProgConfig.SYSTEM_DARK_THEME_START.get()) {
            i = 1;
        } else if (!ProgConfig.SYSTEM_BLACK_WHITE_ICON_START.get() && ProgConfig.SYSTEM_DARK_THEME_START.get()) {
            i = 2;
        } else {
            i = 3;
        }

        final String colorSel = "#4682B4;";
        final String color = "transparent;";

        switch (i) {
            case 0 -> {
                hBoxImage0.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage3.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            }
            case 1 -> {
                hBoxImage0.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage1.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage3.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            }
            case 2 -> {
                hBoxImage0.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage2.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage3.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            }
            case 3 -> {
                hBoxImage0.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
                hBoxImage3.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
            }
        }
    }

    private void makeImage() {
        final int size = 350;
        ImageView iv0 = new ImageView();
        hBoxImage0.getChildren().add(iv0);
        String path = "/de/p2tools/mtplayer/res/startdialog/gui_color_" + 0 + ".png";
        Image image = new Image(path, size, size, true, true);
        iv0.setSmooth(true);
        iv0.setImage(image);
        iv0.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tglBlackWhiteIcon.setSelected(false);
                tglDarkTheme.setSelected(false);
                setHBox();
                event.consume();
            }
        });

        ImageView iv1 = new ImageView();
        hBoxImage1.getChildren().add(iv1);
        path = "/de/p2tools/mtplayer/res/startdialog/gui_color_" + 1 + ".png";
        image = new Image(path, size, size, true, true);
        iv1.setSmooth(true);
        iv1.setImage(image);
        iv1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tglBlackWhiteIcon.setSelected(true);
                tglDarkTheme.setSelected(false);
                setHBox();
                event.consume();
            }
        });

        ImageView iv2 = new ImageView();
        hBoxImage2.getChildren().add(iv2);
        path = "/de/p2tools/mtplayer/res/startdialog/gui_color_" + 2 + ".png";
        image = new Image(path, size, size, true, true);
        iv2.setSmooth(true);
        iv2.setImage(image);
        iv2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tglBlackWhiteIcon.setSelected(false);
                tglDarkTheme.setSelected(true);
                setHBox();
                event.consume();
            }
        });

        ImageView iv3 = new ImageView();
        hBoxImage3.getChildren().add(iv3);
        path = "/de/p2tools/mtplayer/res/startdialog/gui_color_" + 3 + ".png";
        image = new Image(path, size, size, true, true);
        iv3.setSmooth(true);
        iv3.setImage(image);
        iv3.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tglBlackWhiteIcon.setSelected(true);
                tglDarkTheme.setSelected(true);
                setHBox();
                event.consume();
            }
        });
    }
}
