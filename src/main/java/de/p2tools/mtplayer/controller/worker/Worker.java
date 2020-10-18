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

import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class Worker {

    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> themeForChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    private ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");

    final SelectedFilter sfTemp = new SelectedFilter();
    String downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.get();
    String downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.get();
    String aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.get();

    private final ProgData progData;

    public Worker(ProgData progData) {
        this.progData = progData;

        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, false);
                } else {
                    progData.maskerPane.setMaskerVisible(true, true);
                }
                progData.maskerPane.setMaskerProgress(event.progress, event.text);

                // the channel combo will be resetted, therefor save the filter
                saveFilter();
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerVisible(true, false);
                progData.maskerPane.setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                new ProgSave().saveAll(); // damit nichts verlorengeht
                createChannelAndThemeList();
                if (ProgConfig.ABO_SEARCH_NOW.getBool() || ProgData.automode) {
                    searchForAbosAndMaybeStart();
                } else {
                    progData.maskerPane.setMaskerVisible(false);
                }

                // activate the saved filter
                resetFilter();
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

    private void saveFilter() {
        SelectedFilterFactory.copyFilter(progData.storedFilters.getActFilterSettings(), sfTemp);
        downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.get();
        downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.get();
        aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.get();
    }

    private void resetFilter() {
        SelectedFilterFactory.copyFilter(sfTemp, progData.storedFilters.getActFilterSettings());
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue(downloadFilterChannel);
        ProgConfig.FILTER_DOWNLOAD_ABO.setValue(downloadFilterAbo); // todo ???
        ProgConfig.FILTER_ABO_CHANNEL.setValue(aboFilterChannel);
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
        progData.maskerPane.setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Downloads suchen");

//        Thread th = new Thread(() -> {
//            try {
// todo da kommst sonst zu Laufzeitproblemen

        PLog.sysLog("Downloads aus Abos suchen");
        // erledigte entfernen, nicht gestartete Abos entfernen und nach neu Abos suchen
        progData.downloadList.searchForDownloadsFromAbos();

        if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_START_NOW.get()) || ProgData.automode) {
            // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
            PLog.sysLog("Downloads aus Abos starten");
            progData.downloadList.startDownloads();
        }

//            } catch (Exception ex) {
//                PLog.errorLog(951241204, ex);
//            } finally {

        progData.maskerPane.switchOffMasker();
        PDuration.counterStop("Worker.searchForAbosAndMaybeStart");


//            }
//        });
//        th.setName("searchForAbosAndMaybeStart");
//        th.start();
    }

    private void createChannelAndThemeList() {
        // alle Sender laden
        allChannelList.setAll(Arrays.asList(progData.filmlist.sender));
        // und jetzt noch die Themen für den Sender des aktuellen Filter laden
        createThemeList(progData.storedFilters.getActFilterSettings().getChannel());
    }

    public void createThemeList(String sender) {
        // toDo geht vielleicht besser??
//        System.out.println("createThemeList: " + sender);

        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[0]));
        } else {
            makeTheme(sender.trim(), theme);
        }

        Platform.runLater(() -> { // todo brauchts da nicht??
            saveFilter();
            this.progData.storedFilters.getActFilterSettings().setReportChange(false);
            themeForChannelList.setAll(theme);
            this.progData.storedFilters.getActFilterSettings().setReportChange(true);
            resetFilter();
            this.progData.storedFilters.initFilter();
        });
    }

    private void makeTheme(String sender, ArrayList<String> theme) {
        // todo liste sortieren
        if (sender.contains(",")) {

            String[] senderArr = sender.toLowerCase().split(",");
            final TreeSet<String> tree = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            tree.add("");

            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
                for (String s : senderArr) {
                    if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(s.trim())) {
                        tree.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
                        break;
                    }
                }
            }
            theme.addAll(tree);

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
            saveFilter();
            channelsForAbosList.setAll(listAboChannel);
            allAboNamesList.setAll(listAboName);
            resetFilter();
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
