/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.history.ConvertOldHistoryFactory;

public class ProgConfigUpdateAfterLoad {
    // hier werden geänderte Programmeinstellungen/Funktionen angepasst,
    // muss immer nur einmal laufen!!
    private ProgConfigUpdateAfterLoad() {
    }

    public static void setUpdateDone() {
        ProgConfig.SYSTEM_HISTORY_INIT_FILM_AUDIO_LIST.setValue(true); // für Version 23
    }

    public static void update() {
        if (ProgData.FILMLIST_IS_DOWNLOADING.get() || ProgData.AUDIOLIST_IS_DOWNLOADING.get()) {
            // dan wird noch eine Liste geladen, kommt dann nochmal
            return;
        }

        if (!ProgConfig.SYSTEM_HISTORY_INIT_FILM_AUDIO_LIST.getValue()) {
            // Abos und LIST in der History anlegen
            ConvertOldHistoryFactory.initFilmAudioList();
        }

        setUpdateDone();
    }
}
