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


package de.p2tools.mtplayer.controller.load;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.p2lib.mediathek.audiolistload.P2ReadAudioFactory;
import de.p2tools.p2lib.mediathek.audiolistload.P2ReadAudio_toFilmList;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;

public class LoadAudioFactory {

    private LoadAudioFactory() {
    }

    public static void loadAudioListProgStart() {
        // nur einmal direkt nach dem Programmstart, geladen wird, wenn lokale nicht von HEUTE
        if ((!ProgConfig.SYSTEM_USE_AUDIOLIST.get())) {
            // dann will der User nicht :)
            return;
        }

        ProgData.AUDIOLIST_IS_DOWNLOADING.set(true);
        P2ReadAudio_toFilmList p2LoadAudioList = new P2ReadAudio_toFilmList(ProgData.getInstance().pEventHandler, new FilmListMTP());
        p2LoadAudioList.loadAudioListAtProgStart();
    }

    public static void loadAudioListFromWeb(boolean alwaysNew, boolean init) {
        // aus dem Menü oder Button in den Einstellungen immer neu aus dem Web laden, wird immer aus dem Web geladen
        if ((!ProgConfig.SYSTEM_USE_AUDIOLIST.get())) {
            // dann will der User nicht :)
            return;
        }

        if (!alwaysNew && !P2ReadAudioFactory.isNotFromToday(P2LoadConst.dateStoredAudiolist.getValueSafe())) {
            // dann nicht immer laden und noch nicht zu alt
            return;
        }

        ProgData.AUDIOLIST_IS_DOWNLOADING.set(true);
        if (init) {
            LoadFactory.initLoadFactoryConst();
        }
        P2ReadAudio_toFilmList p2LoadAudioList = new P2ReadAudio_toFilmList(ProgData.getInstance().pEventHandler, new FilmListMTP());
        p2LoadAudioList.loadNewAudioListFromWeb();
    }
}
