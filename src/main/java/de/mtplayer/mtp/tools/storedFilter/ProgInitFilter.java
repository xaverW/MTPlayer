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

package de.mtplayer.mtp.tools.storedFilter;

import de.mtplayer.mtp.controller.config.ProgData;

public class ProgInitFilter {

    public static void setProgInitFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        SelectedFilter sf = new SelectedFilter();
        sf.setName("alle Filme");

        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(false);
        sf.setNotFuture(false);

        sf.initFilter();
        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("nur neue Filme");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(10);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(true);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("nur Bookmarks");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(true);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(false);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("aktuelle Nachrichten");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Nachrichten");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(15);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(30);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("Nachrichten mit Europa UND Brexit");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Europa:Brexit");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(0);
        sf.setMaxDur(45);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("nur ARD ODER ZDF");
        sf.setChannelVis(true);
        sf.setChannelExact(false);
        sf.setChannel("ard,zdf");

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("Sport");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Sport");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("Livestreams");
        sf.setChannelVis(true);
        sf.setChannelExact(true);

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

        sf.setMinMaxDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(true);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("Abendkrimi");
        sf.setChannelVis(true);
        sf.setChannelExact(false);
        sf.setChannel("ard,zdf");

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);

        sf.setSomewhereVis(true);
        sf.setSomewhere("Krimi,Thriller,Tatort,Film");
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(10);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(40);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(true);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(64800);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(false);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


        //========================================================
        sf = new SelectedFilter();
        sf.setName("nur \"neue\" in HD");
        sf.setChannelVis(true);
        sf.setChannelExact(true);
        sf.setChannel("");

        sf.setThemeVis(false);
        sf.setThemeExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setSomewhere("");
        sf.setUrlVis(false);

        sf.setDaysVis(true);
        sf.setDays(10);

        sf.setMinMaxDurVis(true);
        sf.setMinDur(20);
        sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MINUTE);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(0);
        sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(true);
        sf.setOnlyNew(true);
        sf.setOnlyUt(false);
        sf.setOnlyLive(false);
        sf.setOnlyAktHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(true);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.storedFilters.getStordeFilterList().add(sf);


    }

}
