/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import javafx.beans.property.BooleanProperty;

import java.util.Arrays;

class DownloadAddInfo {
    String fileSize_HD = "";
    String fileSize_high = "";
    String fileSize_small = "";

    String resolution = FilmDataMTP.RESOLUTION_HD;
    boolean info, subtitle, subDisable = false;

    FilmDataMTP film;
    DownloadData download;
    SetData psetData;

    String path = "";
    String name = "";

    BooleanProperty chkAll;
    DownloadAddInfo[] downloadAddInfos;

    public DownloadAddInfo(BooleanProperty chkAll, DownloadAddInfo[] downloadAddInfos) {
        this.chkAll = chkAll;
        this.downloadAddInfos = downloadAddInfos;
    }


    void setResolution(String resolution) {
        if (chkAll.get()) {

            Arrays.stream(downloadAddInfos).forEach(d -> {
                if (resolution.equals(FilmDataMTP.RESOLUTION_HD) && d.film.isHd()) {
                    d.resolution = FilmDataMTP.RESOLUTION_HD;

                } else if (resolution.equals(FilmDataMTP.RESOLUTION_SMALL) && film.isSmall()) {
                    d.resolution = FilmDataMTP.RESOLUTION_SMALL;

                } else {
                    d.resolution = FilmDataMTP.RESOLUTION_NORMAL;
                }
            });

        } else {
            this.resolution = resolution;
        }
    }

    void setInfo(boolean info) {
        if (chkAll.get()) {
            Arrays.stream(downloadAddInfos).forEach(d -> d.info = info);
        } else {
            this.info = info;
        }
    }

    void setSubtitle(boolean subtitle) {
        if (chkAll.get()) {
            Arrays.stream(downloadAddInfos).forEach(d -> {
                if (!d.subDisable) {
                    d.subtitle = subtitle;
                }
            });
        } else {
            this.subtitle = subtitle;
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setPath(String path) {
        if (chkAll.get()) {
            Arrays.stream(downloadAddInfos).forEach(d -> d.path = path);
        } else {
            this.path = path;
        }
    }
}
