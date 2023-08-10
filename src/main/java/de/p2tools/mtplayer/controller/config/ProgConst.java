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

public class ProgConst {

    public static final String PROGRAM_NAME = "MTPlayer";
    public static final String USER_AGENT_DEFAULT = "";
    public static final int MAX_USER_AGENT_SIZE = 100;

    // settings file
    public static final String CONFIG_FILE = "mtp.xml";
    public static final String STYLE_FILE = "style.css";
    public static final String CONFIG_FILE_COPY = "mtp.xml_copy_";
    public static final String CONFIG_DIRECTORY = "p2Mtplayer"; // im Homeverzeichnis

    public static final String FILE_BOOKMARKS = "bookmarks.txt";
    public static final String FILE_FINISHED_ABOS = "downloads.txt";
    public static final String FILE_HISTORY = "history.txt";
    public static final String FILE_MEDIA_DB = "mediadb.txt";
    public static final String LOG_DIR = "Log";
    public static final String CSS_FILE = "de/p2tools/mtplayer/mtfx.css";
    public static final String CSS_FILE_DARK_THEME = "de/p2tools/mtplayer/mtfx-dark.css";

    public static final int SEARCH_FAST_THEME_TITLE = 0;
    public static final int SEARCH_FAST_THEME = 1;
    public static final int SEARCH_FAST_TITLE = 2;

    public static final String M3U8_URL = "m3u8";

    public static final int SYSTEM_BLACKLIST_MAX_FILM_DAYS = 300; // Filter Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_BLACKLIST_MIN_FILM_DURATION = 100; // Filter Blacklist: nur Filme mit mind. xx Minuten länge laden

    public static final int SYSTEM_LOAD_FILMLIST_MAX_DAYS = 300; // Filter beim Programmstart/Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_LOAD_FILMLIST_MIN_DURATION = 30; // Filter Programmstart: nur Filme mit mind. xx Minuten länge laden
    public static final int SYSTEM_FILTER_MAX_WAIT_TIME = 2_000; // 1.000 ms

    public static final int DOWNLOAD_DIALOG_LOAD_MAX_FILESIZE_FROM_WEB = 10;

    //Mediensammlung
    public static final int MEDIA_COLLECTION_FILESIZE_ALL_FILES = 0;
    public static final int MEDIA_COLLECTION_FILESIZE_MAX = 20;

    public final static int MEDIA_COLLECTION_SEARCH_THEME = 0;
    public final static int MEDIA_COLLECTION_SEARCH_TITEL = 1;
    public final static int MEDIA_COLLECTION_SEARCH_TT = 2;

    public static final String MEDIA_COLLECTION_EXPORT_FILE_NAME = "MediaDB.txt"; // der erste vorgegebene Name für den Export
    public static final int MEDIA_COLLECTION_EXPORT_INTERN = 0; // nur interne exportieren
    public static final int MEDIA_COLLECTION_EXPORT_EXTERN = 1; // nur externe exportieren
    public static final int MEDIA_COLLECTION_EXPORT_INTERN_EXTERN = 2; // alles exportieren


    // Website MTPlayer
    public static final String URL_WEBSITE = "https://www.p2tools.de/mtplayer/";
    public static final String URL_WEBSITE_DOWNLOAD = "https://www.p2tools.de/mtplayer/download.html";
    public static final String URL_WEBSITE_HELP = "https://www.p2tools.de/mtplayer/manual/";

    // die URL der Sets, nicht mehr mit jeder Programmversion hochzählen, nur noch wenn notwendig
    public static final String PROGRAM_SET_URL_LINUX = "https://www.p2tools.de/extra/mtplayer/v-6/pset_linux.xml";
    public static final String PROGRAM_SET_URL_WINDOWS = "https://www.p2tools.de/extra/mtplayer/v-6/pset_windows.xml";
    public static final String PROGRAM_SET_URL_MAC = "https://www.p2tools.de/extra/mtplayer/v-6/pset_mac.xml";

    //todo: beim Umstellen auf MOD wieder prüfen
    public static final String PSET_FILE_LINUX = "/de/p2tools/mtplayer/res/file/pset_linux.xml";
    public static final String PSET_FILE_WINDOWS = "/de/p2tools/mtplayer/res/file/pset_windows.xml";
    public static final String PSET_FILE_MAC = "/de/p2tools/mtplayer/res/file/pset_mac.xml";

    // ProgrammUrls
    public static final String ADRESSE_WEBSITE_VLC = "http://www.videolan.org";
    public static final String ADRESSE_WEBSITE_FFMPEG = "http://ffmpeg.org";

    public static final String FILE_PROG_ICON = "/de/p2tools/mtplayer/res/P2.png";

    // Dateien/Verzeichnisse
    public static final String JSON_DATEI_FILME = "filme.json";

    // minimale Größe (256 kB) eines Films um nicht als Fehler zu gelten
    public static final int MIN_DATEI_GROESSE_FILM = 256 * 1000;

    public static final int MAX_DEST_PATH_IN_DIALOG_DOWNLOAD = 10;

    public static final double GUI_FILTER_DIVIDER_LOCATION = 0.3;
    public static final double GUI_INFO_DIVIDER_LOCATION = 0.7;
    public static final double GUI_DIVIDER_LOCATION = 0.7;

    public static final double CONFIG_DIALOG_SET_DIVIDER = 0.2;

    public static final int LAENGE_DATEINAME_MAX = 200; // Standardwert für die Länge des Zieldateinamens
    public static final int LAENGE_FELD_MAX = 100; // Standardwert für die Länge des Feldes des
    public final static int MAX_COPY_OF_BACKUPFILE = 5; // Maximum number of backup files to be stored.

    public static final int MIN_TABLE_HEIGHT = 200;
    public static final int MIN_TABLE_HEIGHT_LOW = 100;
    public static final int MIN_TEXTAREA_HEIGHT_LOW = 50;

    public static final String THEME_LIVE = "Livestream";

    public static final String DREISAT = "3Sat";
    public static final String ARD = "ARD";

    public static final String ARTE_DE = "ARTE.DE";
    public static final String ARTE_EN = "ARTE.EN";
    public static final String ARTE_ES = "ARTE.ES";
    public static final String ARTE_FR = "ARTE.FR";
    public static final String ARTE_IT = "ARTE.IT";
    public static final String ARTE_PL = "ARTE.PL";

    public static final String BR = "BR";
    public static final String DW = "DW";
    public static final String HR = "HR";
    public static final String KIKA = "KiKA";
    public static final String MDR = "MDR";
    public static final String NDR = "NDR";
    public static final String ORF = "ORF";
    public static final String PHOENIX = "PHOENIX";
    public static final String RBB = "RBB";
    public static final String RBB_TV = "rbtv";
    public static final String RADIO_BREMEN = "Radio Bremen TV";

    public static final String SR = "SR";
    public static final String SRF = "SRF";
    public static final String SWR = "SWR";
    public static final String WDR = "WDR";
    public static final String ZDF = "ZDF";
    public static final String ZDF_TIVI = "ZDF-tivi";

    public static final String[] SENDER = {DREISAT, ARD,
            ARTE_DE, ARTE_EN, ARTE_ES, ARTE_FR, ARTE_IT, ARTE_PL,
            BR, DW, HR, KIKA, MDR, NDR, ORF, PHOENIX, RBB, RBB_TV, RADIO_BREMEN, SR,
            SRF, SWR, WDR, ZDF, ZDF_TIVI};
}
