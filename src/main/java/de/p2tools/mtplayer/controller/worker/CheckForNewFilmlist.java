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


package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mediathek.film.SearchFilmlistUpdate;
import de.p2tools.p2lib.p2event.P2Events;
import de.p2tools.p2lib.p2event.P2Listener;

public class CheckForNewFilmlist extends SearchFilmlistUpdate {

    public CheckForNewFilmlist(ProgData progData) {
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                //dann wird wieder gesucht
                setFoundNewList(false);
            }
        });

        progData.pEventHandler.addListener(new P2Listener(P2Events.EVENT_TIMER_SECOND) {
            @Override
            public void pingGui() {
                // kann dauern und hält dann das Programm beim Start auf
                new Thread(() -> hasNewFilmlist(ProgData.getInstance().filmList.getFilmlistId())).start();
            }
        });
    }

    public boolean check() {
        return super.check(ProgData.getInstance().filmList.getFilmlistId());
    }
}
