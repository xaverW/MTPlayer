/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataProps;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;

import java.util.function.Predicate;

public class PredicateFactory {
    private PredicateFactory() {
    }

    public static Predicate<FilmData> getPredicate(ProgData progData) {

        FilmFilter filmFilter = progData.filmFilterWorker.getActFilterSettings();
        FastFilmFilter fastFilmFilter = progData.filmFilterWorker.getFastFilterSettings();

        Filter fChannel;
        Filter fSomewhere;
        Filter fUrl;
        Filter fShowDate;

        String filterChannel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        String filterSomewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        String filterUrl = filmFilter.isUrlVis() ? filmFilter.getUrl() : "";
        String filterShowDate = filmFilter.isShowDateVis() ? filmFilter.getShowDate() : "";

        // Sender
        fChannel = new Filter(filterChannel, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);
        // URL
        fUrl = new Filter(filterUrl, false); // gibt URLs mit ",", das also nicht trennen
        //ShowDate
        fShowDate = new Filter(filterShowDate, false);

        final boolean onlyBookmark = filmFilter.isOnlyVis() && filmFilter.isOnlyBookmark();
        final boolean onlyHd = filmFilter.isOnlyVis() && filmFilter.isOnlyHd();
        final boolean onlyUt = filmFilter.isOnlyVis() && filmFilter.isOnlyUt();
        final boolean onlyLive = filmFilter.isOnlyVis() && filmFilter.isOnlyLive();
        final boolean onlyNew = filmFilter.isOnlyVis() && filmFilter.isOnlyNew();
        final boolean onlyAktHist = filmFilter.isOnlyVis() && filmFilter.getOnlyActHistory();

        final boolean noAbos = filmFilter.isNotVis() && filmFilter.isNotAbo();
        final boolean noShown = filmFilter.isNotVis() && filmFilter.isNotHistory();
        final boolean noDouble = filmFilter.isNotVis() && filmFilter.isNotDouble();
        final boolean noGeo = filmFilter.isNotVis() && filmFilter.isNotGeo();
        final boolean noFuture = filmFilter.isNotVis() && filmFilter.isNotFuture();

        final int checkBlack = filmFilter.blacklistOnOffProperty().getValue();

        // Länge am Slider in Min
        final int minLengthMinute = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : 0;
        final int maxLengthMinute = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        // FilmUhrZeit in Sek. von 0:00 Uhr
        final int minTimeSec = filmFilter.isMinMaxTimeVis() ? filmFilter.getMinTime() : 0;
        final int maxTimeSec = filmFilter.isMinMaxTimeVis() ? filmFilter.getMaxTime() : FilterCheck.FILTER_TIME_MAX_SEC;
        final boolean minMaxTimeInvert = filmFilter.isMinMaxTimeInvert();

        long days;
        try {
            if (filmFilter.getTimeRange() == FilterCheck.FILTER_ALL_OR_MIN) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * filmFilter.getTimeRange();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }
        if (!filmFilter.isTimeRangeVis()) {
            days = 0;
        }

        Predicate<FilmData> predicate = film -> true;

        if (onlyBookmark) {
            predicate = predicate.and(FilmDataProps::isBookmark);
        }
        if (onlyHd) {
            predicate = predicate.and(FilmDataProps::isHd);
        }
        if (onlyUt) {
            predicate = predicate.and(FilmDataProps::isUt);
        }
        if (onlyLive) {
            predicate = predicate.and(FilmDataProps::isLive);
        }
        if (onlyAktHist) {
            predicate = predicate.and(FilmDataProps::getActHist);
        }
        if (onlyNew) {
            predicate = predicate.and(FilmDataProps::isNewFilm);
        }

        if (noAbos) {
            //todo -> abo??
            predicate = predicate.and(f -> f.arr[FilmDataXml.FILM_ABO_NAME].isEmpty()); //dann gibts kein Abo, auch keins "zu kleiner Film", ...
        }
        if (noShown) {
            predicate = predicate.and(f -> !f.isShown());
        }
        if (noDouble) {
            predicate = predicate.and(f -> !f.isDoubleUrl());
        }
        if (noGeo) {
            predicate = predicate.and(f -> !f.isGeoBlocked());
        }

        if (noFuture) {
            predicate = predicate.and(f -> !f.isInFuture());
        }

        if (checkBlack != BlacklistFilterFactory.BLACKLILST_FILTER_OFF) {
            //dann auch mit Blacklist suchen
            if (checkBlack == BlacklistFilterFactory.BLACKLILST_FILTER_ON) {
                predicate = predicate.and(f -> !f.isBlackBlocked());
            } else {
                //dann invers, alles was geblockt wird
                predicate = predicate.and(FilmDataProps::isBlackBlocked);
            }
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilterCheck.checkDays(d, f.filmDate.getTime()));
        }

        // Filmlänge
        if (minLengthMinute != 0) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchLengthMin(minLengthMinute, f.getDurationMinute()));
        }
        if (maxLengthMinute != FilterCheck.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchLengthMax(maxLengthMinute, f.getDurationMinute()));
        }

        // Film-Uhrzeit
        if (minTimeSec != 0 || maxTimeSec != FilterCheck.FILTER_TIME_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchFilmTime(minTimeSec, maxTimeSec, minMaxTimeInvert, f.filmTime));
        }

        predicate = addFastFilter(filmFilter, fastFilmFilter, predicate);

        if (!fChannel.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchChannelSmart(fChannel, f));
        }

        if (!fSomewhere.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchSomewhere(fSomewhere, f));
        }

        if (!fUrl.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchUrl(fUrl, f));
        }

        //Sendetag
        if (!fShowDate.filter.equals(FilterCheck.FILTER_SHOW_DATE_ALL)) {
            predicate = predicate.and(f -> checkShowDate(filmFilter.getShowDate(), f));
        }

        return predicate;
    }

    private static Predicate<FilmData> addFastFilter(FilmFilter filmFilter, FastFilmFilter fastFilmFilter,
                                                     Predicate<FilmData> predicate) {
        Filter fastFilter = new Filter(fastFilmFilter.getFilterTerm(), true);

        // Thema
        final boolean themeIsExact = filmFilter.isThemeIsExact();
        String filterTheme = filmFilter.isThemeVis() ? filmFilter.getResTheme() : "";
        Filter fTheme = new Filter(filterTheme, themeIsExact, true);

        // ThemaTitel
        String filterThemeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        Filter fThemeTitle = new Filter(filterThemeTitle, true);

        // Titel
        String filterTitle = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        Filter fTitle = new Filter(filterTitle, true);

        if (ProgConfig.FAST_SEARCH_ON.getValue() &&
                ProgConfig.FAST_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME_TITLE) {
            // dann mit dem FAST
            if (!fastFilter.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchThemeTitle(fastFilter, f));
            }
        } else {
            // mit dem regulären Filter
            if (!fThemeTitle.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchThemeTitle(fThemeTitle, f));
            }
        }

        if (ProgConfig.FAST_SEARCH_ON.getValue() &&
                ProgConfig.FAST_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME) {
            // dann mit dem FAST
            if (!fastFilter.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchTheme(fastFilter, f));
            }
        } else {
            // mit dem regulären Filter
            if (!fTheme.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchThemeExact(fTheme, f));
            }
        }

        if (ProgConfig.FAST_SEARCH_ON.getValue() &&
                ProgConfig.FAST_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_TITLE) {
            // dann mit dem FAST
            if (!fastFilter.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchTitle(fastFilter, f));
            }
        } else {
            // mit dem regulären Filter
            if (!fTitle.isEmpty) {
                predicate = predicate.and(f -> FilmFilterCheck.checkMatchTitle(fTitle, f));
            }
        }
        return predicate;
    }

    private static boolean checkShowDate(String showDate, FilmData film) {
        if (showDate.isEmpty() || film.filmDate.isEmpty()) {
            //dann will der User nicht oder der Film hat kein Datum
            return true;
        }

        if (film.arr[FilmDataXml.FILM_DATE].equals(showDate)) {
            return true;
        }

        return false;
    }
}
