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

package de.mtplayer.mtp.controller.worker;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class Worker {

    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> themeForChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    private ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");

    private final ProgData progData;

    public Worker(ProgData progData) {
        this.progData = progData;
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                getChannelAndTheme();
                if (ProgConfig.ABO_SEARCH_NOW.getBool()) {
                    searchForAbosAndMaybeStart();
                }
            }
        });

        getAboNames();
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.downloadList.sizeProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());
    }

    public void searchForAbosAndMaybeStart() {
        if (progData.loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            return;
        }

        PDuration.counterStart("DownloadGuiController.searchForAbosAndMaybeStart");
        progData.mtPlayerController.setMasker();

        Thread th = new Thread(() -> {
            try {
                // erledigte entfernen, nicht gestartete Abos entfernen und neu nach Abos suchen
                progData.downloadList.searchForAbos();

                if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_START_NOW.get())) {
                    // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
                    progData.downloadList.startDownloads();
                }

                progData.mtPlayerController.resetMasker();
                PDuration.counterStop("DownloadGuiController.searchForAbosAndMaybeStart");
            } catch (Exception ex) {
                PLog.errorLog(951241204, ex);
            }
        });

        th.setName("searchForAbosAndMaybeStart");
        th.start();
    }

    private void getChannelAndTheme() {
        Platform.runLater(() -> allChannelList.setAll(Arrays.asList(progData.filmlist.sender)));
        getTheme("");
    }

    private void getAboNames() {
        final ArrayList<String> listAboChannel = progData.aboList.getAboChannelList();
        final ArrayList<String> listAboName = progData.aboList.getAboNameList();

        Platform.runLater(() -> {
            channelsForAbosList.setAll(listAboChannel);
            allAboNamesList.setAll(listAboName);
        });
    }

    public void getTheme(String sender) {
        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[0]));
        } else {
            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
                if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
                    break;
                }
            }
        }

        Platform.runLater(() -> themeForChannelList.setAll(theme));
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }

    public ObservableList<String> getThemeForChannelList() {
        return themeForChannelList;
    }

    public ObservableList<String> getChannelsForAbosList() {
        return channelsForAbosList;
    }

    public ObservableList<String> getAllAboNamesList() {
        return allAboNamesList;
    }

}
