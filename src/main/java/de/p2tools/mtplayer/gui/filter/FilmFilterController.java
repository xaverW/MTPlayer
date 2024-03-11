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
import javafx.scene.control.Separator;
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

        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator1");
        sp1.setMinHeight(0);

        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator3");
        sp2.setMinHeight(0);

        final VBox vBoxFilter = getVBoxFilter(true);
        vBoxFilter.setSpacing(10);
//        VBox.setVgrow(filmFilterControllerClearFilter, Priority.ALWAYS);
        vBoxFilter.getChildren().addAll(filmFilterControllerTextFilter,
                filmFilterControllerFilter,
                P2GuiTools.getVBoxGrower(), sp1,
                filmFilterControllerClearFilter, sp2,
                filmFilterControllerProfiles);

        getVBoxBottom().getChildren().add(filmFilterControllerBlacklist);
    }
}
