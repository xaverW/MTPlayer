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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilterController extends PClosePaneV {

    public static final int FILTER_SPACING_FILTER = 15;
    public static final int FILTER_SPACING_DOWNLOAD = 25;
    public static final int FILTER_SPACING_TEXTFILTER = 10;
    public static final int FILTER_SPACING_CLEAR = 10;
    public static final int FILTER_SPACING_PROFIlE = 10;

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
        vbFilter.setPadding(new Insets(10, 15, 5, 15));
        vbFilter.setSpacing(FILTER_SPACING_TEXTFILTER);
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

    public void addCont(String txt, Control control, VBox vBox) {
        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox v = new VBox(2);
        Label label = new Label(txt);
        v.getChildren().addAll(label, control);
        vBox.getChildren().add(v);
    }

    public void addCont(HBox txt, Control control, VBox vBox) {
        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox v = new VBox(2);
        v.getChildren().addAll(txt, control);
        vBox.getChildren().add(v);
    }
}
