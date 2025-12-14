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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneColorMode {
    private final HBox hBoxLight1 = new HBox();
    private final HBox hBoxLight2 = new HBox();
    private final HBox hBoxDark1 = new HBox();
    private final HBox hBoxDark2 = new HBox();

    public StartPaneColorMode(Stage stage) {
    }

    public void close() {
    }

    public TitledPane make() {
        makeImage();
        setHBox();
        VBox vBox = new VBox(10);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        Label lbl = new Label("Wie soll die Programmoberfläche aussehen?");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox);

        ProgConfig.SYSTEM_ICON_THEME_1_START.addListener((u, o, n) -> {
            setHBox();
        });
        ProgConfig.SYSTEM_THEME_DARK_START.addListener((u, o, n) -> {
            setHBox();
        });

        final GridPane gridPaneGui = new GridPane();
        gridPaneGui.setHgap(3);
        gridPaneGui.setVgap(3);

        gridPaneGui.add(hBoxLight1, 0, 0);
        gridPaneGui.add(hBoxLight2, 1, 0);
        gridPaneGui.add(hBoxDark1, 0, 1);
        gridPaneGui.add(hBoxDark2, 1, 1);

        gridPaneGui.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize());

        HBox hBoxColor = new HBox();
        hBoxColor.setAlignment(Pos.CENTER);
        hBoxColor.getChildren().add(gridPaneGui);
        vBox.getChildren().add(hBoxColor);

        return new TitledPane("Farbe", vBox);
    }

    private void setHBox() {
        final String colorSel = "#4682B4;";
        final String color = "transparent;";

        if (!ProgConfig.SYSTEM_THEME_DARK_START.get() && ProgConfig.SYSTEM_ICON_THEME_1_START.get()) {
            hBoxLight1.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxLight2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
        } else if (!ProgConfig.SYSTEM_THEME_DARK_START.get() && !ProgConfig.SYSTEM_ICON_THEME_1_START.get()) {
            hBoxLight1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxLight2.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
        } else if (ProgConfig.SYSTEM_THEME_DARK_START.get() && ProgConfig.SYSTEM_ICON_THEME_1_START.get()) {
            hBoxLight1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxLight2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark1.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
        } else {
            hBoxLight1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxLight2.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark1.setStyle("-fx-border-color: " + color + " -fx-border-style: solid; -fx-border-width: 8;");
            hBoxDark2.setStyle("-fx-border-color: " + colorSel + " -fx-border-style: solid; -fx-border-width: 8;");
        }
    }

    private void makeImage() {
        final int size = 300;
        ImageView ivLight1 = new ImageView();
        hBoxLight1.getChildren().add(ivLight1);
        String path = "/de/p2tools/mtplayer/res/startdialog/gui_light_1.png";
        Image image = new Image(path, size, size, true, true);
        ivLight1.setSmooth(true);
        ivLight1.setImage(image);
        ivLight1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ProgConfig.SYSTEM_THEME_DARK_START.setValue(false);
            ProgConfig.SYSTEM_ICON_THEME_1_START.setValue(true);
            setHBox();
            event.consume();
        });

        ImageView ivLight2 = new ImageView();
        hBoxLight2.getChildren().add(ivLight2);
        path = "/de/p2tools/mtplayer/res/startdialog/gui_light_2.png";
        image = new Image(path, size, size, true, true);
        ivLight2.setSmooth(true);
        ivLight2.setImage(image);
        ivLight2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ProgConfig.SYSTEM_THEME_DARK_START.setValue(false);
            ProgConfig.SYSTEM_ICON_THEME_1_START.setValue(false);
            setHBox();
            event.consume();
        });

        ImageView ivDark1 = new ImageView();
        path = "/de/p2tools/mtplayer/res/startdialog/gui_dark_1.png";
        image = new Image(path, size, size, true, true);
        ivDark1.setSmooth(true);
        ivDark1.setImage(image);
        ivDark1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ProgConfig.SYSTEM_THEME_DARK_START.setValue(true);
            ProgConfig.SYSTEM_ICON_THEME_1_START.setValue(true);
            setHBox();
            event.consume();
        });
        hBoxDark1.getChildren().add(ivDark1);

        ImageView ivDark2 = new ImageView();
        hBoxDark2.getChildren().add(ivDark2);
        path = "/de/p2tools/mtplayer/res/startdialog/gui_dark_2.png";
        image = new Image(path, size, size, true, true);
        ivDark2.setSmooth(true);
        ivDark2.setImage(image);
        ivDark2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ProgConfig.SYSTEM_THEME_DARK_START.setValue(true);
            ProgConfig.SYSTEM_ICON_THEME_1_START.setValue(false);
            setHBox();
            event.consume();
        });
    }
}
