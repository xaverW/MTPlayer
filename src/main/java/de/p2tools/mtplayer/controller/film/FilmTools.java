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
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.mtplayer.gui.dialog.DownloadAddDialogController;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilmTools {

    public static final String THEME_LIVE = "Livestream";

    public static void setFilmShown(ProgData progData, ArrayList<FilmDataMTP> filmArrayList, boolean setShown) {
        if (setShown) {
            progData.history.addFilmDataListToHistory(filmArrayList);
        } else {
            progData.history.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void bookmarkFilm(ProgData progData, FilmDataMTP film, boolean bookmark) {
        ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);

        bookmarkFilm(progData, filmArrayList, bookmark);
    }

    public static void bookmarkFilm(ProgData progData, ArrayList<FilmDataMTP> filmArrayList, boolean bookmark) {
        if (bookmark) {
            progData.bookmarks.addFilmDataListToHistory(filmArrayList);
        } else {
            progData.bookmarks.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void saveFilm(FilmDataMTP film, SetData pSet) {
        ArrayList<FilmDataMTP> list = new ArrayList<>();
        list.add(film);
        saveFilm(list, pSet);
    }

    public static void saveFilm(List<FilmDataMTP> list, SetData pSet) {
        if (list.isEmpty()) {
            return;
        }

        ProgData progData = ProgData.getInstance();
        ArrayList<FilmDataMTP> filmsAddDownloadList = new ArrayList<>();

        if (progData.setDataList.getSetDataListSave().isEmpty()) {
            new NoSetDialogController(progData, NoSetDialogController.TEXT.SAVE);
            return;
        }

        String resolution = "";
        if (progData.actFilmFilterWorker.getActFilterSettings().isOnlyHd()) {
            resolution = FilmDataMTP.RESOLUTION_HD;
        }

        for (final FilmDataMTP film : list) {
            // erst mal schauen obs den schon gibt
            DownloadData download = progData.downloadList.getDownloadWithFilmUrl(film.arr[FilmDataMTP.FILM_URL]);
            if (download == null) {
                filmsAddDownloadList.add(film);
            } else {
                // dann ist der Film schon in der Downloadliste

                if (list.size() <= 1) {
                    PAlert.BUTTON answer = PAlert.showAlert_yes_no("Anlegen?", "Nochmal anlegen?",
                            "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                                    film.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                                    "Nochmal anlegen?");
                    switch (answer) {
                        case NO:
                            // alles Abbrechen
                            return;
                        case YES:
                            filmsAddDownloadList.add(film);
                            break;
                    }

                } else {
                    PAlert.BUTTON answer = PAlert.showAlert_yes_no_cancel("Anlegen?", "Nochmal anlegen?",
                            "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                                    film.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                                    "Nochmal anlegen (Ja / Nein)?" + P2LibConst.LINE_SEPARATOR +
                                    "Oder alles Abbrechen?");
                    switch (answer) {
                        case CANCEL:
                            // alles Abbrechen
                            return;
                        case NO:
                            continue;
                        case YES:
                            filmsAddDownloadList.add(film);
                            break;
                    }
                }
            }
        }
        if (!filmsAddDownloadList.isEmpty()) {
            new DownloadAddDialogController(progData, filmsAddDownloadList, pSet, resolution);
        }
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

    public static void clearAllBookmarks() {
        FilmlistMTP filmlist = ProgData.getInstance().filmlist;
        filmlist.stream().forEach(film -> film.setBookmark(false));
    }
}
