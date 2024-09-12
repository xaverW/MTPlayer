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
import de.p2tools.mtplayer.gui.filter.FilmFilterController;
import de.p2tools.mtplayer.gui.filter.FilterPaneDialog;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class FilmGui {

    ProgData progData;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    private final FilmFilterController filmFilterController;
    private final FilmGuiController filmGuiController;
    private boolean bound = false;
    private FilterPaneDialog filterPaneDialog = null;

    public FilmGui() {
        progData = ProgData.getInstance();
        filmFilterController = new FilmFilterController();
        filmGuiController = new FilmGuiController();
    }

    private void setSplit() {
        if (bound) {
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.FILM_GUI_FILTER_DIVIDER);
            bound = false;
        }
        if (filterPaneDialog != null) {
            filterPaneDialog.closeSetNoRip();
            filterPaneDialog = null;
        }
        splitPane.getItems().clear();

        if (ProgConfig.FILM_GUI_FILTER_IS_VISIBLE.get()) {

            if (ProgConfig.FILM_GUI_FILTER_IS_RIP.get()) {

                filterPaneDialog = new FilterPaneDialog(filmFilterController, "Filmfilter",
                        ProgConfig.FILM_GUI_FILTER_DIALOG_SIZE,
                        ProgConfig.FILM_GUI_FILTER_IS_RIP, ProgData.FILM_TAB_ON);
                splitPane.getItems().addAll(filmGuiController);

            } else {
                splitPane.getItems().addAll(filmFilterController, filmGuiController);
                splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.FILM_GUI_FILTER_DIVIDER);
                bound = true;
            }

        } else {
            splitPane.getItems().addAll(filmGuiController);
        }
    }

    public SplitPane pack() {
        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.Film);

        // Gui
        progData.filmGuiController = filmGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(filmFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        ProgConfig.FILM_GUI_FILTER_IS_VISIBLE.addListener((observable, oldValue, newValue) -> setSplit());
        ProgConfig.FILM_GUI_FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }
}
