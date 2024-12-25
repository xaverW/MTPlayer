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
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneColorMode {
    private final Stage stage;
    HBox hBoxColor = new HBox();
    private final P2ToggleSwitch tglDarkTheme = new P2ToggleSwitch("Dunkles Erscheinungsbild der Programmoberfläche");
    private final P2ToggleSwitch tglBlackWhiteIcon = new P2ToggleSwitch("Schwarz-Weiße Icons");

    public PaneColorMode(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglDarkTheme.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_DARK_THEME_START);
        tglBlackWhiteIcon.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACK_WHITE_ICON_START);
    }

    public TitledPane make() {
        VBox vBox = new VBox(10);
        hBoxColor.setAlignment(Pos.CENTER);

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
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

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

        gridPaneGui.add(getImage(0, true), 0, 0);
        gridPaneGui.add(getImage(1, true), 1, 0);
        gridPaneGui.add(getImage(2, true), 0, 1);
        gridPaneGui.add(getImage(3, true), 1, 1);

        gridPaneGui.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());
        hBoxColor.getChildren().add(gridPaneGui);
        vBox.getChildren().add(hBoxColor);

        return new TitledPane("Farbe", vBox);
    }

    private void setHBox() {
        hBoxColor.getChildren().clear();

        if (!tglDarkTheme.isSelected() && !tglBlackWhiteIcon.isSelected()) {
            // hell / bunt
            hBoxColor.getChildren().add(getImage(0, false));

        } else if (!tglDarkTheme.isSelected() && tglBlackWhiteIcon.isSelected()) {
            // dark / bunt
            hBoxColor.getChildren().add(getImage(1, false));

        } else if (tglDarkTheme.isSelected() && !tglBlackWhiteIcon.isSelected()) {
            // hell / weiß
            hBoxColor.getChildren().add(getImage(2, false));

        } else {
            // dark / weiß
            hBoxColor.getChildren().add(getImage(3, false));
        }
    }

    private ImageView getImage(int i, boolean vorschau) {
        final String path = "/de/p2tools/mtplayer/res/startdialog/gui_color_" + i + (vorschau ? "_" : "") + ".png";
        Image image = new Image(path, vorschau ? 400 : 600, vorschau ? 400 : 600, true, true);
        ImageView iv = new ImageView();
        iv.setSmooth(true);
        iv.setImage(image);
        return iv;
    }
}
