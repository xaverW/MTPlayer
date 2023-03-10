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
import de.p2tools.mtplayer.controller.data.BlackListFactory;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlacklistFilterFactory {

    private static long maxFilmDays = 0;
    private static boolean doNotShowFutureFilms, doNotShowGeoBlockedFilms;
    private static long minFilmDuration = 0;

    private BlacklistFilterFactory() {
    }

    public static synchronized void getBlackFilteredFilmlist() {
        // hier wird die komplette Filmliste gegen die Blacklist gefiltert
        // mit der Liste wird dann im TabFilme weiter gearbeitet
        final ProgData progData = ProgData.getInstance();
        final FilmlistMTP filmlist = progData.filmlist;
        final FilmlistMTP listFiltered = progData.filmlistFiltered;

        PDuration.counterStart("getBlackFiltered");
        loadCurrentBlacklistSettings();
        listFiltered.clear();

        if (filmlist != null) {
            listFiltered.setMeta(filmlist);

            Stream<FilmDataMTP> initialStream = filmlist.parallelStream();

            if (progData.actFilmFilterWorker.getActFilterSettings().isBlacklistOnly()) {
                //blacklist in ONLY
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOnly");
                initialStream = initialStream.filter(filmDataMTP -> filmDataMTP.isBlackBlocked());

            } else if (progData.actFilmFilterWorker.getActFilterSettings().isBlacklistOn()) {
                //blacklist in ON
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOn");
                initialStream = initialStream.filter(filmDataMTP -> !filmDataMTP.isBlackBlocked());

            } else {
                //blacklist in OFF
                PLog.sysLog("FilmlistBlackFilter - isBlacklistOff");
            }

            listFiltered.addAll(initialStream.collect(Collectors.toList()));

            // Array mit Sendernamen/Themen füllen
            listFiltered.loadTheme();
        }
        PDuration.counterStop("getBlackFiltered");
    }

    public static synchronized void markFilmBlack(boolean notify) {
        //hier werden die Filme gekennzeichnet, ob sie "black" sind
        //und das dauert: Filmliste geladen, addBlack, ConfigDialog, Filter blkBtn

        boolean maskerSet = false;
        PDuration.counterStart("markFilmBlack");
        PLog.sysLog("markFilmBlack -> start");

        if (!ProgData.getInstance().maskerPane.isVisible()) {
            maskerSet = true;
            ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
            ProgData.getInstance().maskerPane.setMaskerVisible(true);
        }

        //und jetzt die Filmliste durchlaufen parallel/stream ist gleich??
        ProgData.getInstance().blackList.clearCounter();
        loadCurrentBlacklistSettings();
        ProgData.getInstance().filmlist.stream().forEach(filmDataMTP ->
                filmDataMTP.setBlackBlocked(checkFilmIsBlocked(filmDataMTP, true)));
        getBlackFilteredFilmlist();

        PLog.sysLog("markFilmBlack -> stop");
        PDuration.counterStop("markFilmBlack");

        if (notify) {
            Listener.notify(Listener.EVENT_BLACKLIST_CHANGED, BlackListFactory.class.getSimpleName());
        }
        if (maskerSet) {
            //nur dann wieder ausschalten
            ProgData.getInstance().maskerPane.switchOffMasker();
        }
    }

    private static void loadCurrentBlacklistSettings() {
        //die aktuellen allgemeinen Blacklist-Einstellungen laden
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

    public static synchronized boolean checkFilmIsBlocked(FilmData filmData, boolean incCounter) {
        //zum Suchen von Downloads, wenn Black im Abo beachtet werden soll
        //und zum Markieren der Filme nach dem Neuladen einer Filmliste oder Ändern der Blacklist
        //hier werden die Filme gegen die Blacklist geprüft
        //liefert TRUE -> wenn der Film zur Blacklist passt, also geblockt werden soll
        //Counter werden vorher schon gelöscht

        if (doNotShowGeoBlockedFilms && filmData.isGeoBlocked()) {
            return true;
        }
        if (doNotShowFutureFilms && filmData.isInFuture()) {
            return true;
        }
        if (minFilmDuration != 0 && !checkOkFilmLength(filmData)) {
            return true;
        }
        if (maxFilmDays > 0 && !checkOkDate(filmData)) {
            return true;
        }

        filmData.setLowerCase();
        if (ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getValue()) {
            for (final BlackData blackData : ProgData.getInstance().blackList) {
                if (checkFilmIsBlocked(blackData, filmData)) {
                    //dann hat dieser Filter getroffen -> anzeigen
                    if (incCounter) {
                        blackData.incCountHits();
                    }
                    filmData.clearLowerCase();
                    return false;
                }
            }
            //dann hat kein Filter getroffen -> nicht anzeigen
            return true;

        } else {
            for (final BlackData blackData : ProgData.getInstance().blackList) {
                if (checkFilmIsBlocked(blackData, filmData)) {
                    //dann hat dieser Filter getroffen -> nicht anzeigen
                    if (incCounter) {
                        blackData.incCountHits();
                    }
                    filmData.clearLowerCase();
                    return true;
                }
            }
        }

        filmData.clearLowerCase();
        return false;
    }

    public static boolean checkFilmIsBlocked(FilmData filmData, BlackData blackData, boolean countHits) {
        //zum Zählen der Treffer -> Dialog
        //oder zum Setzen des letzten Films "setLastShownFilm" -> Gui
        filmData.setLowerCase();
        if (checkFilmIsBlocked(blackData, filmData)) {
            if (countHits) {
                blackData.incCountHits();
            }
            filmData.clearLowerCase();
            return true;
        }

        filmData.clearLowerCase();
        return false;
    }

    public static boolean checkFilmIsBlockedAndCountHits(FilmData filmData, BlackList list, boolean abort) {
        //wird nur nach dem Neuladen einer neuen Filmliste aus dem Web gemacht (FilmBlackList): nach Treffer abbrechen
        //oder zum Zählen der Treffer gemacht: nach Treffer nicht abbrechen, alle Treffer zählen
        //--> gibt (braucht) da keine WhiteList!!
        //CLEAR-COUNTER wird da vorher schon gemacht
        boolean ret = false;
        filmData.setLowerCase();
        for (final BlackData blackData : list) {
            if (checkFilmIsBlocked(blackData, filmData)) {
                blackData.incCountHits();
                ret = true;
                if (abort) {
                    filmData.clearLowerCase();
                    return true;
                }
            }
        }
        filmData.clearLowerCase();
        return ret;
    }

    public static AboData findAbo(FilmData film) {
        //liefert ein Abo zu dem Film, auch Abos die ausgeschaltet sind, Film zu klein ist, ...
        if (film.isLive()) {
            //Livestreams gehören nicht in ein Abo
            return null;
        }

        film.setLowerCase();
        final AboData aboData = ProgData.getInstance().aboList.stream()
                .filter(abo -> FilmFilterCheck.checkFilterMatch(
                        abo.fChannel,
                        abo.fTheme,
                        abo.fThemeTitle,
                        abo.fTitle,
                        abo.fSomewhere,
                        film))
                .findFirst()
                .orElse(null);

        film.clearLowerCase();
        return aboData;
    }

    private static boolean checkFilmIsBlocked(BlackData blackData, FilmData filmData) {
        //erst mal "schnell" prüfen->bringt ~20%
        if (blackData.quickChannel) {
            if (filmData.FILM_CHANNEL_STR.contains(blackData.fChannel.filterArr[0])) {
                //dann wird geblockt
                return true;
            } else {
                return false;
            }
        }
        if (blackData.quickTheme) {
            if (blackData.fTheme.isExact) {
                if (filmData.FILM_THEME_STR.equals(blackData.fTheme.filterArr[0])) {
                    //dann wird geblockt
                    return true;
                } else {
                    return false;
                }

            } else {
                if (filmData.FILM_THEME_STR.contains(blackData.fTheme.filterArr[0])) {
                    //dann wird geblockt
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (blackData.quickThemTitle) {
            if (filmData.FILM_THEME_STR.contains(blackData.fThemeTitle.filterArr[0]) ||
                    filmData.FILM_TITLE_STR.contains(blackData.fThemeTitle.filterArr[0])) {
                //dann wird geblockt
                return true;
            } else {
                return false;
            }
        }
        if (blackData.quickTitle) {
            if (filmData.FILM_TITLE_STR.contains(blackData.fTitle.filterArr[0])) {
                //dann wird geblockt
                return true;
            } else {
                return false;
            }
        }

        //wenn Filter passt (Blacklist) dann wird geblockt
        boolean ret = FilmFilterCheck.checkFilterMatch(
                blackData.fChannel,
                blackData.fTheme,
                blackData.fThemeTitle,
                blackData.fTitle,
                blackData.fSomewhere,
                filmData);

        if (ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check film based on date
     *
     * @param film item to be checked
     * @return true if film can be displayed
     */
    private static boolean checkOkDate(FilmData film) {
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
    private static boolean checkOkFilmLength(FilmData film) {
        return film.getDurationMinute() == 0 || film.getDurationMinute() >= minFilmDuration;
    }
}