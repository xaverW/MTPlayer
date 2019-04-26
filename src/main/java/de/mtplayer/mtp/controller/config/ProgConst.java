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

package de.mtplayer.mtp.controller.config;

public class ProgConst {

    public static final String PROGRAMNAME = "MTPlayer";
    public static final String USER_AGENT_DEFAULT = "";

    // settings file
    public static final String CONFIG_FILE = "mtplayer.xml";
    public static final String CONFIG_FILE_COPY = "mtplayer.xml_copy_";
    public static final String CONFIG_DIRECTORY = "p2Mtplayer"; // im Homeverzeichnis
    public static final String XML_START = "Mediathek";

    public static final String FILE_BOOKMARKS = "bookmarks.txt";
    public static final String FILE_ERLEDIGTE_ABOS = "downloads.txt";
    public static final String FILE_HISTORY = "history.txt";
    public static final String FILE_MEDIA_DB = "mediadb.txt";
    public static final String LOG_DIR = "Log";
    public static final String CSS_FILE = "/de/mtplayer/mtp/mtfx.css";

    public static final String FORMAT_ZIP = ".zip";
    public static final String FORMAT_XZ = ".xz";
    public static final String RTMP_PRTOKOLL = "rtmp";
    public static final String RTMP_FLVSTREAMER = "-r ";

    public static final int LOAD_FILMS_MAX_DAYS_MAX = 300; // Filter beim Programmstart/Blacklist: nur Filme der letzten xx Tage laden

    public static final int CHECK_FILMLIST_UPDATE = 30 * 60; // 30 Minuten
    public static final int CHECK_FILMLIST_UPDATE_PROGRAMSTART = CHECK_FILMLIST_UPDATE - 5 * 60; // 5 Minuten

    public static final int DOWNLOAD_DIALOG_LOAD_MAX_FILESIZE_FROM_WEB = 10;

    // beim Programmstart wird die Liste geladen wenn sie älter ist als ..
    public static final int ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE = 4 * 60 * 60;
    public static final int MAX_USER_AGENT_SIZE = 100;

    // Uhrzeit ab der die Diffliste alle Änderungen abdeckt, die Filmliste darf also nicht vor xx erstellt worden sein
    public static final String TIME_MAX_AGE_FOR_DIFF = "09";

    // MediathekView URLs
    public static final String ADRESSE_FILMLISTEN_SERVER_DIFF = "http://res.mediathekview.de/diff.xml";
    public static final String ADRESSE_FILMLISTEN_SERVER_AKT = "http://res.mediathekview.de/akt.xml";


    // Website MTPlayer
    public static final String ADRESSE_WEBSITE = "https://www.p2tools.de/mtplayer/";
    public static final String ADRESSE_WEBSITE_HELP = "https://www.p2tools.de/mtplayer/manual/";

    //  public static final String ADRESSE_MTPLAYER_VERSION = "http://p2.localhost:8080/extra/mtplayer-info.xml";
    public static final String ADRESSE_MTPLAYER_VERSION = "https://www.p2tools.de/extra/mtplayer-info.xml";


    // die URL der Sets, nicht mehr mit jeder Programmversion hochzählen, nur noch wenn notwendig
    // public static final String URL_MTPLAYER_CONFIG_DIRECTORY = "https://www.p2tools.de/extra/mtplayer/v-" + Functions.getProgVersion() + "/";
    public static final String URL_MTPLAYER_CONFIG_DIRECTORY = "https://www.p2tools.de/extra/mtplayer/v-3/";
    public static final String URL_MTPLAYER_PROGRAM_SETS = URL_MTPLAYER_CONFIG_DIRECTORY + "pset-templates.xml";

    // ProgrammUrls
    public static final String ADRESSE_WEBSITE_VLC = "http://www.videolan.org";
    public static final String ADRESSE_WEBSITE_FLVSTREAMER = "https://savannah.nongnu.org/projects/flvstreamer";
    public static final String ADRESSE_WEBSITE_FFMPEG = "http://ffmpeg.org";

    // Dateien/Verzeichnisse
    public static final String JSON_DATEI_FILME = "filme.json";

    // minimale Größe (256 kB) eines Films um nicht als Fehler zu gelten
    public static final int MIN_DATEI_GROESSE_FILM = 256 * 1000;
    // es können maximal soviele Filme eines Senders/Servers gleichzeitig geladen werden
    public static final int MAX_SENDER_FILME_LADEN = 2;

    public static final int MAX_PFADE_DIALOG_DOWNLOAD = 15;

    public static final String GUI_FILME_FILTER_DIVIDER_LOCATION = "0.3";
    public static final String GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION = "0.3";
    public static final String GUI_ABO_FILTER_DIVIDER_LOCATION = "0.3";

    public static final String GUI_FILME_DIVIDER_LOCATION = "0.7";
    public static final String GUI_DOWNLOAD_DIVIDER_LOCATION = "0.7";
    public static final String GUI_ABO_DIVIDER_LOCATION = "0.7";
    public static final String GUI_MSG_DIVIDER_LOCATION = "0.7";
    public static final String GUI_MSG_LOG_DIVIDER_LOCATION = "0.5";

    public static final String CONFIG_DIALOG_SET_DIVIDER = "0.2";

    public static final int DOWNLOAD_CHART_MAX_TIME = 30; // Minuten

    public static final int LAENGE_DATEINAME_MAX = 200; // Standardwert für die Länge des Zieldateinamens
    public static final int LAENGE_FELD_MAX = 100; // Standardwert für die Länge des Feldes des
    public final static int MAX_COPY_OF_BACKUPFILE = 5; // Maximum number of backup files to be stored.
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final int MIN_TABLE_HEIGHT = 250;


    public static final String DREISAT = "3Sat";
    public static final String ARD = "ARD";
    public static final String ARTE_DE = "ARTE.DE";
    public static final String ARTE_FR = "ARTE.FR";
    public static final String BR = "BR";
    public static final String DW = "DW";
    public static final String HR = "HR";
    public static final String KIKA = "KiKA";
    public static final String MDR = "MDR";
    public static final String NDR = "NDR";
    public static final String ORF = "ORF";
    public static final String PHOENIX = "PHOENIX";
    public static final String RBB = "RBB";
    public static final String SR = "SR";
    public static final String SRF = "SRF";
    public static final String SRF_PODCAST = "SRF.Podcast";
    public static final String SWR = "SWR";
    public static final String WDR = "WDR";
    public static final String ZDF = "ZDF";
    public static final String ZDF_TIVI = "ZDF-tivi";

    public static final String[] SENDER = {DREISAT, ARD, ARTE_DE, ARTE_FR,
            BR, DW, HR, KIKA, MDR, NDR, ORF, PHOENIX, RBB, SR,
            SRF, SRF_PODCAST, SWR, WDR, ZDF, ZDF_TIVI};

}