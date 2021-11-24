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

package de.p2tools.mtplayer.tools.storedFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;

public class ProgInitFilter {

    public static void setProgInitFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        SelectedFilter sf = new SelectedFilter("alle Filme");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("nur neue Filme");

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
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("nur Bookmarks");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("aktuelle Nachrichten");

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
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("Nachrichten mit Europa UND Brexit");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Europa:Brexit");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(45);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("nur ARD ODER ZDF");

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
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("Sport");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Sport");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("Livestreams");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("Abendkrimi");

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
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(true);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(64800);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);

        //========================================================
        sf = new SelectedFilter("nur \"neue\" in HD");

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
        sf.setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

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

        progData.storedFilters.getStoredFilterList().add(sf);
    }
}
