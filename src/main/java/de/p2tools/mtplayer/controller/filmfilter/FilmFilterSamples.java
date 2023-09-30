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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mtfilter.FilterCheck;

public class FilmFilterSamples {

    private FilmFilterSamples() {
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
        FilmFilter sf = new FilmFilter("alles anzeigen");

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur neue Fernsehfilme");

        sf.setChannelVis(true);
        sf.setThemeVis(true);
        sf.setThemeExact(false);
        sf.setTheme("#:^(Der |ZDF-)?Fernsehfilm( der Woche|e (&|und) Serien( - Fernsehfilme)?)?$|^Film( \\& Fiktion|e, Dokus(,| &) Kabarett|e? im (Ersten|MDR|NDR|rbb)|Mittwoch im Ersten| und Serie)$|^Kino - (Filme|Stummfilme)$|^(3sat |CH:|Schweizer |Spiel)?Film(kultur|szene)?$");
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(40);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);
        
        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(45);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(true);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(64800);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

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
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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

        progData.filmFilterWorker.getStoredFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("keine Beitrage mit: \"Audiodeskription\" und \"Gebärdensprache\" anzeigen");

        sf.setChannelVis(true);
        sf.setChannel("");
        sf.setThemeVis(false);
        sf.setThemeExact(true);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(true);
        sf.setTitle("#:(?!.*Audiodeskription)(?!.*Gebärdensprache).*");
        sf.setSomewhereVis(false);
        sf.setSomewhere("");
        sf.setUrlVis(false);

        sf.setTimeRangeVis(false);
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(FilterCheck.FILTER_ALL_OR_MIN);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(FilterCheck.FILTER_ALL_OR_MIN);
        sf.setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

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
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.filmFilterWorker.getStoredFilterList().add(sf);
    }
}
