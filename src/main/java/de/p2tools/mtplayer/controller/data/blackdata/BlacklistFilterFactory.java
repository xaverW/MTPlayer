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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataProps;
import de.p2tools.p2lib.mediathek.filter.FilmFilterCheck;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BlacklistFilterFactory {
    public static final int BLACKLILST_FILTER_OFF = 0;
    public static final int BLACKLILST_FILTER_ON = 1;
    public static final int BLACKLILST_FILTER_INVERS = 2;

    private static long maxFilmDays = 0; // Zeit in ms ab wann erlaubt, oder 0 wenn alles
    private static boolean doNotShowFutureFilms, doNotShowGeoBlockedFilms, doNotShowDoubleFilms;
    private static long minFilmDuration = 0;
    private static int act = 0;
    private static int now = 0;

    private BlacklistFilterFactory() {
    }

    public static void markFilmBlackThread(boolean notify) {
        ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Blacklist", -1, false);
        new Thread(() -> {
            BlacklistFilterFactory.markFilmBlack(notify);
            ProgData.busy.busyOffFx();
        }).start();
    }

    public static synchronized void markFilmBlack(boolean notify) {
        // Filmliste geladen, Button/Menü, ConfigDialog, Filter blkBtn
        // hier werden die Filme gekennzeichnet, ob sie "black" sind und das dauert

        P2Duration.counterStart("markFilmBlack");
        P2Log.sysLog("markFilmBlack -> start");

        final boolean maskerPane;
        if (ProgData.getInstance().maskerPane.isVisible()) {
            maskerPane = true;
            ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
        } else {
            maskerPane = false;
        }

        ProgData.getInstance().blackList.clearCounter();
        loadCurrentBlacklistSettings();

        //Filmliste durchlaufen und geblockte Filme markieren (parallel: Blockiert sich selbst durch Film.setBlocked)
        P2Duration.counterStart("forEach");
        final int sum = ProgData.getInstance().filmList.size();
        act = 0;
        now = 0;

        ProgData.getInstance().filmList.forEach(filmDataMTP -> {
            ++act;
            ++now;
            if (now > 5_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
                if (maskerPane) {
                    ProgData.getInstance().maskerPane.setMaskerProgress(percent, "Blacklist filtern");
                }
            }
            filmDataMTP.setBlackBlocked(checkFilmIsBlockedCompleteBlackData(filmDataMTP,
                    ProgData.getInstance().blackList, true));
        });
        P2Duration.counterStop("forEach");

        //und jetzt die filteredList erstellen
        makeBlackFilteredFilmlist();

        if (maskerPane) {
            ProgData.getInstance().maskerPane.setMaskerProgress(-1.0, "Blacklist filtern");
        }
        P2Log.sysLog("markFilmBlack -> stop");
        P2Duration.counterStop("markFilmBlack");

        if (notify) {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BLACKLIST_CHANGED);
        }
    }

    public static synchronized void makeBlackFilteredFilmlist() {
        // nach dem markieren der Filme in der Liste, nach dem ein-/ausschalten der Blacklist
        // es wird die Black-gefilterte Filmliste erstellt
        final ProgData progData = ProgData.getInstance();
        final FilmListMTP filmList = progData.filmList;
        final FilmListMTP filmListFiltered = progData.filmListFiltered;

        P2Duration.counterStart("makeBlackFilteredFilmlist");
        loadCurrentBlacklistSettings();
        filmListFiltered.clear();

        if (filmList != null) {
            filmListFiltered.setMeta(filmList);

            Stream<FilmDataMTP> initialStream = filmList.parallelStream();

            if (progData.filterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_INVERS) {
                //blacklist ONLY
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOnly");
                initialStream = initialStream.filter(FilmDataProps::isBlackBlocked);

            } else if (progData.filterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_ON) {
                //blacklist ON
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOn");
                initialStream = initialStream.filter(filmDataMTP -> !filmDataMTP.isBlackBlocked());

            } else {
                //blacklist OFF
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOff");
            }

            filmListFiltered.addAll(initialStream.toList());
            // Array mit Sendernamen/Themen füllen
            filmListFiltered.loadTheme();
        }
        P2Duration.counterStop("makeBlackFilteredFilmlist");
    }

    public static synchronized boolean checkFilmIsBlockedCompleteBlackData(FilmData filmData, List<BlackData> list,
                                                                           boolean incCounter) {
        // beim Abo-Suchen wenn eingeschaltet, und beim markieren der Filme: "markFilmBlack()"
        // hier werden der Film gegen alle BLACK-Einstellungen der Blacklist geprüft
        // liefert TRUE -> wenn der Film zur Blacklist passt, also geblockt werden soll (oder nicht WHITE)
        // Counter werden vorher schon gelöscht und werden gesetzt

        if (doNotShowGeoBlockedFilms && filmData.isGeoBlocked()) {
            return true;
        }
        if (doNotShowDoubleFilms && filmData.isDoubleUrl()) {
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

        return checkFilmAndCountHits(filmData, list, incCounter);
    }

    public static boolean checkFilmAndCountHits(FilmData filmData, List<BlackData> list, boolean countHits) {
        // Aufruf nach dem Neuladen einer Filmliste aus dem Web (ist der FilmListFilter beim Laden) oder Test/Markieren eines Films
        // nach Treffer **abbrechen**
        // Counter werden vorher schon gelöscht
        filmData.setLowerCase();
        for (final BlackData blackData : list) {
            if (!blackData.isActive()) {
                //dann ist er ausgeschaltet
                continue;
            }

            if (checkFilmIsBlocked(blackData, filmData)) {
                if (countHits) {
                    blackData.incCountHits();
                }
                filmData.clearLowerCase();
                return true;
            }
        }
        filmData.clearLowerCase();
        return false;
    }

    private static void checkFilmAndCountHitsForAll(FilmData filmData, List<BlackData> list) {
        filmData.setLowerCase();
        list.parallelStream().forEach(blackData -> {
            if (BlacklistFilterFactory.checkFilmIsBlocked(blackData, filmData)) {
                blackData.incCountHits();
            }
        });
        filmData.clearLowerCase();
    }

    public static synchronized void countHits(BlackData blackData) {
        // Aufruf mit Button im AddBlackList-Dialog zum Zählen
        // hier wird ein BlackDate gegen die Filmliste gefiltert und die Treffer ermittelt
        List<BlackData> bl = new ArrayList<>();
        bl.add(blackData);
        countHits(bl);
    }

    public static synchronized void countHits(List<BlackData> list) {
        // Aufruf mit Button zum Zählen, Einstellungen
        // hier wird die Blacklist gegen die Filmliste gefiltert und die Treffer
        // für *jeden* Blacklist-Eintrag ermittelt, wird nicht nach einem Treffer abgebrochen
        P2Duration.counterStart("countHits");

        for (BlackData bl : list) {
            bl.clearCounter();
        }

        List<BlackData> copyList = new ArrayList<>(list);
        final int sum = ProgData.getInstance().filmList.size();
        act = 0;
        now = 0;
        final FilmListMTP filmDataMTPS = ProgData.getInstance().filmList;
        if (filmDataMTPS != null) {
            // wenn parallel, variieren die Werte etwas??
            filmDataMTPS.forEach(film -> {
                ++act;
                ++now;
                if (now > 1_000) {
                    now = 0;
                    final double percent = (double) act / sum;
                    ProgData.busy.setProgress(percent);
                }
                checkFilmAndCountHitsForAll(film, copyList);
            });
        }
        P2Duration.counterStop("countHits");
    }

    private static void loadCurrentBlacklistSettings() {
        // die aktuellen allgemeinen Blacklist-Einstellungen laden
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
        doNotShowDoubleFilms = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_DOUBLE.getValue();
    }

    private static boolean checkFilmIsBlocked(BlackData blackData, FilmData filmData) {
        // erst mal "schnell" prüfen->bringt ~20%
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
                    return !blackData.fTheme.exclude;
                } else {
                    return blackData.fTheme.exclude;
                }
            }
        }
        if (blackData.quickThemTitle) {
            if (filmData.FILM_THEME_STR.contains(blackData.fThemeTitle.filterArr[0]) ||
                    filmData.FILM_TITLE_STR.contains(blackData.fThemeTitle.filterArr[0])) {
                //dann wird geblockt
                return !blackData.fThemeTitle.exclude;
            } else {
                return blackData.fThemeTitle.exclude;
            }
        }
        if (blackData.quickTitle) {
            if (filmData.FILM_TITLE_STR.contains(blackData.fTitle.filterArr[0])) {
                //dann wird geblockt
                return !blackData.fTitle.exclude;
            } else {
                return blackData.fTitle.exclude;
            }
        }

        // wenn Filter passt (Blacklist) dann wird geblockt
        boolean ret = FilmFilterCheck.checkFilterMatch(
                blackData.fChannel,
                blackData.fTheme,
                blackData.fThemeTitle,
                blackData.fTitle,
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
        final long filmTime = film.filmDate.getTime(); // liefert die ms nach "o"
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
