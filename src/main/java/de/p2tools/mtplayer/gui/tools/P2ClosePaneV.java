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

import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.P2ProgIcons;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class P2ClosePaneV extends VBox {

    private final VBox vBoxAll = new VBox();
    private final boolean scroll;
    private final BooleanProperty visibleProperty;
    private final BooleanProperty ripProperty;

    public P2ClosePaneV(BooleanProperty visibleProperty, boolean scroll) {
        this.scroll = scroll;
        this.visibleProperty = visibleProperty;
        this.ripProperty = null;
        init();
    }

    public P2ClosePaneV(BooleanProperty visibleProperty, BooleanProperty ripProperty, boolean scroll) {
        this.scroll = scroll;
        this.visibleProperty = visibleProperty;
        this.ripProperty = ripProperty;
        init();
    }

    private void init() {
        Button button = new Button();
        button.getStyleClass().add("close-button");
        button.setGraphic(P2ProgIcons.ICON_BUTTON_CLOSE.getImageView());
        button.setOnAction(a -> visibleProperty.setValue(false));

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getStyleClass().add("close-pane");
        hBox.setAlignment(Pos.TOP_RIGHT);
        if (ripProperty != null) {
            Button buttonRip = new Button();
            buttonRip.getStyleClass().add("rip-button");
            buttonRip.setGraphic(P2ProgIcons.ICON_BUTTON_RIP.getImageView());
            buttonRip.setOnAction(a -> ripProperty.setValue(true));
            hBox.getChildren().addAll(buttonRip, button);
            hBox.visibleProperty().bind(ripProperty.not());
            hBox.managedProperty().bind(ripProperty.not());

        } else {
            hBox.getChildren().addAll(button);
        }

        if (scroll) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(vBoxAll);

            getChildren().addAll(hBox, scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
        } else {
            getChildren().addAll(hBox, vBoxAll);
            VBox.setVgrow(vBoxAll, Priority.ALWAYS);
        }
    }

    public VBox getVBoxAll() {
        return vBoxAll;
    }
}
