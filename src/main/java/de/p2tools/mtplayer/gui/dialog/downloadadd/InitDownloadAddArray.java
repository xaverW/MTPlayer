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

    public static DownloadAddData[] initDownloadInfoArrayFilm(ArrayList<FilmDataMTP> filmsToDownloadList, AddDto addDto) {
        String aktPath = "";
        if (!ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.isEmpty()) {
            // dann den ersten Pfad setzen
            aktPath = ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.get(0);
        }

        // DownloadArr anlegen
        DownloadAddData[] downloadAddData = new DownloadAddData[filmsToDownloadList.size()];
        for (int i = 0; i < filmsToDownloadList.size(); ++i) {
            downloadAddData[i] = new DownloadAddData();
            downloadAddData[i].setData = addDto.setDataStart;
            downloadAddData[i].download = new DownloadData(DownloadConstants.SRC_DOWNLOAD,
                    addDto.setDataStart, filmsToDownloadList.get(i),
                    null, "", aktPath, "");

            downloadAddData[i].path = downloadAddData[i].download.getDestPath();
            downloadAddData[i].name = downloadAddData[i].download.getDestFileName();

            // Dateigröße
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                downloadAddData[i].fileSize_HD = downloadAddData[i].download.getFilm().isHd() ?
                        FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(), downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD)) : "";
                downloadAddData[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(),
                        downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddData[i].fileSize_small = downloadAddData[i].download.getFilm().isSmall() ?
                        FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(), downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~5 ist, dauert das viel zu lang
                downloadAddData[i].fileSize_HD = "";
                downloadAddData[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(),
                        downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddData[i].fileSize_small = "";
            }

            // Infofile
            downloadAddData[i].download.setInfoFile(downloadAddData[i].setData.isInfoFile());

            // Subtitle
            if (downloadAddData[i].download.getFilm().getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                downloadAddData[i].subIsDisabled = true;
                downloadAddData[i].download.setSubtitle(false);
            } else {
                downloadAddData[i].subIsDisabled = false;
                downloadAddData[i].download.setSubtitle(downloadAddData[i].setData.isSubtitle());
            }

            // Auflösung: Die Werte passend zum Film setzen
            if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_HD) ||
                    addDto.filterResolution.equals(FilmDataMTP.RESOLUTION_HD) ||
                    downloadAddData[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_HD))
                    && downloadAddData[i].download.getFilm().isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_HD;

            } else if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_SMALL) ||
                    downloadAddData[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_SMALL))
                    && downloadAddData[i].download.getFilm().isSmall()) {
                downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_SMALL;

            } else {
                downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
        }
        return downloadAddData;
    }

    public static DownloadAddData[] initDownloadInfoArrayDownload(ArrayList<DownloadData> downloadDataArrayList, AddDto addDto) {
        // DownloadArr anlegen
        DownloadAddData[] downloadAddData = new DownloadAddData[downloadDataArrayList.size()];
        for (int i = 0; i < downloadDataArrayList.size(); ++i) {
            downloadAddData[i] = new DownloadAddData();
            downloadAddData[i].download = downloadDataArrayList.get(i).getCopy();
            downloadAddData[i].downloadOrg = downloadDataArrayList.get(i);

            downloadAddData[i].path = downloadAddData[i].download.getDestPath();
            downloadAddData[i].name = downloadAddData[i].download.getDestFileName();
            downloadAddData[i].setData = downloadAddData[i].download.getSetData();

            // Dateigröße
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                downloadAddData[i].fileSize_HD = downloadAddData[i].download.getFilm().isHd() ?
                        FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(), downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD)) : "";
                downloadAddData[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(),
                        downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddData[i].fileSize_small = downloadAddData[i].download.getFilm().isSmall() ?
                        FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(), downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~5 ist, dauert das viel zu lang
                downloadAddData[i].fileSize_HD = "";
                downloadAddData[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddData[i].download.getFilm(),
                        downloadAddData[i].download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddData[i].fileSize_small = "";
            }

            // Subtitle
            if (downloadAddData[i].download.getFilm().getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                downloadAddData[i].subIsDisabled = true;
                downloadAddData[i].download.setSubtitle(false);
            }

            // Auflösung: Die Werte passend zum Film setzen
            if (downloadAddData[i].download.getFilm() != null) {

                if (downloadAddData[i].download.getFilm().isHd() &&
                        downloadAddData[i].download.getUrl().equals(downloadAddData[i].download.getFilm()
                                .getUrlForResolution(FilmDataMTP.RESOLUTION_HD))) {
                    downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_HD;

                } else if (downloadAddData[i].download.getFilm().isSmall() &&
                        downloadAddData[i].download.getUrl().equals(downloadAddData[i].download.getFilm()
                                .getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL))) {
                    downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_SMALL;

                } else {
                    downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
                }
            } else {
                downloadAddData[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
        }
        return downloadAddData;
    }
}
