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

package de.p2tools.mtplayer.gui.filter;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilterController extends VBox {

    public static final int FILTER_SPACING_TEXTFILTER = 5;

    public FilterController() {
        setSpacing(5);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public VBox getVBoxFilter() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5, 10, 5, 10));
        vbox.setSpacing(FILTER_SPACING_TEXTFILTER);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        getChildren().addAll(vbox);
        return vbox;
    }

    public VBox getVBoxBlack() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("extra-pane-filter");
        vBox.setPadding(new Insets(5, 10, 5, 10));
        vBox.setSpacing(5);
        getChildren().addAll(vBox);
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
