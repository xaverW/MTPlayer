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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    private final VBox vBoxFilter;
    private final VBox vBoxBlacklist;
    private final ProgData progData;

    private final PToggleSwitch tglBlacklist = new PToggleSwitch("Blacklist einschalten:");

    FilmFilterControllerFilter filter;
    FilmFilterControllerClearFilter clearFilter;
    FilmFilterControllerProfiles profiles;

    public FilmFilterController() {
        super();
        progData = ProgData.getInstance();

        filter = new FilmFilterControllerFilter();
        clearFilter = new FilmFilterControllerClearFilter();
        profiles = new FilmFilterControllerProfiles();

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);
        sp.setPadding(new Insets(0, 15, 0, 15));

        vBoxFilter = getVBoxAll();
        vBoxFilter.setSpacing(0);
        vBoxFilter.getChildren().addAll(filter, clearFilter, sp, profiles);

        tglBlacklist.setTooltip(new Tooltip("Blacklist einschalten"));
        tglBlacklist.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().blacklistOnProperty());
        vBoxBlacklist = getVBoxBotton();
        vBoxBlacklist.getChildren().add(tglBlacklist);
    }

}
