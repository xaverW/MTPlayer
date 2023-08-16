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
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.Collator;
import java.util.*;

public class Worker {

    private final ObservableList<String> allChannelList = FXCollections.observableArrayList("");
    private final ObservableList<String> themeForChannelList = FXCollections.observableArrayList("");
    private final ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    private final ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");

    private String downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
    private String downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
    private String aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();

    private final ProgData progData;

    public Worker(ProgData progData) {
        this.progData = progData;
        getAboNames();
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

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
            PLog.sysLog("Es gibt eine neue Filmliste und die soll sofort geladen werden");
            LoadFilmFactory.getInstance().loadNewListFromWeb(false);
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

    public void workOnFilmListLoadStart() {
        // the channel combo will be reset, therefore save the filter
        saveFilter();
    }

    public void workOnFilmListLoadFinished() {
        Platform.runLater(() -> {
            // alle Sender laden
            allChannelList.setAll(Arrays.asList(progData.filmList.sender));

            // und jetzt noch die Themen für den Sender des aktuellen Filters laden
            createThemeList(progData.filmFilterWorker.getActFilterSettings().getChannel());
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                DownloadFactory.searchForAbosAndMaybeStart();
            }

            // activate the saved filter
            resetFilter();
        });
    }

    private void saveFilter() {
        // Filter Downloads
        downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
        downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
        // Filter Abos
        aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
    }

    private void resetFilter() {
        // nur wenn noch drin, dann wieder setzen
        if (allAboNamesList.contains(downloadFilterAbo)) {
            ProgConfig.FILTER_DOWNLOAD_ABO.setValue(downloadFilterAbo);
        } else {
            ProgConfig.FILTER_DOWNLOAD_ABO.setValue("");
        }
        if (allChannelList.contains(downloadFilterChannel)) {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue(downloadFilterChannel);
        } else {
            ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue("");
        }
        if (allChannelList.contains(aboFilterChannel)) {
            ProgConfig.FILTER_ABO_CHANNEL.setValue(aboFilterChannel);
        } else {
            ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        }
    }

    public void createThemeList(String sender) {
        //toDo geht vielleicht besser??
        PDuration.counterStart("createThemeList");
        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[0]));
        } else {
            makeTheme(sender.trim(), theme);
        }

        Collator collator = Collator.getInstance(Locale.GERMANY);
        collator.setStrength(Collator.PRIMARY);
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);

        Comparator<String> comparator = (arg1, arg2) -> {
            if (arg1.startsWith("\"") || arg1.startsWith("#") || arg1.startsWith("„")) {
                arg1 = arg1.substring(1);
            }
            if (arg2.startsWith("\"") || arg2.startsWith("#") || arg2.startsWith("„")) {
                arg2 = arg2.substring(1);
            }

            return collator.compare(arg1, arg2);
        };
        theme.sort(comparator);

        Platform.runLater(() -> {
            saveFilter();
            themeForChannelList.setAll(theme);
//            this.progData.filmFilterWorker.addBackward();
            resetFilter();
        });
        PDuration.counterStop("createThemeList");
    }

    private void makeTheme(String sender, ArrayList<String> theme) {
        if (sender.contains(",")) {
            String[] senderArr = sender.toLowerCase().split(",");
            final TreeSet<String> tree = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            tree.add("");

            for (int i = 1; i < progData.filmListFiltered.themePerChannel.length; ++i) {
                for (String s : senderArr) {
                    if (progData.filmListFiltered.sender[i].equalsIgnoreCase(s.trim())) {
                        tree.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[i]));
                        break;
                    }
                }
            }
            theme.addAll(tree);

        } else {
            for (int i = 1; i < progData.filmListFiltered.themePerChannel.length; ++i) {
                if (progData.filmListFiltered.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[i]));
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
}
