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

package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2lib.mediathek.filter.Filter;

public class ProgConst {

    public static final String PROGRAM_NAME = "MTPlayer";

    // <td><a href="/download/mtplayer/act/MTPlayer-17__2024.06.01.zip"> MTPlayer-17__2024.06.01.zip</a></td>
    // <td><a href="/download/mtplayer/daily/MTPlayer-17-15__2024.12.09.zip"> MTPlayer-17-15__2024.12.09.zip</a></td>
    // ACT:  MTPlayer-17__2024.06.01.zip
    // BETA: MTPlayer-17-15__2024.12.09.zip
    // ACT:  MTPlayer-18__Raspberry__2025.02.18
    // BETA: MTPlayer-18-2__Raspberry__2025.02.18
    public static final String FILE_NAME_MTPLAYER = "MTPlayer";

    public static final String USER_AGENT_DEFAULT = "";
    public static final int MAX_USER_AGENT_SIZE = 100;

    // settings file
    public static final String CONFIG_FILE = "mtp.xml";
    public static final String STYLE_FILE = "style.css";
    public static final String CONFIG_DIRECTORY = "p2Mtplayer"; // im Homeverzeichnis

    public static final String FILE_BOOKMARKS_TXT = "bookmarks.txt";
    public static final String FILE_BOOKMARKS_XML = "bookmarks.xml";
    public static final String FILE_FINISHED_ABOS = "downloads.txt";
    public static final String FILE_HISTORY = "history.txt";
    public static final String FILE_MEDIA_DB = "mediadb.txt";
    public static final String LOG_DIR = "Log";
    public static final String CSS_FILE = "de/p2tools/mtplayer/mtfx.css";
    public static final String CSS_FILE_DARK_THEME = "de/p2tools/mtplayer/mtfx-dark.css";

    public static final String PROGRAM_ICON = "de/p2tools/mtplayer/res/p2_logo_32.png";
//    public static final String MTPLAYER_DOWNLOAD_URL = "https://www.p2tools.de/download/";
//    public static final String RASPBERRY_DOWNLOAD_URL = "https://www.p2tools.de/download/";


    public static final int SEARCH_FAST_THEME_TITLE = 0;
    public static final int SEARCH_FAST_THEME = 1;
    public static final int SEARCH_FAST_TITLE = 2;

    public static final String M3U8_URL = "m3u8";
    public static final String REG_EX = Filter.FILTER_REG_EX;

    public static final int SYSTEM_BLACKLIST_MAX_FILM_DAYS = 300; // Filter Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_BLACKLIST_MIN_FILM_DURATION = 100; // Filter Blacklist: nur Filme mit mind. xx Minuten länge laden

    public static final int SYSTEM_LOAD_FILMLIST_MAX_DAYS = 300; // Filter beim Programmstart/Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_LOAD_FILMLIST_MIN_DURATION = 30; // Filter Programmstart: nur Filme mit mind. xx Minuten länge laden
    public static final int SYSTEM_FILTER_MAX_WAIT_TIME = 2_000; // 1.000 ms

    //Mediensammlung
    public static final int MEDIA_COLLECTION_FILESIZE_ALL_FILES = 0;
    public static final int MEDIA_COLLECTION_FILESIZE_MAX = 20;

    public final static int MEDIA_SEARCH_THEME_OR_PATH = 0;
    public final static int MEDIA_SEARCH_TITEL_OR_NAME = 1;
    public final static int MEDIA_SEARCH_TT_OR_PN = 2;

    public static final String MEDIA_COLLECTION_EXPORT_FILE_NAME = "MediaDB.txt"; // der erste vorgegebene Name für den Export
    public static final int MEDIA_COLLECTION_EXPORT_INTERN = 0; // nur interne exportieren
    public static final int MEDIA_COLLECTION_EXPORT_EXTERN = 1; // nur externe exportieren
    public static final int MEDIA_COLLECTION_EXPORT_INTERN_EXTERN = 2; // alles exportieren


    // Website MTPlayer
    public static final String URL_WEBSITE = "https://www.p2tools.de/mtplayer/";
    public static final String URL_WEBSITE_DOWNLOAD_MTPLAYER = "https://www.p2tools.de/mtplayer/download/";
    public static final String URL_WEBSITE_HELP = "https://www.p2tools.de/mtplayer/manual/";

    // die URL der Sets, nicht mehr mit jeder Programmversion hochzählen, nur noch wenn notwendig
    public static final String PROGRAM_SET_URL_LINUX = "https://www.p2tools.de/extra/mtplayer/v-8/pset_linux.xml";
    public static final String PROGRAM_SET_URL_WINDOWS = "https://www.p2tools.de/extra/mtplayer/v-8/pset_windows.xml";
    public static final String PROGRAM_SET_URL_MAC = "https://www.p2tools.de/extra/mtplayer/v-8/pset_mac.xml";

    //todo: beim Umstellen auf MOD wieder prüfen
    public static final String PSET_FILE_LINUX = "de/p2tools/mtplayer/res/file/pset_linux.xml";
    public static final String PSET_FILE_WINDOWS = "de/p2tools/mtplayer/res/file/pset_windows.xml";
    public static final String PSET_FILE_MAC = "de/p2tools/mtplayer/res/file/pset_mac.xml";

    // ProgrammUrls
    public static final String ADRESSE_WEBSITE_VLC = "https://www.videolan.org";
    public static final String ADRESSE_WEBSITE_FFMPEG = "https://ffmpeg.org";

//    public static final String FILE_PROG_ICON = "/de/p2tools/mtplayer/res/P2.png";

    // Dateien/Verzeichnisse
    public static final String JSON_DATEI_FILME = "filme.json";

    // minimale Größe (256 kB) eines Films um nicht als Fehler zu gelten
    public static final int MIN_DATEI_GROESSE_FILM = 256 * 1000;

    public static final int MAX_DEST_PATH_IN_DIALOG_DOWNLOAD = 10;
    public static int DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE = 15;

    public static final double GUI_FILTER_DIVIDER_LOCATION = 0.3;
    public static final double GUI_INFO_DIVIDER_LOCATION = 0.7;
    public static final double GUI_DIVIDER_LOCATION = 0.7;

    public static final double CONFIG_DIALOG_SET_DIVIDER = 0.2;

    public static final int LAENGE_DATEINAME_MAX = 200; // Standardwert für die Länge des Zieldateinamens
    public static final int LAENGE_FELD_MAX = 100; // Standardwert für die Länge des Feldes des

    public static final int MIN_TABLE_HEIGHT = 200;
    public static final int MIN_TABLE_HEIGHT_LOW = 100;
    public static final int MIN_TEXTAREA_HEIGHT_LOW = 50;

    public static final int MAX_FILTER_GO_BACK = 20;

    public static final String THEME_LIVE = "Livestream";
}
