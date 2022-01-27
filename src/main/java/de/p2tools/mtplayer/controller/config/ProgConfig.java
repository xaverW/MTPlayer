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

import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.filmlist.filmlistUrls.FilmlistUrlList;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.mtplayer.tools.MLBandwidthTokenBucket;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.StoredFilters;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.pData.PDataProgConfig;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.ProgramTools;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;

public class ProgConfig extends PDataProgConfig {

    private static ProgConfig instance;
    private static final ArrayList<Config> arrayList = new ArrayList<>();
    public static final String SYSTEM = "system";


    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addInt("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addInt("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT = 5;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addInt("__system-parameter__download-max-restart-http_10__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT);
    // Beim Dialog "Download weiterführen" wird nach dieser Zeit der Download weitergeführt
    public static int SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT = 60;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS = addInt("__system-parameter__download-continue-second_60__", SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT);
    // Beim Dialog "Automode" wird nach dieser Zeit der das Programm beendet
    public static int SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT = 15;
    public static IntegerProperty SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS = addInt("__system-parameter__automode-quitt-second_60__", SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT);
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static int SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT = 30;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addInt("__system-parameter__download-errormsg-in-second_30__", SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT);
    // Downloadprogress im Terminal anzeigen
    public static BooleanProperty SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addBool("__system-parameter__download_progress_", Boolean.TRUE);
    // ===========================================

    // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStr("system-prog-version", ProgramTools.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStr("system-prog-build-no", ProgramTools.getBuild());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStr("system-prog-build-date", ProgramTools.getCompileDate());//z.B.: 27.07.2

    // Configs zum Aktualisieren beim Programmupdate
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBool("system-after-update-filter", Boolean.FALSE);

    // Configs zur Programmupdatesuche
    public static StringProperty SYSTEM_UPDATE_DATE = addStr("system-update-date"); // Datum der letzten Prüfung
    public static StringProperty SYSTEM_UPDATE_PROGSET_VERSION = addStr("system-update-progset-version");

    public static BooleanProperty SYSTEM_UPDATE_SEARCH_ACT = addBool("system-update-search-act", Boolean.TRUE); //Infos und Programm
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBool("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBool("system-update-search-daily", Boolean.FALSE); //daily suchen

    public static StringProperty SYSTEM_UPDATE_LAST_INFO = addStr("system-update-last-info");
    public static StringProperty SYSTEM_UPDATE_LAST_ACT = addStr("system-update-last-act");
    public static StringProperty SYSTEM_UPDATE_LAST_BETA = addStr("system-update-last-beta");
    public static StringProperty SYSTEM_UPDATE_LAST_DAILY = addStr("system-update-last-daily");

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);

    // MediaDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_MEDIA = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_HISTORY = new SimpleIntegerProperty(-1);
    public static BooleanProperty SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA = new SimpleBooleanProperty(true);

    // MediaDB
    public static StringProperty MEDIA_CONFIG_DIALOG_SIZE = addStr("media-config-dialog-size", "800:700");
    public static IntegerProperty MEDIA_CONFIG_DIALOG_SEARCH = addInt("media-config-dialog-search", ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
    public static BooleanProperty MEDIA_CONFIG_DIALOG_ACCORDION = addBool("media-config-dialog-accordion", Boolean.TRUE);
    public static StringProperty MEDIA_DIALOG_SIZE = addStr("media-dialog-size", "800:700");
    public static IntegerProperty MEDIA_DIALOG_SEARCH_ABO = addInt("media-dialog-search-abo", ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
    public static StringProperty MEDIA_DB_SUFFIX = addStr("media-db-suffix");
    public static BooleanProperty MEDIA_DB_WITH_OUT_SUFFIX = addBool("media-db-with-out-suffix");
    public static BooleanProperty MEDIA_DB_NO_HIDDEN_FILES = addBool("media-db-no-hidden-files");
    public static IntegerProperty MEDIA_DB_FILE_SIZE_MBYTE = addInt("media-db-filesize_mbyte", ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES);
    public static BooleanProperty MEDIA_DB_EXPORT_INTERN = addBool("media-db-export-intern", Boolean.FALSE);
    public static BooleanProperty MEDIA_DB_EXPORT_EXTERN = addBool("media-db-export-extern", Boolean.FALSE);
    public static BooleanProperty MEDIA_DB_EXPORT_INTERN_EXTERN = addBool("media-db-export-intern-extern", Boolean.TRUE);
    public static StringProperty MEDIA_DB_EXPORT_FILE = addStr("media-db-export-file");

    // Configs
    public static BooleanProperty SYSTEM_TRAY = addBool("system-tray", Boolean.FALSE);
    public static StringProperty SYSTEM_USERAGENT = addStr("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBool("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBool("system-only-ascii", Boolean.FALSE);
    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStr("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStr("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_EXTERN_PROGRAM = addStr("system-extern-program");
    public static StringProperty SYSTEM_PROG_PLAY_FILME = addStr("system-prog-play-filme");
    public static BooleanProperty SYSTEM_MARK_GEO = addBool("system-mark-geo", Boolean.TRUE);
    public static StringProperty SYSTEM_GEO_HOME_PLACE = addStr("system-geo-home-place", FilmData.GEO_DE);
    public static BooleanProperty SYSTEM_STYLE = addBool("system-style", Boolean.FALSE);
    public static IntegerProperty SYSTEM_STYLE_SIZE = addInt("system-style-size", 14);
    public static StringProperty SYSTEM_LOG_DIR = addStr("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBool("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_FILM = addBool("system-small-row-table-film", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_DOWNLOAD = addBool("system-small-row-table-download", Boolean.FALSE);
    public static BooleanProperty SYSTEM_DARK_THEME = addBool("system-dark-theme", Boolean.FALSE);
    public static BooleanProperty SYSTEM_THEME_CHANGED = addBool("system-theme-changed");
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBool("system-ssl-always-true");
    public static BooleanProperty TIP_OF_DAY_SHOW = addBool("tip-of-day-show", Boolean.TRUE);//Tips anzeigen
    public static StringProperty TIP_OF_DAY_WAS_SHOWN = addStr("tip-of-day-was-shown");//bereits angezeigte Tips
    public static StringProperty TIP_OF_DAY_DATE = addStr("tip-of-day-date"); //Datum des letzten Tips
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addInt("system-filter-wait-time", 100);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBool("system-filter-return", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStr("system-download-dir-new-version", "");

    // Fenstereinstellungen
    public static StringProperty SYSTEM_SIZE_GUI = addStr("system-size-gui", "1000:900");
    public static StringProperty SYSTEM_SIZE_DIALOG_FILMINFO = addStr("system-size-dialog-filminfo", "600:800");

    // Einstellungen Filmliste
    public static BooleanProperty SYSTEM_LOAD_FILMS_ON_START = addBool("system-load-films-on-start", Boolean.TRUE);
    public static StringProperty SYSTEM_LOAD_FILMS_MANUALLY = addStr("system-load-films-manually", "");
    public static StringProperty SYSTEM_LOAD_NOT_SENDER = addStr("system-load-not-sender", "");
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addInt("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addInt("system-load-filmlist-min-duration", 0); //es werden nur Filme mit mind. x Minuten geladen
    public static StringProperty SYSTEM_PATH_VLC = addStr("system-path-vlc", SetsPrograms.getTemplatePathVlc());
    public static StringProperty SYSTEM_PATH_FFMPEG = addStr("system-path-ffmpeg", SetsPrograms.getTemplatePathFFmpeg());

    // Blacklist
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_FUTURE = addBool("system-blacklist-show-no-future");
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_GEO = addBool("system-blacklist-show-no-geo");
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_ABO = addBool("system-blacklist-show-abo");
    public static IntegerProperty SYSTEM_BLACKLIST_MAX_FILM_DAYS = addInt("system-blacklist-max-film-days", 0);
    public static IntegerProperty SYSTEM_BLACKLIST_MIN_FILM_DURATION = addInt("system-blacklist-min-film-duration", 0); // Minuten
    public static BooleanProperty SYSTEM_BLACKLIST_IS_WHITELIST = addBool("system-blacklist-is-whitelist");

    // Download
    public static BooleanProperty DOWNLOAD_START_NOW = addBool("download-start-now", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_BEEP = addBool("download-beep");
    public static BooleanProperty DOWNLOAD_ERROR_MSG = addBool("download-error-msg", Boolean.TRUE);
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addInt("download-max-downloads", 1);
    public static BooleanProperty DOWNLOAD_MAX_ONE_PER_SERVER = addBool("download-max-one-per-server"); // nur ein Download pro Server - sonst max 2
    public static IntegerProperty DOWNLOAD_MAX_BANDWIDTH_KBYTE = addInt("download-max-bandwidth-kilobyte", MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
    public static IntegerProperty DOWNLOAD_BANDWIDTH_KBYTE = addInt("download-bandwidth-byte"); // da wird die genutzte Bandbreite gespeichert

    // Gui Film
    public static DoubleProperty FILM_GUI_FILTER_DIVIDER = addDouble("film-gui-filter-divider", ProgConst.GUI_FILME_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty FILM_GUI_FILTER_DIVIDER_ON = addBool("film-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty FILM_GUI_DIVIDER = addDouble("film-gui-divider", ProgConst.GUI_FILME_DIVIDER_LOCATION);
    public static BooleanProperty FILM_GUI_DIVIDER_ON = addBool("film-gui-divider-on", Boolean.TRUE);
    public static StringProperty FILM_GUI_TABLE_WIDTH = addStr("film-gui-table-width");
    public static StringProperty FILM_GUI_TABLE_SORT = addStr("film-gui-table-sort");
    public static StringProperty FILM_GUI_TABLE_UP_DOWN = addStr("film-gui-table-up-down");
    public static StringProperty FILM_GUI_TABLE_VIS = addStr("film-gui-table-vis");
    public static StringProperty FILM_GUI_TABLE_ORDER = addStr("film-gui-table-order");

    // Gui Download
    public static StringProperty DOWNLOAD_DIALOG_PATH_SAVING = addStr("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static StringProperty DOWNLOAD_DIALOG_HD_HEIGHT_LOW = addStr("download-dialog-hd-height-low", FilmData.RESOLUTION_NORMAL);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBool("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBool("download-dialog-start-download-not", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_TIME = addBool("download-dialog-start-download-time", Boolean.FALSE);
    public static StringProperty DOWNLOAD_DIALOG_EDIT_SIZE = addStr("download-dialog-edit-size", "800:800");
    public static StringProperty DOWNLOAD_DIALOG_START_AT_TIME_SIZE = addStr("download-dialog-start-at-time-size", "800:400");
    public static StringProperty DOWNLOAD_DIALOG_ADD_SIZE = addStr("download-dialog-add-size");
    public static StringProperty DOWNLOAD_DIALOG_ADD_MORE_SIZE = addStr("download-dialog-add-more-size");
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStr("download-dialog-continue-size");
    public static StringProperty DOWNLOAD_DIALOG_ERROR_SIZE = addStr("download-dialog-error-size", "");
    public static DoubleProperty DOWNLOAD_GUI_FILTER_DIVIDER = addDouble("download-gui-filter-divider", ProgConst.GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty DOWNLOAD_GUI_FILTER_DIVIDER_ON = addBool("download-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty DOWNLOAD_GUI_DIVIDER = addDouble("download-gui-divider", ProgConst.GUI_DOWNLOAD_DIVIDER_LOCATION);
    public static BooleanProperty DOWNLOAD_GUI_DIVIDER_ON = addBool("download-gui-divider-on", Boolean.TRUE);
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStr("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStr("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStr("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStr("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStr("download-gui-table-order");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBool("download-show-notification", Boolean.TRUE);

    // Downloadchart
    public static BooleanProperty DOWNLOAD_CHART_SEPARAT = addBool("download-chart-separat", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_EXISTING = addBool("download-chart-only-existing", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_RUNNING = addBool("download-chart-only-running", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_CHART_SHOW_MAX_TIME_MIN = addInt("download-chart-show-max-time-min", 30); //MAX Minuten im Chart

    // Gui Abo
    public static BooleanProperty ABO_SEARCH_NOW = addBool("abo-search-now", Boolean.TRUE);
    public static IntegerProperty ABO_MINUTE_MIN_SIZE = addInt("abo-minute-min-size", FilmFilter.FILTER_DURATION_MIN_MINUTE); //Vorgabe beim Anlegen eines Abos
    public static IntegerProperty ABO_MINUTE_MAX_SIZE = addInt("abo-minute-max-size", FilmFilter.FILTER_DURATION_MAX_MINUTE); //Vorgabe beim Anlegen eines Abos
    public static StringProperty ABO_DIALOG_EDIT_SIZE = addStr("abo-dialog-edit-size", "600:800");
    public static DoubleProperty ABO_GUI_FILTER_DIVIDER = addDouble("abo-gui-filter-divider", ProgConst.GUI_ABO_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty ABO_GUI_FILTER_DIVIDER_ON = addBool("abo-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty ABO_GUI_DIVIDER = addDouble("abo-gui-divider", ProgConst.GUI_ABO_DIVIDER_LOCATION);
    public static BooleanProperty ABO_GUI_DIVIDER_ON = addBool("abo-gui-divider-on", Boolean.TRUE);
    public static StringProperty ABO_GUI_TABLE_WIDTH = addStr("abo-gui-table-width");
    public static StringProperty ABO_GUI_TABLE_SORT = addStr("abo-gui-table-sort");
    public static StringProperty ABO_GUI_TABLE_UP_DOWN = addStr("abo-gui-table-up-down");
    public static StringProperty ABO_GUI_TABLE_VIS = addStr("abo-gui-table-vis");
    public static StringProperty ABO_GUI_TABLE_ORDER = addStr("abo-gui-table-order");

    // ConfigDialog
    public static StringProperty CONFIG_DIALOG_SIZE = addStr("config-dialog-size");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBool("config_dialog-accordion", Boolean.TRUE);
    public static DoubleProperty CONFIG_DIALOG_SET_DIVIDER = addDouble("config-dialog-set-divider", ProgConst.CONFIG_DIALOG_SET_DIVIDER);
    public static StringProperty CONFIG_DIALOG_IMPORT_SET_SIZE = addStr("config-dialog-import-set-size", "800:700");

    // StartDialog
    public static StringProperty START_DIALOG_DOWNLOAD_PATH = addStr("start-dialog-download-path", PSystemUtils.getStandardDownloadPath());

    // FilmInfoDialog
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_URL = addBool("film-info-dialog-show-url", Boolean.TRUE);

    // DownloadAddDialog
    public static BooleanProperty DOWNLOAD_INFO_DIALOG_SHOW_URL = addBool("download-info-dialog-show-url", Boolean.TRUE);

    // Filter Filme
    public static IntegerProperty FILTER_FILM_SEL_FILTER = addInt("filter-film-sel-filter");

    // Filter Abo
    public static StringProperty FILTER_ABO_CHANNEL = addStr("filter-abo-channel");
    public static StringProperty FILTER_ABO_NAME = addStr("filter-abo-name");
    public static StringProperty FILTER_ABO_DESCRIPTION = addStr("filter-abo-description");
    public static StringProperty FILTER_ABO_TYPE = addStr("filter-abo-type");

    // Filter Download
    public static StringProperty FILTER_DOWNLOAD_CHANNEL = addStr("filter-download-channel");
    public static StringProperty FILTER_DOWNLOAD_SOURCE = addStr("filter-download-source");
    public static StringProperty FILTER_DOWNLOAD_TYPE = addStr("filter-download-type");
    public static StringProperty FILTER_DOWNLOAD_ABO = addStr("filter-download-abo");
    public static StringProperty FILTER_DOWNLOAD_STATE = addStr("filter-download-state");

    // Shorcuts Hauptmenü
    public static String SHORTCUT_QUIT_PROGRAM_INIT = "Ctrl+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM = addStr("SHORTCUT_QUIT_PROGRAM", SHORTCUT_QUIT_PROGRAM_INIT);

    public static String SHORTCUT_QUIT_PROGRAM_WAIT_INIT = "Ctrl+Shift+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM_WAIT = addStr("SHORTCUT_QUIT_PROGRAM_WAIT", SHORTCUT_QUIT_PROGRAM_WAIT_INIT);

    public static String SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT = "Ctrl+Alt+M";
    public static StringProperty SHORTCUT_SEARCH_MEDIA_COLLECTION = addStr("SHORTCUT_SEARCH_MEDIA_COLLECTION", SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT);

    // Shortcuts Filmmenü
    public static String SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT = "Ctrl+M";
    public static StringProperty SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION = addStr("SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION", SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT);

    public static String SHORTCUT_SHOW_FILTER_INIT = "Alt+F";
    public static StringProperty SHORTCUT_SHOW_FILTER = addStr("SHORTCUT_SHOW_FILTER", SHORTCUT_SHOW_FILTER_INIT);

    public static String SHORTCUT_SHOW_INFOS_INIT = "Alt+I";
    public static StringProperty SHORTCUT_SHOW_INFOS = addStr("SHORTCUT_SHOW_INFO", SHORTCUT_SHOW_INFOS_INIT);

    public static String SHORTCUT_INFO_FILM_INIT = "Ctrl+I";
    public static StringProperty SHORTCUT_INFO_FILM = addStr("SHORTCUT_INFO_FILM", SHORTCUT_INFO_FILM_INIT);

    public static String SHORTCUT_PLAY_FILM_INIT = "Ctrl+P";
    public static StringProperty SHORTCUT_PLAY_FILM = addStr("SHORTCUT_PLAY_FILM", SHORTCUT_PLAY_FILM_INIT);

    public static String SHORTCUT_SAVE_FILM_INIT = "Ctrl+S";
    public static StringProperty SHORTCUT_SAVE_FILM = addStr("SHORTCUT_SAVE_FILM", SHORTCUT_SAVE_FILM_INIT);

    // Shortcuts Downloadmenü
    public static String SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION_INIT = "Alt+M";
    public static StringProperty SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION = addStr("SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION", SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION_INIT);

    public static String SHORTCUT_DOWNLOAD_START_INIT = "Ctrl+D";
    public static StringProperty SHORTCUT_DOWNLOAD_START = addStr("SHORTCUT_DOWNLOAD_START", SHORTCUT_DOWNLOAD_START_INIT);

    public static String SHORTCUT_DOWNLOAD_STOP_INIT = "Ctrl+T";
    public static StringProperty SHORTCUT_DOWNLOAD_STOP = addStr("SHORTCUT_DOWNLOAD_STOP", SHORTCUT_DOWNLOAD_STOP_INIT);

    public static String SHORTCUT_DOWNLOAD_CHANGE_INIT = "Ctrl+C";
    public static StringProperty SHORTCUT_DOWNLOAD_CHANGE = addStr("SHORTCUT_DOWNLOAD_CHANGE", SHORTCUT_DOWNLOAD_CHANGE_INIT);

    public static String SHORTCUT_DOWNLOAD_UNDO_DELETE_INIT = "Ctrl+R";
    public static StringProperty SHORTCUT_DOWNLOAD_UNDO_DELETE = addStr("SHORTCUT_DOWNLOAD_UNDO_DELETE", SHORTCUT_DOWNLOAD_UNDO_DELETE_INIT);

    public static String SHORTCUT_DOWNLOADS_UPDATE_INIT = "CTRL+U";
    public static StringProperty SHORTCUT_DOWNLOAD_UPDATE = addStr("SHORTCUT_DOWNLOAD_UPDATE", SHORTCUT_DOWNLOADS_UPDATE_INIT);

    public static String SHORTCUT_DOWNLOAD_CLEAN_UP_INIT = "CTRL+O";
    public static StringProperty SHORTCUT_DOWNLOAD_CLEAN_UP = addStr("SHORTCUT_DOWNLOAD_CLEAN_UP", SHORTCUT_DOWNLOAD_CLEAN_UP_INIT);

    public static String SHORTCUT_EXTERN_PROGRAM_INIT = "CTRL+E";
    public static StringProperty SHORTCUT_EXTERN_PROGRAM = addStr("SHORTCUT_EXTERN_PROGRAM", SHORTCUT_EXTERN_PROGRAM_INIT);


    public static String PARAMETER_INFO = P2LibConst.LINE_SEPARATOR + "\t"
            + "\"__system-parameter__xxx\" können nur im Konfigfile geändert werden" + P2LibConst.LINE_SEPARATOR
            + "\t" + "und sind auch nicht für ständige Änderungen gedacht." + P2LibConst.LINE_SEPARATOR
            + "\t" + "Wird eine Zeile gelöscht, wird der Parameter wieder mit dem Standardwert angelegt."
            + P2LibConst.LINE_SEPARATOR
            + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Timeout für direkte Downloads, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, am Ende aller Downloads" + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: " +
            SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, direkt beim Download," + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Download weiterführen\" wird nach dieser Zeit der Download weitergeführt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Automode\" wird nach dieser Zeit der das Programm beendet, Standardwert: "
            + SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadfehlermeldung wird xx Sedunden lang angezeigt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadprogress im Terminal (-auto) anzeigen: "
            + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getValue() + P2LibConst.LINE_SEPARATOR;

    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private ProgConfig() {
        super(arrayList, "ProgConfig");
    }

    public static final ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile) {
        ProgData progData = ProgData.getInstance();

        configFile.addConfigs(ProgConfig.getInstance());//Progconfig
        configFile.addConfigs(ProgColorList.getConfigsData());//Color

        configFile.addConfigs(progData.setDataList);

        final SelectedFilter akt_sf = progData.storedFilters.getActFilterSettings();//akt-Filter
        akt_sf.setName(StoredFilters.SELECTED_FILTER_NAME);// nur zur Info im Config-File
        configFile.addConfigs(akt_sf);
        configFile.addConfigs(progData.storedFilters.getStoredFilterList());//Filterprofile

        configFile.addConfigs(progData.aboList);
        configFile.addConfigs(progData.blackList);
        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.downloadList);
        configFile.addConfigs(progData.mediaCollectionDataList);

        FilmlistUrlList filmlistUrlList = progData.searchFilmListUrls.getFilmlistUrlList_akt();
        filmlistUrlList.setTag("filmlistUrlList-akt");
        configFile.addConfigs(filmlistUrlList);

        filmlistUrlList = progData.searchFilmListUrls.getFilmlistUrlList_diff();
        filmlistUrlList.setTag("filmlistUrlList-diff");
        configFile.addConfigs(filmlistUrlList);
    }

    public static void logAllConfigs() {
        final ArrayList<String> list = new ArrayList<>();

        list.add(PARAMETER_INFO);

        list.add(PLog.LILNE2);
        list.add("Programmeinstellungen");
        list.add("===========================");
        arrayList.stream().forEach(c -> {
            String s = c.getKey();
            if (s.startsWith("_")) {
                while (s.length() < 55) {
                    s += " ";
                }
            } else {
                while (s.length() < 35) {
                    s += " ";
                }
            }

            list.add(s + "  " + c.getActValueString());
        });
        list.add(PLog.LILNE2);
        PStringUtils.appendString(list, "|  ", "=");

        PLog.emptyLine();
        PLog.sysLog(list);
        PLog.emptyLine();
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }

    private static StringProperty addStr(String key) {
        return addStrProp(arrayList, key);
    }

    private static StringProperty addStrC(String comment, String key) {
        return addStrPropC(comment, arrayList, key);
    }

    private static StringProperty addStr(String key, String init) {
        return addStrProp(arrayList, key, init);
    }

    private static StringProperty addStrC(String comment, String key, String init) {
        return addStrPropC(comment, arrayList, key, init);
    }

    private static DoubleProperty addDouble(String key, double init) {
        return addDoubleProp(arrayList, key, init);
    }

    private static DoubleProperty addDoubleC(String comment, String key, double init) {
        return addDoublePropC(comment, arrayList, key, init);
    }

    private static IntegerProperty addInt(String key) {
        return addIntProp(arrayList, key, 0);
    }

    private static IntegerProperty addInt(String key, int init) {
        return addIntProp(arrayList, key, init);
    }

    private static IntegerProperty addIntC(String comment, String key, int init) {
        return addIntPropC(comment, arrayList, key, init);
    }

    private static LongProperty addLong(String key) {
        return addLongProp(arrayList, key, 0);
    }

    private static LongProperty addLong(String key, long init) {
        return addLongProp(arrayList, key, init);
    }

    private static LongProperty addLongC(String comment, String key, long init) {
        return addLongPropC(comment, arrayList, key, init);
    }

    private static BooleanProperty addBool(String key, boolean init) {
        return addBoolProp(arrayList, key, init);
    }

    private static BooleanProperty addBool(String key) {
        return addBoolProp(arrayList, key, Boolean.FALSE);
    }

    private static BooleanProperty addBoolC(String comment, String key, boolean init) {
        return addBoolPropC(comment, arrayList, key, init);
    }
}
