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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.tools.MLConfigs;
import de.p2tools.p2Lib.guiTools.pClosePane.PClosePaneV;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilterController extends PClosePaneV {

    private final VBox vBoxAll;
    private final ProgData progData;

    public FilterController(MLConfigs mlConfig) {
        super(mlConfig.getBooleanProperty(), true);
        vBoxAll = super.getVBoxAll();
        progData = ProgData.getInstance();
    }

    public VBox getVBoxAll() {
        return vBoxAll;
    }

    public VBox getVBoxFilter(boolean vgrow) {
        VBox vbFilter = new VBox();
        vbFilter.setPadding(new Insets(15, 15, 15, 15));
        vbFilter.setSpacing(20);
        if (vgrow) {
            VBox.setVgrow(vbFilter, Priority.ALWAYS);
        }

        vBoxAll.getChildren().addAll(vbFilter);
        return vbFilter;
    }

    public VBox getVBoxBotton() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("extra-pane");
        vBox.setPadding(new Insets(15, 15, 15, 15));
        vBox.setSpacing(20);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBoxAll.getChildren().addAll(vBox);
        return vBox;
    }
}
