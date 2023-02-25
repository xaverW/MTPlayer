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

package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.BlackList;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.gui.dialog.AddBlacklistDialogController;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlacklistFilterFactory {

    private static long maxFilmDays = 0;
    private static boolean doNotShowFutureFilms, doNotShowGeoBlockedFilms;
    private static long minFilmDuration = 0;

    private BlacklistFilterFactory() {
    }

    public static void addBlack() {
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel();
        if (!filmSelection.isPresent()) {
            return;
        }

        BlackData blackData = new BlackData(filmSelection.get().getChannel(), filmSelection.get().getTheme(),
                filmSelection.get().getTitle(), "");
        AddBlacklistDialogController addBlacklistDialogController =
                new AddBlacklistDialogController(ProgData.getInstance(), filmSelection.get(), blackData);

        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }

        ProgData.getInstance().filmGuiController.setLastShownFilm(blackData);
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static void addBlack(String sender, String theme, String titel) {
        BlackData blackData = new BlackData(sender, theme, titel, "");
        ProgData.getInstance().filmGuiController.setLastShownFilm(blackData);
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static synchronized void countHits(BlackList list, boolean abort) {
        //hier wird die Blacklist gegen die Filmliste gefiltert und die Treffer
        //für jeden Blacklist-Eintrag ermittelt

        PDuration.counterStart("countHitsList");
        list.clearCounter();

        final FilmlistMTP filmList = ProgData.getInstance().filmlist;
        if (filmList != null) {
            filmList.parallelStream().forEach(film -> applyBlacklist(film, abort, list));
        }

        PDuration.counterStop("countHitsList");
    }

    public static synchronized void countHits(BlackData blackData) {
        //hier wird ein BlackDate gegen die Filmliste gefiltert und die Treffer ermittelt
        PDuration.counterStart("countHitsBlackData");
        blackData.setCountHits(0);
        final FilmlistMTP filmlist = ProgData.getInstance().filmlist;
        if (filmlist != null) {
            filmlist.parallelStream().forEach(film -> {
                if (FilmFilterFactory.checkFilmWithBlacklistFilter(blackData, film)) {
                    blackData.incCountHits();
                }
            });
        }
        PDuration.counterStop("countHitsBlackData");
    }

    private static void applyBlacklist(FilmData film, boolean abort, BlackList list) {
        //zum Sortieren ist es sinnvoll, dass ALLE MÖGLICHEN Treffer gesucht werden
        for (final BlackData blackData : list) {

            if (FilmFilterFactory.checkFilmWithBlacklistFilter(blackData, film)) {
                blackData.incCountHits();
                if (abort) {
                    return;
                }
            }
        }
    }

    public static synchronized void getBlackFiltered() {
        // hier wird die komplette Filmliste gegen die Blacklist gefiltert
        // mit der Liste wird dann im TabFilme weiter gearbeitet
        final ProgData progData = ProgData.getInstance();
        final FilmlistMTP filmlist = progData.filmlist;
        final FilmlistMTP listFiltered = progData.filmlistFiltered;

        PDuration.counterStart("FilmlistBlackFilter.getBlackFiltered");
        loadCurrentFilterSettings();
        listFiltered.clear();

        if (filmlist != null) {
            listFiltered.setMeta(filmlist);

            Stream<FilmDataMTP> initialStream = filmlist.parallelStream();

            if (progData.actFilmFilterWorker.getActFilterSettings().isBlacklistOnly()) {
                //blacklist in ONLY
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOnly");
                initialStream = initialStream.filter(f -> !f.isBlackBlocked());

            } else if (progData.actFilmFilterWorker.getActFilterSettings().isBlacklistOn()) {
                //blacklist in ON
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOn");
                initialStream = initialStream.filter(f -> f.isBlackBlocked());

            } else {
                //blacklist in OFF
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOff");
            }

            listFiltered.addAll(initialStream.collect(Collectors.toList()));

            // Array mit Sendernamen/Themen füllen
            listFiltered.loadTheme();
        }
        PDuration.counterStop("FilmlistBlackFilter.getBlackFiltered");
    }

    public static synchronized void markFilmBlack() {
        PDuration.counterStart("FilmlistBlackFilter.markFilmBlack");
        loadCurrentFilterSettings();
        ProgData.getInstance().filmlist.stream().forEach(film -> {
            if (checkBlacklist(film)) {
                film.setBlackBlocked(true);
            } else {
                film.setBlackBlocked(false);
            }
        });
        PDuration.counterStop("FilmlistBlackFilter.markFilmBlack");
    }

    /**
     * Filterfunction for Downloads from Abos.
     *
     * @param film item to te tested
     * @return true if item should be displayed.
     */
    public static synchronized boolean checkBlacklistForDownloads(FilmData film) {
        // hier werden die Filme für Downloads aus Abos gesucht,
        // und wenn die Blacklist bei den Abos berücksichtigt werden soll,
        // wird damit geprüft
        loadCurrentFilterSettings(); // todo das muss nur beim ersten mal gemacht werden
        return checkBlacklist(film);
    }

    private static synchronized boolean checkBlacklist(FilmData film) {
        // hier werden die Filme gegen die Blacklist geprüft
        final ProgData progData = ProgData.getInstance();

        if (maxFilmDays > 0 && !checkDate(film)) {
            return false;
        }
        if (doNotShowGeoBlockedFilms && film.isGeoBlocked()) {
            return false;
        }
        if (doNotShowFutureFilms && film.isInFuture()) {
            return false;
        }
        if (minFilmDuration != 0 && !checkFilmLength(film)) {
            return false;
        }

        // wegen der Möglichkeit "Whiteliste" muss das extra geprüft werden
        if (progData.blackList.isEmpty()) {
            return true;
        }

        for (final BlackData blackData : ProgData.getInstance().blackList) {
            if (FilmFilterFactory.checkFilmWithBlacklistFilter(blackData, film)) {
                blackData.incCountHits();
                return ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getValue();
            }
        }

        return !ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getValue();
    }


    /**
     * Load current filter settings from Config
     */
    private static void loadCurrentFilterSettings() {
        try {
            if (ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS.getValue() == 0) {
                maxFilmDays = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS.getValue();
                maxFilmDays = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            maxFilmDays = 0;
        }

        minFilmDuration = ProgConfig.SYSTEM_BLACKLIST_MIN_FILM_DURATION.getValue(); // Minuten
        doNotShowFutureFilms = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE.getValue();
        doNotShowGeoBlockedFilms = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO.getValue();
    }

    /**
     * Check film based on date
     *
     * @param film item to be checked
     * @return true if film can be displayed
     */
    private static boolean checkDate(FilmData film) {
        if (maxFilmDays == 0) {
            return true;
        }

        final long filmTime = film.filmDate.getTime();
        if (filmTime != 0 && filmTime <= maxFilmDays) {
            return false;
        }

        return true;
    }

    /**
     * Filter based on film length.
     *
     * @param film item to check
     * @return true if film should be displayed
     */
    private static boolean checkFilmLength(FilmData film) {
        return film.getDurationMinute() == 0 || minFilmDuration <= film.getDurationMinute();
    }
}
