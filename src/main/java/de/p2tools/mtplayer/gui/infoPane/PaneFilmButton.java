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


package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Color;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class PaneFilmButton extends VBox {

    private final boolean live;
    private final TilePane tilePane;

    public PaneFilmButton(boolean live) {
        this.live = live;
        this.tilePane = new TilePane();
        tilePane.setVgap(P2LibConst.DIST_BUTTON);
        tilePane.setHgap(P2LibConst.DIST_BUTTON);
        tilePane.setPadding(new Insets(P2LibConst.PADDING));

        setSpacing(0);
        setPadding(new Insets(0));
        getChildren().add(tilePane);

        addButton();
        ProgData.getInstance().setDataList.addListener((u, o, n) -> addButton());
    }

    private void addButton() {
        tilePane.getChildren().clear();

        SetDataList setDataList = ProgData.getInstance().setDataList.getSetDataListButton();
        setDataList.forEach(setData -> {
            Button btn = new Button();
            btn.textProperty().bind(setData.visibleNameProperty());
            btn.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
            btn.setMaxWidth(Double.MAX_VALUE);
            if (!setData.getColor().equals(SetData.RESET_COLOR)) {
                final String c = P2Color.getCssColor(setData.getColor());
                final String css = "-fx-border-color: " + c + "; " /*+ " -fx-text-fill: #" + c + "; "*/;
                btn.setStyle(css);
                btn.getStyleClass().add("setButton");
            }

            btn.setOnAction(a -> {
                        if (live) {
                            FilmPlayFactory.playFilmListWithSet(setData,
                                    ProgData.getInstance().liveFilmGuiController.getSelList(true));
                        } else {
                            FilmPlayFactory.playFilmListWithSet(setData,
                                    ProgData.getInstance().filmGuiController.getSelList(true));
                        }
                    }
            );
            tilePane.getChildren().add(btn);
        });
    }
}
