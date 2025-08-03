/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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
import de.p2tools.p2lib.mediathek.audiolistload.P2LoadAudioList;

public class LoadAudioFactory {

    private LoadAudioFactory() {
    }

    public static void loadAudioListProgStart() {
        // neu einmal direkt nach dem Programmstart
        ProgData.AUDIOLIST_IS_DOWNLOADING.set(true);
        P2LoadAudioList p2LoadAudioList = new P2LoadAudioList(ProgData.getInstance().pEventHandler, new FilmListMTP());
        LoadFactory.initLoadFactoryConst();
        p2LoadAudioList.loadAudioListAtProgStart();
    }

    public static void loadAudioListButton() {
        // aus dem Menü oder Button in den Einstellungen
        // immer neu aus dem Web laden
        ProgData.AUDIOLIST_IS_DOWNLOADING.set(true);
        P2LoadAudioList p2LoadAudioList = new P2LoadAudioList(ProgData.getInstance().pEventHandler, new FilmListMTP());
        LoadFactory.initLoadFactoryConst();
        p2LoadAudioList.loadNewAudioListFromWeb();
    }


}
