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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.mtplayer.controller.filter.FilterWorker;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataProps;
import de.p2tools.p2lib.mediathek.filter.FilmFilterCheck;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

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

    public static synchronized void markFilmsIfBlack(boolean notify) {
        // Filme die Black sind, markieren
        // Blacklist hinzugefügt, Config-Dialog beendet und Black geändert
        // es werden Filme/Audio beide markiert
        ProgData.getInstance().blackList.clearCounter();
        markFilmsIfBlack(true, notify);
        markFilmsIfBlack(false, notify);
    }

    public static synchronized void markFilmsIfBlack(boolean audio, boolean notify) {
        // Filme die Black sind, markieren, es werden die Filme gekennzeichnet, ob sie "black" sind
        // und es wird die gefilterte Liste erstellt
        // Filmliste geladen

        P2Duration.counterStart("markFilmBlack" + (audio ? "-Audio" : "-Film"));
        P2Log.sysLog("markFilmBlack " + (audio ? "Audio" : "Film") + " -> start");

        final boolean maskerPane;
        if (ProgData.getInstance().maskerPane.isVisible()) {
            maskerPane = true;
            if (!audio) {
                ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
            }
        } else {
            maskerPane = false;
        }

        loadCurrentBlacklistSettings();

        //Filmliste durchlaufen und geblockte Filme markieren (parallel: Blockiert sich selbst durch Film.setBlocked)
        P2Duration.counterStart("forEach");
        FilmListMTP list = audio ? ProgData.getInstance().audioList : ProgData.getInstance().filmList;
        final int sum = list.size();
        act = 0;
        now = 0;

        if (maskerPane) {
            // intermediate, dauert norm. nicht lang, dann springt die Anzeige
            if (!audio) {
                ProgData.getInstance().maskerPane.setMaskerProgress(-1.0, "Blacklist filtern");
            }
        }
        list.forEach(film -> {
            ++act;
            ++now;
            if (now > 5_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
//                if (maskerPane) {
//                    if (!audio) {
//                        ProgData.getInstance().maskerPane.setMaskerProgress(-1.0, "Blacklist filtern");
//                    }
//                }
            }
            film.setBlackBlocked(checkFilmIsBlackComplete(audio, film,
                    ProgData.getInstance().blackList, true));
        });
        P2Duration.counterStop("forEach");

        //und jetzt die FilteredList erstellen
        makeBlackFilteredFilmlist(audio);

        if (maskerPane) {
            // sonst springt es beim ProgStart
            if (!audio) {
                ProgData.getInstance().maskerPane.setMaskerProgress(-1.0, "Blacklist filtern");
            }
        }
        P2Log.sysLog("markFilmBlack " + (audio ? "Audio" : "Film") + " stop");
        P2Duration.counterStop("markFilmBlack" + (audio ? "-Audio" : "-Film"));

        if (notify) {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BLACKLIST_CHANGED);
        }
    }

    public static synchronized void makeBlackFilteredFilmlist(boolean audio) {
        // nach dem Markieren der Filme in der Liste, nach dem ein-/ausschalten der Blacklist
        // es wird die Black-gefilterte Filmliste erstellt
        final ProgData progData = ProgData.getInstance();
        final FilmListMTP filmList;
        final FilmListMTP filmListFiltered;
        final FilterWorker filterWorker = audio ? progData.filterWorkerAudio : progData.filterWorkerFilm;
        if (audio) {
            filmList = progData.audioList;
            filmListFiltered = progData.audioListFiltered;
        } else {
            filmList = progData.filmList;
            filmListFiltered = progData.filmListFiltered;
        }

        P2Duration.counterStart("makeBlackFilteredFilmlist");
//        loadCurrentBlacklistSettings();
        filmListFiltered.clear();

        if (filmList != null) {
            filmListFiltered.setMeta(filmList);
            Stream<FilmDataMTP> initialStream = filmList.parallelStream();

            if (filterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_INVERS) {
                //blacklist ONLY
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOnly");
                initialStream = initialStream.filter(FilmDataProps::isBlackBlocked);

            } else if (filterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_ON) {
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

    public static synchronized boolean checkFilmIsBlackComplete(boolean audio,
                                                                FilmData filmData,
                                                                List<BlackData> list,
                                                                boolean incCounter) {
        // beim Abo-Suchen wenn eingeschaltet, und beim markieren der Filme: "markFilmsIfBlack()"
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
        if (minFilmDuration != 0 && !checkFilmLength(filmData)) {
            return true;
        }
        if (maxFilmDays > 0 && !checkDate(filmData)) {
            return true;
        }

        return checkFilmIsBlack(audio, filmData, list, incCounter);
    }

    private static boolean checkFilmLength(FilmData film) {
        return film.getDurationMinute() == 0 || film.getDurationMinute() >= minFilmDuration;
    }

    private static boolean checkDate(FilmData film) {
        final long filmTime = film.filmDate.getTime(); // liefert die ms nach "0"
        return filmTime == 0 || filmTime > maxFilmDays;
    }

    public static boolean checkFilmIsBlack(boolean audio, FilmData filmData, List<BlackData> list, boolean countHits) {
        // Aufruf nach dem Neuladen einer Filmliste aus dem Web (ist der FilmListFilter beim Laden) oder checkFilmIsBlackComplete
        // nach Treffer **abbrechen** und Counter werden vorher schon gelöscht
        filmData.setLowerCase();
        // todo java.util.ConcurrentModificationException
        for (final BlackData blackData : list) {
            if (!blackData.isActive()) {
                //dann ist er ausgeschaltet
                continue;
            }
            if (audio && blackData.getList() == ProgConst.LIST_FILM) {
                continue;
            }
            if (!audio && blackData.getList() == ProgConst.LIST_AUDIO) {
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

    public static boolean checkFilmIsBlocked(BlackData blackData, FilmData filmData) {
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
}
