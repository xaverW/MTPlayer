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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneCleaningConfigController {

    private final ProgData progData;
    private final Stage stage;
    private boolean media;

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
        initPane(vBox);
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        anchorPane.getChildren().add(vBox);
        return anchorPane;
    }


    private void initPane(VBox vBox) {
        //Media
        RadioButton rbTheme = new RadioButton("Thema des Films");
        RadioButton rbTitel = new RadioButton("Titel des Films");
        RadioButton rbTT = new RadioButton("Thema-Titel des Films");
        ToggleGroup tg = new ToggleGroup();
        rbTitel.setToggleGroup(tg);
        rbTheme.setToggleGroup(tg);
        rbTT.setToggleGroup(tg);
        switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME:
                rbTheme.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL:
                rbTitel.setSelected(true);
                break;
            default:
                rbTT.setSelected(true);
                break;
        }
        rbTheme.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME);
                }
            }
        });
        rbTitel.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                }
            }
        });
        rbTT.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                }
            }
        });

        PToggleSwitch tglExact = new PToggleSwitch("Exakt den Begriff suchen");
        tglExact.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_EXACT_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_EXACT_ABO);

        PToggleSwitch tglClean = new PToggleSwitch("Putzen");
        tglClean.disableProperty().bind(tglExact.selectedProperty());
        tglClean.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_ABO);

        PToggleSwitch tglAndOr = new PToggleSwitch("Verknüpfen mit UND [sonst ODER]");
        tglAndOr.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglAndOr.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO);

        PToggleSwitch tglNum = new PToggleSwitch("Zahlen entfernen: 20, 20.");
        tglNum.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglNum.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_ABO);

        PToggleSwitch tglDate = new PToggleSwitch("Datum entfernen: 20.03.2023");
        tglDate.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglDate.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_ABO);

        PToggleSwitch tglClip = new PToggleSwitch("Klammern entfernen: [], {}, ()");
        tglClip.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglClip.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_ABO);

        PToggleSwitch tglList = new PToggleSwitch("Cleaning Liste anwenden");
        tglList.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglList.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_ABO);

        final Button btnHelp = PButton.helpButton(stage, "Putzen",
                HelpText.CLEANING_MEDIA);

        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
        vBox.getChildren().addAll(gridPane);

        gridPane.add(new Label("Zum Suchen verwenden:"), 0, row);
        gridPane.add(btnHelp, 1, row, 1, 2);
        GridPane.setValignment(btnHelp, VPos.TOP);
        
        gridPane.add(rbTheme, 0, ++row);
        gridPane.add(rbTitel, 0, ++row);
        gridPane.add(rbTT, 0, ++row);

        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(tglExact, 0, ++row);

        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(tglClean, 0, ++row);

        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(tglAndOr, 0, ++row);
        gridPane.add(tglNum, 0, ++row);
        gridPane.add(tglDate, 0, ++row);
        gridPane.add(tglClip, 0, ++row);
        gridPane.add(tglList, 0, ++row);
    }
}
