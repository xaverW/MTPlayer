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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;

import java.util.ArrayList;

public class InitDownloadAddArray {

    private InitDownloadAddArray() {
    }

    public static AddDownloadData[] initDownloadInfoArrayFilm(ArrayList<FilmDataMTP> filmsToDownloadList,
                                                              AddDownloadDto addDownloadDto) {
        // neue Downloads anlegen
        String aktPath = "";
        if (!ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.isEmpty()) {
            // dann den ersten Pfad setzen
            aktPath = ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.get(0);
        }

        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[filmsToDownloadList.size()];
        for (int i = 0; i < filmsToDownloadList.size(); ++i) {
            FilmDataMTP film = filmsToDownloadList.get(i);
            addDownloadData[i] = new AddDownloadData();


            // Auflösung: Erst mal schauen, was es gibt
            String resolution;
            if (ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_HD) &&
                    film.isHd()) {
                resolution = FilmDataMTP.RESOLUTION_HD;

            } else if (ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_SMALL) &&
                    film.isSmall()) {
                resolution = FilmDataMTP.RESOLUTION_SMALL;

            } else {
                resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
            // Downloads anlegen
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                addDownloadData[i].download = new DownloadData(DownloadConstants.SRC_DOWNLOAD,
                        addDownloadDto.setDataStart, film, null,
                        aktPath, resolution, true);
            } else {
                addDownloadData[i].download = new DownloadData(DownloadConstants.SRC_DOWNLOAD,
                        addDownloadDto.setDataStart, film, null,
                        aktPath, resolution, false);
            }
            // für den Fall, dass sie sich geändert hat
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.set(addDownloadData[i].download.getResolution());


            // Start festlegen
            if (ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW.getValue()) {
                // dann sofort starten
                addDownloadData[i].startNow = true;
            } else if (ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT.getValue()) {
                // dann nicht sofort starten
                addDownloadData[i].startNow = false;
            } else if (ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME.getValue()) {
                // dann mit Startzeit starten
                addDownloadData[i].download.setStartTimeAlsoTomorrow(addDownloadDto.p2TimePicker.getTime());
            }

            // Dateigröße
            addSize(addDownloadData, i);

            // Infofile
            addDownloadData[i].download.setInfoFile(addDownloadData[i].download.getSetData().isInfoFile());

            // Subtitle
            if (addDownloadData[i].download.getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                addDownloadData[i].download.setSubtitle(false);
            } else {
                addDownloadData[i].download.setSubtitle(addDownloadData[i].download.getSetData().isSubtitle());
            }
        } // for...

        return addDownloadData;
    }

    public static AddDownloadData[] initDownloadInfoArrayDownload(ArrayList<DownloadData> downloadDataArrayList,
                                                                  AddDownloadDto addDownloadDto) {
        // Downloads ändern
        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[downloadDataArrayList.size()];
        for (int i = 0; i < downloadDataArrayList.size(); ++i) {
            addDownloadData[i] = new AddDownloadData();
            addDownloadData[i].download = downloadDataArrayList.get(i).getCopy();
            addDownloadData[i].downloadOrg = downloadDataArrayList.get(i);

            // Dateigröße
            addSize(addDownloadData, i);

            // Auflösung: Die Werte passend zum Film setzen
            if (addDownloadData[i].download.isHd() &&
                    addDownloadData[i].download.getUrl().equals(addDownloadData[i].download.getFilmUrlHd())) {
                addDownloadData[i].download.setResolution(FilmDataMTP.RESOLUTION_HD);

            } else if (addDownloadData[i].download.isSmall() &&
                    addDownloadData[i].download.getUrl().equals(addDownloadData[i].download.getFilmUrlSmall())) {
                addDownloadData[i].download.setResolution(FilmDataMTP.RESOLUTION_SMALL);

            } else {
                addDownloadData[i].download.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
            }

            // Subtitle
            if (addDownloadData[i].download.getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                addDownloadData[i].download.setSubtitle(false);
            }
        }
        return addDownloadData;
    }

    private static void addSize(AddDownloadData[] addDownloadData, int i) {
        // Dateigröße
        if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
            DownloadFactory.setDownloadSize(addDownloadData[i].download);
        }
    }
}

