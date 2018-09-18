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

package de.mtplayer.mtp.controller.history;

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class HistoryList extends SimpleListProperty<HistoryData> {

    private final HashSet<String> urlHash = new HashSet<>();
    private final String fileName;
    private final String settingsDir;
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;
    private BooleanProperty isWorking = new SimpleBooleanProperty(false);
    public final HistoryWorker historyWorker;

    public HistoryList(String fileName, String settingsDir) {
        super(FXCollections.observableArrayList());

        this.fileName = fileName;
        this.settingsDir = settingsDir;
        this.historyWorker = new HistoryWorker(fileName, settingsDir);

        historyWorker.readHistoryDataFromFile(this);
        fillUrlHash();
    }

    public SortedList<HistoryData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<HistoryData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPred(Predicate<HistoryData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filteredListClearPred() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void clearAll() {
        clearList();
        historyWorker.deleteHistoryFile();
    }

    public synchronized boolean checkIfUrlAlreadyIn(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }

    public synchronized boolean checkIfLiveStream(String theme) {
        // live ist nie alt
        return theme.equals(FilmTools.THEME_LIVE);
    }

    public synchronized void addHistoryDataToHistory(String theme, String title, String url) {
        // einen Film in die History schreiben

        if (checkIfUrlAlreadyIn(url) || checkIfLiveStream(theme)) {
            return;
        }

        PDuration.counterStart("History: addDataToHistory");
        final ArrayList<HistoryData> list = new ArrayList<>();
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
        HistoryData historyData = new HistoryData(datum, theme, title, url);
        addToThisList(historyData);
        list.add(historyData);

        writeToFile(list, true);
        PDuration.counterStop("History: addDataToHistory");
    }

    public synchronized void addFilmDataToHistory(ArrayList<Film> filmList) {
        // eine Liste Filme in die History schreiben

        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(filmList.size());
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        PDuration.counterStart("History: addDataToHistory");
        for (final Film film : filmList) {
            if (checkIfLiveStream(film.getTheme())) {
                continue;
            }

            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            film.setShown(true);
            film.setActHist(true);

            if (checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, film.arr[FilmXml.FILM_THEME], film.arr[FilmXml.FILM_TITLE], film.getUrlHistory());
            addToThisList(historyData);
            list.add(historyData);
        }

        writeToFile(list, true);
        PDuration.counterStop("History: addDataToHistory");
    }

    public synchronized void addDownloadDataListToHistory(ArrayList<Download> downloadList) {
        // eine Liste Downloads in die Hitory schreiben

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(downloadList.size());
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        PDuration.counterStart("History: addDataToHistory");
        for (final Download download : downloadList) {
            if (checkIfLiveStream(download.getTheme())) {
                continue;
            }

            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            if (download.getFilm() != null) {
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
        PDuration.counterStop("History: addDataToHistory");
    }


    private void writeToFile(List<HistoryData> list, boolean append) {
        waitWhileWorkingAndSetWorking();

        try {
            Thread th = new Thread(new HistoryWriteToFile(list, append, isWorking, historyWorker));
            th.setName("HistoryWriteToFile");
            th.start();
            // th.run();
        } catch (Exception ex) {
            PLog.errorLog(912030254, ex, "writeToFile");
            isWorking.setValue(false);
        }
    }

    private void waitWhileWorking() {
        while (isWorking.get()) {
            // sollte nicht passieren, aber wenn ..
            PLog.errorLog(741025896, "waitWhileWorking");

            try {
                wait(100);
            } catch (final Exception ex) {
                PLog.errorLog(915236547, ex, "waitWhileWorking");
                isWorking.setValue(false);
            }
        }

    }

    private void waitWhileWorkingAndSetWorking() {
        waitWhileWorking();
        isWorking.setValue(true);
    }

    public synchronized void removeHistoryDataFromHistory(ArrayList<HistoryData> historyDataList) {
        // Historydaten aus der History löschen und File wieder schreiben

        if (historyDataList == null || historyDataList.isEmpty()) {
            return;
        }

        PDuration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(historyDataList.size() + 1, 0.75F);
        for (HistoryData historyData : historyDataList) {
            hash.add(historyData.getUrl());
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmlist.stream().forEach(film -> {
            if (hash.contains(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });

        removeFromHistory(hash);
        PDuration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeFilmDataFromHistory(ArrayList<Film> filmList) {
        // eine Liste Filme aus der History löschen und File wieder schreiben

        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        PDuration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.stream().forEach(film -> {
            film.setShown(false); // todo mal vormerken ob evtl. die ganze Filmliste nach dieser URL durchsucht werden soll, wird sonst erst beim nächsten Start angezeigt
            film.setActHist(false);
            hash.add(film.getUrlHistory());
        });

        removeFromHistory(hash);
        PDuration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeDownloadDataFromHistory(ArrayList<Download> downloadList) {
        // eine Liste Downloads aus der History löschen und File wieder schreiben

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        PDuration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(downloadList.size() + 1, 0.75F);
        downloadList.stream().forEach(download -> {
            if (download.getFilm() != null) {
                download.getFilm().setShown(false);
                download.getFilm().setActHist(false);
            }
            hash.add(download.getHistoryUrl());
        });

        removeFromHistory(hash);
        PDuration.counterStop("History: removeDataFromHistory");
    }

    private boolean found = false;

    private void removeFromHistory(HashSet<String> urlHash) {
        final ArrayList<HistoryData> newHistoryList = new ArrayList<>();

        found = false;

        PDuration.counterStart("History: removeFromHistory");
        PLog.sysLog("Aus Historyliste löschen: " + urlHash.size() + ", löschen aus: " + fileName);

        waitWhileWorking(); // wird diese Liste abgesucht

        this.stream().forEach(historyData -> {

            if (urlHash.contains(historyData.getUrl())) {
                // nur dann muss das Logfile auch geschrieben werden
                found = true;
            } else {
                // kommt wieder in die history
                newHistoryList.add(historyData);
            }

        });

        if (found) {
            // und nur dann wurde was gelöscht und muss geschreiben werden
            replaceThisList(newHistoryList);
            ChangeListener cl = new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println("-------> ACHTUNG");
                }
            };
            this.addListener(cl);

            writeToFile(newHistoryList, false);
            Listener.notify(Listener.EREIGNIS_GUI_HISTORY_CHANGED, HistoryList.class.getSimpleName());
        }

        PDuration.counterStop("History: removeFromHistory");
    }

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
        this.stream().forEach(h -> urlHash.add(h.getUrl()));
    }

}
