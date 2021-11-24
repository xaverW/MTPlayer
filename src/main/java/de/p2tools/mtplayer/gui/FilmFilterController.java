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
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    private final VBox vBoxFilter;
    private final VBox vBoxBlacklist;
    private final ProgData progData;

    private final PToggleSwitch tglBlacklist = new PToggleSwitch("Blacklist:");

    FilmFilterControllerTextFilter textFilter;
    FilmFilterControllerFilter filter;
    FilmFilterControllerClearFilter clearFilter;
    FilmFilterControllerProfiles profiles;

    public FilmFilterController() {
        super(ProgConfig.FILM_GUI_FILTER_DIVIDER_ON);
        progData = ProgData.getInstance();

        textFilter = new FilmFilterControllerTextFilter();
        filter = new FilmFilterControllerFilter();
        clearFilter = new FilmFilterControllerClearFilter();
        profiles = new FilmFilterControllerProfiles();

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);
        sp.setPadding(new Insets(0, 15, 0, 15));

        vBoxFilter = getVBoxAll();
        vBoxFilter.setSpacing(0);
        VBox.setVgrow(clearFilter, Priority.ALWAYS);

        vBoxFilter.getChildren().addAll(textFilter, filter, clearFilter, sp, profiles);

        Label lblRight = new Label();
        tglBlacklist.setAllowIndeterminate(true);
        tglBlacklist.setLabelRight(lblRight, "ein", "aus", "invers");
        tglBlacklist.setTooltip(new Tooltip("Blacklist aus: Alle Filme werden angezeigt.\n" +
                "Blacklist ein: Von der Blacklist erfasste Filme werden nicht angezeigt.\n" +
                "Blacklist invers: Nur von der Blacklist erfasste Filme werden angezeigt."));


        tglBlacklist.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().blacklistOnProperty());
        tglBlacklist.indeterminateProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().blacklistOnlyProperty());

        vBoxBlacklist = getVBoxBotton();
        HBox hBox = new HBox(5);
        HBox.setHgrow(tglBlacklist, Priority.ALWAYS);
        hBox.getChildren().addAll(tglBlacklist, lblRight);
        vBoxBlacklist.getChildren().add(hBox);
    }
}
