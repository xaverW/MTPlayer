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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PaneBookmarkInfo extends SplitPane {

    private final Label lblChannelS = new Label("");
    private final Label lblThemeS = new Label("");
    private final Label lblTitleS = new Label("");
    private final Label lblDateS = new Label();
    private final TextArea txtInfo = new TextArea();

    private final Text text = new Text("Kein Film in der Liste");
    private BookmarkData bookmarkData = null;
    private boolean changed = false;

    public PaneBookmarkInfo() {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        gridPane.add(new Label("Sender:"), 0, row);
        gridPane.add(lblChannelS, 1, row, 2, 1);
        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(lblThemeS, 1, row, 2, 1);
        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(lblTitleS, 1, row, 2, 1);
        gridPane.add(new Label("Datum:"), 0, ++row);
        gridPane.add(lblDateS, 1, row);
        gridPane.add(text, 2, row);

        text.setVisible(false);
        text.setFont(Font.font(null, FontWeight.BOLD, -1));

        txtInfo.setWrapText(true);
        txtInfo.setPrefRowCount(2);
        txtInfo.textProperty().addListener((u, o, n) -> {
            if (bookmarkData != null) {
                changed = true;
                bookmarkData.setInfo(txtInfo.getText());
            }
        });
        VBox vBox = new VBox(1);
        Label lbl = new Label("Info:");
        vBox.getChildren().addAll(lbl, txtInfo);
        VBox.setVgrow(txtInfo, Priority.ALWAYS);

        SplitPane splitPane = this;
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(gridPane, Boolean.FALSE);
        splitPane.getItems().addAll(vBox, gridPane);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.BOOKMARK_DIALOG_INFO_DIVIDER);
    }

    public void setBookmarkData(BookmarkData bd) {
        this.bookmarkData = null;

        if (bd == null) {
            lblChannelS.setText("");
            lblThemeS.setText("");
            lblTitleS.setText("");
            lblDateS.setText("");
            text.setVisible(false);
            text.setManaged(false);
            txtInfo.setText("");
            txtInfo.setDisable(true);
            return;
        }

        lblChannelS.setText(bd.getChannel());
        lblThemeS.setText(bd.getTheme());
        lblTitleS.setText(bd.getTitle());
        lblDateS.setText(bd.getDate().get_dd_MM_yyyy());
        text.setVisible(bd.getFilmData() == null && ProgConfig.BOOKMARK_DIALOG_SHOW_INFO.get());
        text.setManaged(bd.getFilmData() == null && ProgConfig.BOOKMARK_DIALOG_SHOW_INFO.get());
        txtInfo.setText(bd.getInfo());
        txtInfo.setDisable(false);

        this.bookmarkData = bd;
    }

    public boolean isChanged() {
        return changed;
    }
}

