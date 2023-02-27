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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    private FilmFilterControllerTextFilter filmFilterControllerTextFilter;
    private FilmFilterControllerFilter filmFilterControllerFilter;
    private FilmFilterControllerClearFilter filmFilterControllerClearFilter;
    private FilmFilterControllerProfiles filmFilterControllerProfiles;
    private FilmFilterControllerBlacklist filmFilterControllerBlacklist;

    public FilmFilterController() {
        super(ProgConfig.FILM_GUI_FILTER_DIVIDER_ON);

        filmFilterControllerTextFilter = new FilmFilterControllerTextFilter();
        filmFilterControllerFilter = new FilmFilterControllerFilter();
        filmFilterControllerClearFilter = new FilmFilterControllerClearFilter();
        filmFilterControllerProfiles = new FilmFilterControllerProfiles();
        filmFilterControllerBlacklist = new FilmFilterControllerBlacklist();

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);
        sp.setPadding(new Insets(0, 15, 0, 15));

        getVBoxAll().setSpacing(0);
        VBox.setVgrow(filmFilterControllerClearFilter, Priority.ALWAYS);

        getVBoxAll().getChildren().addAll(filmFilterControllerTextFilter, filmFilterControllerFilter,
                filmFilterControllerClearFilter, sp, filmFilterControllerProfiles);

        getVBoxBottom().getChildren().add(filmFilterControllerBlacklist);
    }
}
