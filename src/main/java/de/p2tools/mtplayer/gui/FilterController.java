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
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilterController extends AnchorPane {


    private final VBox vBoxAll = new VBox(20);
    private final ProgData progData;

    public FilterController(MLConfigs mlConfig) {
//        progData = ProgData.getInstance();
//
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);

//        getChildren().addAll(scrollPane);

//        AnchorPane.setLeftAnchor(scrollPane, 0.0);
//        AnchorPane.setBottomAnchor(scrollPane, 0.0);
//        AnchorPane.setRightAnchor(scrollPane, 0.0);
//        AnchorPane.setTopAnchor(scrollPane, 0.0);

//        scrollPane.setContent(vBoxAll);

        progData = ProgData.getInstance();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBoxAll);

        Button button = new Button();
        button.getStyleClass().add("close-button");
        button.setOnAction(a -> mlConfig.setValue(false));

        HBox hBox = new HBox();
        hBox.getStyleClass().add("close-pane");
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(button);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        getChildren().addAll(vBox);
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
        vBox.getStyleClass().add("extra-pane");
//        vBox.setStyle(PConst.CSS_BACKGROUND_COLOR_GREY);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        vBox.setSpacing(20);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBoxAll.getChildren().addAll(vBox);
        return vBox;
    }
}
