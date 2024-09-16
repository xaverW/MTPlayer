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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class P2ClosePaneH extends HBox {

    private final VBox vBoxAll = new VBox();
    private final boolean addScroll;
    private final boolean addRipButton;
    private final Button buttonClose = new Button();
    private final Button buttonRip = new Button();
    private final BooleanProperty showNoButton;

    public P2ClosePaneH() {
        // alles anzeigen
        this.addScroll = true;
        this.addRipButton = true;
        this.showNoButton = null;
        init();
    }

    public P2ClosePaneH(boolean addRipButton, boolean addScroll) {
        this.addScroll = addScroll;
        this.addRipButton = addRipButton;
        this.showNoButton = null;
        init();
    }

    public P2ClosePaneH(boolean addRipButton, boolean addScroll, BooleanProperty showNoButton) {
        this.addScroll = addScroll;
        this.addRipButton = addRipButton;
        this.showNoButton = showNoButton == null ? new SimpleBooleanProperty(true) : showNoButton;
        init();
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Button getButtonRip() {
        return buttonRip;
    }

    public void addPane(Pane pane) {
        getVBoxAll().getChildren().add(pane);
    }

    public VBox getVBoxAll() {
        return vBoxAll;
    }

    private void init() {
        buttonClose.getStyleClass().add("close-button");
        buttonClose.setGraphic(P2ProgIcons.ICON_BUTTON_CLOSE.getImageView());

        buttonRip.getStyleClass().add("rip-button");
        buttonRip.setGraphic(P2ProgIcons.ICON_BUTTON_RIP.getImageView());

        VBox vBox = new VBox();
        vBox.getStyleClass().add("close-pane");
        vBox.setAlignment(Pos.TOP_CENTER);
        if (showNoButton != null) {
            vBox.visibleProperty().bind(showNoButton.not());
            vBox.managedProperty().bind(showNoButton.not());
        }

        if (addRipButton) {
            vBox.getChildren().addAll(buttonClose, P2GuiTools.getVBoxGrower(), buttonRip);
        } else {
            vBox.getChildren().addAll(buttonClose);
        }

        if (addScroll) {
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
}
