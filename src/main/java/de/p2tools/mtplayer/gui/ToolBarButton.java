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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class ToolBarButton extends Button {

    /**
     *
     */
    private final Node imageIcon;
    private boolean vis = true;
    private String name = "";
    private final VBox vbox;

    public ToolBarButton(VBox vbox, String name, String toolTip, Node imageIcon) {

        this.vbox = vbox;
        setName(name);
        this.imageIcon = imageIcon;

        getStyleClass().add("btnFunction");
        setTooltip(new Tooltip(toolTip));
        setGraphic(this.imageIcon);
        this.vbox.getChildren().addAll(this);
    }

    public boolean isVis() {
        return vis;
    }

    public void setVis(boolean vis) {
        this.vis = vis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
