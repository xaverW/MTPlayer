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
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class HistorySearchFactory {
    private HistorySearchFactory() {
    }

    public static HBox getSearchHbox(MediaDataDto mediaDataDto) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        Label lblText = P2Text.getLblTextBold(getTextSearchInWhat(mediaDataDto));
        mediaDataDto.searchInWhat.addListener((u, o, n) ->
                lblText.setText(getTextSearchInWhat(mediaDataDto)));

        Button btnChange = new Button();
        btnChange.setTooltip(new Tooltip("Einstellung wo gesucht wird"));
        btnChange.setGraphic(PIconFactory.PICON.BTN_RESET.getFontIcon());
        btnChange.setOnAction(a -> {
            if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
            } else if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TT_OR_PN);
            } else {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_THEME_OR_PATH);
            }
        });

        hBox.getChildren().addAll(lblText, P2GuiTools.getHBoxGrower(), btnChange);
        return hBox;
    }

    private static String getTextSearchInWhat(MediaDataDto mediaDataDto) {
        switch (mediaDataDto.searchInWhat.getValue()) {
            case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                return "Thema";
            case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                return "Titel";
            default:
                return "Thema oder Titel";
        }
    }
}
