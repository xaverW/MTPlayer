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
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    public FilmFilterController() {
        super(ProgConfig.FILM_GUI_FILTER_DIVIDER_ON);
        final FilmFilterControllerTextFilter filmFilterControllerTextFilter = new FilmFilterControllerTextFilter();
        final FilmFilterControllerFilter filmFilterControllerFilter = new FilmFilterControllerFilter();
        final FilmFilterControllerClearFilter filmFilterControllerClearFilter = new FilmFilterControllerClearFilter();
        final FilmFilterControllerProfiles filmFilterControllerProfiles = new FilmFilterControllerProfiles();
        final FilmFilterControllerBlacklist filmFilterControllerBlacklist = new FilmFilterControllerBlacklist();

        final VBox vBox = getVBoxFilter(true);
        vBox.getChildren().addAll(filmFilterControllerTextFilter,
                filmFilterControllerFilter,
                P2GuiTools.getVBoxGrower(),
                filmFilterControllerClearFilter);

        getVBoxBlack().getChildren().add(filmFilterControllerProfiles);
        getVBoxBlack().getChildren().add(filmFilterControllerBlacklist);
    }
}
