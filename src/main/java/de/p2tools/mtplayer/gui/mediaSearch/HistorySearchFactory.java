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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class HistorySearchFactory {
    private HistorySearchFactory() {
    }

    public static GridPane getSearchHbox(MediaDataDto mediaDataDto, TextField txtSearch, Button btnReset,
                                         ComboBox<String> comboBox) {
        final boolean mediaDataExist = !mediaDataDto.searchTheme.isEmpty() || !mediaDataDto.searchTitle.isEmpty();

        final Button btnClear = new Button();
        btnClear.setGraphic(P2IconFactory.P2ICON.BTN_CLEAR.getFontIcon());
        btnClear.setTooltip(new Tooltip("Das Suchfeld löschen"));
        btnClear.setOnAction(a -> {
            txtSearch.clear();
            comboBox.getSelectionModel().clearSelection();
        });

        Button btnChange = new Button();
        btnChange.setTooltip(new Tooltip("Einstellung wo gesucht wird"));
        btnChange.setGraphic(P2IconFactory.P2ICON.BTN_ROTATE_3D.getFontIcon());
        btnChange.setOnAction(a -> {
            if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
            } else if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TT_OR_PN);
            } else {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_THEME_OR_PATH);
            }
        });

        Label lblText = new Label(getTextSearchInWhat(mediaDataDto));
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER);
        mediaDataDto.searchInWhat.addListener((u, o, n) ->
                lblText.setText(getTextSearchInWhat(mediaDataDto)));

        if (mediaDataExist) {
            hBox.getChildren().addAll(lblText, txtSearch, btnReset, btnChange, btnClear);
        } else {
            // wenns keine MediaData gibt, dann brauchts den Reset auch nicht
            hBox.getChildren().addAll(lblText, txtSearch, btnChange, btnClear);
        }

        comboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane gridPane = new GridPane(P2LibConst.DIST_GRIDPANE_HGAP, P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSizeLeft(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(lblText, 0, 0);
        gridPane.add(txtSearch, 1, 0);
        gridPane.add(hBox, 2, 0);
        gridPane.add(new Label("Abos:"), 0, 1);
        gridPane.add(comboBox, 1, 1);

        return gridPane;
    }

    private static String getTextSearchInWhat(MediaDataDto mediaDataDto) {
        switch (mediaDataDto.searchInWhat.getValue()) {
            case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                return "Thema:";
            case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                return "Titel:";
            default:
                return "Thema oder Titel:";
        }
    }
}
