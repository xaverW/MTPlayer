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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompList;
import de.p2tools.mtplayer.controller.data.compdata.SortData;
import de.p2tools.mtplayer.controller.data.compdata.SortList;
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.p2lib.guitools.pcbo.P2CboSeparator;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FilmFilterControllerSort extends VBox {

    private final ComboBox<SortData> cboSort = new P2CboSeparator<>();
    private final ProgData progData;
    private final FilterDto filterDto;
    private final SortList sortList;
    private final CompList compList;

    public FilmFilterControllerSort(FilterDto filterDto) {
        this.filterDto = filterDto;
        progData = ProgData.getInstance();
        sortList = this.filterDto.audio ? progData.sortListAudio : progData.sortListFilm;
        compList = this.filterDto.audio ? progData.compListAudio : progData.compListFilm;
        init();
    }

    private void init() {
        cboSort.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(new Label("Sortierung:"), cboSort);
        sortList.addListener((u, o, n) -> initCbo());
        initCbo();
    }

    private void initCbo() {
        SortData sortData = new SortData("");
        cboSort.getItems().clear();
        cboSort.getItems().add(sortData);
        cboSort.getItems().addAll(sortList);

        cboSort.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            SortData sd = cboSort.getSelectionModel().getSelectedItem();
            if (sd != null) {
                if (filterDto.audio) {
                    ProgConfig.SYSTEM_SORT_AUDIO.set(cboSort.getSelectionModel().getSelectedIndex());
                } else {
                    ProgConfig.SYSTEM_SORT_FILM.set(cboSort.getSelectionModel().getSelectedIndex());
                }

                if (sd == sortData) {
                    compList.clear();
                    ProgData.getInstance().pEventHandler.notifyListener(filterDto.audio ? PEvents.EVENT_SORT_AUDIO : PEvents.EVENT_SORT_FILM);
                } else {
                    compList.setAll(sd.getCompList());
                    ProgData.getInstance().pEventHandler.notifyListener(filterDto.audio ? PEvents.EVENT_SORT_AUDIO : PEvents.EVENT_SORT_FILM);
                }
            }
        });

        // damit evtl. Table sortieren
        if (filterDto.audio) {
            if (cboSort.getItems().size() > ProgConfig.SYSTEM_SORT_AUDIO.get()) {
                cboSort.getSelectionModel().select(ProgConfig.SYSTEM_SORT_AUDIO.get());
            }
        } else {
            if (cboSort.getItems().size() > ProgConfig.SYSTEM_SORT_FILM.get()) {
                cboSort.getSelectionModel().select(ProgConfig.SYSTEM_SORT_FILM.get());
            }
        }
    }
}
