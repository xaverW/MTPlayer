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

package de.p2tools.mtplayer.controller.data.history;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmToolsFactory;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class HistoryList extends SimpleListProperty<HistoryData> {

    public enum HISTORY_LIST {
        HISTORY, ABO
    }

    private static final AtomicBoolean isWorking = new AtomicBoolean(false);
    private final HISTORY_LIST historyEnum;
    private final HashSet<String> urlHash = new HashSet<>();
    private final String settingsDir;
    private final String fileName;
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;
    private boolean found = false;

    public HistoryList(String fileName, HISTORY_LIST historyEnum) {
        super(FXCollections.observableArrayList());
        this.settingsDir = ProgInfos.getSettingsDirectory_String();
        this.fileName = fileName;
        this.historyEnum = historyEnum;
    }

    public void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");
        HistoryFactory.readHistoryDataFromFile(settingsDir, fileName, this);
        fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    public SortedList<HistoryData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<HistoryData> getFilteredList() {
        if (filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPredicate(Predicate<HistoryData> predicate) {
        filteredList.setPredicate(predicate);
    }

    //===============
    public synchronized void clearAll(Stage stage) {
        // aus dem Menü (Bookmark löschen), Dialog Mediensammlung: Alles löschen (Abo, History)
        final int size = this.size();
        final String title;
        title = "Filme";

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", title + " löschen",
                "Soll die gesamte Liste " +
                        "(" + size + " " + title + ")" +
                        " gelöscht werden?")) {
            clearList();
            FileFactory.deleteHistoryFile(fileName);
            if (historyEnum.equals(HISTORY_LIST.HISTORY)) {
                // dann auch History in den Filmen löschen
                ProgData.getInstance().filmList.forEach(film -> {
                    film.setShown(false);
                    film.setActHist(false);
                });
            }
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED);
        }
    }

    //===============
    public synchronized boolean checkIfUrlAlreadyIn(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }


    //===============
    //ADD
    //===============
    public synchronized void addHistoryDataToHistory(String theme, String title, String url) {
        // wenn Abo, dann fertigen Download in die **Abo-History** schreiben
        if (checkIfUrlAlreadyIn(url) || FilmToolsFactory.checkIfLiveStream(theme)) {
            return;
        }

        P2Duration.counterStart("addHistoryDataToHistory");
        final ArrayList<HistoryData> list = new ArrayList<>();
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());
        HistoryData historyData = new HistoryData(datum, theme, title, url);
        addToThisList(historyData);
        list.add(historyData);

        writeToFile(list, true);
        P2Duration.counterStop("addHistoryDataToHistory");
    }

    public synchronized void addFilmDataListToHistory(List<FilmDataMTP> filmList) {
        // Button oder Menü
        // PlayFilm oder Tabellenmenü: Mark/Unmark
        // eine Liste Filme in die History schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(filmList.size());
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());

        P2Duration.counterStart("addFilmDataToHistory");
        for (final FilmDataMTP film : filmList) {
            if (film.isLive()) {
                continue;
            }

            if (historyEnum.equals(HISTORY_LIST.HISTORY)) {
                // auch wenn schon in der History, dann doch den Film als gesehen markieren
                film.setShown(true);
                film.setActHist(true);
            }

            if (checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, film.arr[FilmDataXml.FILM_THEME], film.arr[FilmDataXml.FILM_TITLE], film.getUrlHistory());
            addToThisList(historyData);
            list.add(historyData);
        }

        writeToFile(list, true);
        P2Duration.counterStop("addFilmDataToHistory");
    }

    public synchronized void addDownloadDataListToHistory(ArrayList<DownloadData> downloadList) {
        // Menü/Automatisch
        // eine Liste Downloads in die History schreiben
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(downloadList.size());
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());

        P2Duration.counterStart("addDownloadDataListToHistory");
        for (final DownloadData download : downloadList) {
            if (FilmToolsFactory.checkIfLiveStream(download.getTheme())) {
                continue;
            }
            if (!download.getSetData().isPlay() && !download.getSetData().isSaveAbo()) {
                // dann ist es nicht zum Abspielen oder Speichern->Button
                continue;
            }

            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            if (historyEnum.equals(HISTORY_LIST.HISTORY) && download.getFilm() != null) {
                // History-Liste (nicht Abos)
                download.getFilm().setShown(true);
                download.getFilm().setActHist(true);
            }

            if (checkIfUrlAlreadyIn(download.getHistoryUrl())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, download.getTheme(), download.getTitle(), download.getHistoryUrl());
            addToThisList(historyData);
            list.add(historyData);
        }

        writeToFile(list, true);
        P2Duration.counterStop("addDownloadDataListToHistory");
    }


    //===============
    //remove
    //===============
    public synchronized void removeHistoryDataFromHistory(ArrayList<HistoryData> historyDataList) {
        // Historydaten aus der History löschen und File wieder schreiben
        if (historyDataList == null || historyDataList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(historyDataList.size() + 1, 0.75F);
        for (HistoryData historyData : historyDataList) {
            hash.add(historyData.getUrl());
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmList.forEach(film -> {
            if (hash.contains(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeFilmDataFromHistory(ArrayList<FilmDataMTP> filmList) {
        // eine Liste Filme aus der History löschen und File wieder schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.forEach(film -> {
            if (historyEnum.equals(HISTORY_LIST.HISTORY)) {
                film.setShown(false);
                film.setActHist(false);
            }

            hash.add(film.getUrlHistory());
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeDownloadDataFromHistory(List<DownloadData> downloadList) {
        // eine Liste Downloads aus der History löschen und File wieder schreiben
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(downloadList.size() + 1, 0.75F);
        downloadList.forEach(download -> {
            if (historyEnum.equals(HISTORY_LIST.HISTORY) && download.getFilm() != null) {
                download.getFilm().setShown(false);
                download.getFilm().setActHist(false);
            }

            hash.add(download.getHistoryUrl());
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    private void removeFromHistory(HashSet<String> removeUrlHash) {
        final ArrayList<HistoryData> newHistoryList = new ArrayList<>();
        found = false;
        P2Duration.counterStart("History: removeFromHistory");
        P2Log.sysLog("Aus Historyliste löschen: " + removeUrlHash.size() + ", löschen aus: " + fileName);

        waitWhileWorking(); // wird diese Liste abgesucht

        this.forEach(historyData -> {
            if (removeUrlHash.contains(historyData.getUrl())) {
                // nur dann muss das Logfile auch geschrieben werden
                found = true;
            } else {
                // kommt wieder in die history
                newHistoryList.add(historyData);
            }
        });

        if (found) {
            // und nur dann wurde was gelöscht und muss geschrieben werden
            replaceThisList(newHistoryList);
            writeToFile(newHistoryList, false);
        }

        P2Duration.counterStop("History: removeFromHistory");
    }

    //===============
    private void writeToFile(List<HistoryData> list, boolean append) {
        waitWhileWorkingAndSetWorking();

        try {
            Thread th = new Thread(new HistoryWriteToFile(settingsDir, fileName, list, append, isWorking));
            th.setName("HistoryWriteToFile");
            th.start();
        } catch (Exception ex) {
            P2Log.errorLog(912030254, ex, "writeToFile");
            isWorking.set(false);
        }
    }

    private void waitWhileWorking() {
        int counter = 25;
        while (isWorking.get()) {
            --counter;
            if (counter < 0) {
                break;
            }

            P2Log.errorLog(741025896, "waitWhileWorking: write to history file");
            try {
                wait(200);
            } catch (final Exception ex) {
                P2Log.errorLog(915236547, ex, "waitWhileWorking");
            }
        }
        isWorking.set(false);
    }

    private void waitWhileWorkingAndSetWorking() {
        waitWhileWorking();
        isWorking.set(true);
    }

    //===============
    private void clearList() {
        urlHash.clear();
        this.clear();
    }

    private void addToThisList(HistoryData historyData) {
        this.add(historyData);
        urlHash.add(historyData.getUrl());
    }

    private void replaceThisList(List<HistoryData> historyData) {
        clearList();
        this.addAll(historyData);
        fillUrlHash();
    }

    private void fillUrlHash() {
        urlHash.clear();
        this.forEach(h -> urlHash.add(h.getUrl()));
    }
}
