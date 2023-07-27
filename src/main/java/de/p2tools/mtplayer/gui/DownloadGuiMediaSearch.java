/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PGuiTools;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DownloadGuiMediaSearch {

    public static VBox getSearchMedia(Label lblSumMedia) {
        // Suchen wie
        HBox hBoxWhat = getSearchWhat(ProgConfig.GUI_MEDIA_BUILD_SEARCH_MEDIA);

        // Suchen wo
        HBox hBoxWhere = new HBox(P2LibConst.DIST_EDGE);
        hBoxWhere.setAlignment(Pos.CENTER_LEFT);
        hBoxWhere.setPadding(new Insets(0));
        Label lblMedSearchIn = new Label(getTextMed());
        ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.addListener((u, o, n) -> lblMedSearchIn.setText(getTextMed()));
        Button btnChangeMedia = new Button();
        btnChangeMedia.getStyleClass().add("buttonVerySmall");
        btnChangeMedia.setTooltip(new Tooltip("Einstellung wo gesucht wird, ändern"));
        btnChangeMedia.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CHANGE.getImageView());
        btnChangeMedia.setOnAction(a -> {
            if (ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_THEME) {
                ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
            } else if (ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_TITEL) {
                ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TT);
            } else {
                ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEME);
            }
        });

        if (lblSumMedia == null) {
            hBoxWhere.getChildren().addAll(lblMedSearchIn, PGuiTools.getHBoxGrower(), btnChangeMedia);
        } else {
            hBoxWhere.getChildren().addAll(lblMedSearchIn, PGuiTools.getHBoxGrower(), lblSumMedia, btnChangeMedia);
        }

        VBox vLeft = new VBox(5);
        vLeft.setPadding(new Insets(5));
        vLeft.getChildren().addAll(hBoxWhat, hBoxWhere);
        return vLeft;
    }

    public static VBox getSearchAbo(Label lblSumMedia, boolean abo) {
        // Suchen wie
        HBox hBoxWhat = getSearchWhat(ProgConfig.GUI_MEDIA_BUILD_SEARCH_ABO);

        // Suchen wo
        HBox hBoxWhere = new HBox(P2LibConst.DIST_EDGE);
        hBoxWhere.setAlignment(Pos.CENTER_LEFT);
        hBoxWhere.setPadding(new Insets(0));
        Label lblAbosSearchIn = new Label(abo ? getTextAbo() : getTextHistory());
        ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) ->
                lblAbosSearchIn.setText(abo ? getTextAbo() : getTextHistory()));

        Button btnChangeAbo = new Button();
        btnChangeAbo.getStyleClass().add("buttonVerySmall");
        btnChangeAbo.setTooltip(new Tooltip("Einstellung wo gesucht wird, ändern"));
        btnChangeAbo.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CHANGE.getImageView());
        btnChangeAbo.setOnAction(a -> {
            if (ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_THEME) {
                ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
            } else if (ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_TITEL) {
                ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TT);
            } else {
                ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEME);
            }
        });

        if (lblSumMedia == null) {
            hBoxWhere.getChildren().addAll(lblAbosSearchIn, PGuiTools.getHBoxGrower(), btnChangeAbo);
        } else {
            hBoxWhere.getChildren().addAll(lblAbosSearchIn, PGuiTools.getHBoxGrower(), lblSumMedia, btnChangeAbo);
        }

        VBox vLeft = new VBox(5);
        vLeft.setPadding(new Insets(5));
        vLeft.getChildren().addAll(hBoxWhat, hBoxWhere);
        return vLeft;
    }

    private static String getTextMed() {
        switch (ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEME:
                return "Medien suchen im: Pfad";
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                return "Medien suchen im: Dateinamen";
            default:
                return "Medien suchen im: Pfad oder Dateinamen";
        }
    }

    private static String getTextAbo() {
        switch (ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEME:
                return "Abos suchen im: Thema des Abos";
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                return "Abos suchen im: Titel des Abos";
            default:
                return "Abos suchen im: Thema oder Titel des Abos";
        }
    }

    private static String getTextHistory() {
        switch (ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEME:
                return "History suchen im: Thema des History-Films";
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                return "History suchen im: Titel des History-Films";
            default:
                return "History suchen im: Thema oder Titel des History-Films";
        }
    }

    private static HBox getSearchWhat(IntegerProperty ip) {
        // Suchen wie
        HBox hBoxWhat = new HBox(P2LibConst.DIST_EDGE);
        hBoxWhat.setAlignment(Pos.CENTER_LEFT);
        hBoxWhat.setPadding(new Insets(0));
        Label lblText = new Label(getTextSearchInAbo(ip.getValue()));

        ip.addListener((u, o, n) -> {
            lblText.setText(getTextSearchInAbo(ip.getValue()));
        });
        Button btnChangeMedia = new Button();
        btnChangeMedia.getStyleClass().add("buttonVerySmall");
        btnChangeMedia.setTooltip(new Tooltip("Einstellung wie der Suchtext gebaut wird, ändern"));
        btnChangeMedia.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CHANGE.getImageView());
        btnChangeMedia.setOnAction(a -> {
            if (ip.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_THEME) {
                ip.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
            } else if (ip.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_TITEL) {
                ip.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TT);
            } else {
                ip.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEME);
            }
        });
        hBoxWhat.getChildren().addAll(lblText, PGuiTools.getHBoxGrower(), btnChangeMedia);
        return hBoxWhat;
    }

    private static String getTextSearchInAbo(int search) {
        switch (search) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEME:
                return "Suchtext bauen mit: Thema des Films";
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                return "Suchtext bauen mit: Titel des Films";
            default:
                return "Suchtext bauen mit: Thema und Titel des Films";
        }
    }
}
