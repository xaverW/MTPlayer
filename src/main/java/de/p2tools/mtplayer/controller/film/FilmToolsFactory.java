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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.p2lib.alert.PAlert;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class FilmToolsFactory {
    private FilmToolsFactory() {
    }

    public static void setFilmShown(ArrayList<FilmDataMTP> filmArrayList, boolean setShown) {
        if (setShown) {
            ProgData.getInstance().history.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().history.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void bookmarkFilm(FilmDataMTP film, boolean bookmark) {
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);
        bookmarkFilmList(filmArrayList, bookmark);
    }

    public static void bookmarkFilmList(ArrayList<FilmDataMTP> filmArrayList, boolean bookmark) {
        if (bookmark) {
            ProgData.getInstance().bookmarks.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().bookmarks.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void clearAllBookmarks() {
        FilmlistMTP filmlist = ProgData.getInstance().filmlist;
        filmlist.stream().forEach(film -> film.setBookmark(false));
    }

    public static void markBookmarks() {
        if (ProgData.getInstance().bookmarks.isEmpty()) {
            return;
        }

        FilmlistMTP filmlist = ProgData.getInstance().filmlist;
        HistoryList bookmarks = ProgData.getInstance().bookmarks;

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

    /**
     * die Einstellung _alle Sender nicht laden_ ist sinnlos, ist ein Fehler des Nutzers
     * und das ist nur ein Hinweis daruf!
     *
     * @param stage
     * @return
     */
    public static boolean checkAllSenderSelectedNotToLoad(Stage stage) {
        ArrayList<String> aListSenderNotToLoad = getSenderListNotToLoad();
        boolean allSender = true;
        for (String sender : ProgConst.SENDER) {
            Optional<String> optional = aListSenderNotToLoad.stream().filter(aktSender -> aktSender.equals(sender)).findAny();
            if (!optional.isPresent()) {
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
        return allSender;
    }
}
