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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilterController extends AnchorPane {

    private final VBox vBoxAll = new VBox(20);
    private final ProgData progData;

    public FilterController() {
        progData = ProgData.getInstance();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(scrollPane);

        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);

        scrollPane.setContent(vBoxAll);
    }

    public VBox getVBoxAll() {
        return vBoxAll;
    }

    public void addVgrowVboxAll() {
        vBoxAll.getChildren().add(PGuiTools.getVBoxGrower());
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
        vBox.setStyle(PConst.CSS_BACKGROUND_COLOR_GREY);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        vBox.setSpacing(20);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBoxAll.getChildren().addAll(vBox);
        return vBox;
    }

}
