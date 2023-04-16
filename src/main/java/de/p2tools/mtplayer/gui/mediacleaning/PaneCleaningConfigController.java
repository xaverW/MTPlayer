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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PaneCleaningConfigController {

    private final ProgData progData;
    private final Stage stage;
    private boolean media;
    private final RadioButton rbTitel = new RadioButton("Titel des Films");
    private final RadioButton rbTT = new RadioButton("Thema und Titel des Films");
    private final RadioButton rbInTitel = new RadioButton();
    private final RadioButton rbInTT = new RadioButton();

    private final PToggleSwitch tglExact = new PToggleSwitch("Exakt den Begriff suchen");
    private final PToggleSwitch tglClean = new PToggleSwitch("Putzen");
    private final PToggleSwitch tglAndOr = new PToggleSwitch("Verknüpfen mit UND [sonst ODER]");
    private final PToggleSwitch tglNum = new PToggleSwitch("Zahlen entfernen: 20, 20.");
    private final PToggleSwitch tglDate = new PToggleSwitch("Datum entfernen: 20.03.2023");
    private final PToggleSwitch tglClip = new PToggleSwitch("Klammern entfernen: [], {}, ()");
    private final PToggleSwitch tglList = new PToggleSwitch("Cleaning Liste anwenden");

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
        //Suchbegriff
        ToggleGroup tg = new ToggleGroup();
        rbTitel.setToggleGroup(tg);
        rbTT.setToggleGroup(tg);
        switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL:
                rbTitel.setSelected(true);
                break;
            default:
                rbTT.setSelected(true);
                break;
        }
        rbTitel.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                }
            }
        });
        rbTT.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_TT_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                }
            }
        });

        //suchen wo
        ToggleGroup tgIn = new ToggleGroup();
        rbInTitel.setToggleGroup(tgIn);
        rbInTT.setToggleGroup(tgIn);
        rbInTitel.setText(media ? "Dateinamen" : "Titel des Abos / History-Films");
        rbInTT.setText(media ? "Pfad oder Dateinamen" : "Thema oder Titel des Abos / History-Films");
        switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.intValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL:
                rbInTitel.setSelected(true);
                break;
            default:
                rbInTT.setSelected(true);
                break;
        }
        rbInTitel.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
                }
            }
        });
        rbInTT.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
                }
            }
        });

        tglExact.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_EXACT_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_EXACT_ABO);

        tglClean.disableProperty().bind(tglExact.selectedProperty());
        tglClean.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_ABO);

        tglAndOr.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglAndOr.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO);

        tglNum.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglNum.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_ABO);

        tglDate.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglDate.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_ABO);

        tglClip.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglClip.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_ABO);

        tglList.disableProperty().bind(tglExact.selectedProperty().or(tglClean.selectedProperty().not()));
        tglList.selectedProperty().bindBidirectional(media ?
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_ABO);
    }

    private void addGrid(VBox vBox) {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
        vBox.getChildren().addAll(gridPane);

        //Suchen was
        Text text;
        if (media) {
            text = new Text("Suche in der Mediensammlung");
        } else {
            text = new Text("Suche in den Abos und der History");
        }
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, row);
        ++row;
        gridPane.add(new Label("Der Suchbegriff wird gebildet aus dem:"), 0, ++row);
        gridPane.add(rbTitel, 0, ++row);
        gridPane.add(rbTT, 0, ++row);

        //Suchen wo
        ++row;
        gridPane.add(new Label("Der Suchbegriff muss vorkommen im:"), 0, ++row);
        gridPane.add(rbInTitel, 0, ++row);
        gridPane.add(rbInTT, 0, ++row);

        //Sucheinstellungen
        text = new Text("Einstellungen der Suche");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(text, 0, ++row);

        ++row;
        gridPane.add(tglExact, 0, ++row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(tglClean, 0, ++row);

        ++row;
        gridPane.add(tglAndOr, 0, ++row);
        gridPane.add(tglNum, 0, ++row);
        gridPane.add(tglDate, 0, ++row);
        gridPane.add(tglClip, 0, ++row);
        gridPane.add(tglList, 0, ++row);
    }
}
