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

package de.p2tools.mtplayer.controller.data.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.gui.dialog.DownloadAddDialogController;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.tools.FileSizeUrl;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;

import java.util.ArrayList;

public class FilmTools {

    public static final String THEME_LIVE = "Livestream";

    public static String getSizeFromWeb(Film film, String url) {
        if (url.equals(film.arr[FilmXml.FILM_URL])) {
            return film.arr[FilmXml.FILM_SIZE];
        } else {
            return FileSizeUrl.getFileSizeFromUrl(url);
        }
    }

    public static void setFilmShown(ProgData progData, ArrayList<Film> filmArrayList, boolean setShown) {
        if (setShown) {
            progData.history.addFilmDataToHistory(filmArrayList);
        } else {
            progData.history.removeFilmDataFromHistory(filmArrayList);
        }
    }


    public static void bookmarkFilm(ProgData progData, Film film, boolean bookmark) {
        ArrayList<Film> filmArrayList = new ArrayList<>(1);
        filmArrayList.add(film);

        bookmarkFilm(progData, filmArrayList, bookmark);
    }

    public static void bookmarkFilm(ProgData progData, ArrayList<Film> filmArrayList, boolean bookmark) {
        if (bookmark) {
            progData.bookmarks.addFilmDataToHistory(filmArrayList);
        } else {
            progData.bookmarks.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void playFilm(Film film, SetData psetData) {
        SetData setData;
        String resolution = "";

        if (psetData != null) {
            setData = psetData;
        } else {
            setData = ProgData.getInstance().setDataList.getSetDataPlay();
        }

        if (setData == null) {
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.PLAY);
            return;
        }

        if (ProgData.getInstance().storedFilters.getActFilterSettings().isOnlyHd()) {
            resolution = Film.RESOLUTION_HD;
        }

        // und starten
        ProgData.getInstance().starterClass.startUrlWithProgram(film, setData, resolution);
    }

    public static void saveFilm(Film film, SetData pSet) {
        ArrayList<Film> list = new ArrayList<>();
        list.add(film);
        saveFilm(list, pSet);
    }

    public static void saveFilm(ArrayList<Film> list, SetData pSet) {
        if (list.isEmpty()) {
            return;
        }

        ProgData progData = ProgData.getInstance();
        ArrayList<Film> filmsAddDownloadList = new ArrayList<>();

        if (progData.setDataList.getSetDataListSave().isEmpty()) {
            new NoSetDialogController(progData, NoSetDialogController.TEXT.SAVE);
            return;
        }

        String resolution = "";
        if (progData.storedFilters.getActFilterSettings().isOnlyHd()) {
            resolution = Film.RESOLUTION_HD;
        }

        for (final Film film : list) {
            // erst mal schauen obs den schon gibt
            Download download = progData.downloadList.getDownloadUrlFilm(film.arr[Film.FILM_URL]);
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
}
