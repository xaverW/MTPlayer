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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneDownloadPath {
    private final TextField txtPath = new TextField();
    private final Stage stage;

    public StartPaneDownloadPath(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        txtPath.textProperty().unbindBidirectional(ProgConfig.DOWNLOAD_PATH);
    }

    public TitledPane makePath() {
        VBox vBox = new VBox(10);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Der Ordner, der beim Speichern vorgeschlagen wird, kann hier ausgewählt werden.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        if (ProgData.debug) {
            //dann einen anderen Downloadpfad
            ProgConfig.DOWNLOAD_PATH.setValue("/tmp/Download");
        }
        txtPath.textProperty().bindBidirectional(ProgConfig.DOWNLOAD_PATH);
        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            P2DirFileChooser.DirChooser(stage, txtPath);
        });
        btnFile.setGraphic(PIconFactory.PICON.BTN_DIR_OPEN.getFontIcon());
        btnFile.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen"));

        final Button btnHelp = PIconFactory.getHelpButton(stage,
                "Zielverzeichnis",
                "Hier kann das Verzeichnis angegeben werden, " +
                        "in dem die Downloads gespeichert werden.");

        int row = 0;
        gridPane.add(new Label("Pfad:"), 0, row);
        gridPane.add(txtPath, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnHelp, 3, row);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(), P2GridConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);

        return new TitledPane("Pfad für die Downloads", vBox);
    }
}
