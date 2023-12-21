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


package de.p2tools.mtplayer.gui.mediaSearch;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MediaSearchFactory {
    private MediaSearchFactory() {
    }

    public static VBox getSearchVbox(MediaDataDto mediaDataDto, Label lblSum, boolean showBuildSearchFrom) {
        // Suchen wie
        HBox hBoxBuildSearchFrom = getSearchHbox(mediaDataDto, lblSum, true);
        HBox hBoxSearchIn = getSearchHbox(mediaDataDto, lblSum, false);

        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(5));
        if (showBuildSearchFrom) {
            vBox.getChildren().addAll(hBoxBuildSearchFrom);
        }
        vBox.getChildren().addAll(hBoxSearchIn);
        return vBox;
    }

    private static String getTextSearchInWhat(MediaDataDto mediaDataDto) {
        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_MEDIA) {
            switch (mediaDataDto.searchInWhat.getValue()) {
                case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                    return "Medien suchen im: Pfad";
                case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                    return "Medien suchen im: Dateinamen";
                default:
                    return "Medien suchen im: Pfad oder Dateinamen";
            }

        } else if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_ABO) {
            switch (mediaDataDto.searchInWhat.getValue()) {
                case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                    return "Abos suchen im: Thema des Abos";
                case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                    return "Abos suchen im: Titel des Abos";
                default:
                    return "Abos suchen im: Thema oder Titel des Abos";
            }

        } else {
            switch (mediaDataDto.searchInWhat.getValue()) {
                case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                    return "History suchen im: Thema des History-Films";
                case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                    return "History suchen im: Titel des History-Films";
                default:
                    return "History suchen im: Thema oder Titel des History-Films";
            }
        }
    }

    private static HBox getSearchHbox(MediaDataDto mediaDataDto, Label lblSum, boolean buildSearchFrom) {
        // Suchen, woraus der Suchtext gebaut wird
        HBox hBox = new HBox(P2LibConst.PADDING);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(0));

        Label lblText;
        if (buildSearchFrom) {
            lblText = new Label(getTextBuildSearchFrom(mediaDataDto.buildSearchFrom.getValue()));
            mediaDataDto.buildSearchFrom.addListener((u, o, n) -> {
                lblText.setText(getTextBuildSearchFrom(mediaDataDto.buildSearchFrom.getValue()));
            });

        } else {
            lblText = new Label(getTextSearchInWhat(mediaDataDto));
            mediaDataDto.searchInWhat.addListener((u, o, n) ->
                    lblText.setText(getTextSearchInWhat(mediaDataDto)));
        }

        Button btnChange = new Button();
        btnChange.getStyleClass().add("buttonVerySmall");
        if (buildSearchFrom) {
            btnChange.setTooltip(new Tooltip("Einstellung wie der Suchtext gebaut wird"));
        } else {
            btnChange.setTooltip(new Tooltip("Einstellung wo gesucht wird"));
        }
        btnChange.setGraphic(ProgIcons.ICON_BUTTON_CHANGE.getImageView());
        btnChange.setOnAction(a -> {
            if (buildSearchFrom) {
                if (mediaDataDto.buildSearchFrom.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                    mediaDataDto.buildSearchFrom.setValue(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
                } else if (mediaDataDto.buildSearchFrom.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                    mediaDataDto.buildSearchFrom.setValue(ProgConst.MEDIA_SEARCH_TT_OR_PN);
                } else {
                    mediaDataDto.buildSearchFrom.setValue(ProgConst.MEDIA_SEARCH_THEME_OR_PATH);
                }

            } else {
                if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                    mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
                } else if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                    mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TT_OR_PN);
                } else {
                    mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_THEME_OR_PATH);
                }
            }
        });

        if (lblSum == null) {
            hBox.getChildren().addAll(lblText, P2GuiTools.getHBoxGrower(), btnChange);
        } else {
            hBox.getChildren().addAll(lblText, P2GuiTools.getHBoxGrower(), lblSum, btnChange);
        }
        return hBox;
    }

    private static String getTextBuildSearchFrom(int search) {
        return switch (search) {
            case ProgConst.MEDIA_SEARCH_THEME_OR_PATH -> "Suchtext bauen mit: Thema des Films";
            case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME -> "Suchtext bauen mit: Titel des Films";
            default -> "Suchtext bauen mit: Thema und Titel des Films";
        };
    }
}
