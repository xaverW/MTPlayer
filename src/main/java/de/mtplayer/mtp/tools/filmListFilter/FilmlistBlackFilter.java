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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.tools.duration.PDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmlistBlackFilter {

    final static List<Predicate<Film>> filterList = new ArrayList<>();
    private static long days = 0;
    private static boolean doNotShowFutureFilms, doNotShowGeoBlockedFilms;
    private static long filmlaengeSoll = 0;
    private final static ProgData PROG_DATA = ProgData.getInstance();

    public static synchronized void getBlackFiltered() {
        // hier wird die komplette Filmliste gegen die Blacklist gefilter
        // mit der Liste wird dannn im TabFilme weiter gearbeitet

        final Filmlist filmlist = PROG_DATA.filmlist;
        final Filmlist listRet = PROG_DATA.filmlistFiltered;

        loadCurrentFilterSettings();

        PDuration.counterStart("FilmlistBlackFilter.getBlackFiltered");
        listRet.clear();
        PROG_DATA.blackList.clearCounter();

        if (filmlist != null) {
            listRet.setMeta(filmlist);

            Stream<Film> initialStream = filmlist.parallelStream();

            filterList.clear();
            if (PROG_DATA.storedFilter.getSelectedFilter().isBlacklistOn()) {
                // add the filter predicates to the list
                // only when blacklist in ON!

                if (days > 0) {
                    filterList.add(FilmlistBlackFilter::checkDate);
                }

                if (doNotShowGeoBlockedFilms) {
//                    filterList.add(FilmlistBlackFilter::checkFilmNotGeoBlocked);
                    filterList.add(film -> !film.isGeoBlocked());
                }
                if (doNotShowFutureFilms) {
//                    filterList.add(FilmlistBlackFilter::checkFilmNotInFuture);
                    filterList.add(film -> !film.isInFuture());
                }
                if (filmlaengeSoll != 0) {
                    filterList.add(FilmlistBlackFilter::checkFilmLength);
                }

                if (!PROG_DATA.blackList.isEmpty()) {
                    filterList.add(film -> applyBlacklistFilters(film, true));
                }

                for (final Predicate<Film> pred : filterList) {
                    initialStream = initialStream.filter(pred);
                }
            }

            final List<Film> col = initialStream.collect(Collectors.toList());

            listRet.addAll(col);
            col.clear();

            // Array mit Sendernamen/Themen füllen
            listRet.loadTheme();
        }
        PDuration.counterStop("FilmlistBlackFilter.getBlackFiltered");
    }

    /**
     * Filterfunction for Downloads from Abos.
     *
     * @param film item to te tested
     * @return true if item should be displayed.
     */
    public static synchronized boolean checkBlacklistForDownloads(Film film) {
        // hier werden die Filme für Downloads aus Abos gesucht,
        // und wenn die Blacklist bei den Abos berücksichtigt werden soll,
        // wird damit geprüft

        loadCurrentFilterSettings(); // todo das muss nur beim ersten mal gemacht werden

        if (days > 0 && !checkDate(film)) {
            return false;
        }
//        if (doNotShowGeoBlockedFilms && !checkFilmNotGeoBlocked(film)) {
//            return false;
//        }
//        if (doNotShowFutureFilms && !checkFilmNotInFuture(film)) {
//            return false;
//        }
        if (doNotShowGeoBlockedFilms && film.isGeoBlocked()) {
            return false;
        }
        if (doNotShowFutureFilms && film.isInFuture()) {
            return false;
        }
        if (filmlaengeSoll != 0 && !checkFilmLength(film)) {
            return false;
        }

        // wegen der Möglichkeit "Whiteliste" muss das extra geprüft werden
        if (PROG_DATA.blackList.isEmpty()) {
            return true;
        }

        return applyBlacklistFilters(film, false);
    }

    /**
     * Load current filter settings from XvConfig
     */
    private static void loadCurrentFilterSettings() {
        try {
            if (ProgConfig.SYSTEM_BLACKLIST_SHOW_ONLY_DAYS.getInt() == 0) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * ProgConfig.SYSTEM_BLACKLIST_SHOW_ONLY_DAYS.getInt();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        try {
            filmlaengeSoll = Long.valueOf(ProgConfig.SYSTEM_BLACKLIST_FILMSIZE.get()) * 60; // Minuten
        } catch (final Exception ex) {
            filmlaengeSoll = 0;
        }

        doNotShowFutureFilms = Boolean.parseBoolean(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE.get());
        doNotShowGeoBlockedFilms = Boolean.parseBoolean(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO.get());
    }

    /**
     * Check film based on date
     *
     * @param film item to be checked
     * @return true if film can be displayed
     */
    private static boolean checkDate(Film film) {
        if (days == 0) {
            return true;
        }

        final long filmTime = film.filmDate.getTime();
        if (filmTime != 0 && filmTime <= days) {
            return false;
        }

        return true;
    }

//    /**
//     * Check if film would be geoblocked for user
//     *
//     * @param film item to be checked
//     * @return true if it is NOT blocked, false if it IS blocked
//     */
//    private static boolean checkFilmNotGeoBlocked(Film film) {
//        return !film.isGeoBlocked();
//    }
//
//    /**
//     * Check if a future film should be displayed.
//     *
//     * @param film item to be checked.
//     * @return true if it should be displayed (not in future).
//     */
//    private static boolean checkFilmNotInFuture(Film film) {
////        if (film.isInFuture()) {
////            return false;
////        }
//
//// todo ??
//
//        try {
//            if (film.filmDate.getTime() > System.currentTimeMillis()) {
//                // Film in Zukunft, filtern
//                return false;
//            }
//        } catch (final Exception ex) {
//            PLog.errorLog(696987123, ex);
//        }
//
//        return true;
//    }

    /**
     * Filter based on film length.
     *
     * @param film item to check
     * @return true if film should be displayed
     */
    private static boolean checkFilmLength(Film film) {
        return film.dauerL == 0 || filmlaengeSoll <= film.dauerL;

    }

    /**
     * Apply filters to film.
     *
     * @param film item to be filtered
     * @return true if film can be displayed
     */

    private static boolean applyBlacklistFilters(Film film, boolean inc) {
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

                if (inc) {
                    blackData.incCountHits();
                }
                return ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getBool();
            }
        }
        return !ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getBool();
    }

}
