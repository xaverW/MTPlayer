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

package de.mtplayer.mtp.tools.filmListFilter;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.tools.log.Duration;

public class FilmlistBlackFilterCountHits {

    private final static ProgData PROG_DATA = ProgData.getInstance();

    public static synchronized void countHits() {
        // hier wird die Blacklist gegen die Filmliste gefilter und die Treffer
        // für jeden Blacklisteintrag ermittelt
        final Filmlist filmlist = PROG_DATA.filmlist;

        Duration.counterStart("FilmlistBlackFilterCountHits.countHits");
        for (final BlackData blackData : PROG_DATA.blackList) {
            blackData.setCountHits(0);
        }

        if (filmlist != null) {
            filmlist.parallelStream().forEach(film -> applyBlacklistFilters(film));
        }

        Duration.counterStop("FilmlistBlackFilterCountHits.countHits");
    }


    /**
     * Apply filters to film.
     *
     * @param film item to be filtered
     */

    private static void applyBlacklistFilters(Film film) {
        for (final BlackData blackData : PROG_DATA.blackList) {

            if (FilmFilter.checkFilmWithFilter(
                    blackData.fChannel,
                    blackData.fTheme,
                    blackData.fThemeTitle,
                    blackData.fTitle,
                    blackData.fSomewhere,

                    0,
                    SelectedFilter.FILTER_DURATION_MAX_SEC,

                    film,
                    false /* auch die Länge prüfen */)) {
                blackData.incCountHits();
            }

        }


    }

}
