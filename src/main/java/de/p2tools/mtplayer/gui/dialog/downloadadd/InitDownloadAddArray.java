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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;

import java.util.ArrayList;

public class InitDownloadAddArray {

    private InitDownloadAddArray() {
    }

    public static AddDownloadData[] initDownloadInfoArrayFilm(ArrayList<FilmDataMTP> filmsToDownloadList, AddDownloadDto addDownloadDto) {
        String aktPath = "";
        if (!ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.isEmpty()) {
            // dann den ersten Pfad setzen
            aktPath = ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.get(0);
        }

        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[filmsToDownloadList.size()];
        for (int i = 0; i < filmsToDownloadList.size(); ++i) {
            addDownloadData[i] = new AddDownloadData();
            addDownloadData[i].setData = addDownloadDto.setDataStart;
            addDownloadData[i].download = new DownloadData(DownloadConstants.SRC_DOWNLOAD,
                    addDownloadDto.setDataStart, filmsToDownloadList.get(i),
                    null, "", aktPath, "");

            addDownloadData[i].path = addDownloadData[i].download.getDestPath();
            addDownloadData[i].name = addDownloadData[i].download.getDestFileName();

            // Dateigröße
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                addDownloadData[i].fileSize_HD = addDownloadData[i].download.getFilm().isHd() ?
                        FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(), addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD)) : "";
                addDownloadData[i].fileSize_high = FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(),
                        addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                addDownloadData[i].fileSize_small = addDownloadData[i].download.getFilm().isSmall() ?
                        FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(), addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~5 ist, dauert das viel zu lang
                addDownloadData[i].fileSize_HD = "";
                addDownloadData[i].fileSize_high = FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(),
                        addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                addDownloadData[i].fileSize_small = "";
            }

            // Infofile
            addDownloadData[i].download.setInfoFile(addDownloadData[i].setData.isInfoFile());

            // Subtitle
            if (addDownloadData[i].download.getFilm().getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                addDownloadData[i].subIsDisabled = true;
                addDownloadData[i].download.setSubtitle(false);
            } else {
                addDownloadData[i].subIsDisabled = false;
                addDownloadData[i].download.setSubtitle(addDownloadData[i].setData.isSubtitle());
            }

            // Auflösung: Die Werte passend zum Film setzen
            if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_HD) ||
                    addDownloadDto.filterResolution.equals(FilmDataMTP.RESOLUTION_HD) ||
                    addDownloadData[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_HD))
                    && addDownloadData[i].download.getFilm().isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_HD;

            } else if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_SMALL) ||
                    addDownloadData[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_SMALL))
                    && addDownloadData[i].download.getFilm().isSmall()) {
                addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_SMALL;

            } else {
                addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
        }
        return addDownloadData;
    }

    public static AddDownloadData[] initDownloadInfoArrayDownload(ArrayList<DownloadData> downloadDataArrayList, AddDownloadDto addDownloadDto) {
        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[downloadDataArrayList.size()];
        for (int i = 0; i < downloadDataArrayList.size(); ++i) {
            addDownloadData[i] = new AddDownloadData();
            addDownloadData[i].download = downloadDataArrayList.get(i).getCopy();
            addDownloadData[i].downloadOrg = downloadDataArrayList.get(i);

            addDownloadData[i].path = addDownloadData[i].download.getDestPath();
            addDownloadData[i].name = addDownloadData[i].download.getDestFileName();
            addDownloadData[i].setData = addDownloadData[i].download.getSetData();

            // Dateigröße
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                addDownloadData[i].fileSize_HD = addDownloadData[i].download.getFilm().isHd() ?
                        FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(), addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD)) : "";
                addDownloadData[i].fileSize_high = FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(),
                        addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                addDownloadData[i].fileSize_small = addDownloadData[i].download.getFilm().isSmall() ?
                        FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(), addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~5 ist, dauert das viel zu lang
                addDownloadData[i].fileSize_HD = "";
                addDownloadData[i].fileSize_high = FilmFactory.getSizeFromWeb(addDownloadData[i].download.getFilm(),
                        addDownloadData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                addDownloadData[i].fileSize_small = "";
            }

            // Subtitle
            if (addDownloadData[i].download.getFilm().getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                addDownloadData[i].subIsDisabled = true;
                addDownloadData[i].download.setSubtitle(false);
            }

            // Auflösung: Die Werte passend zum Film setzen
            if (addDownloadData[i].download.getFilm() != null) {

                if (addDownloadData[i].download.getFilm().isHd() &&
                        addDownloadData[i].download.getUrl().equals(addDownloadData[i].download.getFilm()
                                .getUrlForResolution(FilmDataMTP.RESOLUTION_HD))) {
                    addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_HD;

                } else if (addDownloadData[i].download.getFilm().isSmall() &&
                        addDownloadData[i].download.getUrl().equals(addDownloadData[i].download.getFilm()
                                .getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL))) {
                    addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_SMALL;

                } else {
                    addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
                }
            } else {
                addDownloadData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
        }
        return addDownloadData;
    }
}
