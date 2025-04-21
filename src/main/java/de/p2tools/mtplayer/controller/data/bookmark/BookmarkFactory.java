/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmListMTP;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BookmarkFactory {

    private BookmarkFactory() {
    }

    public static void clearAll(Stage stage) {
        // aus dem Menü (Bookmark löschen), Dialog Mediensammlung: Alles löschen (Abo, History), Dialog Bookmark
        final int size = ProgData.getInstance().bookmarkList.size();

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Bookmarks löschen",
                "Soll die gesamte Liste " +
                        "(" + size + " " + "Bookmarks" + ")" +
                        " gelöscht werden?")) {
            ProgData.getInstance().bookmarkList.clearList();
            FileFactory.deleteHistoryFile(ProgConst.FILE_BOOKMARKS);
            ProgData.getInstance().filmList.forEach(film -> film.setBookmark(false));
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED); //todo
        }
    }

    public static void clearAllWithoutFilm(Stage stage) {
        // Dialog Bookmark
        int size = 0;
        List<BookmarkData> delList = new ArrayList<>();
        for (BookmarkData b : ProgData.getInstance().bookmarkList) {
            if (b.getFilmData() == null) {
                ++size;
                delList.add(b);
            }
        }
        if (size == 0) {
            P2Alert.showInfoAlert(stage, "Löschen", "Bookmarks löschen",
                    "Es sind keine Bookmarks ohne einen Film, in der Liste.");
            return;
        }

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Bookmarks löschen",
                "Sollen die " + size + " Bookmarks" +
                        " gelöscht werden?")) {

            final HashSet<String> hash = new HashSet<>(size, 0.75F);
            delList.forEach(bookmarkData -> hash.add(bookmarkData.getUrl()));
            ProgData.getInstance().bookmarkList.removeUrlHashSet(hash);
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED); //todo
        }
    }

    public static void removeBookmark(BookmarkData bookmarkData) {
        // Button in der Tabelle BookmarkDialog
        if (bookmarkData.getFilmData() == null) {
            final HashSet<String> hash = new HashSet<>(1, 0.75F);
            hash.add(bookmarkData.getUrl());
            ProgData.getInstance().bookmarkList.removeUrlHashSet(hash);
        } else {
            removeBookmark(bookmarkData.getFilmData());
        }
    }

    public static void removeBookmark(FilmDataMTP film) {
        // Button in der Tabelle / Kontextmenü Tabelle
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);
        removeBookmarkList(filmArrayList);
    }

    public static void removeBookmarkList(ArrayList<FilmDataMTP> removeList) {
        // eine Liste Filme aus der History löschen und File wieder schreiben
        if (removeList == null || removeList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("Bookmark: removeDataFromBookmark");
        // urls zum Entfernen, sammeln
        final HashSet<String> hash = new HashSet<>(removeList.size() + 1, 0.75F);
        removeList.forEach(film -> {
            film.setBookmark(false);
            hash.add(film.getUrlHistory());
        });

        ProgData.getInstance().bookmarkList.removeUrlHashSet(hash);
        hash.clear();
        P2Duration.counterStop("Bookmark: removeDataFromBookmark");
    }

    public static void addBookmark(FilmDataMTP film) {
        // Button in der Tabelle / Kontextmenü Tabelle
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);
        addBookmarkList(filmArrayList);
    }

    public static void addBookmarkList(ArrayList<FilmDataMTP> filmArrayList) {
        // Button Tabelle oder Menü
        if (filmArrayList == null || filmArrayList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("addFilmDataToBookmark");
        final ArrayList<BookmarkData> addList = new ArrayList<>(filmArrayList.size());
        for (final FilmDataMTP film : filmArrayList) {
            if (film.isLive()) {
                continue;
            }

            film.setBookmark(true);
            if (ProgData.getInstance().bookmarkList.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }

            BookmarkData bookmarkData = new BookmarkData(film);
            ProgData.getInstance().bookmarkList.addToThisList(bookmarkData);
            addList.add(bookmarkData);
        }

        BookmarkFileFactory.writeToFile(addList, true);
        P2Duration.counterStop("addFilmDataToBookmark");
    }

    public static void markBookmarks() {
        // beim Programmstart die Filme markieren
        if (ProgData.getInstance().bookmarkList.isEmpty()) {
            return;
        }

        FilmListMTP filmlist = ProgData.getInstance().filmList;
        BookmarkList bookmarkList = ProgData.getInstance().bookmarkList;

        HashMap<String, BookmarkData> hash = new HashMap<>();
        bookmarkList.forEach(b -> hash.put(b.getUrl(), b));

        P2Duration.counterStart("markBookmarks2");
        filmlist.forEach(film -> {
            BookmarkData bookmarkData = hash.get(film.getUrlHistory());
            if (bookmarkData != null) {
                film.setBookmark(true);
                bookmarkData.setFilmData(film);
            }
        });
        P2Duration.counterStop("markBookmarks2");
    }
}
