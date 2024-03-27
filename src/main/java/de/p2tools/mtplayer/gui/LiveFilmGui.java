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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.live.LiveFilmFilterController;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class LiveFilmGui {

    ProgData progData;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    private final LiveFilmFilterController liveFilmFilterController;
    private final LiveFilmGuiController liveFilmGuiController;
    private boolean bound = false;

    public LiveFilmGui() {
        progData = ProgData.getInstance();
        liveFilmFilterController = new LiveFilmFilterController();
        liveFilmGuiController = new LiveFilmGuiController();
    }

    public void closeSplit() {
        ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.get());
    }

    private void setSplit() {
        if (ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.getValue()) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(liveFilmFilterController, liveFilmGuiController);
            bound = true;
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER);
        } else {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER);
            }
            splitPane.getItems().clear();
            splitPane.getItems().addAll(liveFilmGuiController);
        }
    }

    public SplitPane pack() {
        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.LIVE_FILM);
//        menuController.setId("film-menu-pane");

        // Gui
        progData.liveFilmGuiController = liveFilmGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(liveFilmFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }
}
