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
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmToolsFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class HistoryListJson extends SimpleListProperty<HistoryData> {

    private static final AtomicBoolean isWorking = new AtomicBoolean(false);
    private final HashMap<String, HistoryData> urlHashMap = new HashMap<>();
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;

    public HistoryListJson() {
        super(FXCollections.observableArrayList());
    }

    public void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");
        HistoryReadWriteJsonFactory.read();
        makeUrlHash();
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
    public synchronized void replaceList(List<HistoryData> list) {
        this.setAll(list);
        makeUrlHash();
    }

    public synchronized void clearAll() {
        this.clear();
        this.urlHashMap.clear();
    }

    public synchronized void clearAll(Stage stage, int source) {
        // aus dem Menü: Alles löschen (Abo, History)
        final int size = this.size();
        final String text;
        if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
            text = "Soll die gesamte Liste " +
                    "(" + size + ")" +
                    " gelöscht werden?";

        } else if (source == HistoryData.SOURCE_SHOWN) {
            text = "Sollen alle \"Gesehenen\" " +
                    "( " + size + " )" +
                    " gelöscht werden?";

        } else {
            text = "Sollen alle \"Abos\" " +
                    "( " + size + " )" +
                    " gelöscht werden?";
        }

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Film löschen", text)) {
            clearList(source);
            if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD || source == HistoryData.SOURCE_SHOWN) {
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
    public synchronized HistoryData getHistoryData(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHashMap.get(urlFilm);
    }

    public synchronized boolean checkIfUrlAlreadyIn(int source, String urlFilm) {
        // wenn url gefunden, dann true zurück
        HistoryData h = urlHashMap.get(urlFilm);
        if (h == null) {
            return false;
        }

        if (h.getSource() == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
            return true;
        }

        return source == h.getSource();
    }


    //===============
    //ADD
    //===============
    public synchronized void updateHistory(int source, List<HistoryData> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            return;
        }

        List<HistoryData> addList = new ArrayList<>();
        historyList.forEach(addHistory -> {
            if (!FilmToolsFactory.checkIfLiveStream(addHistory.getTheme())) {

                HistoryData alreadyIn = urlHashMap.get(addHistory.getUrl());
                if (alreadyIn == null) {
                    // noch nicht drin
                    addList.add(addHistory);

                } else if (alreadyIn.getSource() != source) {
                    // drin aber mit anderem source -> dann beides
                    alreadyIn.setSource(HistoryData.SOURCE_SHOWN_DOWNLOAD);
                }
            }
        });

        // und dann wird er neu angelegt
        addToThisList(addList);
    }

    public synchronized void addDownloadToHistory(DownloadData download) {
        // gestarteter Download, landet in ABOS und SHOWN
        if (download == null) {
            return;
        }

        if (FilmToolsFactory.checkIfLiveStream(download.getTheme())) {
            return;
        }

        if (download.getFilm() != null) {
            download.getFilm().setShown(true);
            download.getFilm().setActHist(true);
        }

        HistoryData historyData = urlHashMap.get(download.getHistoryUrl());
        if (historyData != null) {
            historyData.addSourceShownDownload();
            return;
        }

        // und dann wird er neu angelegt
        addToThisList(new HistoryData(download, HistoryData.SOURCE_SHOWN_DOWNLOAD));
    }

    public synchronized void addDownloadToHistory(ArrayList<DownloadData> downloadList) {
        // Menü/Automatisch
        // eine Liste Downloads in die History schreiben
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        for (final DownloadData download : downloadList) {
            addDownloadToHistory(download);
        }
    }

    public synchronized void addFilmToShown(FilmDataMTP film) {
        // Button oder Menü, PlayFilm oder Tabellenmenü: Mark/Unmark
        // eine Liste Filme in die History schreiben
        if (film == null) {
            return;
        }

        if (film.isLive()) {
            return;
        }

        // auch wenn schon in der History, dann doch den Film als gesehen markieren
        film.setShown(true);
        film.setActHist(true);

        HistoryData historyData = urlHashMap.get(film.getUrlHistory());
        if (historyData != null) {
            historyData.addSourceShown();
            return;
        }

        HistoryData h = new HistoryData(HistoryData.SOURCE_SHOWN,
                film.arr[FilmDataXml.FILM_CHANNEL], film.arr[FilmDataXml.FILM_THEME],
                film.arr[FilmDataXml.FILM_TITLE], film.getUrlHistory());
        addToThisList(h);
    }

    public synchronized void addFilmToShown(List<FilmDataMTP> filmList) {
        // Button oder Menü, PlayFilm oder Tabellenmenü: Mark/Unmark
        // eine Liste Filme in die History schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        for (final FilmDataMTP film : filmList) {
            addFilmToShown(film);
        }
    }


    //===============
    //remove
    //===============
    public synchronized void removeShown(HistoryData historyData) {
        // HistoryData aus den SHOWN löschen
        if (historyData == null) {
            return;
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmList.forEach(film -> {
            if (historyData.getUrl().equals(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });

        removeFromHistory(HistoryData.SOURCE_SHOWN, historyData);
    }

    public synchronized void removeHistory(HistoryData historyData) {
        // HistoryData aus der History löschen
        if (historyData == null) {
            return;
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmList.forEach(film -> {
            if (historyData.getUrl().equals(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });

        removeFromHistory(historyData);
    }

    public synchronized void removeHistory(ArrayList<HistoryData> removeList) {
        // HistoryData aus der History löschen
        if (removeList == null || removeList.isEmpty()) {
            return;
        }

        final HashSet<String> hash = new HashSet<>(removeList.size() + 1, 0.75F);
        for (HistoryData historyData : removeList) {
            hash.add(historyData.getUrl());
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmList.forEach(film -> {
            if (hash.contains(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });
        hash.clear();

        removeFromHistory(removeList);
    }

    public synchronized void removeDownloadFromHistory(List<DownloadData> downloadList) {
        // eine Liste Downloads aus der History löschen -> undo Download
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        final HashSet<String> hash = new HashSet<>(downloadList.size() + 1, 0.75F);
        downloadList.forEach(download -> {
            if (download.getFilm() != null) {
                download.getFilm().setShown(false);
                download.getFilm().setActHist(false);
            }
            hash.add(download.getHistoryUrl());
        });
        hash.clear();

        removeFromHistory(HistoryData.SOURCE_SHOWN_DOWNLOAD, hash);
    }

    public synchronized void removeFilmFromHistory(FilmDataMTP film) {
        // Menü: Film als gesehen/ungesehen setzen
        if (film == null) {
            return;
        }

        film.setShown(false);
        film.setActHist(false);

        HistoryData historyData = urlHashMap.get(film.getUrlHistory());
        if (historyData == null) {
            return;
        }

        removeFromHistory(HistoryData.SOURCE_SHOWN, historyData);
    }

    public synchronized void removeFilmFromHistory(ArrayList<FilmDataMTP> filmList) {
        // Menü: Film als gesehen/ungesehen setzen
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeFilmFromHistory");
        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.forEach(film -> {
            film.setShown(false);
            film.setActHist(false);
            hash.add(film.getUrlHistory());
        });

        removeFromHistory(HistoryData.SOURCE_SHOWN, hash);
        hash.clear();
        P2Duration.counterStop("History: removeFilmFromHistory");
    }

    private void removeFromHistory(int source, HashSet<String> removeUrlHash) {
        final ArrayList<HistoryData> removeList = new ArrayList<>();
        P2Log.sysLog("Aus History löschen: " + removeUrlHash.size());

        waitWhileWorking(); // wird diese Liste abgesucht

        // source shown/abo: dann kommts auf jeden Fall weg
        this.forEach(historyData -> {
            if (removeUrlHash.contains(historyData.getUrl())) {
                // dann soll er weg
                if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
                    // alle weg
                    removeList.add(historyData);

                } else if (source == historyData.getSource()) {
                    // nur wenn der Source zum History passt
                    removeList.add(historyData);
                }
            }
        });

        removeFromHistory(removeList);
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
    private void clearList(int source) {
        if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
            // dann alle
            urlHashMap.clear();
            this.clear();

        } else if (source == HistoryData.SOURCE_SHOWN) {
            // SHOWN löschen, Rest ist dann ABO
            this.removeIf(h -> h.getSource() == HistoryData.SOURCE_SHOWN);
            this.forEach(historyData -> historyData.setSource(HistoryData.SOURCE_DOWNLOAD));
            makeUrlHash();

        } else {
            // ABOS löschen, Rest ist dann nur noch SHOWN
            this.removeIf(h -> h.getSource() == HistoryData.SOURCE_DOWNLOAD);
            this.forEach(historyData -> historyData.setSource(HistoryData.SOURCE_SHOWN));
            makeUrlHash();
        }
    }

    private void addToThisList(List<HistoryData> historyDataList) {
        this.addAll(historyDataList);
        makeUrlHash();
    }

    private void addToThisList(HistoryData historyData) {
        this.add(historyData);
        urlHashMap.put(historyData.getUrl(), historyData);
    }

    private void replaceThisList(List<HistoryData> historyData) {
        this.setAll(historyData);
        makeUrlHash();
    }

    private void removeFromHistory(HistoryData historyData) {
        this.remove(historyData);
        makeUrlHash();
    }

    private void removeFromHistory(int source, HistoryData historyData) {
        if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
            removeFromHistory(historyData);
            return;
        }

        if (source == HistoryData.SOURCE_SHOWN) {
            if (historyData.getSource() == HistoryData.SOURCE_SHOWN) {
                removeFromHistory(historyData);
            } else {
                historyData.setSource(HistoryData.SOURCE_DOWNLOAD);
            }
            return;
        }

        if (source == HistoryData.SOURCE_DOWNLOAD) {
            if (historyData.getSource() == HistoryData.SOURCE_DOWNLOAD) {
                removeFromHistory(historyData);
            } else {
                historyData.setSource(HistoryData.SOURCE_SHOWN);
            }
        }
    }

    private void removeFromHistory(List<HistoryData> historyList) {
        this.removeAll(historyList);
        makeUrlHash();
    }

    private void removeFromHistory(int source, List<HistoryData> historyList) {
        List<HistoryData> remove = new ArrayList<>();
        historyList.forEach(h -> {
            if (source == HistoryData.SOURCE_SHOWN_DOWNLOAD) {
                // dann alle weg
                remove.add(h);

            } else if (source == HistoryData.SOURCE_SHOWN) {
                if (h.getSource() == HistoryData.SOURCE_SHOWN) {
                    remove.add(h);
                } else {
                    h.setSource(HistoryData.SOURCE_DOWNLOAD);
                }

            } else if (source == HistoryData.SOURCE_DOWNLOAD) {
                if (h.getSource() == HistoryData.SOURCE_DOWNLOAD) {
                    remove.add(h);
                } else {
                    h.setSource(HistoryData.SOURCE_SHOWN);
                }
            }
        });

        this.removeAll(remove);
        makeUrlHash();
    }

    public void makeUrlHash() {
        urlHashMap.clear();
        this.forEach(h -> urlHashMap.put(h.getUrl(), h));
    }
}
