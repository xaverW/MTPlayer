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

package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class BookmarkList extends SimpleListProperty<BookmarkData> {

    private final HashSet<String> urlHash = new HashSet<>();
    private final String settingsDir;
    private final String fileName;
    private FilteredList<BookmarkData> filteredList = null;
    private SortedList<BookmarkData> sortedList = null;
    private final BooleanProperty isWorking = new SimpleBooleanProperty(false);
    private boolean found = false;

    public BookmarkList(String fileName) {
        super(FXCollections.observableArrayList());
        this.settingsDir = ProgInfos.getSettingsDirectory_String();
        this.fileName = fileName;
    }

    public void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");
        BookmarkFactory.readBookmarkDataFromFile(settingsDir, fileName, this);
        fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    public SortedList<BookmarkData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<BookmarkData> getFilteredList() {
        if (filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPredicate(Predicate<BookmarkData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filteredListSetPredFalse() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filteredListSetPredTrue() {
        filteredList.setPredicate(p -> true);
    }

    //===============
    public synchronized void clearAll(Stage stage) {
        // aus dem Menü (Bookmark löschen), Dialog Mediensammlung: Alles löschen (Abo, History), Dialog Bookmark
        final int size = this.size();

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Bookmarks löschen",
                "Soll die gesamte Liste " +
                        "(" + size + " " + "Bookmarks" + ")" +
                        " gelöscht werden?")) {
            clearList();
            FileFactory.deleteHistoryFile(settingsDir, fileName);
            ProgData.getInstance().filmList.forEach(film -> film.setBookmark(false));
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED); //todo
        }
    }

    public synchronized void clearAllWithoutFilm(Stage stage) {
        // Dialog Bookmark
        int size = 0;
        List<BookmarkData> delList = new ArrayList<>();
        for (BookmarkData b : this) {
            if (b.getFilmDataMTP() == null) {
                ++size;
                delList.add(b);
            }
        }
        if (size == 0) {
            return;
        }

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Bookmarks löschen",
                "Soll die " + size + " Bookmarks" +
                        " gelöscht werden?")) {

            final HashSet<String> hash = new HashSet<>(1, 0.75F);
            delList.forEach(bookmarkData -> hash.add(bookmarkData.getUrl()));
            ProgData.getInstance().bookmarkList.removeFromBookmark(hash);
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED); //todo
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
    public synchronized void addFilmDataToBookmark(List<FilmDataMTP> filmList) {
        // Button oder Menü
        // eine Liste Filme in die History schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("addFilmDataToBookmark");
        final ArrayList<BookmarkData> list = new ArrayList<>(filmList.size());
        for (final FilmDataMTP film : filmList) {
            if (film.isLive()) {
                continue;
            }

            film.setBookmark(true);
            if (checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }
            BookmarkData bookmarkData = new BookmarkData(film);
            addToThisList(bookmarkData);
            list.add(bookmarkData);
        }

        writeToFile(list, true);
        P2Duration.counterStop("addFilmDataToBookmark");
    }

    //===============
    //remove
    //===============
    public synchronized void removeFilmDataFromBookmark(ArrayList<FilmDataMTP> filmList) {
        // eine Liste Filme aus der History löschen und File wieder schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("Bookmark: removeDataFromBookmark");
        // urls zum Entfernen, sammeln
        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.forEach(film -> {
            film.setBookmark(false);
            hash.add(film.getUrlHistory());
        });

        removeFromBookmark(hash);
        hash.clear();
        P2Duration.counterStop("Bookmark: removeDataFromBookmark");
    }

    public void removeFromBookmark(HashSet<String> removeUrlHash) {
        final ArrayList<BookmarkData> newList = new ArrayList<>();
        found = false;
        P2Duration.counterStart("Bookmark: removeFromBookmark");
        P2Log.sysLog("Aus Bookmarks löschen: " + removeUrlHash.size() + ", löschen aus: " + fileName);

        waitWhileWorking(); // wird diese Liste abgesucht

        this.forEach(bookmarkData -> {
            if (removeUrlHash.contains(bookmarkData.getUrl())) {
                // nur dann muss das Logfile auch geschrieben werden
                found = true;
            } else {
                // kommt wieder in die history
                newList.add(bookmarkData);
            }
        });

        if (found) {
            // und nur dann wurde was gelöscht und muss geschrieben werden
            replaceAndWrite(newList);
        }

        P2Duration.counterStop("Bookmark: removeFromBookmark");
    }

    //===============
    private void writeToFile(List<BookmarkData> list, boolean append) {
        waitWhileWorking();
        isWorking.setValue(true);

        try {
            Thread th = new Thread(new BookmarkWriteToFile(settingsDir, fileName, list, append, isWorking));
            th.setName("writeToFile");
            th.start();
        } catch (Exception ex) {
            P2Log.errorLog(959623657, ex, "writeToFile");
            isWorking.setValue(false);
        }
    }

    private void waitWhileWorking() {
        while (isWorking.get()) {
            // sollte nicht passieren, aber wenn ..
            P2Log.errorLog(745845895, "waitWhileWorking: write to bookmark file");

            try {
                wait(100);
            } catch (final Exception ex) {
                P2Log.errorLog(402154895, ex, "waitWhileWorking");
                isWorking.setValue(false);
            }
        }
    }

    //===============
    public void clearList() {
        urlHash.clear();
        super.clear();
    }

    private void addToThisList(BookmarkData bookmarkData) {
        this.add(bookmarkData);
        urlHash.add(bookmarkData.getUrl());
    }

    private void replaceAndWrite(List<BookmarkData> bookmarkData) {
        clearList();
        this.addAll(bookmarkData);
        fillUrlHash();
        writeToFile(bookmarkData, false);
    }

    private void fillUrlHash() {
        urlHash.clear();
        this.forEach(h -> urlHash.add(h.getUrl()));
    }
}
