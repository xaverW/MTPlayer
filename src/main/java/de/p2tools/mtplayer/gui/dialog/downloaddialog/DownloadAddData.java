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


package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;

import java.util.Arrays;

public class DownloadAddData {
    String fileSize_HD = "";
    String fileSize_high = "";
    String fileSize_small = "";

    String resolution = FilmDataMTP.RESOLUTION_HD;
    boolean makeInfo, makeSubTitle, subIsDisabled = false;

    FilmDataMTP film;
    DownloadData download;
    SetData setData;

    String path = "";
    String name = "";

    DownloadAddData[] downloadAddDataArr;

    public DownloadAddData(DownloadAddData[] downloadAddDataArr) {
        this.downloadAddDataArr = downloadAddDataArr;
    }

    void setResolution(String resolution, boolean all) {
        if (all) {
            Arrays.stream(downloadAddDataArr).forEach(d -> {
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

    void setInfo(boolean info, boolean all) {
        if (all) {
            Arrays.stream(downloadAddDataArr).forEach(d -> d.makeInfo = info);
        } else {
            this.makeInfo = info;
        }
    }

    void setSubtitle(boolean subtitle, boolean all) {
        if (all) {
            Arrays.stream(downloadAddDataArr).forEach(d -> {
                if (d.subIsDisabled) {
                    // dann immer false, gibts nicht
                    d.makeSubTitle = false;
                } else {
                    d.makeSubTitle = subtitle;
                }
            });

        } else {
            if (this.subIsDisabled) {
                // dann immer false, gibts nicht
                this.makeSubTitle = false;
            } else {
                this.makeSubTitle = subtitle;
            }
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setPath(String path, boolean all) {
        if (all) {
            Arrays.stream(downloadAddDataArr).forEach(d -> d.path = path);
        } else {
            this.path = path;
        }
    }
}
