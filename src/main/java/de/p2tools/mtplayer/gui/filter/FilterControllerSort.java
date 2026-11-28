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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompList;
import de.p2tools.mtplayer.controller.data.compdata.SortData;
import de.p2tools.mtplayer.controller.data.compdata.SortList;
import de.p2tools.p2lib.guitools.pcbo.P2CboSeparator;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FilterControllerSort extends VBox {

    public enum SORTER {FILM, AUDIO, LIVE, DOWNLOAD, ABO}

    private final ComboBox<SortData> cboSort = new P2CboSeparator<>();
    private final ProgData progData;
    private final SortList sortList;
    private final CompList compList;
    private boolean stop = false;
    private final Enum<SORTER> sorterEnum;
    private final IntegerProperty idx;
    private final int eventSort;

    public FilterControllerSort(Enum<SORTER> sorterEnum) {
        progData = ProgData.getInstance();
        this.sorterEnum = sorterEnum;
        sortList = progData.sortList;
        if (sorterEnum.equals(SORTER.FILM)) {
            compList = progData.compListFilm;
            idx = ProgConfig.SYSTEM_SORT_IDX_FILM;
            eventSort = PEvents.EVENT_SORT_FILM;

        } else if (sorterEnum.equals(SORTER.AUDIO)) {
            compList = progData.compListAudio;
            idx = ProgConfig.SYSTEM_SORT_IDX_AUDIO;
            eventSort = PEvents.EVENT_SORT_AUDIO;

        } else if (sorterEnum.equals(SORTER.LIVE)) {
            compList = progData.compListLive;
            idx = ProgConfig.SYSTEM_SORT_IDX_LIVE;
            eventSort = PEvents.EVENT_SORT_LIVE;

        } else if (sorterEnum.equals(SORTER.DOWNLOAD)) {
            compList = progData.compListDownload;
            idx = ProgConfig.SYSTEM_SORT_IDX_DOWNLOAD;
            eventSort = PEvents.EVENT_SORT_DOWNLOAD;

        } else {
            compList = progData.compListAbo;
            idx = ProgConfig.SYSTEM_SORT_IDX_ABO;
            eventSort = PEvents.EVENT_SORT_ABO;
        }

        init();
        initCbo();
    }

    private void init() {
        setSpacing(2);
        cboSort.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(new Label("Sortierung:"), cboSort);
    }

    private void initCbo() {
        loadCbo();
        progData.pEventHandler.addListener(
                new P2Listener(PEvents.EVENT_BTN_RUN_SORT) {
                    @Override
                    public void pingGui() {
                        loadCbo();
                    }
                });

        cboSort.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            if (stop) {
                return;
            }

            P2Duration.counterStart("Sort");
            int id = cboSort.getSelectionModel().getSelectedIndex();
            if (id >= 0) {
                idx.set(id);
            }
            setCbo();
            // todo
            Platform.runLater(() -> {
                System.out.println("fertig");
                P2Duration.counterStop("Sort");
            });
        });
    }

    private void loadCbo() {
        stop = true;
        cboSort.getItems().clear();
        cboSort.getItems().add(new SortData(""));
        cboSort.getItems().addAll(sortList);
        cboSort.getSelectionModel().selectFirst();
        setCbo();
        stop = false;
    }

    private void setCbo() {
        if (cboSort.getItems().size() > idx.get()) {
            cboSort.getSelectionModel().select(idx.get());

        } else {
            idx.set(0);
            cboSort.getSelectionModel().clearSelection();
            cboSort.getSelectionModel().selectFirst();
        }

        setCompList();
        ProgData.getInstance().pEventHandler.notifyListener(eventSort);
    }

    private void setCompList() {
        SortData sd = cboSort.getSelectionModel().getSelectedItem();
        if (sd == null || sd == cboSort.getItems().get(0)) {
            compList.clear();
        } else {
            compList.setAll(sd.getCompList());
        }
    }
}
