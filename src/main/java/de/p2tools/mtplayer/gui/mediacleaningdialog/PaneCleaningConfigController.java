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

package de.p2tools.mtplayer.gui.mediacleaningdialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
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
    private final MediaDataDto mediaDataDto;

    private final P2ToggleSwitch tglExact = new P2ToggleSwitch("Exakt den Begriff suchen");
    private final P2ToggleSwitch tglClean = new P2ToggleSwitch("Putzen");
    private final P2ToggleSwitch tglList = new P2ToggleSwitch("Zusätzlich Cleaning-Liste anwenden");
    private final P2ToggleSwitch tglAndOr = new P2ToggleSwitch("Suchbegriffe verknüpfen mit UND [sonst ODER]");

    public PaneCleaningConfigController(Stage stage, MediaDataDto mediaDataDto) {
        this.stage = stage;
        progData = ProgData.getInstance();
        this.mediaDataDto = mediaDataDto;
    }

    public void close() {
        tglExact.selectedProperty().unbindBidirectional(mediaDataDto.cleaningExact);
        tglClean.selectedProperty().unbindBidirectional(mediaDataDto.cleaning);
        tglAndOr.selectedProperty().unbindBidirectional(mediaDataDto.cleaningAndOr);
        tglList.selectedProperty().unbindBidirectional(mediaDataDto.cleaningList);
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
        tglExact.selectedProperty().bindBidirectional(mediaDataDto.cleaningExact);

        tglClean.disableProperty().bind(tglExact.selectedProperty());
        tglClean.selectedProperty().bindBidirectional(mediaDataDto.cleaning);

        tglAndOr.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglAndOr.selectedProperty().bindBidirectional(mediaDataDto.cleaningAndOr);

        tglList.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglList.selectedProperty().bindBidirectional(mediaDataDto.cleaningList);
    }

    private void addGrid(VBox vBox) {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize());
        vBox.getChildren().addAll(gridPane);

        //Suchen was: Medien/Abo
        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_MEDIA) {
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
        gridPane.add(tglList, 0, ++row);
        gridPane.add(tglAndOr, 0, ++row);
    }
}
