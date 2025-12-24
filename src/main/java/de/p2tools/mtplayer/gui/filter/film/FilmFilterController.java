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
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.mtplayer.controller.filter.FilterWorker;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FilmFilterController extends FilterController {

    final FilmFilterControllerTextFilter filmFilterControllerTextFilter;
    final FilmFilterControllerOnlyNew filmFilterControllerOnlyNew;
    final FilmFilterControllerFilter filmFilterControllerFilter;
    final FilmFilterControllerClearFilter filmFilterControllerClearFilter;

    final FilmFilterControllerProfiles filmFilterControllerProfiles;
    final FilmFilterControllerBlacklist filmFilterControllerBlacklist;

    final FilmSmallFilterControllerFilter filmSmallFilterControllerFilter;
    private final FilterDto filterDto;

    public FilmFilterController(FilterDto filterDto) {
        this.filterDto = filterDto;
        filmFilterControllerTextFilter = new FilmFilterControllerTextFilter(filterDto);
        filmFilterControllerOnlyNew = new FilmFilterControllerOnlyNew(filterDto);
        filmFilterControllerFilter = new FilmFilterControllerFilter(filterDto);
        filmFilterControllerClearFilter = new FilmFilterControllerClearFilter(filterDto);

        filmFilterControllerProfiles = new FilmFilterControllerProfiles(filterDto);
        filmFilterControllerBlacklist = new FilmFilterControllerBlacklist(filterDto);

        filmSmallFilterControllerFilter = new FilmSmallFilterControllerFilter(filterDto);
        if (filterDto.audio) {
            ProgConfig.AUDIOFILTER_SMALL_FILTER.addListener((u, o, n) -> {
                setFilter();
//                setGui();
            });

        } else {
            ProgConfig.FILMFILTER_SMALL_FILTER.addListener((u, o, n) -> {
                setFilter();
//                setGui();
            });
        }

        setFilterStart();
        initGui();
//        setGui();
    }

    private void setFilterStart() {
        boolean small = filterDto.audio ? ProgConfig.AUDIOFILTER_SMALL_FILTER.get() : ProgConfig.FILMFILTER_SMALL_FILTER.get();
        if (small) {
            // actFilterSettings sind die Einstellungen des Filters beim Beenden
            filterDto.filterWorker.getActFilterSettings()
                    .copyTo(filterDto.filterWorker.getStoredSmallFilterSettings());
            // vis setzen
            FilterWorker.setSmallFilter(filterDto.filterWorker.getStoredSmallFilterSettings());

        } else {
            filterDto.filterWorker.getActFilterSettings()
                    .copyTo(filterDto.filterWorker.getStoredFilterSettings());
        }
    }

    private void setFilter() {
        boolean small = filterDto.audio ? ProgConfig.AUDIOFILTER_SMALL_FILTER.get() : ProgConfig.FILMFILTER_SMALL_FILTER.get();
        if (small) {
            // dann den kleinen Filter
            filterDto.filterWorker.getActFilterSettings()
                    .copyTo(filterDto.filterWorker.getStoredFilterSettings());

            FilterWorker.setSmallFilter(filterDto.filterWorker.getStoredSmallFilterSettings());
            filterDto.filterWorker.setActFilterSettings(filterDto.filterWorker.getStoredSmallFilterSettings());

        } else {
            // dann alle Filter
            filterDto.filterWorker.getActFilterSettings()
                    .copyTo(filterDto.filterWorker.getStoredSmallFilterSettings());

            filterDto.filterWorker.setActFilterSettings(
                    filterDto.filterWorker.getStoredFilterSettings());
        }
    }

//    private void setGui() {
//        boolean small = filterDto.audio ? ProgConfig.AUDIOFILTER_SMALL_FILTER.get() : ProgConfig.FILMFILTER_SMALL_FILTER.get();
//        if (small) {
//            getChildren().clear();
//            final VBox vBox = getVBoxFilter();
//            vBox.getChildren().addAll(filmFilterControllerTextFilter);
//            vBox.getChildren().add(P2GuiTools.getHDistance(10));
//            vBox.getChildren().addAll(filmFilterControllerFilter);
//            vBox.getChildren().add(P2GuiTools.getHDistance(10));
//            vBox.getChildren().addAll(filmFilterControllerOnlyNew);
//
//            vBox.getChildren().add(P2GuiTools.getVBoxGrower());
//            vBox.getChildren().addAll(filmSmallFilterControllerFilter);
//            getVBoxBlack().getChildren().add(filmFilterControllerBlacklist);
//
//        } else {
//            // dann alle Filter
//            getChildren().clear();
//            final VBox vBox = getVBoxFilter();
//            vBox.getChildren().addAll(filmFilterControllerTextFilter);
//
//            Separator sp = new Separator();
//            sp.getStyleClass().add("pseperator2");
//            sp.setMinHeight(0);
//            sp.setMaxHeight(1);
//            sp.visibleProperty().bind(filmFilterControllerTextFilter.visibleProperty());
//            sp.managedProperty().bind(filmFilterControllerTextFilter.visibleProperty());
//            vBox.getChildren().add(sp);
//
//            vBox.getChildren().addAll(filmFilterControllerFilter,
//                    P2GuiTools.getVBoxGrower(),
//                    filmFilterControllerClearFilter);
//
//            getVBoxBlack().getChildren().add(filmFilterControllerProfiles);
//            getVBoxBlack().getChildren().add(filmFilterControllerBlacklist);
//        }
//    }

    private void initGui() {
        BooleanProperty small = filterDto.audio ? ProgConfig.AUDIOFILTER_SMALL_FILTER : ProgConfig.FILMFILTER_SMALL_FILTER;

        final VBox vBox = getVBoxFilter();
        vBox.getChildren().addAll(filmFilterControllerTextFilter);

        Region reg1 = P2GuiTools.getHDistance(10);
        vBox.getChildren().add(reg1);

        vBox.getChildren().addAll(filmFilterControllerFilter);

        Region reg2 = P2GuiTools.getHDistance(10); // small
        reg2.visibleProperty().bind(small);
        reg2.managedProperty().bind(small);
        vBox.getChildren().add(reg2); // small

        Region reg3 = P2GuiTools.getVBoxGrower(); // all
        reg3.visibleProperty().bind(small.not());
        reg3.managedProperty().bind(small.not());
        vBox.getChildren().add(reg3); // all

        vBox.getChildren().addAll(filmFilterControllerClearFilter); //  all
        filmFilterControllerClearFilter.visibleProperty().bind(small.not());
        filmFilterControllerClearFilter.managedProperty().bind(small.not());

        getVBoxBlack().getChildren().add(filmFilterControllerProfiles); // all
        filmFilterControllerProfiles.visibleProperty().bind(small.not());
        filmFilterControllerProfiles.managedProperty().bind(small.not());

        vBox.getChildren().addAll(filmFilterControllerOnlyNew); // small
        filmFilterControllerOnlyNew.visibleProperty().bind(small);
        filmFilterControllerOnlyNew.managedProperty().bind(small);

        Region reg4 = P2GuiTools.getVBoxGrower(); // small
        reg4.visibleProperty().bind(small);
        reg4.managedProperty().bind(small);
        vBox.getChildren().add(reg4); // small

        vBox.getChildren().addAll(filmSmallFilterControllerFilter); // small
        filmSmallFilterControllerFilter.visibleProperty().bind(small);
        filmSmallFilterControllerFilter.managedProperty().bind(small);

        getVBoxBlack().getChildren().add(filmFilterControllerBlacklist);
    }
}
