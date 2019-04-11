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

import de.mtplayer.mtp.controller.ProgSave;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.dialog.NoSetDialogController;
import de.p2tools.p2Lib.tools.duration.PDuration;
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
                if (event.progress == ListenerFilmlistLoad.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, false);
                } else {
                    progData.maskerPane.setMaskerVisible(true, true);
                }
                progData.maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerVisible(true, false);
                progData.maskerPane.setMaskerProgress(ListenerFilmlistLoad.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                new ProgSave().saveAll(); // damit nichts verlorengeht
                getChannelAndTheme();
                if (ProgConfig.ABO_SEARCH_NOW.getBool()) {
                    searchForAbosAndMaybeStart();
                } else {
                    progData.maskerPane.setMaskerVisible(false);
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
            progData.maskerPane.switchOffMasker();
            return;
        }


        if (progData.setDataList.getSetDataForAbo() == null) {
            // SetData sind nicht eingerichtet
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            progData.maskerPane.switchOffMasker();
            return;
        }


        PDuration.counterStart("Worker.searchForAbosAndMaybeStart");
        progData.maskerPane.setMaskerVisible(true, false);
        progData.maskerPane.setMaskerProgress(ListenerFilmlistLoad.PROGRESS_INDETERMINATE, "Downloads suchen");

        Thread th = new Thread(() -> {
            try {

                PLog.sysLog("Downloads aus Abos suchen");
                // erledigte entfernen, nicht gestartete Abos entfernen und nach neu Abos suchen
                progData.downloadList.searchForDownloadsFromAbos();

                if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_START_NOW.get())) {
                    // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
                    PLog.sysLog("Downloads aus Abos starten");
                    progData.downloadList.startDownloads();
                }

            } catch (Exception ex) {
                PLog.errorLog(951241204, ex);
            } finally {
                progData.maskerPane.switchOffMasker();
                PDuration.counterStop("Worker.searchForAbosAndMaybeStart");
            }
        });

        th.setName("searchForAbosAndMaybeStart");
        th.start();
    }

    private void getChannelAndTheme() {
//        Platform.runLater(() -> allChannelList.setAll(Arrays.asList(progData.filmlist.sender)));
        allChannelList.setAll(Arrays.asList(progData.filmlist.sender));
        getTheme("");
    }

    public void getTheme(String sender) {
        // toDo geht vielleicht besser??
        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[0]));
        } else {
            makeTheme(sender.trim(), theme);
//            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
//                if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
//                    theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
//                    break;
//                }
//            }
        }

        Platform.runLater(() -> themeForChannelList.setAll(theme));
    }

    private void makeTheme(String sender, ArrayList<String> theme) {
        // todo liste sortieren
        if (sender.contains(",")) {
            String[] senderArr = sender.split(",");
            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
                for (String s : senderArr) {
                    if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(s.trim())) {
                        theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
                        break;
                    }
                }
            }

        } else {
            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
                if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
                    break;
                }
            }
        }

    }

    private void getAboNames() {
        final ArrayList<String> listAboChannel = progData.aboList.getAboChannelList();
        final ArrayList<String> listAboName = progData.aboList.getAboNameList();

        Platform.runLater(() -> {
            channelsForAbosList.setAll(listAboChannel);
            allAboNamesList.setAll(listAboName);
        });
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
