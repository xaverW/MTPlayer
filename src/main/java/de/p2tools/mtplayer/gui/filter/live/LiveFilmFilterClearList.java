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

package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgData;
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

public class LiveFilmFilterClearList extends VBox {

    private final ProgData progData;

    public LiveFilmFilterClearList() {
        super();
        progData = ProgData.getInstance();
        addButton();
    }

    private void addButton() {
        Button btnClearList = new Button();
        btnClearList.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClearList.setTooltip(new Tooltip("Filmliste löschen"));
        btnClearList.setOnAction(a -> progData.liveFilmFilterWorker.getLiveFilmList().clear());
        btnClearList.disableProperty().bind(progData.liveFilmFilterWorker.getLiveFilmList().sizeProperty().isEqualTo(0));

        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 0, 0, 0));
        hBox.getChildren().addAll(new Label("Liste löschen:"), P2GuiTools.getHBoxGrower(), btnClearList);
        getChildren().addAll(hBox);
    }
}
