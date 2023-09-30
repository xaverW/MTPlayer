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

package de.p2tools.mtplayer.gui.mediacleaning;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PaneCleaningConfigController {

    private final ProgData progData;
    private final Stage stage;
    private boolean media;

    private final P2ToggleSwitch tglExact = new P2ToggleSwitch("Exakt den Begriff suchen");
    private final P2ToggleSwitch tglClean = new P2ToggleSwitch("Putzen");
    private final P2ToggleSwitch tglAndOr = new P2ToggleSwitch("Verknüpfen mit UND [sonst ODER]");
    private final P2ToggleSwitch tglNum = new P2ToggleSwitch("Zahlen entfernen: 20, 20.");
    private final P2ToggleSwitch tglDate = new P2ToggleSwitch("Datum entfernen: 20.03.2023");
    private final P2ToggleSwitch tglClip = new P2ToggleSwitch("Klammern entfernen: [], {}, ()");
    private final P2ToggleSwitch tglList = new P2ToggleSwitch("Cleaning Liste anwenden");

    public PaneCleaningConfigController(Stage stage, boolean media) {
        this.stage = stage;
        this.media = media;
        progData = ProgData.getInstance();
    }

    public void close() {
    }

    public AnchorPane makePane() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));
        initPane();
        addGrid(vBox);
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        anchorPane.getChildren().add(vBox);
        return anchorPane;
    }


    private void initPane() {
        tglExact.selectedProperty().bindBidirectional(media ? ProgConfig.GUI_MEDIA_CLEAN_EXACT_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_EXACT_ABO);

        tglClean.disableProperty().bind(tglExact.selectedProperty());
        tglClean.selectedProperty().bindBidirectional(media ? ProgConfig.GUI_MEDIA_CLEAN_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_ABO);

        tglAndOr.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglAndOr.selectedProperty().bindBidirectional(media ? ProgConfig.GUI_MEDIA_CLEAN_AND_OR_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_AND_OR_ABO);

        tglNum.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglNum.selectedProperty().bindBidirectional(media ?
                ProgConfig.GUI_MEDIA_CLEAN_NUMBER_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_NUMBER_ABO);

        tglDate.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglDate.selectedProperty().bindBidirectional(media ?
                ProgConfig.GUI_MEDIA_CLEAN_DATE_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_DATE_ABO);

        tglClip.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglClip.selectedProperty().bindBidirectional(media ?
                ProgConfig.GUI_MEDIA_CLEAN_CLIP_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_CLIP_ABO);

        tglList.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglList.selectedProperty().bindBidirectional(media ?
                ProgConfig.GUI_MEDIA_CLEAN_LIST_MEDIA : ProgConfig.GUI_MEDIA_CLEAN_LIST_ABO);
    }

    private void addGrid(VBox vBox) {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize());
        vBox.getChildren().addAll(gridPane);

        //Suchen was
        if (media) {
            Text text = new Text("Suche in der ");
            text.setFont(Font.font(null, FontWeight.BOLD, -1));
            text.getStyleClass().add("downloadGuiMediaText");
            Text txtMed = new Text("Mediensammlung");
            txtMed.setUnderline(true);
            txtMed.setFont(Font.font(null, FontWeight.BOLD, -1));
            txtMed.getStyleClass().add("downloadGuiMediaText");
            HBox h = new HBox(0);
            h.getChildren().addAll(text, txtMed);
            gridPane.add(h, 0, row);

        } else {
            Text text = new Text("Suche in den ");
            text.setFont(Font.font(null, FontWeight.BOLD, -1));
            text.getStyleClass().add("downloadGuiMediaText");
            Text txtAbo = new Text("Abos und der History");
            txtAbo.setFont(Font.font(null, FontWeight.BOLD, -1));
            txtAbo.getStyleClass().add("downloadGuiMediaText");
            txtAbo.setUnderline(true);
            HBox h = new HBox(0);
            h.getChildren().addAll(text, txtAbo);
            gridPane.add(h, 0, row);
        }

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(tglExact, 0, ++row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(tglClean, 0, ++row);

        ++row;
        ++row;
        gridPane.add(tglAndOr, 0, ++row);
        gridPane.add(tglNum, 0, ++row);
        gridPane.add(tglDate, 0, ++row);
        gridPane.add(tglClip, 0, ++row);
        gridPane.add(tglList, 0, ++row);
    }
}
