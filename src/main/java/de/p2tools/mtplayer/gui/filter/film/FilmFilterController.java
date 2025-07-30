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

package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filmfilter.FilterWorker;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    final FilmFilterControllerTextFilter filmFilterControllerTextFilter;
    final FilmFilterControllerFilter filmFilterControllerFilter;
    final FilmFilterControllerClearFilter filmFilterControllerClearFilter;

    final FilmFilterControllerProfiles filmFilterControllerProfiles;
    final FilmFilterControllerBlacklist filmFilterControllerBlacklist;

    final FilmSmallFilterControllerFilter filmSmallFilterControllerFilter;

    public FilmFilterController() {
        filmFilterControllerTextFilter = new FilmFilterControllerTextFilter();
        filmFilterControllerFilter = new FilmFilterControllerFilter();
        filmFilterControllerClearFilter = new FilmFilterControllerClearFilter();

        filmFilterControllerProfiles = new FilmFilterControllerProfiles();
        filmFilterControllerBlacklist = new FilmFilterControllerBlacklist();

        filmSmallFilterControllerFilter = new FilmSmallFilterControllerFilter();
        ProgConfig.FILMFILTER_SMALL_FILTER.addListener((u, o, n) -> {
            setFilter();
            setGui();
        });

        setFilterStart();
        setGui();
    }

    private void setFilterStart() {
        if (ProgConfig.FILMFILTER_SMALL_FILTER.get()) {
            // actFilterSettings sind die Einstellungen des Filters beim Beenden
            ProgData.getInstance().filterWorker.getActFilterSettings()
                    .copyTo(ProgData.getInstance().filterWorker.getStoredSmallFilterSettings());
            // vis setzen
            FilterWorker.setSmallFilter(ProgData.getInstance().filterWorker.getStoredSmallFilterSettings());

        } else {
            ProgData.getInstance().filterWorker.getActFilterSettings()
                    .copyTo(ProgData.getInstance().filterWorker.getStoredFilterSettings());
        }
    }

    private void setFilter() {
        if (ProgConfig.FILMFILTER_SMALL_FILTER.get()) {
            // dann den kleinen Filter
            ProgData.getInstance().filterWorker.getActFilterSettings()
                    .copyTo(ProgData.getInstance().filterWorker.getStoredFilterSettings());

            FilterWorker.setSmallFilter(ProgData.getInstance().filterWorker.getStoredSmallFilterSettings());
            ProgData.getInstance().filterWorker.setActFilterSettings(ProgData.getInstance().filterWorker.getStoredSmallFilterSettings());

        } else {
            // dann alle Filter
            ProgData.getInstance().filterWorker.getActFilterSettings()
                    .copyTo(ProgData.getInstance().filterWorker.getStoredSmallFilterSettings());

            ProgData.getInstance().filterWorker.setActFilterSettings(
                    ProgData.getInstance().filterWorker.getStoredFilterSettings());
        }
    }

    private void setGui() {
        if (ProgConfig.FILMFILTER_SMALL_FILTER.get()) {
            getChildren().clear();
            final VBox vBox = getVBoxFilter();
            vBox.getChildren().addAll(filmFilterControllerTextFilter);
            vBox.getChildren().add(P2GuiTools.getHDistance(20));
            vBox.getChildren().addAll(filmFilterControllerFilter);
            vBox.getChildren().add(P2GuiTools.getVBoxGrower());
            vBox.getChildren().addAll(filmSmallFilterControllerFilter);

        } else {
            // dann alle Filter
            getChildren().clear();
            final VBox vBox = getVBoxFilter();
            vBox.getChildren().addAll(filmFilterControllerTextFilter);

            Separator sp = new Separator();
            sp.getStyleClass().add("pseperator1");
            sp.setMinHeight(0);
            sp.setMaxHeight(1);
            sp.visibleProperty().bind(filmFilterControllerTextFilter.visibleProperty());
            sp.managedProperty().bind(filmFilterControllerTextFilter.visibleProperty());
            vBox.getChildren().add(sp);

            vBox.getChildren().addAll(filmFilterControllerFilter,
                    P2GuiTools.getVBoxGrower(),
                    filmFilterControllerClearFilter);

            getVBoxBlack().getChildren().add(filmFilterControllerProfiles);
            getVBoxBlack().getChildren().add(filmFilterControllerBlacklist);
        }
    }
}
