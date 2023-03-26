/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColor;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class FilmGuiButtonPane {
    private final FilmGuiController filmGuiController;

    public FilmGuiButtonPane(FilmGuiController filmGuiController) {
        this.filmGuiController = filmGuiController;
    }

    public TilePane getButtonPane(SetDataList setDataList) {
        TilePane tilePaneButton = new TilePane();
        tilePaneButton.setVgap(P2LibConst.DIST_BUTTON);
        tilePaneButton.setHgap(P2LibConst.DIST_BUTTON);
        tilePaneButton.setPadding(new Insets(P2LibConst.DIST_EDGE));

        setDataList.stream().forEach(setData -> {
            Button btn = new Button(setData.getVisibleName());
            btn.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
            btn.setMaxWidth(Double.MAX_VALUE);
            if (!setData.getColor().equals(SetData.RESET_COLOR)) {
                final String c = PColor.getCssColor(setData.getColor());
                final String css = "-fx-border-color: #" + c + "; " /*+ " -fx-text-fill: #" + c + "; "*/;
                btn.setStyle(css);
                btn.getStyleClass().add("setButton");
            }

            btn.setOnAction(a -> filmGuiController.playFilmUrlWithSet(setData));
            tilePaneButton.getChildren().add(btn);
        });
        return tilePaneButton;
    }

}
