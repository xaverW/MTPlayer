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
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.Collator;
import java.util.*;

public class Worker {

    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> themeForChannelList = FXCollections.observableArrayList("");
    private ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    private ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");

    final FilmFilter sfTemp = new FilmFilter();
    String downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.get();
    String downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.get();
    String aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();

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

    public void workOnLoadStart() {
        // the channel combo will be reseted, therefore save the filter
        saveFilter();
    }

    public void workOnLoadFinished() {
        Platform.runLater(() -> {
            // alle Sender laden
            allChannelList.setAll(Arrays.asList(progData.filmlist.sender));

            // und jetzt noch die Themen für den Sender des aktuellen Filters laden
            createThemeList(progData.actFilmFilterWorker.getActFilterSettings().getChannel());
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.automode) {
                searchForAbosAndMaybeStart();
            }

            // activate the saved filter
            resetFilter();
        });
    }

    private void saveFilter() {
        progData.actFilmFilterWorker.getActFilterSettings().copyTo(sfTemp);
        downloadFilterChannel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
        downloadFilterAbo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
        aboFilterChannel = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
    }

    private void resetFilter() {
        sfTemp.copyTo(progData.actFilmFilterWorker.getActFilterSettings());
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue(downloadFilterChannel);
        ProgConfig.FILTER_DOWNLOAD_ABO.setValue(downloadFilterAbo); // todo ???
        ProgConfig.FILTER_ABO_CHANNEL.setValue(aboFilterChannel);
    }

    public void searchForAbosAndMaybeStart() {
        if (LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            return;
        }

        if (progData.setDataList.getSetDataForAbo() == null) {
            // SetData sind nicht eingerichtet
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            return;
        }

        PDuration.counterStart("searchForAbosAndMaybeStart");
        PLog.sysLog("Downloads aus Abos suchen");
        //erledigte entfernen, nicht gestartete Abos entfernen und nach neu Abos suchen
        progData.downloadList.searchForDownloadsFromAbos();

        if (ProgConfig.DOWNLOAD_START_NOW.getValue() || ProgData.automode) {
            // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
            PLog.sysLog("Downloads aus Abos starten");
            progData.downloadList.startDownloads();
        }
        PDuration.counterStop("searchForAbosAndMaybeStart");
    }

    public void createThemeList(String sender) {
        //toDo geht vielleicht besser??
        PDuration.counterStart("createThemeList");
        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[0]));
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
        Collections.sort(theme, comparator);

        Platform.runLater(() -> {
            saveFilter();
            this.progData.actFilmFilterWorker.getActFilterSettings().setReportChange(false);
            themeForChannelList.setAll(theme);
            this.progData.actFilmFilterWorker.getActFilterSettings().setReportChange(true);
            resetFilter();
            this.progData.actFilmFilterWorker.initFilter();
        });
        PDuration.counterStop("createThemeList");
    }

    private void makeTheme(String sender, ArrayList<String> theme) {
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
