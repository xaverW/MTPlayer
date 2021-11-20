/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.download;

public class DownloadFieldNames {

    public static final String DOWNLOAD_NR = "Nr";
    public static final String DOWNLOAD_FILM_NR = "Filmnr";

    public static final String DOWNLOAD_ABO = "Abo";
    public static final String DOWNLOAD_SENDER = "Sender";
    public static final String DOWNLOAD_THEME = "Thema";
    public static final String DOWNLOAD_TITLE = "Titel";

    public static final String DOWNLOAD_BUTTON1 = "";
    public static final String DOWNLOAD_BUTTON2 = "";

    public static final String DOWNLOAD_PROGRESS = "Fortschritt";
    public static final String DOWNLOAD_REMAINING_TIME = "Restzeit";
    public static final String DOWNLOAD_BANDWIDTH = "Geschwindigkeit";
    public static final String DOWNLOAD_SIZE = "Größe [MB]";

    public static final String DOWNLOAD_DATE = "Datum";
    public static final String DOWNLOAD_TIME = "Zeit";
    public static final String DOWNLOAD_DURATION = "Dauer [min]";
    public static final String DOWNLOAD_HD = "HD";
    public static final String DOWNLOAD_UT = "UT";
    public static final String DOWNLOAD_INTERRUPTED = "Pause";
    public static final String DOWNLOAD_GEO = "Geo";

    public static final String DOWNLOAD_FILM_URL = "Url Film";
    public static final String DOWNLOAD_HISTORY_URL = "Url History";
    public static final String DOWNLOAD_URL = "Url";
    public static final String DOWNLOAD_URL_RTMP = "Url RTMP";
    public static final String DOWNLOAD_URL_SUBTITLE = "Url Untertitel";

    public static final String DOWNLOAD_SET_DATA = "Programmset";
    public static final String DOWNLOAD_PROGRAM = "Programm";
    public static final String DOWNLOAD_PROGRAM_CALL = "Programmaufruf";
    public static final String DOWNLOAD_PROGRAM_CALL_ARRAY = "Programmaufruf Array";
    public static final String DOWNLOAD_PROGRAM_RESTART = "Restart";

    public static final String DOWNLOAD_DEST_FILE_NAME = "Dateiname";
    public static final String DOWNLOAD_DEST_PATH = "Pfad";
    public static final String DOWNLOAD_DEST_PATH_FILE_NAME = "Pfad-Dateiname";

    public static final String DOWNLOAD_TYPE = "Art";
    public static final String DOWNLOAD_SOURCE = "Quelle";
    public static final String DOWNLOAD_PLACED_BACK = "Zurückgestellt";
    public static final String DOWNLOAD_INFO_FILE = "Infodatei";
    public static final String DOWNLOAD_SPOTLIGHT = "Spotlight";
    public static final String DOWNLOAD_SUBTITLE = "Untertitel";
    public static final String DOWNLOAD_PROGRAM_DOWNLOADMANAGER = "Remote Download";
    public static final String DOWNLOAD_START_TIME = "Startzeit";
    public static final String DOWNLOAD_REF = "Ref";


    public static final int DOWNLOAD_NR_NO = 0;
    public static final int DOWNLOAD_FILM_NR_NO = 1;

    public static final int DOWNLOAD_ABO_NO = 2;
    public static final int DOWNLOAD_SENDER_NO = 3;
    public static final int DOWNLOAD_THEME_NO = 4;
    public static final int DOWNLOAD_TITLE_NO = 5;

    public static final int DOWNLOAD_BUTTON1_NO = 6;
    public static final int DOWNLOAD_BUTTON2_NO = 7;

    public static final int DOWNLOAD_PROGRESS_NO = 8;
    public static final int DOWNLOAD_REMAINING_TIME_NO = 9;
    public static final int DOWNLOAD_BANDWIDTH_NO = 10;
    public static final int DOWNLOAD_SIZE_NO = 11;

    public static final int DOWNLOAD_DATE_NO = 12;
    public static final int DOWNLOAD_TIME_NO = 13;
    public static final int DOWNLOAD_DURATION_NO = 14;
    public static final int DOWNLOAD_HD_NO = 15;
    public static final int DOWNLOAD_UT_NO = 16;
    public static final int DOWNLOAD_INTERRUPTED_NO = 17;
    public static final int DOWNLOAD_GEO_NO = 18;

    public static final int DOWNLOAD_FILM_URL_NO = 19;
    public static final int DOWNLOAD_HISTORY_URL_NO = 20;
    public static final int DOWNLOAD_URL_NO = 21;
    public static final int DOWNLOAD_URL_RTMP_NO = 22;
    public static final int DOWNLOAD_URL_SUBTITLE_NO = 23;

    public static final int DOWNLOAD_SET_DATA_NO = 24;
    public static final int DOWNLOAD_PROGRAM_NO = 25;
    public static final int DOWNLOAD_PROGRAM_CALL_NO = 26;
    public static final int DOWNLOAD_PROGRAM_CALL_ARRAY_NO = 27;
    public static final int DOWNLOAD_PROGRAM_RESTART_NO = 28;

    public static final int DOWNLOAD_DEST_FILE_NAME_NO = 29;
    public static final int DOWNLOAD_DEST_PATH_NO = 30;
    public static final int DOWNLOAD_DEST_PATH_FILE_NAME_NO = 31;

    public static final int DOWNLOAD_TYPE_NO = 32;
    public static final int DOWNLOAD_SOURCE_NO = 33;
    public static final int DOWNLOAD_PLACED_BACK_NO = 34;
    public static final int DOWNLOAD_INFO_FILE_NO = 35;
    public static final int DOWNLOAD_SPOTLIGHT_NO = 36;
    public static final int DOWNLOAD_SUBTITLE_NO = 37;
    public static final int DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO = 38;
    public static final int DOWNLOAD_START_TIME_NO = 39;
    public static final int DOWNLOAD_REF_NO = 40;

    public static final String[] COLUMN_NAMES = {
            DOWNLOAD_NR,
            DOWNLOAD_FILM_NR,
            DOWNLOAD_ABO,
            DOWNLOAD_SENDER,
            DOWNLOAD_THEME,
            DOWNLOAD_TITLE,

            DOWNLOAD_BUTTON1,
            DOWNLOAD_BUTTON2,

            DOWNLOAD_PROGRESS,
            DOWNLOAD_REMAINING_TIME,
            DOWNLOAD_BANDWIDTH,
            DOWNLOAD_SIZE,
            DOWNLOAD_DATE,
            DOWNLOAD_TIME,
            DOWNLOAD_DURATION,
            DOWNLOAD_HD,
            DOWNLOAD_UT,
            DOWNLOAD_INTERRUPTED,
            DOWNLOAD_GEO,
            DOWNLOAD_FILM_URL,
            DOWNLOAD_HISTORY_URL,
            DOWNLOAD_URL,
            DOWNLOAD_URL_RTMP,
            DOWNLOAD_URL_SUBTITLE,
            DOWNLOAD_SET_DATA,
            DOWNLOAD_PROGRAM,
            DOWNLOAD_PROGRAM_CALL,
            DOWNLOAD_PROGRAM_CALL_ARRAY,
            DOWNLOAD_PROGRAM_RESTART,

            DOWNLOAD_DEST_FILE_NAME,
            DOWNLOAD_DEST_PATH,
            DOWNLOAD_DEST_PATH_FILE_NAME,

            DOWNLOAD_TYPE,
            DOWNLOAD_SOURCE,
            DOWNLOAD_PLACED_BACK,
            DOWNLOAD_INFO_FILE,
            DOWNLOAD_SPOTLIGHT,
            DOWNLOAD_SUBTITLE,
            DOWNLOAD_PROGRAM_DOWNLOADMANAGER,
            DOWNLOAD_START_TIME,
            DOWNLOAD_REF
    };

    public static final String[] XML_NAMES = {"Nr",
            "Filmnr",
            "Abo",
            "Sender",
            "Thema",
            "Titel",

            "Button-Start",
            "Button-Del",

            "Fortschritt",
            "Restzeit",
            "Geschwindigkeit",

            "Groesse"/* DOWNLOAD_GROESSE */,
            "Datum",
            "Zeit",
            "Dauer",
            "HD",
            "UT",
            "Pause",
            "Geo",
            "Film-URL",
            "History-URL",
            "URL",
            "URL-rtmp",
            "URL-Untertitel",
            "Programmset",
            "Programm",
            "Programmaufruf_",
            "Programmaufruf",
            "Restart",
            "Dateiname",
            "Pfad",
            "Pfad-Dateiname",
            "Art",
            "Quelle",
            "Zurueckgestellt",
            "Infodatei",
            "Spotlight",
            "Untertitel",
            "Remote-Download",
            "Startzeit",
            "Ref"};

    public static int MAX_ELEM = XML_NAMES.length;
}
