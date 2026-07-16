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
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BookmarkFactory {

    private BookmarkFactory() {
    }

    public static int deleteAll(Stage stage, boolean ask, boolean audio) {
        // aus dem Menü (alle Bookmarks löschen)
        final int size = ProgData.getInstance().bookmarkList.filtered(b -> b.isAudio() == audio).size();
        if (!ask || size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", "Bookmarks löschen",
                "Soll die gesamte Liste " +
                        "(" + size + " " + "Bookmarks" + ")" +
                        " gelöscht werden?")) {
            if (audio) {
                ProgData.getInstance().bookmarkList.removeIf(BookmarkData::isAudio);
                ProgData.getInstance().audioList.forEach(film -> film.setBookmark(false));

            } else {
                ProgData.getInstance().bookmarkList.removeIf(bookmarkData -> !bookmarkData.isAudio());
                ProgData.getInstance().filmList.forEach(film -> film.setBookmark(false));
            }

            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BOOKMARK_CHANGED);
        }
        return size;
    }

    public static void removeBookmark(BookmarkData bookmarkData) {
        // Button in der Tabelle BookmarkDialog
        if (bookmarkData.getFilmData() == null) {
            final HashSet<String> hash = new HashSet<>(1, 0.75F);
            hash.add(bookmarkData.getUrl());
            ProgData.getInstance().bookmarkList.removeUrlHash(hash);
        } else {
            removeBookmark(bookmarkData.getFilmData());
        }

        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BOOKMARK_CHANGED);
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

        // urls zum Entfernen, sammeln
        final HashSet<String> hash = new HashSet<>(removeList.size() + 1, 0.75F);
        removeList.forEach(film -> {
            film.setBookmark(false);
            hash.add(film.getUrlHistory());
        });
        ProgData.getInstance().bookmarkList.removeUrlHash(hash);
        hash.clear();
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BOOKMARK_CHANGED);
    }

    public static void removeBookmarkList(List<BookmarkData> list) {
        final HashSet<String> hash = new HashSet<>(list.size(), 0.75F);
        list.forEach(bookmarkData -> {
            if (bookmarkData.getFilmData() != null) {
                bookmarkData.getFilmData().setBookmark(false);
            }
            hash.add(bookmarkData.getUrl());
        });

        ProgData.getInstance().bookmarkList.removeUrlHash(hash);
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BOOKMARK_CHANGED);
    }

    public static void deleteAge(Stage stage, int age) {
        // Button alte löschen aus Dialog Bookmark
        P2Log.sysLog("Alte Bookmarks löschen: " + age);

        List<BookmarkData> delList = new ArrayList<>();
        for (BookmarkData b : ProgData.getInstance().bookmarkList) {
            // dann erst mal schauen
            LocalDate ld = b.getDate().getLocalDate();
            if (ld == null) {
                continue;
            }

            long days = ChronoUnit.DAYS.between(ld, LocalDate.now());
            long diff = Math.abs(days);
            if (diff >= age) {
                delList.add(b);
            }
        }
        if (delList.isEmpty()) {
            P2Alert.showInfoAlert(stage, "Löschen", "Bookmarks löschen",
                    "Es sind keine Bookmarks zum Löschen, in der Liste.");
            return;
        }
        removeBookmarkList(delList);
    }

    public static void deleteShown(Stage stage) {
        // Button alte löschen aus Dialog Bookmark
        P2Log.sysLog("Gesehene Bookmarks löschen");

        List<BookmarkData> delList = new ArrayList<>();

        for (BookmarkData b : ProgData.getInstance().bookmarkList) {
            if (b.getFilmData() != null && b.getFilmData().isShown()) {
                delList.add(b);
                continue;
            }
        }
        if (delList.isEmpty()) {
            P2Alert.showInfoAlert(stage, "Löschen", "Bookmarks löschen",
                    "Es sind keine Bookmarks zum Löschen, in der Liste.");
            return;
        }

        removeBookmarkList(delList);
    }

    public static void addBookmark(boolean audio, FilmDataMTP film) {
        // Button in der Tabelle / Kontextmenü Tabelle
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);
        addBookmarkList(audio, filmArrayList);
    }

    public static void addBookmarkList(boolean audio, ArrayList<FilmDataMTP> filmArrayList) {
        // Button Tabelle oder Menü
        if (filmArrayList == null || filmArrayList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("addFilmDataToBookmark");
        for (final FilmDataMTP film : filmArrayList) {
            if (film.isLive()) {
                continue;
            }

            film.setBookmark(true);
            if (ProgData.getInstance().bookmarkList.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }

            BookmarkData bookmarkData = new BookmarkData(audio, film);
            ProgData.getInstance().bookmarkList.addToThisList(bookmarkData);
        }

        P2Duration.counterStop("addFilmDataToBookmark");
    }

    public static void markBookmarks(boolean audio) {
        // beim Programmstart die Filme markieren
        if (ProgData.getInstance().bookmarkList.isEmpty()) {
            return;
        }

        BookmarkList bookmarkList = ProgData.getInstance().bookmarkList;

        HashMap<String, BookmarkData> hash = new HashMap<>();
        bookmarkList.forEach(b -> {
            if (audio && b.isAudio()) {
                b.setFilmData(null);
            } else if (!audio && !b.isAudio()) {
                b.setFilmData(null);
            }
            hash.put(b.getUrl(), b);
        });

        P2Duration.counterStart("markBookmarks");
        if (audio) {

            ProgData.getInstance().audioList.forEach(a -> {
                BookmarkData bookmarkData = hash.get(a.getUrlHistory());
                if (bookmarkData != null) {
                    a.setBookmark(true);
                    bookmarkData.setFilmData(a);
                }
            });

        } else {
            // Filme
            ProgData.getInstance().filmList.forEach(film -> {
                BookmarkData bookmarkData = hash.get(film.getUrlHistory());
                if (bookmarkData != null) {
                    film.setBookmark(true);
                    bookmarkData.setFilmData(film);
                }
            });
        }

        if (ProgConfig.BOOKMARK_DEL_NOT_IN_FILMLIST.get()) {
            delNotInList(audio);
        }

        P2Duration.counterStop("markBookmarks");
    }

    public static int delNotInList(boolean audio) {
        BookmarkList bookmarkList = ProgData.getInstance().bookmarkList;
        int count = bookmarkList.size();
        List<BookmarkData> list = new ArrayList<>();
        bookmarkList.forEach(b -> {
            // Audio/Filme
            if (b.getFilmData() != null) {
                list.add(b);

            } else if (audio && !b.isAudio() ||
                    !audio && b.isAudio()) {
                list.add(b);
            }
        });
        bookmarkList.setAll(list);
        return count - bookmarkList.size();
    }
}
