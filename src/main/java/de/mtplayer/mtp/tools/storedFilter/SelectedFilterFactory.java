/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.tools.storedFilter;

import java.util.ArrayList;

public class SelectedFilterFactory {
    public static SelectedFilter getFilterCopy(SelectedFilter sfFrom) {
        SelectedFilter sf = new SelectedFilter();
        copyFilter(sfFrom, sf);
        return sf;
    }

    public static void copyFilter(SelectedFilter sfFrom, SelectedFilter sfTo) {
        sfTo.setName(sfFrom.getName());

        sfTo.setChannelVis(sfFrom.isChannelVis());
        sfTo.setChannelExact(sfFrom.isChannelExact());
        sfTo.setChannel(sfFrom.getChannel());
        sfTo.setThemeVis(sfFrom.isThemeVis());
        sfTo.setThemeExact(sfFrom.isThemeExact());
        sfTo.setTheme(sfFrom.getTheme());
        sfTo.setThemeTitleVis(sfFrom.isThemeTitleVis());
        sfTo.setThemeTitle(sfFrom.getThemeTitle());
        sfTo.setTitleVis(sfFrom.isTitleVis());
        sfTo.setTitle(sfFrom.getTitle());
        sfTo.setSomewhereVis(sfFrom.isSomewhereVis());
        sfTo.setSomewhere(sfFrom.getSomewhere());
        sfTo.setUrlVis(sfFrom.isUrlVis());
        sfTo.setUrl(sfFrom.getUrl());

        sfTo.setTimeRangeVis(sfFrom.isTimeRangeVis());
        sfTo.setTimeRange(sfFrom.getTimeRange());

        sfTo.setMinMaxDurVis(sfFrom.isMinMaxDurVis());
        sfTo.setMinDur(sfFrom.getMinDur());
        sfTo.setMaxDur(sfFrom.getMaxDur());

        sfTo.setMinMaxTimeVis(sfFrom.isMinMaxTimeVis());
        sfTo.setMinMaxTimeInvert(sfFrom.isMinMaxTimeInvert());
        sfTo.setMinTime(sfFrom.getMinTime());
        sfTo.setMaxTime(sfFrom.getMaxTime());

        sfTo.setOnlyVis(sfFrom.isOnlyVis());
        sfTo.setOnlyBookmark(sfFrom.isOnlyBookmark());
        sfTo.setOnlyHd(sfFrom.isOnlyHd());
        sfTo.setOnlyNew(sfFrom.isOnlyNew());
        sfTo.setOnlyUt(sfFrom.isOnlyUt());
        sfTo.setOnlyLive(sfFrom.isOnlyLive());
        sfTo.setOnlyAktHistory(sfFrom.isOnlyAktHistory());

        sfTo.setNotVis(sfFrom.isNotVis());
        sfTo.setNotAbo(sfFrom.isNotAbo());
        sfTo.setNotHistory(sfFrom.isNotHistory());
        sfTo.setNotDouble(sfFrom.isNotDouble());
        sfTo.setNotGeo(sfFrom.isNotGeo());
        sfTo.setNotFuture(sfFrom.isNotFuture());

        sfTo.setBlacklistOn(sfFrom.isBlacklistOn());
    }

    public static boolean compareFilterWithoutNameOfFilter(SelectedFilter sfFrom, SelectedFilter sfTo) {
        if (sfFrom == null || sfTo == null) {
            return false;
        }

        for (int i = 0; i < sfFrom.sfBooleanPropArr.length; i++) {
            if (sfFrom.sfBooleanPropArr[i].get() != sfTo.sfBooleanPropArr[i].get()) {
                return false;
            }
        }
        for (int i = 0; i < sfFrom.sfIntegerPropArr.length; i++) {
            if (sfFrom.sfIntegerPropArr[i].get() != sfTo.sfIntegerPropArr[i].get()) {
                return false;
            }
        }
        // nur die Filter (nicht den Namen) vergleichen
        for (int i = 1; i < sfFrom.sfStringPropArr.length; i++) {
            if (!sfFrom.sfStringPropArr[i].getValueSafe().equals(sfTo.sfStringPropArr[i].getValueSafe())) {
                return false;
            }
        }

        return true;
    }

    public static ArrayList<String> printFilter(SelectedFilter sf) {
        ArrayList<String> list = new ArrayList<>();
        list.add("getName " + sf.getName());

        list.add("");
        list.add("isChannelVis " + sf.isChannelVis());
        list.add("isChannelExact " + sf.isChannelExact());
        list.add("getChannel " + sf.getChannel());
        list.add("isThemeVis " + sf.isThemeVis());
        list.add("isThemeExact " + sf.isThemeExact());
        list.add("getTheme " + sf.getTheme());
        list.add("isThemeTitleVis " + sf.isThemeTitleVis());
        list.add("getThemeTitle " + sf.getThemeTitle());
        list.add("isTitleVis " + sf.isTitleVis());
        list.add("getTitle " + sf.getTitle());
        list.add("isSomewhereVis " + sf.isSomewhereVis());
        list.add("getSomewhere " + sf.getSomewhere());
        list.add("isUrlVis " + sf.isUrlVis());
        list.add("getUrl " + sf.getUrl());

        list.add("");
        list.add("isTimeRangeVis " + sf.isTimeRangeVis());
        list.add("getTimeRange" + sf.getTimeRange());

        list.add("");
        list.add("isMinMaxDurVis " + sf.isMinMaxDurVis());
        list.add("getMinDur " + sf.getMinDur());
        list.add("getMaxDur " + sf.getMaxDur());

        list.add("");
        list.add("isMinMaxTimeVis " + sf.isMinMaxTimeVis());
        list.add("isMinMaxTimeInvert " + sf.isMinMaxTimeInvert());
        list.add("getMinTime " + sf.getMinTime());
        list.add("getMaxTime " + sf.getMaxTime());

        list.add("");
        list.add("isOnlyVis " + sf.isOnlyVis());
        list.add("isOnlyBookmark " + sf.isOnlyBookmark());
        list.add("isOnlyHd " + sf.isOnlyHd());
        list.add("isOnlyNew " + sf.isOnlyNew());
        list.add("isOnlyUt " + sf.isOnlyUt());
        list.add("isOnlyLive " + sf.isOnlyLive());
        list.add("isOnlyAktHistory " + sf.isOnlyAktHistory());

        list.add("");
        list.add("isNotVis " + sf.isNotVis());
        list.add("isNotAbo " + sf.isNotAbo());
        list.add("isNotHistory " + sf.isNotHistory());
        list.add("isNotDouble " + sf.isNotDouble());
        list.add("isNotGeo " + sf.isNotGeo());
        list.add("isNotFuture " + sf.isNotFuture());

        list.add("");
        list.add("isBlacklistOn " + sf.isBlacklistOn());

        return list;
    }
}
