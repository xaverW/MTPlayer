/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;

import java.util.ArrayList;
import java.util.List;

public class FilmSaveFactory {
    private FilmSaveFactory() {
    }

    public static void saveFilm(FilmDataMTP film) {
        // aus Button/Menü
        ArrayList<FilmDataMTP> list = new ArrayList<>();
        list.add(film);
        saveFilmList(list, null);
    }

    public static void saveFilmList() {
        saveFilmList(ProgData.getInstance().filmGuiController.getSelList(true), null);
    }

    public static void saveFilmList(List<FilmDataMTP> list, SetData setData) {
        if (list.isEmpty()) {
            return;
        }

        ProgData progData = ProgData.getInstance();
        ArrayList<FilmDataMTP> filmsAddDownloadList = new ArrayList<>();

        if (progData.setDataList.getSetDataListSave().isEmpty()) {
            new NoSetDialogController(progData, NoSetDialogController.TEXT.SAVE);
            return;
        }
        if (setData == null) {
            setData = progData.setDataList.getSetDataListSave().get(0);
        }

        String resolution = "";
        if (progData.filterWorker.getActFilterSettings().isOnlyHd()) {
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
                    P2Alert.BUTTON answer = P2Alert.showAlert_yes_no("Anlegen?", "Nochmal anlegen?",
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
                    P2Alert.BUTTON answer = P2Alert.showAlert_yes_no_cancel("Anlegen?", "Nochmal anlegen?",
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
            new DownloadAddDialogController(progData, filmsAddDownloadList, setData, resolution);
        }
    }
}
