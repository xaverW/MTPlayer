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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;

public class AddDownloadData {
    String fileSize_HD = "";
    String fileSize_high = "";
    String fileSize_small = "";

    String resolution = FilmDataMTP.RESOLUTION_NORMAL;
    boolean subIsDisabled = false;

    String orgProgArray = "";

    DownloadData download; // ist der neu angelegte / Kopie vom OrgDownload
    DownloadData downloadOrg = null; // ist der OrgDownload der geändert werden soll
    SetData setData;
    boolean startNow = false;

    String path = "";
    String name = "";

    public AddDownloadData() {
    }

    public boolean downloadIsRunning() {
        return downloadOrg != null && !downloadOrg.isNotStartedOrFinished();
    }
}