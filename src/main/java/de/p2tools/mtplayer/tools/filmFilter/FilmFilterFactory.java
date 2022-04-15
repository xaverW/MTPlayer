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

package de.p2tools.mtplayer.tools.filmFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.film.FilmData;

public class FilmFilterFactory {

    private FilmFilterFactory() {
    }

    public static boolean aboExistsAlready(AboData aboExits, AboData checkAbo) {
        // prüfen ob "aboExistiert" das "aboPrüfen" mit abdeckt, also die gleichen (oder mehr)
        // Filme findet, dann wäre das neue Abo hinfällig

        if (!checkAboExistArr(aboExits.getChannel(), checkAbo.getChannel(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getTheme(), checkAbo.getTheme(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getTitle(), checkAbo.getTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getThemeTitle(), checkAbo.getThemeTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getSomewhere(), checkAbo.getSomewhere(), true)) {
            return false;
        }

        return true;
    }

    private static boolean checkAboExistArr(String aboExist, String aboCheck, boolean arr) {
        // da wird man immer eine Variante bauen können, die Filme eines bestehenden Abos
        // mit abdeckt -> nur eine einfache offensichtliche Prüfung

        aboCheck = aboCheck.trim();
        aboExist = aboExist.trim();

        if (aboCheck.isEmpty() && aboExist.isEmpty()) {
            return true;
        }

        if (aboCheck.toLowerCase().equals(aboExist.toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     * Abo und Blacklist prüfen
     *
     * @param sender
     * @param theme
     * @param themeTitle
     * @param title
     * @param somewhere
     * @param searchLengthMinute_min
     * @param searchLengthMinute_max
     * @param film
     * @param withLength
     * @return
     */
    public static boolean checkFilmWithFilter(Filter sender,
                                              Filter theme,
                                              Filter themeTitle,
                                              Filter title,
                                              Filter somewhere,

                                              int timeRange,
                                              int searchLengthMinute_min,
                                              int searchLengthMinute_max,

                                              FilmData film,
                                              boolean withLength) {


        // geht am schnellsten
        if (timeRange != CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE && !CheckFilmFilter.checkMaxDays(timeRange, film)) {
            return false;
        }

        if (withLength && !CheckFilmFilter.checkLength(searchLengthMinute_min, searchLengthMinute_max, film.getDurationMinute())) {
            return false;
        }

        // brauchen länger
        if (!sender.empty && !CheckFilmFilter.checkChannelSmart(sender, film)) {
            return false;
        }

        if (!theme.empty && !CheckFilmFilter.checkTheme(theme, film)) {
            return false;
        }

        if (!themeTitle.empty && !CheckFilmFilter.checkThemeTitle(themeTitle, film)) {
            return false;
        }

        if (!title.empty && !CheckFilmFilter.checkTitle(title, film)) {
            return false;
        }

        if (!somewhere.empty && !CheckFilmFilter.checkSomewhere(somewhere, film)) {
            return false;
        }

        return true;
    }

    public static FilmFilter getBookmarkFilter(FilmFilter filmFilter) {
        FilmFilter sf = filmFilter.getCopy();
        sf.clearFilter();
        sf.setOnlyVis(true);
        sf.setOnlyBookmark(true);
        return sf;
    }

    public static void addStandardFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        FilmFilter sf = new FilmFilter("alle Filme");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(false);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur neue Filme");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(true);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur Bookmarks");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(true);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(false);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("aktuelle Nachrichten");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Nachrichten");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(15);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(30);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("Nachrichten mit Europa UND Brexit");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Europa:Brexit");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(45);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur ARD ODER ZDF");

        sf.setChannelVis(true);
        sf.setChannel("ard,zdf");
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("Sport");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Sport");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("Livestreams");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(true);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("Abendkrimi");

        sf.setChannelVis(true);
        sf.setChannel("ard,zdf");
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(true);
        sf.setSomewhere("Krimi,Thriller,Tatort,Film");
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(40);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(true);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(64800);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(false);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur \"neue\" in HD");

        sf.setChannelVis(true);
        sf.setChannel("");
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setSomewhere("");
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(20);
        sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(true);
        sf.setOnlyNew(true);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(true);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.actFilmFilterWorker.getStoredFilterList().add(sf);
    }
}
