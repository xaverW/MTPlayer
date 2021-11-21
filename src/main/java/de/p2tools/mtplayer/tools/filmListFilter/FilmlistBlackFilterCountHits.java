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

package de.p2tools.mtplayer.tools.filmListFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.Filmlist;
import de.p2tools.p2Lib.tools.duration.PDuration;

public class FilmlistBlackFilterCountHits {

    private final static ProgData PROG_DATA = ProgData.getInstance();

    public static synchronized void countHits(boolean abort) {
        // hier wird die Blacklist gegen die Filmliste gefilter und die Treffer
        // für jeden Blacklisteintrag ermittelt

        PDuration.counterStart("FilmlistBlackFilterCountHits.countHits");
        PROG_DATA.blackList.clearCounter();

        final Filmlist filmlist = PROG_DATA.filmlist;
        if (filmlist != null) {
            filmlist.parallelStream().forEach(film -> applyBlacklistFilters(film, abort));
        }

        PDuration.counterStop("FilmlistBlackFilterCountHits.countHits");
    }


    private static void applyBlacklistFilters(FilmData film, boolean abort) {
        // zum Sortieren ist es sinnvoll, dass ALLE MÖGLICHEN Treffer gesucht werden
        for (final BlackData blackData : PROG_DATA.blackList) {

            if (FilmFilter.checkFilmWithFilter(
                    blackData.fChannel,
                    blackData.fTheme,
                    blackData.fThemeTitle,
                    blackData.fTitle,
                    blackData.fSomewhere,

                    FilmFilter.FILTER_TIME_RANGE_ALL_VALUE,
                    FilmFilter.FILTER_DURATION_MIN_MINUTE,
                    FilmFilter.FILTER_DURATION_MAX_MINUTE,

                    film, false)) {

                blackData.incCountHits();
                if (abort) {
                    return;
                }
            }
        }

    }

}
