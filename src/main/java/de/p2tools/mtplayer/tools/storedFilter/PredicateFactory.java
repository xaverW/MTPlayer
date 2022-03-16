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


package de.p2tools.mtplayer.tools.storedFilter;

import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmDataXml;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;

import java.util.function.Predicate;

public class PredicateFactory {
    private PredicateFactory() {

    }

    public static Predicate<FilmData> getPredicate(SelectedFilter selectedFilter) {
        Filter fChannel;
        Filter fTheme;
        Filter fThemeTitle;
        Filter fTitle;
        Filter fSomewhere;
        Filter fUrl;
        Filter fShowDate;

        String filterChannel = selectedFilter.isChannelVis() ? selectedFilter.getChannel() : "";
        String filterTheme = selectedFilter.isThemeVis() ? selectedFilter.getTheme() : "";
        String filterThemeTitle = selectedFilter.isThemeTitleVis() ? selectedFilter.getThemeTitle() : "";
        String filterTitle = selectedFilter.isTitleVis() ? selectedFilter.getTitle() : "";
        String filterSomewhere = selectedFilter.isSomewhereVis() ? selectedFilter.getSomewhere() : "";
        String filterUrl = selectedFilter.isUrlVis() ? selectedFilter.getUrl() : "";
        String filterShowDate = selectedFilter.isShowDateVis() ? selectedFilter.getShowDate() : "";

        final boolean themeExact = selectedFilter.isThemeExact();
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

        final boolean onlyBookmark = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyBookmark() : false;
        final boolean onlyHd = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyHd() : false;
        final boolean onlyUt = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyUt() : false;
        final boolean onlyLive = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyLive() : false;
        final boolean onlyNew = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyNew() : false;
        final boolean onlyAktHist = selectedFilter.isOnlyVis() ? selectedFilter.getOnlyActHistory() : false;

        final boolean noAbos = selectedFilter.isNotVis() ? selectedFilter.isNotAbo() : false;
        final boolean noShown = selectedFilter.isNotVis() ? selectedFilter.isNotHistory() : false;
        final boolean noDouble = selectedFilter.isNotVis() ? selectedFilter.isNotDouble() : false;
        final boolean noGeo = selectedFilter.isNotVis() ? selectedFilter.isNotGeo() : false;
        final boolean noFuture = selectedFilter.isNotVis() ? selectedFilter.isNotFuture() : false;

        final boolean onlyBlack = selectedFilter.isBlacklistOnly();

        // Länge am Slider in Min
        final int minLengthMinute = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMinDur() : 0;
        final int maxLengthMinute = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMaxDur() : FilmFilter.FILTER_DURATION_MAX_MINUTE;

        // FilmUhrZeit in Sek. von 0:00 Uhr
        final int minTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMinTime() : 0;
        final int maxTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMaxTime() : FilmFilter.FILTER_FILMTIME_MAX_SEC;
        final boolean minMaxTimeInvert = selectedFilter.isMinMaxTimeInvert();

        long days;
        try {
            if (selectedFilter.getTimeRange() == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * selectedFilter.getTimeRange();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }
        if (!selectedFilter.isTimeRangeVis()) {
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
            predicate = predicate.and(f -> f.arr[FilmDataXml.FILM_ABO_NAME].isEmpty());
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
            predicate = predicate.and(f -> FilmFilter.checkDays(d, f));
        }

        // Filmlänge
        if (minLengthMinute != 0) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMin(minLengthMinute, f.getDurationMinute()));
        }
        if (maxLengthMinute != FilmFilter.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMax(maxLengthMinute, f.getDurationMinute()));
        }

        // Film-Uhrzeit
        if (minTimeSec != 0 || maxTimeSec != FilmFilter.FILTER_FILMTIME_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilter.checkFilmTime(minTimeSec, maxTimeSec, minMaxTimeInvert, f.filmTime));
        }


        if (!fChannel.empty) {
            predicate = predicate.and(f -> FilmFilter.checkChannelSmart(fChannel, f));
        }

        if (!fTheme.empty) {
            predicate = predicate.and(f -> FilmFilter.checkTheme(fTheme, f));
        }

        if (!fThemeTitle.empty) {
            predicate = predicate.and(f -> FilmFilter.checkThemeTitle(fThemeTitle, f));
        }

        if (!fTitle.empty) {
            predicate = predicate.and(f -> FilmFilter.checkTitle(fTitle, f));
        }

        if (!fSomewhere.empty) {
            predicate = predicate.and(f -> FilmFilter.checkSomewhere(fSomewhere, f));
        }

        if (!fUrl.empty) {
            predicate = predicate.and(f -> FilmFilter.checkUrl(fUrl, f));
        }

        //Sendetag
        if (!fShowDate.filter.equals(FilmFilter.FILTER_SHOW_DATE_ALL)) {
            predicate = predicate.and(f -> checkShowDate(selectedFilter.getShowDate(), f));
        }

        return predicate;
    }

    public static boolean checkShowDate(String showDate, FilmData film) {
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
