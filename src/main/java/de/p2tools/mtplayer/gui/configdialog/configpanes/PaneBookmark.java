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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneBookmark {

    private final P2ToggleSwitch tglDelBookmark = new P2ToggleSwitch("Bookmarks ohne Film löschen:");
    private final Stage stage;
    private final VBox vBoxAll = new VBox();

    public PaneBookmark(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglDelBookmark.selectedProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_NOT_IN_FILMLIST);
    }

    public void make(Collection<TitledPane> result) {
        TitledPane tpConfig = new TitledPane("Bookmark", vBoxAll);
        result.add(tpConfig);
        vBoxAll.setSpacing(P2LibConst.PADDING_VBOX);
        addGridPane();
    }

    private void addGridPane() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        vBoxAll.getChildren().add(gridPane);

        tglDelBookmark.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_NOT_IN_FILMLIST);
        final Button btnHelpAbo = PIconFactory.getHelpButton(stage, "Bookmarks ohne Film löschen",
                HelpText.DELETE_BOOKMARK_NOT_IN_FILMLIST);

        int row = 0;
        gridPane.add(tglDelBookmark, 0, row);
        gridPane.add(btnHelpAbo, 1, row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());
    }
}