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

package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mLib.tools.FileSizeUrl;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.gui.dialog.DownloadAddDialogController;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.dialog.NoSetDialogController;
import de.mtplayer.mtp.gui.tools.MTOpen;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.collections.ObservableList;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class FilmTools {

    public static final String THEME_LIVE = "Livestream";

    public static void getInfoText(Film film, ObservableList list) {

        list.clear();

        if (film == null) {
            return;
        }

        Text text1, text2;
        text1 = new Text(film.arr[FilmXml.FILM_CHANNEL] + "  -  " + film.arr[FilmXml.FILM_TITLE] + "\n\n");
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));

        text2 = new Text(film.arr[FilmXml.FILM_DESCRIPTION]);
        text2.setWrappingWidth(20);

        list.addAll(text1, text2);

        if (!film.arr[FilmXml.FILM_WEBSITE].isEmpty()) {
            Hyperlink hyperlink = new Hyperlink(film.arr[FilmXml.FILM_WEBSITE]);
            list.addAll(new Text("\n\n zur Website: "), hyperlink);

            hyperlink.setOnAction(a -> {
                try {
                    MTOpen.openURL(film.arr[FilmXml.FILM_WEBSITE]);
                } catch (Exception e) {
                    PLog.errorLog(975421021, e);
                }
            });
        }
    }

    public static String getSizeFromWeb(Film film, String url) {
        if (url.equals(film.arr[FilmXml.FILM_URL])) {
            return film.arr[FilmXml.FILM_SIZE];
        } else {
            return FileSizeUrl.getFileSizeFromUrl(url);
        }
    }

    public static void setFilmShown(ProgData progData, ArrayList<Film> filmArrayList, boolean set) {

        filmArrayList.stream().forEach(film -> {
            if (set) {
                progData.history.writeFilmArray(filmArrayList);
            } else {
                progData.history.removeFilmListFromHistory(filmArrayList);
            }
        });
    }


    public static void playFilm(Film film, SetData psetData) {
        SetData pset;
        String resolution = "";

        if (psetData != null) {
            pset = psetData;
        } else {
            pset = ProgData.getInstance().setList.getPsetPlay();
        }

        if (pset == null) {
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.PLAY);
            return;
        }

        if (ProgData.getInstance().storedFilter.getSelectedFilter().isOnlyHd()) {
            resolution = Film.RESOLUTION_HD;
        }

        // und starten
        ProgData.getInstance().starterClass.startUrlWithProgram(film, pset, resolution);
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

        final SetData psetData = pSet;
        ProgData progData = ProgData.getInstance();
        ArrayList<Film> filmsAddDownloadList = new ArrayList<>();

        if (progData.setList.getListSave().isEmpty()) {
            new NoSetDialogController(progData, NoSetDialogController.TEXT.SAVE);
            return;
        }

        String resolution = "";
        if (progData.storedFilter.getSelectedFilter().isOnlyHd()) {
            resolution = Film.RESOLUTION_HD;
        }

        for (final Film dateFilm : list) {
            // erst mal schauen obs den schon gibt
            Download download = progData.downloadList.getDownloadUrlFilm(dateFilm.arr[Film.FILM_URL]);
            if (download == null) {
                filmsAddDownloadList.add(dateFilm);
            } else {
                // dann ist der Film schon in der Downloadliste

                //todo wenn nur einer in der Liste macht "no" keinen Sinn
                MTAlert.BUTTON answer = new MTAlert().showAlert_yes_no_cancel("Anlegen?", "Nochmal anlegen?",
                        "Download für den Film existiert bereits.\n" + "Nochmal anlegen?");
                switch (answer) {
                    case CANCEL:
                        // alles Abbrechen
                        return;
                    case NO:
                        continue;
                    case YES:
                        filmsAddDownloadList.add(dateFilm);
                        break;
                }
            }
        }
        if (!filmsAddDownloadList.isEmpty()) {
            new DownloadAddDialogController(progData, filmsAddDownloadList, psetData, resolution);
        }
    }
}
