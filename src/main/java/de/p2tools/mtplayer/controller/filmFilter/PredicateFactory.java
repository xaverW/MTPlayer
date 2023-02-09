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


package de.p2tools.mtplayer.controller.filmFilter;

import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.mtFilm.film.FilmDataXml;
import de.p2tools.p2Lib.mtFilter.FilmFilterCheck;
import de.p2tools.p2Lib.mtFilter.Filter;
import de.p2tools.p2Lib.mtFilter.FilterCheck;

import java.util.function.Predicate;

public class PredicateFactory {
    private PredicateFactory() {
    }

    public static Predicate<FilmData> getPredicate(FilmFilter filmFilter) {
        Filter fChannel;
        Filter fTheme;
        Filter fThemeTitle;
        Filter fTitle;
        Filter fSomewhere;
        Filter fUrl;
        Filter fShowDate;

        String filterChannel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        String filterTheme = filmFilter.isThemeVis() ? filmFilter.getTheme() : "";
        String filterThemeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        String filterTitle = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        String filterSomewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        String filterUrl = filmFilter.isUrlVis() ? filmFilter.getUrl() : "";
        String filterShowDate = filmFilter.isShowDateVis() ? filmFilter.getShowDate() : "";

        final boolean themeExact = filmFilter.isThemeExact();
        // Sender
        fChannel = new Filter(filterChannel, true);
        // Thema
        fTheme = new Filter(filterTheme, themeExact, true);
        // ThemaTitel
        fThemeTitle = new Filter(filterThemeTitle, true);
        // Titel
        fTitle = new Filter(filterTitle, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);
        // URL
        fUrl = new Filter(filterUrl, false); // gibt URLs mit ",", das also nicht trennen
        //ShowDate
        fShowDate = new Filter(filterShowDate, false);

        //Sendedatum

        final boolean onlyBookmark = filmFilter.isOnlyVis() ? filmFilter.isOnlyBookmark() : false;
        final boolean onlyHd = filmFilter.isOnlyVis() ? filmFilter.isOnlyHd() : false;
        final boolean onlyUt = filmFilter.isOnlyVis() ? filmFilter.isOnlyUt() : false;
        final boolean onlyLive = filmFilter.isOnlyVis() ? filmFilter.isOnlyLive() : false;
        final boolean onlyNew = filmFilter.isOnlyVis() ? filmFilter.isOnlyNew() : false;
        final boolean onlyAktHist = filmFilter.isOnlyVis() ? filmFilter.getOnlyActHistory() : false;

        final boolean noAbos = filmFilter.isNotVis() ? filmFilter.isNotAbo() : false;
        final boolean noShown = filmFilter.isNotVis() ? filmFilter.isNotHistory() : false;
        final boolean noDouble = filmFilter.isNotVis() ? filmFilter.isNotDouble() : false;
        final boolean noGeo = filmFilter.isNotVis() ? filmFilter.isNotGeo() : false;
        final boolean noFuture = filmFilter.isNotVis() ? filmFilter.isNotFuture() : false;

        final boolean onlyBlack = filmFilter.isBlacklistOnly();

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
            predicate = predicate.and(f -> f.isBookmark());
        }
        if (onlyHd) {
            predicate = predicate.and(f -> f.isHd());
        }
        if (onlyUt) {
            predicate = predicate.and(f -> f.isUt());
        }
        if (onlyLive) {
            predicate = predicate.and(f -> f.isLive());
        }
        if (onlyAktHist) {
            predicate = predicate.and(f -> f.getActHist());
        }
        if (onlyNew) {
            predicate = predicate.and(f -> f.isNewFilm());
        }

        if (noAbos) {
            //todo -> abo??
            predicate = predicate.and(f -> f.arr[FilmDataXml.FILM_ABO_NAME].isEmpty());//dann gibts kein Abo, auch keins "zu kleiner Film", ...
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

        if (onlyBlack) {
            predicate = predicate.and(f -> !f.isBlackBlocked());
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilterCheck.checkDays(d, f.filmDate.getTime()));
        }

        // Filmlänge
        if (minLengthMinute != 0) {
            predicate = predicate.and(f -> FilmFilterCheck.checkLengthMin(minLengthMinute, f.getDurationMinute()));
        }
        if (maxLengthMinute != FilterCheck.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilterCheck.checkLengthMax(maxLengthMinute, f.getDurationMinute()));
        }

        // Film-Uhrzeit
        if (minTimeSec != 0 || maxTimeSec != FilterCheck.FILTER_TIME_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilterCheck.checkFilmTime(minTimeSec, maxTimeSec, minMaxTimeInvert, f.filmTime));
        }


        if (!fChannel.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkChannelSmart(fChannel, f));
        }

        if (!fTheme.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkThemeExact(fTheme, f));
        }

        if (!fThemeTitle.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkThemeTitle(fThemeTitle, f));
        }

        if (!fTitle.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkTitle(fTitle, f));
        }

        if (!fSomewhere.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkSomewhere(fSomewhere, f));
        }

        if (!fUrl.empty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkUrl(fUrl, f));
        }

        //Sendetag
        if (!fShowDate.filter.equals(FilterCheck.FILTER_SHOW_DATE_ALL)) {
            predicate = predicate.and(f -> checkShowDate(filmFilter.getShowDate(), f));
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
