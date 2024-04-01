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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadDataFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;

public class Worker {

    private String downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
    private String downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
    private String aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();

    private final ProgData progData;

    public Worker(ProgData progData) {
        this.progData = progData;
        getAboNames();
        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                Platform.runLater(DownloadDataFactory::searchForAbosAndMaybeStart);
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                Platform.runLater(DownloadDataFactory::searchForAbosAndMaybeStart);
            }
            getAboNames();
        });

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.downloadList.sizeProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.checkForNewFilmlist.foundNewListProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY.getValue()) {
                //dann soll gar keine geladen werden
                return;
            }
            if (!progData.checkForNewFilmlist.isFoundNewList()) {
                //dann gibts auch keine
                return;
            }
            if (LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
                //wird eh grad gemacht
                return;
            }

            //dann soll sofort eine neue Liste geladen werden
            P2Log.sysLog("Es gibt eine neue Filmliste und die soll sofort geladen werden");
            LoadFilmFactory.getInstance().loadNewListFromWeb(false);
        });
    }


    public void workOnFilmListLoadStart() {
        // the channel combo will be reset, therefore save the filter
        saveFilter();
    }

    public void workOnFilmListLoadFinished() {
        Platform.runLater(() -> {
            // alle Sender laden
            ThemeListFactory.allChannelList.setAll(Arrays.asList(progData.filmList.sender));

            // und jetzt noch die Themen für den Sender des aktuellen Filters laden
            ThemeListFactory.createThemeList(progData, progData.filmFilterWorker.getActFilterSettings().getChannel());
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                DownloadDataFactory.searchForAbosAndMaybeStart();
            }

            // activate the saved filter
            resetFilter();
        });
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
        if (ThemeListFactory.allChannelList.contains(downloadFilterChannel)) {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue(downloadFilterChannel);
        } else {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue("");
        }
        if (ThemeListFactory.allChannelList.contains(aboFilterChannel)) {
            ProgConfig.FILTER_ABO_CHANNEL.setValue(aboFilterChannel);
        } else {
            ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        }
    }

    private void getAboNames() {
        final ArrayList<String> listAboChannel = progData.aboList.getAboChannelList();
        final ArrayList<String> listAboName = progData.aboList.getAboNameList();
        Platform.runLater(() -> {
            saveFilter();
            ThemeListFactory.channelsForAbosList.setAll(listAboChannel);
            ThemeListFactory.allAboNamesList.setAll(listAboName);
            resetFilter();
        });
    }
}
