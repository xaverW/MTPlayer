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

package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboSearchDownloadsFactory;
import de.p2tools.mtplayer.controller.load.LoadFilmFactory;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;

public class Worker {

    private String downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
    private String downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
    private String aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();

    private final ProgData progData;

    public Worker(ProgData progData) {
        this.progData = progData;
        getAboNames();
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_BLACKLIST_CHANGED) {
            @Override
            public void pingGui() {
                if (ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue()
                        && ProgConfig.ABO_SEARCH_NOW.getValue()) {
                    // nur auf Blacklist reagieren, wenn auch für Abos eingeschaltet
                    AboSearchDownloadsFactory.searchForDownloadsFromAbosAndMaybeStart();
                }
            }
        });
        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue()) {
                Platform.runLater(AboSearchDownloadsFactory::searchForDownloadsFromAbosAndMaybeStart);
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                getAboNames();
                if (ProgConfig.ABO_SEARCH_NOW.getValue()) {
                    AboSearchDownloadsFactory.searchForDownloadsFromAbosAndMaybeStart();
                }
            });
        });

        progData.checkForNewFilmlist.foundNewListProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY.getValue()) {
                //dann soll gar keine geladen werden
                return;
            }
            if (!progData.checkForNewFilmlist.isFoundNewList()) {
                //dann gibts auch keine
                return;
            }
            if (ProgData.FILMLIST_IS_DOWNLOADING.get()) {
                //wird eh grad gemacht
                return;
            }

            //dann soll sofort eine neue Liste geladen werden
            P2Log.sysLog("Es gibt eine neue Filmliste und die soll sofort geladen werden");
            LoadFilmFactory.loadFilmListFromWeb(false, true);
        });
    }

    public void workOnConfigLoaded() {
        getAboNames();
    }

    public void workOnConfigSaved() {
        getAboNames();
    }

    public void saveFilter() {
        // Filter Downloads
        downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
        downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
        // Filter Abos
        aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
    }

    public void resetFilter() {
        // nur wenn noch drin, dann wieder setzen
        if (ThemeListFactory.allAboNamesList.contains(downloadFilterAbo)) {
            ProgConfig.FILTER_DOWNLOAD_ABO.setValue(downloadFilterAbo);
        } else {
            ProgConfig.FILTER_DOWNLOAD_ABO.setValue("");
        }
        if (ThemeListFactory.allChannelListFilm.contains(downloadFilterChannel)) {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue(downloadFilterChannel);
        } else {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue("");
        }
        if (ThemeListFactory.allChannelListFilm.contains(aboFilterChannel)) {
            ProgConfig.FILTER_ABO_CHANNEL.setValue(aboFilterChannel);
        } else {
            ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        }
    }

    private void getAboNames() {
        final ArrayList<String> listAboChannel = progData.aboList.getAboChannelList();
        final ArrayList<String> listAboName = progData.aboList.getAboNameList();
        saveFilter();
        ThemeListFactory.channelsForAbosList.setAll(listAboChannel);
        ThemeListFactory.allAboNamesList.setAll(listAboName);
        resetFilter();
    }
}
