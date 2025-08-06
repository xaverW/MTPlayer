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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Color;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class PaneFilmButton extends VBox {

    public enum PANE {
        FILM, AUDIO, LIVE
    }

    private final TilePane tilePane;
    private final PANE pane;

    public PaneFilmButton(PANE pane) {
        this.pane = pane;
        VBox.setVgrow(this, Priority.ALWAYS);

        this.tilePane = new TilePane();
        tilePane.setVgap(P2LibConst.DIST_BUTTON);
        tilePane.setHgap(P2LibConst.DIST_BUTTON);
        tilePane.setPadding(new Insets(P2LibConst.PADDING));

        setSpacing(0);
        setPadding(new Insets(0));
        getChildren().add(tilePane);

        addButton();
        ProgData.getInstance().setDataList.addListener((u, o, n) -> addButton());
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILM_BUTTON_CHANGED) {
            @Override
            public void ping() {
                addButton();
            }
        });
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
                switch (pane) {
                    case FILM -> FilmPlayFactory.playFilmListWithSet(false, setData,
                            ProgData.getInstance().filmGuiController.getSelList(true));
                    case AUDIO -> FilmPlayFactory.playFilmListWithSet(true, setData,
                            ProgData.getInstance().audioGuiController.getSelList(true));
                    case LIVE -> FilmPlayFactory.playFilmListWithSet(false, setData,
                            ProgData.getInstance().liveFilmGuiController.getSelList(true));
                }
            });
            tilePane.getChildren().add(btn);
        });
    }
}
