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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PaneBookmarkInfo extends GridPane {

    private final Label lblTitleS = new Label("");
    private final Label lblThemeS = new Label("");
    private final Label lblUrlS = new Label("");
    private final Label lblDateS = new Label();

    private final Text text = new Text("Kein Film in der Liste");

    public PaneBookmarkInfo() {
        final GridPane gridPane = this;
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize());

        int row = 0;
        gridPane.add(new Label("Titel:"), 0, row);
        gridPane.add(lblTitleS, 1, row, 2, 1);
        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(lblThemeS, 1, row, 2, 1);
        gridPane.add(new Label("URL:"), 0, ++row);
        gridPane.add(lblUrlS, 1, row, 2, 1);
        gridPane.add(new Label("Datum:"), 0, ++row);
        gridPane.add(lblDateS, 1, row);
        gridPane.add(text, 2, row);

        text.setVisible(false);
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
    }

    public void setBookmarkData(BookmarkData bookmarkData) {
        if (bookmarkData == null) {
            lblTitleS.setText("");
            lblThemeS.setText("");
            lblUrlS.setText("");
            lblDateS.setText("");
            text.setVisible(false);
            return;
        }

        lblTitleS.setText(bookmarkData.getTitle());
        lblThemeS.setText(bookmarkData.getTheme());
        lblUrlS.setText(bookmarkData.getUrl());
        lblDateS.setText(bookmarkData.getDate().get_dd_MM_yyyy());
        text.setVisible(bookmarkData.getFilmDataMTP() == null && ProgConfig.BOOKMARK_DIALOG_SHOW_INFO.get());
    }
}

