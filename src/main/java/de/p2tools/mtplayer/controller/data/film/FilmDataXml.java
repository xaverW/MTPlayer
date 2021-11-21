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

package de.p2tools.mtplayer.controller.data.film;

import de.p2tools.mtplayer.tools.Data;
import org.apache.commons.lang3.time.FastDateFormat;

public class FilmDataXml extends Data<FilmDataXml> {

    static final FastDateFormat sdf_date_time = FastDateFormat.getInstance("dd.MM.yyyyHH:mm:ss");
    static final FastDateFormat sdf_date = FastDateFormat.getInstance("dd.MM.yyyy");


    public static final int FILM_NR = 0;
    public static final int FILM_CHANNEL = 1;
    public static final int FILM_THEME = 2;
    public static final int FILM_TITLE = 3;
    public static final int FILM_PLAY = 4;
    public static final int FILM_RECORD = 5;
    public static final int FILM_DATE = 6;
    public static final int FILM_TIME = 7;
    public static final int FILM_DURATION = 8;
    public static final int FILM_SIZE = 9;
    public static final int FILM_HD = 10;
    public static final int FILM_UT = 11;
    public static final int FILM_DESCRIPTION = 12;
    public static final int FILM_GEO = 13;
    public static final int FILM_URL = 14;
    public static final int FILM_WEBSITE = 15;
    public static final int FILM_ABO_NAME = 16;
    public static final int FILM_URL_SUBTITLE = 17;
    public static final int FILM_URL_RTMP = 18;
    public static final int FILM_URL_AUTH = 19;
    public static final int FILM_URL_SMALL = 20;
    public static final int FILM_URL_RTMP_SMALL = 21;
    public static final int FILM_URL_HD = 22;
    public static final int FILM_URL_RTMP_HD = 23;
    public static final int FILM_URL_HISTORY = 24;
    public static final int FILM_NEW = 25;
    public static final int FILM_DATE_LONG = 26;
    public static final int MAX_ELEM = 27;
    public static final String TAG = "Filme";
    public static final String TAG_JSON_LIST = "X";
    public static final String[] COLUMN_NAMES = {"Nr",
            "Sender",
            "Thema",
            "Titel",
            "",
            "",
            "Datum",
            "Zeit",
            "Dauer [min]",
            "Größe [MB]",
            "HD",
            "UT",
            "Beschreibung",
            "Geo",
            "Url",
            "Website",
            "Abo",
            "Url Untertitel",
            "Url RTMP",
            "Url Auth",
            "Url Klein",
            "Url RTMP Klein",
            "Url HD",
            "Url RTMP HD",
            "Url History",
            "neu",
            "DatumL"};
    public static final int[] JSON_NAMES = {FILM_CHANNEL,
            FILM_THEME,
            FILM_TITLE,
            FILM_DATE,
            FILM_TIME,
            FILM_DURATION,
            FILM_SIZE,
            FILM_DESCRIPTION,
            FILM_URL,
            FILM_WEBSITE,
            FILM_URL_SUBTITLE,
            FILM_URL_RTMP,
            FILM_URL_SMALL,
            FILM_URL_RTMP_SMALL,
            FILM_URL_HD,
            FILM_URL_RTMP_HD,
            FILM_DATE_LONG,
            FILM_URL_HISTORY,
            FILM_GEO,
            FILM_NEW};
    public final String[] arr = new String[]{"",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""}; // ist einen Tick schneller, hoffentlich :)

    public FilmDataXml() {
        super();
    }

    @Override
    public int compareTo(FilmDataXml arg0) {
        int ret;
        if ((ret = sorter.compare(arr[FILM_CHANNEL], arg0.arr[FILM_CHANNEL])) == 0) {
            return sorter.compare(arr[FILM_THEME], arg0.arr[FILM_THEME]);
        }
        return ret;
    }

}
