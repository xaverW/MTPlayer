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

package de.p2tools.mtplayer.controller.filmFilter;

import de.p2tools.mtplayer.controller.config.ProgConst;

public class FilmFilterToXml {

    public static final String SELECTED_FILTER_NAME = "Name";
    public static final String SELECTED_FILTER_CHANNEL_VIS = "Sender-vis";
    public static final String SELECTED_FILTER_CHANNEL = "Sender";
    public static final String SELECTED_FILTER_THEMA_VIS = "Thema-vis";
    public static final String SELECTED_FILTER_THEMA_EXACT = "Thema-exakt";
    public static final String SELECTED_FILTER_THEMA = "Thema";
    public static final String SELECTED_FILTER_THEMA_TITLE_VIS = "Thema-Titel-vis";
    public static final String SELECTED_FILTER_THEMA_TITLE = "Thema-Titel";
    public static final String SELECTED_FILTER_TITLE_VIS = "Titel-vis";
    public static final String SELECTED_FILTER_TITLE = "Titel";
    public static final String SELECTED_FILTER_SOMEWHERE_VIS = "Irgendwo-vis";
    public static final String SELECTED_FILTER_SOMEWHERE = "Irgendwo";
    public static final String SELECTED_FILTER_URL_VIS = "URL-vis";
    public static final String SELECTED_FILTER_URL = "URL";

    public static final String SELECTED_FILTER_TIME_RANGE_VIS = "Tage-vis";
    public static final String SELECTED_FILTER_TIME_RANGE = "Tage";

    public static final String SELECTED_FILTER_MIN_MAX_DUR_VIS = "Dauer-vis";
    public static final String SELECTED_FILTER_MIN_DUR = "Dauer-min";
    public static final String SELECTED_FILTER_MAX_DUR = "Dauer-max";

    public static final String SELECTED_FILTER_MIN_MAX_TIME_VIS = "Zeit-vis";
    public static final String SELECTED_FILTER_MIN_MAX_TIME_INVERT = "Zeit-on";
    public static final String SELECTED_FILTER_MIN_TIME = "Zeit-min";
    public static final String SELECTED_FILTER_MAX_TIME = "Zeit-max";

    public static final String SELECTED_FILTER_SHOW_DATE_VIS = "Sendedatum-vis";
    public static final String SELECTED_FILTER_SHOW_DATE = "Sendedatum";

    public static final String SELECTED_FILTER_ONLY_VIS = "nur-vis";
    public static final String SELECTED_FILTER_ONLY_BOOKMARK = "nur-bookmark";
    public static final String SELECTED_FILTER_ONLY_HD = "nur-hd";
    public static final String SELECTED_FILTER_ONLY_NEW = "nur-neu";
    public static final String SELECTED_FILTER_ONLY_UT = "nur-ut";
    public static final String SELECTED_FILTER_ONLY_LIVE = "nur-live";
    public static final String SELECTED_FILTER_ONLY_ACT_HISTORY = "nur-akt-gesehen";

    public static final String SELECTED_FILTER_NOT_VIS = "keine-vis";
    public static final String SELECTED_FILTER_NOT_ABO = "keine-abo";
    public static final String SELECTED_FILTER_NOT_HISTORY = "keine-gesehenen";
    public static final String SELECTED_FILTER_NOT_DOUBLE = "keine-doppelten";
    public static final String SELECTED_FILTER_NOT_GEO = "keine-geblockten";
    public static final String SELECTED_FILTER_NOT_FUTURE = "keine-zukunft";

    public static final String SELECTED_FILTER_BLACKLIST_ON = "Blacklist-ein";
    public static final String SELECTED_FILTER_BLACKLIST_ONLY = "nur-Blacklist";

    public static final int FILTER_NAME = 0;
    public static final int FILTER_SENDER_VIS = 1;
    public static final int FILTER_SENDER = 2;
    public static final int FILTER_THEME_VIS = 3;
    public static final int FILTER_THEME_EXACT = 4;
    public static final int FILTER_THEME = 5;
    public static final int FILTER_THEME_TITLE_VIS = 6;
    public static final int FILTER_THEME_TITLE = 7;
    public static final int FILTER_TITLE_VIS = 8;
    public static final int FILTER_TITLE = 9;
    public static final int FILTER_SOMEWHERE_VIS = 10;
    public static final int FILTER_SOMEWHERE = 11;

    public static final int FILTER_URL_VIS = 12;
    public static final int FILTER_URL = 13;

    public static final int FILTER_TIME_RANGE_VIS = 14;
    public static final int FILTER_TIME_RANGE = 15;

    public static final int FILTER_MIN_MAX_DUR_VIS = 16;
    public static final int FILTER_MIN_DUR = 17;
    public static final int FILTER_MAX_DUR = 18;

    public static final int FILTER_MIN_MAX_TIME_VIS = 19;
    public static final int FILTER_MIN_MAX_TIME_ON = 20;
    public static final int FILTER_MIN_TIME = 21;
    public static final int FILTER_MAX_TIME = 22;

    public static final int FILTER_SHOW_DATE_VIS = 23;
    public static final int FILTER_SHOW_DATE = 24;

    public static final int FILTER_ONLY_VIS = 25;
    public static final int FILTER_ONLY_BOOKMARK = 26;
    public static final int FILTER_ONLY_HD = 27;
    public static final int FILTER_ONLY_NEW = 28;
    public static final int FILTER_ONLY_UT = 29;
    public static final int FILTER_ONLY_LIVE = 30;
    public static final int FILTER_ONLY_ACT_HISTORY = 31;

    public static final int FILTER_NOT_VIS = 32;
    public static final int FILTER_NOT_ABO = 33;
    public static final int FILTER_NOT_HISTORY = 34;
    public static final int FILTER_NOT_DOUBLE = 35;
    public static final int FILTER_NOT_GEO = 36;
    public static final int FILTER_NOT_FUTURE = 37;

    public static final int FILTER_BLACKLIST_ON = 38;
    public static final int FILTER_BLACKLIST_ONLY = 39;

    public static final String[] XML_NAMES = {"Name",
            "Sender-vis",
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

            "Sendedatum-vis",
            "Sendedatum",

            "nur-vis",
            "nur-bookmark",
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

            "Blacklist-ein",
            "nur-Blacklist"
    };

    public static final String TAG = "Filter-Film";

    public static String[] getEmptyArray() {
        final String[] array = new String[XML_NAMES.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = "";
        }
        return array;
    }

    public static void setValueArray(FilmFilter sf, String[] array) {
        // fürs Einselesen aus dem Configfile

        sf.setName(array[FILTER_NAME]);

        sf.setChannelVis(Boolean.parseBoolean(array[FILTER_SENDER_VIS]));
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

        sf.setTimeRangeVis(Boolean.parseBoolean(array[FILTER_TIME_RANGE_VIS]));
        sf.setMinMaxDurVis(Boolean.parseBoolean(array[FILTER_MIN_MAX_DUR_VIS]));
        sf.setMinMaxTimeVis(Boolean.parseBoolean(array[FILTER_MIN_MAX_TIME_VIS]));
        sf.setMinMaxTimeInvert(Boolean.parseBoolean(array[FILTER_MIN_MAX_TIME_ON]));

        sf.setShowDateVis(Boolean.parseBoolean(array[FILTER_SHOW_DATE_VIS]));
        sf.setShowDate(array[FILTER_SHOW_DATE]);

        sf.setOnlyVis(Boolean.parseBoolean(array[FILTER_ONLY_VIS]));
        sf.setOnlyBookmark(Boolean.parseBoolean(array[FILTER_ONLY_BOOKMARK]));
        sf.setOnlyHd(Boolean.parseBoolean(array[FILTER_ONLY_HD]));
        sf.setOnlyLive(Boolean.parseBoolean(array[FILTER_ONLY_LIVE]));
        sf.setOnlyActHistory(Boolean.parseBoolean(array[FILTER_ONLY_ACT_HISTORY]));
        sf.setOnlyNew(Boolean.parseBoolean(array[FILTER_ONLY_NEW]));
        sf.setOnlyUt(Boolean.parseBoolean(array[FILTER_ONLY_UT]));

        sf.setNotVis(Boolean.parseBoolean(array[FILTER_NOT_VIS]));
        sf.setNotAbo(Boolean.parseBoolean(array[FILTER_NOT_ABO]));
        sf.setNotHistory(Boolean.parseBoolean(array[FILTER_NOT_HISTORY]));
        sf.setNotDouble(Boolean.parseBoolean(array[FILTER_NOT_DOUBLE]));
        sf.setNotGeo(Boolean.parseBoolean(array[FILTER_NOT_GEO]));
        sf.setNotFuture(Boolean.parseBoolean(array[FILTER_NOT_FUTURE]));

        sf.setBlacklistOn(Boolean.parseBoolean(array[FILTER_BLACKLIST_ON]));
        sf.setBlacklistOnly(Boolean.parseBoolean(array[FILTER_BLACKLIST_ONLY]));

        parsInt(sf, array);
    }

    private static void parsInt(FilmFilter sf, String[] array) {
        // filter days
        if (array[FILTER_TIME_RANGE].equals(ProgConst.FILTER_ALL)) {
            sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);
        } else {
            try {
                sf.setTimeRange(Integer.parseInt(array[FILTER_TIME_RANGE]));
            } catch (Exception ex) {
                sf.setTimeRange(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);
            }
        }

        // filter minDuration
        if (array[FILTER_MIN_DUR].equals(ProgConst.FILTER_ALL)) {
            sf.setMinDur(CheckFilmFilter.FILTER_DURATION_MIN_MINUTE);
        } else {
            try {
                sf.setMinDur(Integer.parseInt(array[FILTER_MIN_DUR]));
            } catch (Exception ex) {
                sf.setMinDur(CheckFilmFilter.FILTER_DURATION_MIN_MINUTE);
            }
        }

        // filter maxDuration
        if (array[FILTER_MAX_DUR].equals(ProgConst.FILTER_ALL)) {
            sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);
        } else {
            try {
                sf.setMaxDur(Integer.parseInt(array[FILTER_MAX_DUR]));
            } catch (Exception ex) {
                sf.setMaxDur(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE);
            }
        }

        // filter minTime
        if (array[FILTER_MIN_TIME].equals(ProgConst.FILTER_ALL)) {
            sf.setMinTime(CheckFilmFilter.FILTER_FILMTIME_MIN_SEC);
        } else {
            try {
                sf.setMinTime(Integer.parseInt(array[FILTER_MIN_TIME]));
            } catch (Exception ex) {
                sf.setMinTime(CheckFilmFilter.FILTER_FILMTIME_MIN_SEC);
            }
        }

        // filter maxTime
        if (array[FILTER_MAX_TIME].equals(ProgConst.FILTER_ALL)) {
            sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);
        } else {
            try {
                sf.setMaxTime(Integer.parseInt(array[FILTER_MAX_TIME]));
            } catch (Exception ex) {
                sf.setMaxTime(CheckFilmFilter.FILTER_FILMTIME_MAX_SEC);
            }
        }
    }

    public static String[] getValueArray(FilmFilter sf) {
// erstellt das Array der Filter fürs Schreiben ins Configfile
        final String[] array = getEmptyArray();

        array[FILTER_NAME] = sf.getName();

        array[FILTER_SENDER_VIS] = String.valueOf(sf.isChannelVis());
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

        array[FILTER_TIME_RANGE_VIS] = String.valueOf(sf.isTimeRangeVis());
        array[FILTER_TIME_RANGE] = sf.getTimeRange() == CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE ? ProgConst.FILTER_ALL : String.valueOf(sf.getTimeRange());

        array[FILTER_MIN_MAX_DUR_VIS] = String.valueOf(sf.isMinMaxDurVis());
        array[FILTER_MIN_DUR] = sf.getMinDur() == CheckFilmFilter.FILTER_DURATION_MIN_MINUTE ?
                ProgConst.FILTER_ALL : String.valueOf(sf.getMinDur());
        array[FILTER_MAX_DUR] = sf.getMaxDur() == CheckFilmFilter.FILTER_DURATION_MAX_MINUTE ?
                ProgConst.FILTER_ALL : String.valueOf(sf.getMaxDur());

        array[FILTER_MIN_MAX_TIME_VIS] = String.valueOf(sf.isMinMaxTimeVis());
        array[FILTER_MIN_MAX_TIME_ON] = String.valueOf(sf.isMinMaxTimeInvert());
        array[FILTER_MIN_TIME] = sf.getMinTime() == CheckFilmFilter.FILTER_FILMTIME_MIN_SEC ?
                ProgConst.FILTER_ALL : String.valueOf(sf.getMinTime());
        array[FILTER_MAX_TIME] = sf.getMaxTime() == CheckFilmFilter.FILTER_FILMTIME_MAX_SEC ?
                ProgConst.FILTER_ALL : String.valueOf(sf.getMaxTime());

        array[FILTER_SHOW_DATE_VIS] = String.valueOf(sf.isShowDateVis());
        array[FILTER_SHOW_DATE] = sf.getShowDate();

        array[FILTER_ONLY_VIS] = String.valueOf(sf.isOnlyVis());
        array[FILTER_ONLY_BOOKMARK] = String.valueOf(sf.isOnlyBookmark());
        array[FILTER_ONLY_HD] = String.valueOf(sf.isOnlyHd());
        array[FILTER_ONLY_LIVE] = String.valueOf(sf.isOnlyLive());
        array[FILTER_ONLY_ACT_HISTORY] = String.valueOf(sf.getOnlyActHistory());
        array[FILTER_ONLY_NEW] = String.valueOf(sf.isOnlyNew());
        array[FILTER_ONLY_UT] = String.valueOf(sf.isOnlyUt());

        array[FILTER_NOT_VIS] = String.valueOf(sf.isNotVis());
        array[FILTER_NOT_ABO] = String.valueOf(sf.isNotAbo());
        array[FILTER_NOT_HISTORY] = String.valueOf(sf.isNotHistory());
        array[FILTER_NOT_DOUBLE] = String.valueOf(sf.isNotDouble());
        array[FILTER_NOT_GEO] = String.valueOf(sf.isNotGeo());
        array[FILTER_NOT_FUTURE] = String.valueOf(sf.isNotFuture());

        array[FILTER_BLACKLIST_ON] = String.valueOf(sf.isBlacklistOn());
        array[FILTER_BLACKLIST_ONLY] = String.valueOf(sf.isBlacklistOnly());

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
