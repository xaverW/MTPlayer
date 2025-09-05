/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.mtplayer.controller.data.blackdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.tools.duration.P2Duration;

import java.util.List;

public class BlacklistCountFactory {
    private static int act = 0;
    private static int now = 0;

    private BlacklistCountFactory() {
    }

    public static synchronized void countHits(List<BlackData> list) {
        // Aufruf mit Button zum Zählen in den Einstellungen
        // hier wird die Blacklist gegen die Filmliste/Audioliste gefiltert und die Treffer gezählt
        // für *jeden* Blacklist-Eintrag ermittelt, wird nicht nach einem Treffer abgebrochen
        P2Duration.counterStart("countHits");

        for (BlackData bl : list) {
            bl.clearCounter();
        }

        final int sum = ProgData.getInstance().audioList.size() + ProgData.getInstance().filmList.size();
        act = 0;
        now = 0;

        // wenn parallel, variieren die Werte etwas??
        final List<BlackData> copyListFilm = BlacklistFactory.getSubList(false, list);
        ProgData.getInstance().filmList.forEach(film -> {
            ++act;
            ++now;
            if (now > 1_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
            }
            countHitsFilmlist(film, copyListFilm);
        });

        final List<BlackData> copyListAudio = BlacklistFactory.getSubList(true, list);
        ProgData.getInstance().audioList.forEach(film -> {
            ++act;
            ++now;
            if (now > 1_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
            }
            countHitsFilmlist(film, copyListAudio);
        });

        P2Duration.counterStop("countHits");
    }

    private static void countHitsFilmlist(FilmData filmData, List<BlackData> list) {
        // nur zum Zählen im Config-Dialog
        filmData.setLowerCase();
        list.parallelStream().forEach(blackData -> {
            if (BlacklistFilterFactory.checkFilmIsBlocked(blackData, filmData)) {
                blackData.incCountHits();
            }
        });
        filmData.clearLowerCase();
    }
}
