/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mLib.tools.Data;
import org.apache.commons.lang3.time.FastDateFormat;

public class FilmXml extends Data<FilmXml> {

    static final FastDateFormat sdf_datum_zeit = FastDateFormat.getInstance("dd.MM.yyyyHH:mm:ss");
    static final FastDateFormat sdf_datum = FastDateFormat.getInstance("dd.MM.yyyy");

    public static final String AUFLOESUNG_NORMAL = "normal";
    public static final String AUFLOESUNG_HD = "hd";
    public static final String AUFLOESUNG_KLEIN = "klein";

    public static final int FILMTIME_EMPTY = -1;

    public static final String GEO_DE = "DE";
    public static final String GEO_AT = "AT";
    public static final String GEO_CH = "CH";
    public static final String GEO_EU = "EU";
    public static final String GEO_WELT = "WELT";


    public static final int FILM_NR = 0;
    public static final int FILM_SENDER = 1;
    public static final int FILM_THEMA = 2;
    public static final int FILM_TITEL = 3;
    public static final int FILM_ABSPIELEN = 4;
    public static final int FILM_AUFZEICHNEN = 5;
    public static final int FILM_DATUM = 6;
    public static final int FILM_ZEIT = 7;
    public static final int FILM_DAUER = 8;
    public static final int FILM_GROESSE = 9;
    public static final int FILM_HD = 10;
    public static final int FILM_UT = 11;
    public static final int FILM_BESCHREIBUNG = 12;
    public static final int FILM_GEO = 13;
    public static final int FILM_URL = 14;
    public static final int FILM_WEBSEITE = 15;
    public static final int FILM_ABO_NAME = 16;
    public static final int FILM_URL_SUBTITLE = 17;
    public static final int FILM_URL_RTMP = 18;
    public static final int FILM_URL_AUTH = 19;
    public static final int FILM_URL_KLEIN = 20;
    public static final int FILM_URL_RTMP_KLEIN = 21;
    public static final int FILM_URL_HD = 22;
    public static final int FILM_URL_RTMP_HD = 23;
    public static final int FILM_URL_HISTORY = 24;
    public static final int FILM_NEU = 25;
    public static final int FILM_DATUM_LONG = 26;
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
            "Dauer",
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
    public static final int[] JSON_NAMES = {FILM_SENDER,
            FILM_THEMA,
            FILM_TITEL,
            FILM_DATUM,
            FILM_ZEIT,
            FILM_DAUER,
            FILM_GROESSE,
            FILM_BESCHREIBUNG,
            FILM_URL,
            FILM_WEBSEITE,
            FILM_URL_SUBTITLE,
            FILM_URL_RTMP,
            FILM_URL_KLEIN,
            FILM_URL_RTMP_KLEIN,
            FILM_URL_HD,
            FILM_URL_RTMP_HD,
            FILM_DATUM_LONG,
            FILM_URL_HISTORY,
            FILM_GEO,
            FILM_NEU};
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

    public FilmXml() {
        super();
    }

    @Override
    public int compareTo(FilmXml arg0) {
        int ret;
        if ((ret = sorter.compare(arr[FILM_SENDER], arg0.arr[FILM_SENDER])) == 0) {
            return sorter.compare(arr[FILM_THEMA], arg0.arr[FILM_THEMA]);
        }
        return ret;
    }

}
