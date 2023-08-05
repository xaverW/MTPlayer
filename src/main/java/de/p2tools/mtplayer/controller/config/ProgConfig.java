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

import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterWorker;
import de.p2tools.mtplayer.controller.filmfilter.FastFilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.starter.AskBeforeDeleteState;
import de.p2tools.mtplayer.controller.tools.MLBandwidthTokenBucket;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.data.PDataProgConfig;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.PShutDown;
import de.p2tools.p2lib.tools.PStringUtils;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ProgConfig extends PDataProgConfig {

    private static ProgConfig instance;

    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addIntProp("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addIntProp("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT = 5;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addIntProp("__system-parameter__download-max-restart-http_10__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT);
    // Beim Dialog "Download weiterführen" wird nach dieser Zeit der Download weitergeführt
    public static int SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT = 60;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS = addIntProp("__system-parameter__download-continue-second_60__", SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT);
    // Beim Dialog "Automode" wird nach dieser Zeit der das Programm beendet
    public static int SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT = 15;
    public static IntegerProperty SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS = addIntProp("__system-parameter__automode-quitt-second_60__", SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT);
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static int SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT = 30;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addIntProp("__system-parameter__download-errormsg-in-second_30__", SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT);
    // Downloadprogress im Terminal anzeigen
    public static BooleanProperty SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addBoolProp("__system-parameter__download_progress_", Boolean.TRUE);
    // ===========================================

    // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStrProp("system-prog-version", ProgramToolsFactory.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStrProp("system-prog-build-no", ProgramToolsFactory.getBuild());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStrProp("system-prog-build-date", ProgramToolsFactory.getCompileDate()); //z.B.: 27.07.2

    //Configs zur Anzeige der Diakritika in der Filmliste
    //TRUE: dann werden Diakritika nicht geändert und angezeigt --> das kommt weg
    public static BooleanProperty SYSTEM_SHOW_DIACRITICS = addBoolProp("system-show-diacritics", Boolean.TRUE);

    //Configs zur Anzeige der Diakritika in der Filmliste
    //TRUE: dann werden Diakritika nicht geändert und angezeigt
    public static BooleanProperty SYSTEM_REMOVE_DIACRITICS = addBoolProp("system-remove-diacritics", Boolean.FALSE);


    // Configs zum Aktualisieren beim Programmupdate
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBoolProp("system-after-update-filter", Boolean.FALSE);

    // Configs zur Programmupdatesuche
    public static StringProperty SYSTEM_UPDATE_DATE = addStrProp("system-update-date"); // Datum der letzten Prüfung

    public static BooleanProperty SYSTEM_UPDATE_SEARCH_ACT = addBoolProp("system-update-search-act", Boolean.TRUE); //Infos und Programm
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBoolProp("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBoolProp("system-update-search-daily", Boolean.FALSE); //daily suchen

    public static StringProperty SYSTEM_UPDATE_LAST_INFO = addStrProp("system-update-last-info");
    public static StringProperty SYSTEM_UPDATE_LAST_ACT = addStrProp("system-update-last-act");
    public static StringProperty SYSTEM_UPDATE_LAST_BETA = addStrProp("system-update-last-beta");
    public static StringProperty SYSTEM_UPDATE_LAST_DAILY = addStrProp("system-update-last-daily");

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_ABO = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_MEDIA = new SimpleIntegerProperty(-1);

    // MediaDialog, Dialog nach Start immer gleich öffnen
    public static BooleanProperty SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA = new SimpleBooleanProperty(true);

    // MediaDB
    public static StringProperty MEDIA_DIALOG_SIZE = addStrProp("media-dialog-size", "800:700");
    public static StringProperty MEDIA_DB_SUFFIX = addStrProp("media-db-suffix");
    public static BooleanProperty MEDIA_DB_WITH_OUT_SUFFIX = addBoolProp("media-db-with-out-suffix");
    public static BooleanProperty MEDIA_DB_NO_HIDDEN_FILES = addBoolProp("media-db-no-hidden-files");
    public static IntegerProperty MEDIA_DB_FILE_SIZE_MBYTE = addIntProp("media-db-filesize_mbyte", ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES);
    public static BooleanProperty MEDIA_DB_EXPORT_INTERN = addBoolProp("media-db-export-intern", Boolean.FALSE);
    public static BooleanProperty MEDIA_DB_EXPORT_EXTERN = addBoolProp("media-db-export-extern", Boolean.FALSE);
    public static BooleanProperty MEDIA_DB_EXPORT_INTERN_EXTERN = addBoolProp("media-db-export-intern-extern", Boolean.TRUE);
    public static StringProperty MEDIA_DB_EXPORT_FILE = addStrProp("media-db-export-file");

    // Configs
    public static BooleanProperty SYSTEM_USE_OWN_PROGRAM_ICON = addBoolProp("system-use--own-program-icon", Boolean.FALSE);
    public static StringProperty SYSTEM_PROGRAM_ICON_PATH = addStrProp("system-program-icon", ""); //ein eigenes Programm-Icon
    public static BooleanProperty SYSTEM_TRAY = addBoolProp("system-tray", Boolean.FALSE);
    public static BooleanProperty SYSTEM_TRAY_USE_OWN_ICON = addBoolProp("system-tray-own-icon", Boolean.FALSE);
    public static StringProperty SYSTEM_TRAY_ICON_PATH = addStrProp("system-tray-icon", ""); //ein eigenes Tray-Icon
    public static StringProperty SYSTEM_USERAGENT = addStrProp("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static IntegerProperty SYSTEM_FILMLIST_FILTER = addIntProp("system-filmlist-filter", BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
    public static StringProperty SYSTEM_SHUT_DOWN_CALL = addStrProp("system-shut-down-call", PShutDown.getShutDownCommand()); // shutDown call
    public static BooleanProperty SYSTEM_SHUT_DOWN_CALL_ON = addBoolProp("system-shut-down-call-on", false); // das shutDown ist aktiv

    //Configs Statusbar
    public static BooleanProperty SYSTEM_STATUS_BAR_ON = addBoolProp("system-status-bar-on", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_SEL = addBoolProp("system-status-bar-field-sel", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_LEFT = addBoolProp("system-status-bar-field-left", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_DOT = addBoolProp("system-status-bar-field-dot", false);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_RIGHT = addBoolProp("system-status-bar-field-right", true);


    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBoolProp("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBoolProp("system-only-ascii", Boolean.FALSE);
    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStrProp("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStrProp("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_PLAY_FILME = addStrProp("system-prog-play-filme");
    public static BooleanProperty SYSTEM_MARK_GEO = addBoolProp("system-mark-geo", Boolean.TRUE);
    public static StringProperty SYSTEM_GEO_HOME_PLACE = addStrProp("system-geo-home-place", FilmDataMTP.GEO_DE);
    public static BooleanProperty SYSTEM_STYLE = addBoolProp("system-style", Boolean.FALSE);
    public static IntegerProperty SYSTEM_STYLE_SIZE = addIntProp("system-style-size", 14);
    public static StringProperty SYSTEM_LOG_DIR = addStrProp("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBoolProp("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_ONLY_ONE_INSTANCE = addBoolProp("system-only-one-instance", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_FILM = addBoolProp("system-small-row-table-film", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_DOWNLOAD = addBoolProp("system-small-row-table-download", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_ABO = addBoolProp("system-small-row-table-abo", Boolean.FALSE);
    public static BooleanProperty SYSTEM_DARK_THEME = addBoolProp("system-dark-theme", Boolean.FALSE);
    public static BooleanProperty SYSTEM_THEME_CHANGED = addBoolProp("system-theme-changed");
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBoolProp("system-ssl-always-true");
    public static BooleanProperty TIP_OF_DAY_SHOW = addBoolProp("tip-of-day-show", Boolean.TRUE); //Tips anzeigen
    public static StringProperty TIP_OF_DAY_WAS_SHOWN = addStrProp("tip-of-day-was-shown"); //bereits angezeigte Tips
    public static StringProperty TIP_OF_DAY_DATE = addStrProp("tip-of-day-date"); //Datum des letzten Tips
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addIntProp("system-filter-wait-time", 100);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBoolProp("system-filter-return", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStrProp("system-download-dir-new-version", "");

    // Fenstereinstellungen
    public static StringProperty SYSTEM_SIZE_GUI = addStrProp("system-size-gui", "1000:900");
    public static StringProperty SYSTEM_SIZE_DIALOG_FILMINFO = addStrProp("system-size-dialog-filminfo", "300:600");

    // Einstellungen Filmliste
    public static StringProperty SYSTEM_FILMLIST_DATE = addStrProp("system-filmlist-date", "");
    public static BooleanProperty SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART = addBoolProp("system-load-filmlist-on-programstart", Boolean.TRUE);
    public static BooleanProperty SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY = addBoolProp("system-load-new-filmlist-immediately", Boolean.FALSE);
    public static StringProperty SYSTEM_LOAD_NOT_SENDER = addStrProp("system-load-not-sender", "");
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addIntProp("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addIntProp("system-load-filmlist-min-duration", 0); //es werden nur Filme mit mind. x Minuten geladen
    public static StringProperty SYSTEM_PATH_VLC = addStrProp("system-path-vlc", SetFactory.getTemplatePathVlc());
    public static StringProperty SYSTEM_PATH_FFMPEG = addStrProp("system-path-ffmpeg", SetFactory.getTemplatePathFFmpeg());

    // Blacklist
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_FUTURE = addBoolProp("system-blacklist-show-no-future");
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_GEO = addBoolProp("system-blacklist-show-no-geo");
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_ABO = addBoolProp("system-blacklist-show-abo");
    public static IntegerProperty SYSTEM_BLACKLIST_MAX_FILM_DAYS = addIntProp("system-blacklist-max-film-days", 0);
    public static IntegerProperty SYSTEM_BLACKLIST_MIN_FILM_DURATION = addIntProp("system-blacklist-min-film-duration", 0); // Minuten

    // Download
    public static BooleanProperty DOWNLOAD_START_NOW = addBoolProp("download-start-now", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_BEEP = addBoolProp("download-beep");
    public static BooleanProperty DOWNLOAD_ERROR_MSG = addBoolProp("download-error-msg", Boolean.TRUE);
    public static IntegerProperty DOWNLOAD_ONLY_STOP = addIntProp("download-only-stop", AskBeforeDeleteState.DOWNLOAD_STOP__ASK); // das sind Downloads ohne Dateien
    public static IntegerProperty DOWNLOAD_STOP = addIntProp("download-stop", AskBeforeDeleteState.DOWNLOAD_STOP__ASK); // das sind Downloads mit bereits geladenen Dateien
    public static IntegerProperty DOWNLOAD_CONTINUE = addIntProp("download-continue", AskBeforeDeleteState.DOWNLOAD_RESTART__ASK);
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addIntProp("download-max-downloads", 1);
    public static BooleanProperty DOWNLOAD_MAX_ONE_PER_SERVER = addBoolProp("download-max-one-per-server"); // nur ein Download pro Server - sonst max 2
    public static IntegerProperty DOWNLOAD_MAX_BANDWIDTH_KBYTE = addIntProp("download-max-bandwidth-kilobyte", MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
    public static IntegerProperty DOWNLOAD_BANDWIDTH_KBYTE = addIntProp("download-bandwidth-byte"); // da wird die genutzte Bandbreite gespeichert

    // Gui Film
    public static StringProperty FILM_GUI_FILTER_DIALOG = addStrProp("film-gui-filter-dialog", "400:500");
    public static BooleanProperty FILM_GUI_FILTER_DIALOG_IS_SHOWING = addBoolProp("film-gui-filter-dialog-is-showing", Boolean.FALSE);
    public static DoubleProperty FILM_GUI_FILTER_DIVIDER = addDoubleProp("film-gui-filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty FILM_GUI_FILTER_DIVIDER_ON = addBoolProp("film-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty FILM_GUI_DIVIDER = addDoubleProp("film-gui-divider", ProgConst.GUI_DIVIDER_LOCATION);
    public static BooleanProperty FILM_GUI_DIVIDER_ON = addBoolProp("film-gui-divider-on", Boolean.TRUE);
    public static StringProperty FILM_GUI_TABLE_WIDTH = addStrProp("film-gui-table-width");
    public static StringProperty FILM_GUI_TABLE_SORT = addStrProp("film-gui-table-sort");
    public static StringProperty FILM_GUI_TABLE_UP_DOWN = addStrProp("film-gui-table-up-down");
    public static StringProperty FILM_GUI_TABLE_VIS = addStrProp("film-gui-table-vis");
    public static StringProperty FILM_GUI_TABLE_ORDER = addStrProp("film-gui-table-order");
    public static StringProperty ADD_BLACK_DIALOG_SIZE = addStrProp("add-black-dialog-size", "600:400");
    public static DoubleProperty FILM_GUI_INFO_DIVIDER = addDoubleProp("film-gui-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);

    // Gui Film -> fast search
    public static BooleanProperty FAST_SEARCH_ON = addBoolProp("fast-search-on", Boolean.FALSE);
    public static IntegerProperty FAST_SEARCH_WHERE = addIntProp("fast-search-where", ProgConst.SEARCH_FAST_THEME_TITLE);

    // Gui Download
    public static StringProperty DOWNLOAD_DIALOG_PATH_SAVING = addStrProp("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static StringProperty DOWNLOAD_DIALOG_HD_HEIGHT_LOW = addStrProp("download-dialog-hd-height-low", FilmDataMTP.RESOLUTION_NORMAL);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBoolProp("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBoolProp("download-dialog-start-download-not", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_TIME = addBoolProp("download-dialog-start-download-time", Boolean.FALSE);
    public static StringProperty DOWNLOAD_DIALOG_EDIT_SIZE = addStrProp("download-dialog-edit-size", "800:800");
    public static StringProperty DOWNLOAD_DIALOG_START_AT_TIME_SIZE = addStrProp("download-dialog-start-at-time-size", "800:400");
    public static StringProperty DOWNLOAD_DIALOG_ADD_SIZE = addStrProp("download-dialog-add-size");
    public static StringProperty DOWNLOAD_DIALOG_ADD_MORE_SIZE = addStrProp("download-dialog-add-more-size");
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStrProp("download-dialog-continue-size");
    public static StringProperty DOWNLOAD_DIALOG_ERROR_SIZE = addStrProp("download-dialog-error-size", "");
    public static DoubleProperty DOWNLOAD_GUI_FILTER_DIVIDER = addDoubleProp("download-gui-filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty DOWNLOAD_GUI_FILTER_DIVIDER_ON = addBoolProp("download-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty DOWNLOAD_GUI_DIVIDER = addDoubleProp("download-gui-divider", ProgConst.GUI_DIVIDER_LOCATION);
    public static BooleanProperty DOWNLOAD_GUI_DIVIDER_ON = addBoolProp("download-gui-divider-on", Boolean.TRUE);
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStrProp("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStrProp("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStrProp("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStrProp("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStrProp("download-gui-table-order");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBoolProp("download-show-notification", Boolean.TRUE);
    public static DoubleProperty DOWNLOAD_GUI_MEDIA_DIVIDER = addDoubleProp("download-gui-media-divider", 0.5);
    public static DoubleProperty DOWNLOAD_GUI_INFO_DIVIDER = addDoubleProp("download-gui-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);
    public static StringProperty DOWNLOAD_STOP_DIALOG_SIZE = addStrProp("download-stop-dialog-size", "950:600");
    public static StringProperty DOWNLOAD_ONLY_STOP_DIALOG_SIZE = addStrProp("download-only-stop-dialog-size", "600:400");

    //Gui Media (Search) Dialog
    public static IntegerProperty GUI_MEDIA_BUILD_SEARCH_MEDIA = addIntProp("gui-media-build-search-media", ProgConst.MEDIA_COLLECTION_SEARCH_TITEL); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty GUI_MEDIA_BUILD_SEARCH_ABO = addIntProp("gui-media-build-search-abo", ProgConst.MEDIA_COLLECTION_SEARCH_TITEL); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty GUI_MEDIA_SEARCH_IN_MEDIA = addIntProp("gui-media-search-in-media", ProgConst.MEDIA_COLLECTION_SEARCH_TITEL); //wo bei Medien gesucht wird: T/Th/TT
    public static IntegerProperty GUI_MEDIA_SEARCH_IN_ABO = addIntProp("gui-media-search-in-abo", ProgConst.MEDIA_COLLECTION_SEARCH_TITEL); //wo bei Abos gesucht wird: T/Th/TT
    public static BooleanProperty GUI_MEDIA_CLEAN_MEDIA = addBoolProp("gui-media-clean-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_ABO = addBoolProp("gui-media-clean-abo", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_EXACT_MEDIA = addBoolProp("gui-media-clean-exact-media", Boolean.FALSE);
    public static BooleanProperty GUI_MEDIA_CLEAN_EXACT_ABO = addBoolProp("gui-media-clean-exact-abo", Boolean.FALSE);
    public static BooleanProperty GUI_MEDIA_CLEAN_AND_OR_MEDIA = addBoolProp("gui-media-clean-and-or-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_AND_OR_ABO = addBoolProp("gui-media-clean-and-or-abo", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_NUMBER_MEDIA = addBoolProp("gui-media-clean-number-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_NUMBER_ABO = addBoolProp("gui-media-clean-number-abo", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_DATE_MEDIA = addBoolProp("gui-media-clean-date-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_DATE_ABO = addBoolProp("gui-media-clean-date-abo", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_CLIP_MEDIA = addBoolProp("gui-media-clean-clip-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_CLIP_ABO = addBoolProp("gui-media-clean-clip-abo", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_LIST_MEDIA = addBoolProp("gui-media-clean-list-media", Boolean.TRUE);
    public static BooleanProperty GUI_MEDIA_CLEAN_LIST_ABO = addBoolProp("gui-media-clean-list-abo", Boolean.TRUE);
    public static StringProperty GUI_MEDIA_CONFIG_DIALOG_SIZE = addStrProp("gui-media-config-dialog-size", "800:700");

    // Downloadchart
    public static BooleanProperty DOWNLOAD_CHART_SEPARAT = addBoolProp("download-chart-separat", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_EXISTING = addBoolProp("download-chart-only-existing", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_RUNNING = addBoolProp("download-chart-only-running", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN = addIntProp("download-chart-max-time-to-show-min", 30); //MAX Minuten im Chart

    // Gui Abo
    public static BooleanProperty ABO_SEARCH_NOW = addBoolProp("abo-search-now", Boolean.TRUE);
    public static IntegerProperty ABO_MINUTE_MIN_SIZE = addIntProp("abo-minute-min-size", FilterCheck.FILTER_ALL_OR_MIN); //Vorgabe beim Anlegen eines Abos
    public static IntegerProperty ABO_MINUTE_MAX_SIZE = addIntProp("abo-minute-max-size", FilterCheck.FILTER_DURATION_MAX_MINUTE); //Vorgabe beim Anlegen eines Abos
    public static StringProperty ABO_DIALOG_EDIT_SIZE = addStrProp("abo-dialog-edit-size", "600:800");
    public static DoubleProperty ABO_GUI_FILTER_DIVIDER = addDoubleProp("abo-gui-filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty ABO_GUI_FILTER_DIVIDER_ON = addBoolProp("abo-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty ABO_GUI_DIVIDER = addDoubleProp("abo-gui-divider", ProgConst.GUI_DIVIDER_LOCATION);
    public static BooleanProperty ABO_GUI_DIVIDER_ON = addBoolProp("abo-gui-divider-on", Boolean.TRUE);
    public static StringProperty ABO_GUI_TABLE_WIDTH = addStrProp("abo-gui-table-width");
    public static StringProperty ABO_GUI_TABLE_SORT = addStrProp("abo-gui-table-sort");
    public static StringProperty ABO_GUI_TABLE_UP_DOWN = addStrProp("abo-gui-table-up-down");
    public static StringProperty ABO_GUI_TABLE_VIS = addStrProp("abo-gui-table-vis");
    public static StringProperty ABO_GUI_TABLE_ORDER = addStrProp("abo-gui-table-order");
    public static DoubleProperty ABO_GUI_INFO_DIVIDER = addDoubleProp("abo-gui-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);
    public static StringProperty ABO_DEL_DIALOG_SIZE = addStrProp("abo-del-dialog-size", "600:400");
    public static IntegerProperty ABO_ONLY_STOP = addIntProp("abo-only-stop", AskBeforeDeleteState.DOWNLOAD_STOP__ASK);


    // GuiPaneDialog
    public static StringProperty FILM_PANE_DIALOG_INFO_SIZE = addStrProp("film-pane-dialog-info-size");
    public static BooleanProperty FILM_PANE_DIALOG_INFO_ON = addBoolProp("film-pane-dialog-info-on");
    public static StringProperty FILM_PANE_DIALOG_BUTTON_SIZE = addStrProp("film-pane-dialog-button-size");
    public static BooleanProperty FILM_PANE_DIALOG_BUTTON_ON = addBoolProp("film-pane-dialog-button-on");
    public static StringProperty FILM_PANE_DIALOG_MEDIA_SIZE = addStrProp("film-pane-dialog-media-size");
    public static BooleanProperty FILM_PANE_DIALOG_MEDIA_ON = addBoolProp("film-pane-dialog-media-on");

    public static StringProperty DOWNLOAD_PANE_DIALOG_INFO_SIZE = addStrProp("download-pane-dialog-info-size");
    public static BooleanProperty DOWNLOAD_PANE_DIALOG_INFO_ON = addBoolProp("download-pane-dialog-info-on");
    public static StringProperty DOWNLOAD_PANE_DIALOG_MEDIA_SIZE = addStrProp("download-pane-dialog-media-size");
    public static BooleanProperty DOWNLOAD_PANE_DIALOG_MEDIA_ON = addBoolProp("download-pane-dialog-media-on");
    public static StringProperty DOWNLOAD_PANE_DIALOG_CHART_SIZE = addStrProp("download-pane-dialog-chart-size");
    public static BooleanProperty DOWNLOAD_PANE_DIALOG_CHART_ON = addBoolProp("download-pane-dialog-chart-on");
    public static StringProperty DOWNLOAD_PANE_DIALOG_DOWN_INFO_SIZE = addStrProp("download-pane-dialog-down-info-size");
    public static BooleanProperty DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON = addBoolProp("download-pane-dialog-down-info-on");

    public static StringProperty ABO_PANE_DIALOG_INFO_SIZE = addStrProp("abo-pane-dialog-info-size");
    public static BooleanProperty ABO_PANE_DIALOG_INFO_ON = addBoolProp("abo-pane-dialog-info-on");

    // ConfigDialog
    public static StringProperty IMPORT_MV_DIALOG_SIZE = addStrProp("import-mv-dialog-size", "600:800");
    public static StringProperty BLACK_DIALOG_SIZE = addStrProp("black-dialog-size", "700:800");
    public static StringProperty CONFIG_DIALOG_SIZE = addStrProp("config-dialog-size");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBoolProp("config_dialog-accordion", Boolean.TRUE);
    public static DoubleProperty CONFIG_DIALOG_SET_DIVIDER = addDoubleProp("config-dialog-set-divider", ProgConst.CONFIG_DIALOG_SET_DIVIDER);
    public static StringProperty CONFIG_DIALOG_IMPORT_SET_SIZE = addStrProp("config-dialog-import-set-size", "800:700");
    public static DoubleProperty CONFIG_DIALOG_FILMLIST_FILTER_SPLITPANE = addDoubleProp("config-dialog-filmlist-filter-splitpane", 0.7);
    public static DoubleProperty CONFIG_DIALOG_BLACKLIST_SPLITPANE = addDoubleProp("config-dialog-blacklist-splitpane", 0.7);

    // StartDialog
    public static StringProperty START_DIALOG_DOWNLOAD_PATH = addStrProp("start-dialog-download-path", PSystemUtils.getStandardDownloadPath());

    // FilmInfoDialog
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_URL = addBoolProp("film-info-dialog-show-url", Boolean.TRUE);

    // DownloadAddDialog
    public static BooleanProperty DOWNLOAD_INFO_DIALOG_SHOW_URL = addBoolProp("download-info-dialog-show-url", Boolean.TRUE);

    // Filter Filme
    public static IntegerProperty FILTER_FILM_SEL_FILTER = addIntProp("filter-film-sel-filter");

    // Filter Abo
    public static StringProperty FILTER_ABO_CHANNEL = addStrProp("filter-abo-channel");
    public static StringProperty FILTER_ABO_NAME = addStrProp("filter-abo-name");
    public static StringProperty FILTER_ABO_DESCRIPTION = addStrProp("filter-abo-description");
    public static StringProperty FILTER_ABO_TYPE = addStrProp("filter-abo-type");

    // Filter Download
    public static StringProperty FILTER_DOWNLOAD_CHANNEL = addStrProp("filter-download-channel");
    public static StringProperty FILTER_DOWNLOAD_SOURCE = addStrProp("filter-download-source");
    public static StringProperty FILTER_DOWNLOAD_TYPE = addStrProp("filter-download-type");
    public static StringProperty FILTER_DOWNLOAD_ABO = addStrProp("filter-download-abo");
    public static StringProperty FILTER_DOWNLOAD_STATE = addStrProp("filter-download-state");

    // Shorcuts Hauptmenü
    public static String SHORTCUT_QUIT_PROGRAM_INIT = "Ctrl+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM = addStrProp("SHORTCUT_QUIT_PROGRAM", SHORTCUT_QUIT_PROGRAM_INIT);

    public static String SHORTCUT_QUIT_PROGRAM_WAIT_INIT = "Ctrl+Shift+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM_WAIT = addStrProp("SHORTCUT_QUIT_PROGRAM_WAIT", SHORTCUT_QUIT_PROGRAM_WAIT_INIT);

    public static String SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT = "Ctrl+Alt+M";
    public static StringProperty SHORTCUT_SEARCH_MEDIA_COLLECTION = addStrProp("SHORTCUT_SEARCH_MEDIA_COLLECTION", SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT);

    // Shortcuts Filmmenü
    public static String SHORTCUT_FILM_SHOWN_INIT = "Ctrl+G";
    public static StringProperty SHORTCUT_FILM_SHOWN = addStrProp("SHORTCUT_FILM_SHOWN", SHORTCUT_FILM_SHOWN_INIT);

    public static String SHORTCUT_FILM_NOT_SHOWN_INIT = "Ctrl+Shift+G";
    public static StringProperty SHORTCUT_FILM_NOT_SHOWN = addStrProp("SHORTCUT_FILM_NOT_SHOWN", SHORTCUT_FILM_NOT_SHOWN_INIT);

    public static String SHORTCUT_ADD_BLACKLIST_INIT = "Ctrl+B";
    public static StringProperty SHORTCUT_ADD_BLACKLIST = addStrProp("SHORTCUT_ADD_BLACKLIST", SHORTCUT_ADD_BLACKLIST_INIT);

    public static String SHORTCUT_ADD_BLACKLIST_THEME_INIT = "Ctrl+Shift+B";
    public static StringProperty SHORTCUT_ADD_BLACKLIST_THEME = addStrProp("SHORTCUT_ADD_BLACKLIST_THEME", SHORTCUT_ADD_BLACKLIST_THEME_INIT);

    public static String SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT = "Ctrl+M";
    public static StringProperty SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION = addStrProp("SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION", SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT);

    public static String SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD_INIT = "Alt+H";
    public static StringProperty SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD = addStrProp("SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD", SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD_INIT);

    public static String SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD_INIT = "Alt+T";
    public static StringProperty SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD = addStrProp("SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD", SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD_INIT);

    public static String SHORTCUT_SHOW_FILTER_INIT = "Alt+F";
    public static StringProperty SHORTCUT_SHOW_FILTER = addStrProp("SHORTCUT_SHOW_FILTER", SHORTCUT_SHOW_FILTER_INIT);

    public static String SHORTCUT_SHOW_INFOS_INIT = "Alt+I";
    public static StringProperty SHORTCUT_SHOW_INFOS = addStrProp("SHORTCUT_SHOW_INFO", SHORTCUT_SHOW_INFOS_INIT);

    public static String SHORTCUT_INFO_FILM_INIT = "Ctrl+I";
    public static StringProperty SHORTCUT_INFO_FILM = addStrProp("SHORTCUT_INFO_FILM", SHORTCUT_INFO_FILM_INIT);

    public static String SHORTCUT_PLAY_FILM_INIT = "Ctrl+P";
    public static StringProperty SHORTCUT_PLAY_FILM = addStrProp("SHORTCUT_PLAY_FILM", SHORTCUT_PLAY_FILM_INIT);

    public static String SHORTCUT_PLAY_FILM_ALL_INIT = "Ctrl+A";
    public static StringProperty SHORTCUT_PLAY_FILM_ALL = addStrProp("SHORTCUT_PLAY_FILM_ALL", SHORTCUT_PLAY_FILM_ALL_INIT);

    public static String SHORTCUT_SAVE_FILM_INIT = "Ctrl+S";
    public static StringProperty SHORTCUT_SAVE_FILM = addStrProp("SHORTCUT_SAVE_FILM", SHORTCUT_SAVE_FILM_INIT);

    // Shortcuts Downloadmenü
    public static String SHORTCUT_DOWNLOAD_START_INIT = "Ctrl+D";
    public static StringProperty SHORTCUT_DOWNLOAD_START = addStrProp("SHORTCUT_DOWNLOAD_START", SHORTCUT_DOWNLOAD_START_INIT);

    public static String SHORTCUT_DOWNLOAD_STOP_INIT = "Ctrl+T";
    public static StringProperty SHORTCUT_DOWNLOAD_STOP = addStrProp("SHORTCUT_DOWNLOAD_STOP", SHORTCUT_DOWNLOAD_STOP_INIT);

    public static String SHORTCUT_DOWNLOAD_CHANGE_INIT = "Ctrl+C";
    public static StringProperty SHORTCUT_DOWNLOAD_CHANGE = addStrProp("SHORTCUT_DOWNLOAD_CHANGE", SHORTCUT_DOWNLOAD_CHANGE_INIT);

    public static String SHORTCUT_UNDO_DELETE_INIT = "Ctrl+Z";
    public static StringProperty SHORTCUT_UNDO_DELETE = addStrProp("SHORTCUT_DOWNLOAD_UNDO_DELETE", SHORTCUT_UNDO_DELETE_INIT);

    public static String SHORTCUT_DOWNLOADS_UPDATE_INIT = "CTRL+U";
    public static StringProperty SHORTCUT_DOWNLOAD_UPDATE = addStrProp("SHORTCUT_DOWNLOAD_UPDATE", SHORTCUT_DOWNLOADS_UPDATE_INIT);

    public static String SHORTCUT_DOWNLOAD_CLEAN_UP_INIT = "CTRL+O";
    public static StringProperty SHORTCUT_DOWNLOAD_CLEAN_UP = addStrProp("SHORTCUT_DOWNLOAD_CLEAN_UP", SHORTCUT_DOWNLOAD_CLEAN_UP_INIT);

    private static final String[] PARAMETER_INFO = new String[]{
            "\"__system-parameter__xxx\" können nur im Konfigfile geändert werden",
            "\t" + "und sind auch nicht für ständige Änderungen gedacht.",
            "\t" + "Wird eine Zeile gelöscht, wird der Parameter wieder mit dem Standardwert angelegt.",
            PLog.LILNE3,
            "  *" + "\t" + "Timeout für direkte Downloads, Standardwert: "
                    + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue(),
            "  *" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, am Ende aller Downloads",
            "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: " +
                    SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue(),
            "  *" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, direkt beim Download,",
            "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: "
                    + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue(),
            "  *" + "\t" + "Beim Dialog \"Download weiterführen\" wird nach dieser Zeit der Download weitergeführt, Standardwert: "
                    + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue(),
            "  *" + "\t" + "Beim Dialog \"Automode\" wird nach dieser Zeit der das Programm beendet, Standardwert: "
                    + SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getValue(),
            "  *" + "\t" + "Downloadfehlermeldung wird xx Sedunden lang angezeigt, Standardwert: "
                    + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue(),
            "  *" + "\t" + "Downloadprogress im Terminal (-auto) anzeigen: "
                    + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getValue()};

    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private ProgConfig() {
        super("ProgConfig");
    }

    public static final ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile) {
        ProgData progData = ProgData.getInstance();

        // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
        ProgConfig.SYSTEM_PROG_VERSION.set(ProgramToolsFactory.getProgVersion());
        ProgConfig.SYSTEM_PROG_BUILD_NO.set(ProgramToolsFactory.getBuild());
        ProgConfig.SYSTEM_PROG_BUILD_DATE.set(ProgramToolsFactory.getCompileDate());

        configFile.addConfigs(ProgConfig.getInstance()); //Progconfig
        configFile.addConfigs(ProgColorList.getInstance()); //Color

        configFile.addConfigs(progData.setDataList);

        // Filter
        final FastFilmFilter fastFilmFilter = progData.filmFilterWorker.getFastFilterSettings(); //fast-Filter
        configFile.addConfigs(fastFilmFilter);

        final FilmFilter akt_sf = progData.filmFilterWorker.getActFilterSettings(); //akt-Filter
        akt_sf.setName(FilmFilterWorker.SELECTED_FILTER_NAME); // nur zur Info im Config-File
        configFile.addConfigs(akt_sf);

        configFile.addConfigs(progData.filmFilterWorker.getStoredFilterList()); //Filterprofile

        // Rest
        configFile.addConfigs(progData.aboList);
        configFile.addConfigs(progData.filmListFilter);
        configFile.addConfigs(progData.blackList);
        configFile.addConfigs(progData.mediaCleaningList);
        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.downloadList);
        configFile.addConfigs(progData.mediaCollectionDataList);
    }

    public static void logAllConfigs() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE1);

        Collections.addAll(list, PARAMETER_INFO);
        list.add(PLog.LILNE2);
        list.add("Programmeinstellungen");
        list.add(PLog.LILNE3);
        Arrays.stream(ProgConfig.getInstance().getConfigsArr()).forEach(c -> {
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
        PStringUtils.appendString(list, "#  ", "#");

        list.add(PLog.LILNE1);
        PLog.debugLog(list);
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }
}
