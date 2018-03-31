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

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmList;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;

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
    private final static Daten daten = Daten.getInstance();

    public static synchronized void getFilmListBlackFiltered() {
        // hier wird die komplette Filmliste gegen die Blacklist gefilter
        // mit der Liste wird dannn im TabFilme weiter gearbeitet

        final FilmList filmList = daten.filmList;
        final FilmList listeRet = daten.filmListFiltered;

        loadCurrentFilterSettings();

        Duration.counterStart("FilmlistBlackFilter.getFilmListBlackFiltered");
        listeRet.clear();

        if (filmList != null) {
            listeRet.setMeta(filmList);

            Stream<Film> initialStream = filmList.parallelStream();

            filterList.clear();
            if (daten.storedFilter.getSelectedFilter().isBlacklistOn()) {
                // add the filter predicates to the list
                // only when blacklist in ON!

                if (days > 0) {
                    filterList.add(FilmlistBlackFilter::checkDate);
                }

                if (doNotShowGeoBlockedFilms) {
                    filterList.add(FilmlistBlackFilter::checkGeoBlockedFilm);
                }
                if (doNotShowFutureFilms) {
                    filterList.add(FilmlistBlackFilter::checkIfFilmIsInFuture);
                }
                if (filmlaengeSoll != 0) {
                    filterList.add(FilmlistBlackFilter::checkFilmLength);
                }

                if (!daten.blackList.isEmpty()) {
                    filterList.add(FilmlistBlackFilter::applyBlacklistFilters);
                }

                for (final Predicate<Film> pred : filterList) {
                    initialStream = initialStream.filter(pred);
                }
            }

            final List<Film> col = initialStream.collect(Collectors.toList());

            listeRet.addAll(col);
            col.clear();

            // Array mit Sendernamen/Themen füllen
            listeRet.themenLaden();
        }
        Duration.counterStop("FilmlistBlackFilter.getFilmListBlackFiltered");
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

        loadCurrentFilterSettings();

        if (days > 0 && !checkDate(film)) {
            return false;
        }
        if (doNotShowGeoBlockedFilms && !film.isGeoBlocked()) {
            return false;
        }
        if (doNotShowFutureFilms && !checkIfFilmIsInFuture(film)) {
            return false;
        }
        if (filmlaengeSoll != 0 && !checkFilmLength(film)) {
            return false;
        }

        // wegen der Möglichkeit "Whiteliste" muss das extra geprüft werden
        if (daten.blackList.isEmpty()) {
            return true;
        }

        return applyBlacklistFilters(film);
    }

    /**
     * Load current filter settings from XvConfig
     */
    private static void loadCurrentFilterSettings() {
        try {
            if (Config.SYSTEM_BLACKLIST_DAYS.getInt() == 0) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * Config.SYSTEM_BLACKLIST_DAYS.getInt();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }
        try {
            filmlaengeSoll = Long.valueOf(Config.SYSTEM_BLACKLIST_FILMLAENGE.get()) * 60; // Minuten
        } catch (final Exception ex) {
            filmlaengeSoll = 0;
        }
        doNotShowFutureFilms = Boolean.parseBoolean(Config.SYSTEM_BLACKLIST_ZUKUNFT_NICHT_ANZEIGEN.get());
        doNotShowGeoBlockedFilms = Boolean.parseBoolean(Config.SYSTEM_BLACKLIST_GEO_NICHT_ANZEIGEN.get());
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
        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    /**
     * Check if film would be geoblocked for user
     *
     * @param film item to be checked
     * @return true if it is NOT blocked, false if it IS blocked
     */
    private static boolean checkGeoBlockedFilm(Film film) {
        return !film.isGeoBlocked();
    }

    /**
     * Check if a future film should be displayed.
     *
     * @param film item to be checked.
     * @return true if it should be displayed.
     */
    private static boolean checkIfFilmIsInFuture(Film film) {
        try {
            if (film.filmDate.getTime() > System.currentTimeMillis()) {
                return false;
            }
        } catch (final Exception ex) {
            Log.errorLog(696987123, ex);
        }
        return true;
    }

    /**
     * Filter based on film length.
     *
     * @param film item to check
     * @return true if film should be displayed
     */
    private static boolean checkFilmLength(Film film) {
        return film.dauerL == 0 || filmlaengeSoll < film.dauerL;

    }

    /**
     * Apply filters to film.
     *
     * @param film item to be filtered
     * @return true if film can be displayed
     */

    private static boolean applyBlacklistFilters(Film film) {
        for (final BlackData blackData : daten.blackList) {

            if (FilmFilter.filterAufFilmPruefen(
                    blackData.fSender,
                    blackData.fThema,
                    blackData.fThemaTitel,
                    blackData.fTitel,
                    blackData.fSomewhere,

                    0,
                    SelectedFilter.FILTER_DURATIION_MAX_SEC,

                    film,
                    false /* auch die Länge prüfen */)) {

                return Config.SYSTEM_BLACKLIST_IST_WHITELIST.getBool();
            }
        }
        return !Config.SYSTEM_BLACKLIST_IST_WHITELIST.getBool();
    }

}
