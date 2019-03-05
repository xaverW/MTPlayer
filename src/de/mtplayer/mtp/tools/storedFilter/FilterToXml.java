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

public class FilterToXml {


    public static final int FILTER_NAME = 0;

    public static final int FILTER_SENDER_VIS = 1;
    public static final int FILTER_SENDER_EXACT = 2;
    public static final int FILTER_SENDER = 3;
    public static final int FILTER_THEME_VIS = 4;
    public static final int FILTER_THEME_EXACT = 5;
    public static final int FILTER_THEME = 6;
    public static final int FILTER_THEME_TITLE_VIS = 7;
    public static final int FILTER_THEME_TITLE = 8;
    public static final int FILTER_TITLE_VIS = 9;
    public static final int FILTER_TITLE = 10;
    public static final int FILTER_SOMEWHERE_VIS = 11;
    public static final int FILTER_SOMEWHERE = 12;

    public static final int FILTER_URL_VIS = 13;
    public static final int FILTER_URL = 14;

    public static final int FILTER_DAYS_VIS = 15;
    public static final int FILTER_DAYS = 16;

    public static final int FILTER_MIN_MAX_VIS = 17;
    public static final int FILTER_MIN_DUR = 18;
    public static final int FILTER_MAX_DUR = 19;

    public static final int FILTER_MIN_MAX_TIME_VIS = 20;
    public static final int FILTER_MIN_MAX_TIME_ON = 21;
    public static final int FILTER_MIN_TIME = 22;
    public static final int FILTER_MAX_TIME = 23;

    public static final int FILTER_ONLY_VIS = 24;
    public static final int FILTER_ONLY_BOOKMARK = 25;
    public static final int FILTER_ONLY_HD = 26;
    public static final int FILTER_ONLY_NEW = 27;
    public static final int FILTER_ONLY_UT = 28;
    public static final int FILTER_ONLY_LIVE = 29;
    public static final int FILTER_ONLY_ACT_HISTORY = 30;

    public static final int FILTER_NOT_VIS = 31;
    public static final int FILTER_NOT_ABO = 32;
    public static final int FILTER_NOT_HISTORY = 33;
    public static final int FILTER_NOT_DOUBLE = 34;
    public static final int FILTER_NOT_GEO = 35;
    public static final int FILTER_NOT_FUTURE = 36;

    public static final int FILTER_BLACKLIST_ON = 37;

    public static final String[] XML_NAMES = {"Name",
            "Sender-vis",
            "Sender-exakt",
            "Sender",
            "Thema-vis",
            "Thema-exakt",
            "Thema",
            "Thema-Titel-vis",
            "Thema-Titel",
            "Titel-vis",
            "Titel",
            "Irgendwo-vis",
            "Irgendwo",
            "URL-vis",
            "URL",

            "Tage-vis",
            "Tage",

            "Dauer-vis",
            "Dauer-min",
            "Dauer-max",

            "Zeit-vis",
            "Zeit-on",
            "Zeit-min",
            "Zeit-max",

            "nur-vis",
            "nur-markiert",
            "nur-hd",
            "nur-neu",
            "nur-ut",
            "nur-live",
            "nur-akt-gesehen",

            "keine-vis",
            "keine-abo",
            "keine-gesehenen",
            "keine-doppelten",
            "keine-geblockten",
            "keine-zukunft",
            "Blacklist-ein"};

    public static final String TAG = "Filter-Film";

    public static String[] getEmptyArray() {
        final String[] array = new String[XML_NAMES.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = "";
        }
        return array;
    }

    public static void setValueArray(SelectedFilter sf, String[] array) {
        // fürs Einselesen aus dem Configfile

        sf.setName(array[FILTER_NAME]);

        sf.setChannelVis(Boolean.parseBoolean(array[FILTER_SENDER_VIS]));
        sf.setChannelExact(Boolean.parseBoolean(array[FILTER_SENDER_EXACT]));
        sf.setChannel(array[FILTER_SENDER]);
        sf.setThemeVis(Boolean.parseBoolean(array[FILTER_THEME_VIS]));
        sf.setThemeExact(Boolean.parseBoolean(array[FILTER_THEME_EXACT]));
        sf.setTheme(array[FILTER_THEME]);
        sf.setThemeTitleVis(Boolean.parseBoolean(array[FILTER_THEME_TITLE_VIS]));
        sf.setThemeTitle(array[FILTER_THEME_TITLE]);
        sf.setTitleVis(Boolean.parseBoolean(array[FILTER_TITLE_VIS]));
        sf.setTitle(array[FILTER_TITLE]);
        sf.setSomewhereVis(Boolean.parseBoolean(array[FILTER_SOMEWHERE_VIS]));
        sf.setSomewhere(array[FILTER_SOMEWHERE]);
        sf.setUrlVis(Boolean.parseBoolean(array[FILTER_URL_VIS]));
        sf.setUrl(array[FILTER_URL]);

        sf.setDaysVis(Boolean.parseBoolean(array[FILTER_DAYS_VIS]));

        sf.setMinMaxDurVis(Boolean.parseBoolean(array[FILTER_MIN_MAX_VIS]));

        sf.setMinMaxTimeVis(Boolean.parseBoolean(array[FILTER_MIN_MAX_TIME_VIS]));
        sf.setMinMaxTimeInvert(Boolean.parseBoolean(array[FILTER_MIN_MAX_TIME_ON]));

        sf.setOnlyVis(Boolean.parseBoolean(array[FILTER_ONLY_VIS]));
        sf.setOnlyBookmark(Boolean.parseBoolean(array[FILTER_ONLY_BOOKMARK]));
        sf.setOnlyHd(Boolean.parseBoolean(array[FILTER_ONLY_HD]));
        sf.setOnlyLive(Boolean.parseBoolean(array[FILTER_ONLY_LIVE]));
        sf.setOnlyAktHistory(Boolean.parseBoolean(array[FILTER_ONLY_ACT_HISTORY]));
        sf.setOnlyNew(Boolean.parseBoolean(array[FILTER_ONLY_NEW]));
        sf.setOnlyUt(Boolean.parseBoolean(array[FILTER_ONLY_UT]));

        sf.setNotVis(Boolean.parseBoolean(array[FILTER_NOT_VIS]));
        sf.setNotAbo(Boolean.parseBoolean(array[FILTER_NOT_ABO]));
        sf.setNotHistory(Boolean.parseBoolean(array[FILTER_NOT_HISTORY]));
        sf.setNotDouble(Boolean.parseBoolean(array[FILTER_NOT_DOUBLE]));
        sf.setNotGeo(Boolean.parseBoolean(array[FILTER_NOT_GEO]));
        sf.setNotFuture(Boolean.parseBoolean(array[FILTER_NOT_FUTURE]));

        sf.setBlacklistOn(Boolean.parseBoolean(array[FILTER_BLACKLIST_ON]));

        parsInt(sf, array);
    }

    private static void parsInt(SelectedFilter sf, String[] array) {
        try {
            sf.setDays(Integer.parseInt(array[FILTER_DAYS]));

            sf.setMinDur(Integer.parseInt(array[FILTER_MIN_DUR]));
            sf.setMaxDur(Integer.parseInt(array[FILTER_MAX_DUR]));

            sf.setMinTime(Integer.parseInt(array[FILTER_MIN_TIME]));
            sf.setMaxTime(Integer.parseInt(array[FILTER_MAX_TIME]));
        } catch (Exception ex) {
            sf.setDays(SelectedFilter.FILTER_DAYS_MAX);

            sf.setMinDur(0);
            sf.setMaxDur(SelectedFilter.FILTER_DURATION_MAX_MIN);

            sf.setMinTime(0);
            sf.setMaxTime(SelectedFilter.FILTER_FILMTIME_MAX_SEC);
        }
    }

    public static String[] getValueArray(SelectedFilter sf) {
        // erstellt das Array der Filter fürs Schreiben ins Configfile
        final String[] array = getEmptyArray();

        array[FILTER_NAME] = sf.getName();

        array[FILTER_SENDER_VIS] = String.valueOf(sf.isChannelVis());
        array[FILTER_SENDER_EXACT] = String.valueOf(sf.isChannelExact());
        array[FILTER_SENDER] = sf.getChannel();
        array[FILTER_THEME_VIS] = String.valueOf(sf.isThemeVis());
        array[FILTER_THEME_EXACT] = String.valueOf(sf.isThemeExact());
        array[FILTER_THEME] = sf.getTheme();
        array[FILTER_THEME_TITLE_VIS] = String.valueOf(sf.isThemeTitleVis());
        array[FILTER_THEME_TITLE] = sf.getThemeTitle();
        array[FILTER_TITLE_VIS] = String.valueOf(sf.isTitleVis());
        array[FILTER_TITLE] = sf.getTitle();
        array[FILTER_SOMEWHERE_VIS] = String.valueOf(sf.isSomewhereVis());
        array[FILTER_SOMEWHERE] = sf.getSomewhere();
        array[FILTER_URL_VIS] = String.valueOf(sf.isUrlVis());
        array[FILTER_URL] = sf.getUrl();

        array[FILTER_DAYS_VIS] = String.valueOf(sf.isDaysVis());
        array[FILTER_DAYS] = String.valueOf(sf.getDays());

        array[FILTER_MIN_MAX_VIS] = String.valueOf(sf.isMinMaxDurVis());
        array[FILTER_MIN_DUR] = String.valueOf(sf.getMinDur());
        array[FILTER_MAX_DUR] = String.valueOf(sf.getMaxDur());

        array[FILTER_MIN_MAX_TIME_VIS] = String.valueOf(sf.isMinMaxTimeVis());
        array[FILTER_MIN_MAX_TIME_ON] = String.valueOf(sf.isMinMaxTimeInvert());
        array[FILTER_MIN_TIME] = String.valueOf(sf.getMinTime());
        array[FILTER_MAX_TIME] = String.valueOf(sf.getMaxTime());

        array[FILTER_ONLY_VIS] = String.valueOf(sf.isOnlyVis());
        array[FILTER_ONLY_BOOKMARK] = String.valueOf(sf.isOnlyBookmark());
        array[FILTER_ONLY_HD] = String.valueOf(sf.isOnlyHd());
        array[FILTER_ONLY_LIVE] = String.valueOf(sf.isOnlyLive());
        array[FILTER_ONLY_ACT_HISTORY] = String.valueOf(sf.isOnlyAktHistory());
        array[FILTER_ONLY_NEW] = String.valueOf(sf.isOnlyNew());
        array[FILTER_ONLY_UT] = String.valueOf(sf.isOnlyUt());

        array[FILTER_NOT_VIS] = String.valueOf(sf.isNotVis());
        array[FILTER_NOT_ABO] = String.valueOf(sf.isNotAbo());
        array[FILTER_NOT_HISTORY] = String.valueOf(sf.isNotHistory());
        array[FILTER_NOT_DOUBLE] = String.valueOf(sf.isNotDouble());
        array[FILTER_NOT_GEO] = String.valueOf(sf.isNotGeo());
        array[FILTER_NOT_FUTURE] = String.valueOf(sf.isNotFuture());

        array[FILTER_BLACKLIST_ON] = String.valueOf(sf.isBlacklistOn());

        return array;
    }

    public static String[] getXmlArray() {
        // erstellt die XML-Namen fürs Lesen/Schreiben aus/ins Configfile
        final String[] array = getEmptyArray();
        for (int i = 0; i < XML_NAMES.length; ++i) {
            array[i] = XML_NAMES[i];
        }
        return array;
    }

}
