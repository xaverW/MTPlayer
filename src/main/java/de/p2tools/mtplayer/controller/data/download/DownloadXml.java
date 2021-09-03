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

package de.p2tools.mtplayer.controller.data.download;


import de.p2tools.mtplayer.tools.Data;

public class DownloadXml extends Data<Download> {


    public static final int DOWNLOAD_NR = 0;
    public static final int DOWNLOAD_FILM_NR = 1;

    public static final int DOWNLOAD_ABO = 2;
    public static final int DOWNLOAD_SENDER = 3;
    public static final int DOWNLOAD_THEME = 4;
    public static final int DOWNLOAD_TITLE = 5;

    public static final int DOWNLOAD_BUTTON1 = 6;
    public static final int DOWNLOAD_BUTTON2 = 7;

    public static final int DOWNLOAD_PROGRESS = 8;
    public static final int DOWNLOAD_REMAINING_TIME = 9;
    public static final int DOWNLOAD_BANDWIDTH = 10;
    public static final int DOWNLOAD_SIZE = 11;

    public static final int DOWNLOAD_DATE = 12;
    public static final int DOWNLOAD_TIME = 13;
    public static final int DOWNLOAD_DURATION = 14;
    public static final int DOWNLOAD_HD = 15;
    public static final int DOWNLOAD_UT = 16;
    public static final int DOWNLOAD_INTERRUPTED = 17;
    public static final int DOWNLOAD_GEO = 18;

    public static final int DOWNLOAD_FILM_URL = 19;
    public static final int DOWNLOAD_HISTORY_URL = 20;
    public static final int DOWNLOAD_URL = 21;
    public static final int DOWNLOAD_URL_RTMP = 22;
    public static final int DOWNLOAD_URL_SUBTITLE = 23;

    public static final int DOWNLOAD_SET_DATA = 24;
    public static final int DOWNLOAD_PROGRAM = 25;
    public static final int DOWNLOAD_PROGRAM_CALL = 26;
    public static final int DOWNLOAD_PROGRAM_CALL_ARRAY = 27;
    public static final int DOWNLOAD_PROGRAM_RESTART = 28;

    public static final int DOWNLOAD_DEST_FILE_NAME = 29;
    public static final int DOWNLOAD_DEST_PATH = 30;
    public static final int DOWNLOAD_DEST_PATH_FILE_NAME = 31;

    public static final int DOWNLOAD_TYPE = 32;
    public static final int DOWNLOAD_SOURCE = 33;
    public static final int DOWNLOAD_PLACED_BACK = 34;
    public static final int DOWNLOAD_INFO_FILE = 35;
    public static final int DOWNLOAD_SPOTLIGHT = 36;
    public static final int DOWNLOAD_SUBTITLE = 37;
    public static final int DOWNLOAD_PROGRAM_DOWNLOADMANAGER = 38;
    public static final int DOWNLOAD_START_TIME = 39;
    public static final int DOWNLOAD_REF = 40;
    public static final String[] COLUMN_NAMES = {"Nr",
            "Filmnr",
            "Abo",
            "Sender",
            "Thema",
            "Titel",
            "",
            "",
            "Fortschritt",
            "Restzeit",
            "Geschwindigkeit",
            "Größe [MB]",
            "Datum",
            "Zeit",
            "Dauer [min]",
            "HD",
            "UT",
            "Pause",
            "Geo",
            "Url Film",
            "Url History",
            "Url",
            "Url RTMP",
            "Url Untertitel",
            "Programmset",
            "Programm",
            "Programmaufruf",
            "Programmaufruf Array",
            "Restart",
            "Dateiname",
            "Pfad",
            "Pfad-Dateiname",
            "Art",
            "Quelle",
            "Zurückgestellt",
            "Infodatei",
            "Spotlight",
            "Untertitel",
            "Remote Download",
            "Startzeit",
            "Ref"};
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
    public static final String TAG = "Downlad";
    public static int MAX_ELEM = XML_NAMES.length;

    public DownloadXml() {
        arr = makeArr(MAX_ELEM);
    }

    @Override
    public int compareTo(Download arg0) {
        int ret;
        if ((ret = sorter.compare(arr[DownloadXml.DOWNLOAD_SENDER], arg0.arr[DownloadXml.DOWNLOAD_SENDER])) == 0) {
            return sorter.compare(arr[DownloadXml.DOWNLOAD_THEME], arg0.arr[DownloadXml.DOWNLOAD_THEME]);
        }
        return ret;
    }
}
