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


package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.tools.duration.PDuration;

public class DownloadListFactory {
    public DownloadListFactory() {
    }

    public static synchronized void addFilmInDownloads() {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        PDuration.counterStart("addFilmInList");
        ProgData progData = ProgData.getInstance();
        int counter = 50; //todo das dauert sonst viel zu lang
        for (DownloadData d : progData.downloadList) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(progData.filmList.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
            d.initResolution();
        }
        PDuration.counterStop("addFilmInList");
    }
}
