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

import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PaneBookmarkInfo extends VBox {

    private final Label lblTitle = new Label("");
    private final Label lblTheme = new Label("");
    private final Label lblUrl = new Label("");
    private final Label lblDate = new Label();
    private final Label lblFilm = new Label("Kein Film in der Liste");

    private BookmarkData bookmarkData = null;

    public PaneBookmarkInfo() {
        VBox.setVgrow(this, Priority.ALWAYS);
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(), P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        gridPane.add(new Label("Titel: "), 0, row);
        gridPane.add(lblTitle, 1, row, 2, 1);
        gridPane.add(new Label("Thema: "), 0, ++row);
        gridPane.add(lblTheme, 1, row, 2, 1);
        gridPane.add(new Label("URL: "), 0, ++row);
        gridPane.add(lblUrl, 1, row, 2, 1);
        gridPane.add(new Label("Datum: "), 0, ++row);
        gridPane.add(lblDate, 1, row);
        gridPane.add(lblFilm, 2, row);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        lblFilm.setVisible(false);

        setSpacing(0);
        setPadding(new Insets(0));
        getChildren().add(gridPane);
    }

    public void setBookmarkData(BookmarkData bookmarkData) {
        this.bookmarkData = bookmarkData;

        if (bookmarkData == null) {
            lblTheme.setText("");
            lblTitle.setText("");
            lblUrl.setText("");
            lblDate.setText("");
            lblFilm.setVisible(false);
            return;
        }

        lblTheme.setText(bookmarkData.getTheme());
        lblTitle.setText(bookmarkData.getTitle());
        lblUrl.setText(bookmarkData.getUrl());
        lblDate.setText(bookmarkData.getDate().get_dd_MM_yyyy());
        lblFilm.setVisible(bookmarkData.getFilmDataMTP() == null);
    }
}

