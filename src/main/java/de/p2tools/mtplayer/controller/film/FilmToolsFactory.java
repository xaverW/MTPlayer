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

package de.p2tools.mtplayer.controller.film;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataProps;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class FilmToolsFactory {
    private static int countDouble = 0;

    private FilmToolsFactory() {
    }

    public static void setFilmShown(ArrayList<FilmDataMTP> filmArrayList, boolean setShown) {
        // Menü: Film als .. setzen
        if (setShown) {
            ProgData.getInstance().historyList.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().historyList.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void changeBookmarkFilm(FilmDataMTP film) {
        // Button in der Tabelle
        bookmarkFilm(film, !film.isBookmark());
    }

    public static void bookmarkFilm(FilmDataMTP film, boolean bookmark) {
        // Button in der Tabelle / Kontextmenü Tabelle
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);
        bookmarkFilmList(filmArrayList, bookmark);
    }

    public static void bookmarkFilmList(ArrayList<FilmDataMTP> filmArrayList, boolean bookmark) {
        // Button Tabelle oder Menü
        if (bookmark) {
            ProgData.getInstance().historyListBookmarks.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().historyListBookmarks.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void clearAllBookmarks() {
        FilmListMTP filmlist = ProgData.getInstance().filmList;
        filmlist.stream().forEach(film -> film.setBookmark(false));
    }

    public static void markBookmarks() {
        if (ProgData.getInstance().historyListBookmarks.isEmpty()) {
            return;
        }

        FilmListMTP filmlist = ProgData.getInstance().filmList;
        HistoryList bookmarks = ProgData.getInstance().historyListBookmarks;

        filmlist.stream().forEach(film -> {
            if (bookmarks.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                film.setBookmark(true);
            }
        });
    }

    /**
     * liefert die String-Liste der Sender die _NICHT_ geladen werden sollen
     *
     * @return
     */
    public static ArrayList<String> getSenderListNotToLoad() {
        return new ArrayList(Arrays.asList(ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue().split(",")));
    }

    public static void checkAllSenderSelectedNotToLoad(Stage stage) {
        // die Einstellung _alle Sender nicht laden_,  wird ja sonst nix geladen, ist ein Fehler des Nutzers
        // und das ist nur ein Hinweis darauf!
        ArrayList<String> aListSenderNotToLoad = getSenderListNotToLoad();
        boolean allSender = true;
        for (String sender : LoadFactoryConst.SENDER) {
            Optional<String> optional = aListSenderNotToLoad.stream().filter(aktSender -> aktSender.equals(sender)).findAny();
            if (optional.isEmpty()) {
                // mindestens einer fehlt :)
                allSender = false;
                break;
            }
        }

        if (allSender) {
            Platform.runLater(() -> PAlert.showErrorAlert(stage,
                    "Sender laden",
                    "Es werden keine Filme geladen. Alle Sender " +
                            "sind vom Laden ausgenommen!" +
                            "\n\n" +
                            "Einstellungen -> Filmliste laden"));
        }
    }

    public static int markFilms(ListProperty<? extends FilmData> filmList) {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden

        String[] senderArr = ProgConfig.SYSTEM_MARK_DOUBLE_CHANNEL_LIST.getValueSafe().split(",");
        final HashSet<String> urlHashSet = new HashSet<>(filmList.size(), 0.75F);
        countDouble = 0;

        PDuration.counterStart("markFilms");
        if (senderArr.length == 0 || senderArr.length == 1 && senderArr[0].isEmpty()) {
            // dann wie bisher
            // todo exception parallel?? Unterschied ~10ms (bei Gesamt: 110ms)
            try {
                PDuration.counterStart("mark(FilmData filmData)");
                filmList.forEach((FilmData f) -> {
                    mark(f);
                    if (!urlHashSet.add(getHashFromFilm(f))) {
                        ++countDouble;
                        f.setDoubleUrl(true);
                    }
                });
                PDuration.counterStop("mark(FilmData filmData)");
            } catch (Exception ex) {
                PLog.errorLog(951024789, ex);
            }

        } else {
            // dann nach Sender-Reihenfolge
            PDuration.counterStart("mark(FilmData filmData)");
            filmList.forEach((FilmData f) -> {
                mark(f);
            });
            PDuration.counterStop("mark(FilmData filmData)");
            for (String sender : senderArr) {
                addSender(filmList, urlHashSet, senderArr, sender);
            }
            // und dann noch für den Rest
            addSender(filmList, urlHashSet, senderArr, "");
        }
        urlHashSet.clear();

        PDuration.counterStop("markFilms");
        if (ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE.getValue()) {
            // dann auch gleich noch entfernen
            PDuration.counterStart("markFilms.removeMarkedFilms");
            filmList.removeIf(FilmDataProps::isDoubleUrl);
        }
        PDuration.counterStop("markFilms.removeMarkedFilms");
        ProgConfig.SYSTEM_FILMLIST_COUNT_DOUBLE.setValue(countDouble);

        return countDouble;
    }

    private static String getHashFromFilm(FilmData filmData) {
        if (ProgConfig.SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE.get()) {
            return filmData.getTheme() + filmData.getTitle() + filmData.getUrl();
        } else {
            return filmData.getUrl();
        }
    }

    private static void mark(FilmData filmData) {
        filmData.setGeoBlocked();
        filmData.setInFuture();
        filmData.setShown(ProgData.getInstance().historyList.checkIfUrlAlreadyIn(filmData.getUrlHistory()));

        if (ProgConfig.SYSTEM_FILMLIST_MARK_UT.getValue()) {
            // und dann auch nach erweiterten UT suchen
            ProgData.getInstance().utDataList.forEach(utData -> {
                if ((utData.getChannel().isEmpty() || filmData.getChannel().contains(utData.getChannel()))
                        && filmData.getTitle().contains(utData.getTitle())) {
                    filmData.setUt(true);
                }
            });
        }
    }

    private static void addSender(ListProperty<? extends FilmData> filmList,
                                  HashSet<String> urlHashSet,
                                  String[] senderArr, String sender) {

        filmList.forEach((FilmData f) -> {
            if (sender.isEmpty()) {
                // dann nur noch die Sender die nicht im senderArr sind
                if (Arrays.stream(senderArr).noneMatch(s -> s.equals(f.arr[FilmDataXml.FILM_CHANNEL]))) {
                    if (!urlHashSet.add(getHashFromFilm(f))) {
                        ++countDouble;
                        f.setDoubleUrl(true);
                    }
                }

            } else if (f.arr[FilmDataXml.FILM_CHANNEL].equals(sender)) {
                // jetzt erst mal die Sender aus dem Array
                if (!urlHashSet.add(getHashFromFilm(f))) {
                    ++countDouble;
                    f.setDoubleUrl(true);
                }
            }
        });
    }
}
