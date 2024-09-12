/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtplayer.gui.tools;

import de.p2tools.p2lib.P2ProgIcons;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class P2ClosePaneH extends HBox {

    private final VBox vBoxAll = new VBox();
    private final BooleanProperty closeProperty;
    private final boolean scroll;
    private final BooleanProperty ripProperty;

    public P2ClosePaneH(BooleanProperty closeProperty, boolean scroll) {
        this.closeProperty = closeProperty;
        this.scroll = scroll;
        this.ripProperty = null;
        init();
    }

    public P2ClosePaneH(BooleanProperty closeProperty, boolean scroll, boolean rip) {
        this.closeProperty = closeProperty;
        this.scroll = scroll;
        this.ripProperty = new SimpleBooleanProperty();
        init();
    }

    private void init() {
        Button button = new Button();
        button.getStyleClass().add("close-button");
        button.setGraphic(P2ProgIcons.ICON_BUTTON_CLOSE.getImageView());
        button.setOnAction(a -> closeProperty.setValue(false));

        VBox vBox = new VBox();
        vBox.getStyleClass().add("close-pane");
        vBox.setAlignment(Pos.TOP_CENTER);
        if (ripProperty != null) {
            Button buttonRip = new Button();
            buttonRip.getStyleClass().add("rip-button");
            buttonRip.setGraphic(P2ProgIcons.ICON_BUTTON_RIP.getImageView());
            buttonRip.setOnAction(a -> ripProperty.setValue(!ripProperty.get()));
            vBox.getChildren().addAll(button, P2GuiTools.getVBoxGrower(), buttonRip);
        } else {
            vBox.getChildren().addAll(button);
        }

        if (scroll) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(vBoxAll);

            getChildren().addAll(scrollPane, vBox);
            HBox.setHgrow(scrollPane, Priority.ALWAYS);
        } else {
            getChildren().addAll(vBoxAll, vBox);
            HBox.setHgrow(vBoxAll, Priority.ALWAYS);
        }
    }

    public VBox getVBoxAll() {
        return vBoxAll;
    }

    public BooleanProperty getRipProperty() {
        return ripProperty;
    }
}
