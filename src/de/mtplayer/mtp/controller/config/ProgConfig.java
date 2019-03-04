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

import de.mtplayer.mLib.tools.MLBandwidthTokenBucket;
import de.mtplayer.mLib.tools.MLConfig;
import de.mtplayer.mLib.tools.MLConfigs;
import de.mtplayer.mLib.tools.SystemInfo;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

public class ProgConfig extends MLConfig {

    public static final String SYSTEM = "system";

    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addNewKey("__system-parameter__download-timeout-second_250__", "250");
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addNewKey("__system-parameter__download-max-restart_5__", "5");
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addNewKey("__system-parameter__download-max-restart-http_10__", "10");
    // Beim Dialog "Download weiterführen" wird in dieser Zeit der DownloadXml weitergeführt
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND = addNewKey("__system-parameter__download-continue-second_60__", "60");
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addNewKey("__system-parameter__download-errormsg-in-second_30__", "30");
    // Downloadprogress im Terminal anzeigen
    public static MLConfigs SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addNewKey("__system-parameter__download_progress_", Boolean.TRUE.toString());
    // ===========================================


    // Configs zur Programmupdatesuche
    public static MLConfigs SYSTEM_UPDATE_PROGRAM_VERSION = addNewKey("system-update-program-version");
    public static MLConfigs SYSTEM_UPDATE_SEARCH = addNewKey("system-update-search", Boolean.TRUE.toString());
    public static MLConfigs SYSTEM_UPDATE_DATE = addNewKey("system-update-date");
    public static MLConfigs SYSTEM_UPDATE_INFO_NR_SHOWN = addNewKey("system-update-info-nr-shown");
    public static MLConfigs SYSTEM_UPDATE_PROGSET_VERSION = addNewKey("system-update-progset-version");


    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_PLAY = new SimpleIntegerProperty(-1);

    // MediaDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_MEDIA = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_MEDIA_DIALOG_HISTORY = new SimpleIntegerProperty(-1);

    // Configs
    public static MLConfigs SYSTEM_USERAGENT = addNewKey("system-useragent", ProgConst.USER_AGENT_DEFAULT);    // Useragent für direkte Downloads
    public static MLConfigs SYSTEM_USE_REPLACETABLE = addNewKey("system-use-replacetable", SystemInfo.isLinux() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    public static MLConfigs SYSTEM_ONLY_ASCII = addNewKey("system-only-ascii", Boolean.FALSE.toString());
    public static MLConfigs SYSTEM_PROG_OPEN_DIR = addNewKey("system-prog-open-dir");
    public static MLConfigs SYSTEM_PROG_OPEN_URL = addNewKey("system-prog-open-url");
    public static MLConfigs SYSTEM_PROG_PLAY_FILME = addNewKey("system-prog-play-filme");
    public static MLConfigs SYSTEM_MARK_GEO = addNewKey("system-mark-geo", Boolean.TRUE.toString());
    public static MLConfigs SYSTEM_GEO_HOME_PLACE = addNewKey("system-geo-home-place", Film.GEO_DE);
    public static MLConfigs SYSTEM_LOG_DIR = addNewKey("system-log-dir", "");
    public static MLConfigs SYSTEM_LOG_ON = addNewKey("system-log-on", Boolean.TRUE.toString());
    public static MLConfigs SYSTEM_SMALL_ROW_TABLE_FILM = addNewKey("system-small-row-table-film", Boolean.FALSE.toString());
    public static MLConfigs SYSTEM_SMALL_ROW_TABLE_DOWNLOAD = addNewKey("system-small-row-table-download", Boolean.FALSE.toString());

    // Fenstereinstellungen
    public static MLConfigs SYSTEM_SIZE_GUI = addNewKey("system-size-gui", "1000:900");
    public static MLConfigs SYSTEM_SIZE_DIALOG_FILMINFO = addNewKey("system-size-dialog-filminfo", "600:800");

    // Einstellungen Filmliste
    public static MLConfigs SYSTEM_LOAD_FILMS_ON_START = addNewKey("system-load-films-on-start", Boolean.TRUE.toString());
    public static MLConfigs SYSTEM_LOAD_FILMS_MANUALLY = addNewKey("system-load-films-manually", "");
    public static MLConfigs SYSTEM_LOAD_NOT_SENDER = addNewKey("system-load-not-sender", "");
    public static MLConfigs SYSTEM_NUM_DAYS_FILMLIST = addNewKey("system-num-days-filmilst", "0"); //es werden nur die x letzten Tage geladen
    public static MLConfigs SYSTEM_PATH_VLC = addNewKey("path-vlc", SetsPrograms.getTemplatePathVlc());
    public static MLConfigs SYSTEM_PATH_FLVSTREAMER = addNewKey("path-flvstreamer", SetsPrograms.getTemplatePathFlv());
    public static MLConfigs SYSTEM_PATH_FFMPEG = addNewKey("path-ffmpeg", SetsPrograms.getTemplatePathFFmpeg());

    // Blacklist
    public static MLConfigs SYSTEM_BLACKLIST_SHOW_NO_FUTURE = addNewKey("blacklist-show-no-future");
    public static MLConfigs SYSTEM_BLACKLIST_SHOW_NO_GEO = addNewKey("blacklist-show-no-geo");
    public static MLConfigs SYSTEM_BLACKLIST_SHOW_ABO = addNewKey("blacklist-show-abo");
    public static MLConfigs SYSTEM_BLACKLIST_SHOW_ONLY_DAYS = addNewKey("blacklist-show-only-days", "0");
    public static MLConfigs SYSTEM_BLACKLIST_FILMSIZE = addNewKey("blacklist-filmsize", "0");
    public static MLConfigs SYSTEM_BLACKLIST_IS_WHITELIST = addNewKey("blacklist-is-whitelist");

    // Download
    public static MLConfigs DOWNLOAD_START_NOW = addNewKey("download-start-now", Boolean.FALSE.toString());
    public static MLConfigs DOWNLOAD_BEEP = addNewKey("download-beep");
    public static MLConfigs DOWNLOAD_ERROR_MSG = addNewKey("download-error-msg", Boolean.TRUE.toString());
    public static MLConfigs DOWNLOAD_CHART_SEPARAT = addNewKey("download-chart-separat", Boolean.TRUE.toString());
    public static MLConfigs DOWNLOAD_MAX_DOWNLOADS = addNewKey("download-max-downloads", "1");
    public static MLConfigs DOWNLOAD_MAX_ONE_PER_SERVER = addNewKey("download-max-one-per-server"); // nur ein Download pro Server - sonst max 2
    public static MLConfigs DOWNLOAD_MAX_BANDWITH_KBYTE = addNewKey("download-max-bandwith-kbyte", String.valueOf(MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE));

    // Gui Film
    public static MLConfigs FILM_GUI_FILTER_DIVIDER = addNewKey("film-gui-filter-divider", ProgConst.GUI_FILME_FILTER_DIVIDER_LOCATION);
    public static MLConfigs FILM_GUI_FILTER_DIVIDER_ON = addNewKey("film-gui-filter-divider-on", Boolean.TRUE.toString());
    public static MLConfigs FILM_GUI_DIVIDER = addNewKey("film-gui-divider", ProgConst.GUI_FILME_DIVIDER_LOCATION);
    public static MLConfigs FILM_GUI_DIVIDER_ON = addNewKey("film-gui-divider-on", Boolean.TRUE.toString());
    public static MLConfigs FILM_GUI_TABLE_WIDTH = addNewKey("film-gui-table-width");
    public static MLConfigs FILM_GUI_TABLE_SORT = addNewKey("film-gui-table-sort");
    public static MLConfigs FILM_GUI_TABLE_UP_DOWN = addNewKey("film-gui-table-up-down");
    public static MLConfigs FILM_GUI_TABLE_VIS = addNewKey("film-gui-table-vis");
    public static MLConfigs FILM_GUI_TABLE_ORDER = addNewKey("film-gui-table-order");

    // Gui Download
    public static MLConfigs DOWNLOAD_DIALOG_PATH_SAVING = addNewKey("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static MLConfigs DOWNLOAD_DIALOG_START_DOWNLOAD = addNewKey("download-dialog-start-download", Boolean.TRUE.toString());
    public static MLConfigs DOWNLOAD_DIALOG_EDIT_SIZE = addNewKey("download-dialog-edit-size", "800:800");
    public static MLConfigs DOWNLOAD_DIALOG_ADD_SIZE = addNewKey("download-dialog-add-size");
    public static MLConfigs DOWNLOAD_DIALOG_ADD_MORE_SIZE = addNewKey("download-dialog-add-more-size");
    public static MLConfigs DOWNLOAD_DIALOG_CONTINUE_SIZE = addNewKey("download-dialog-continue-size");
    public static MLConfigs DOWNLOAD_DIALOG_ERROR_SIZE = addNewKey("download-dialog-error-size", "");
    public static MLConfigs DOWNLOAD_GUI_FILTER_DIVIDER = addNewKey("download-gui-filter-divider", ProgConst.GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION);
    public static MLConfigs DOWNLOAD_GUI_FILTER_DIVIDER_ON = addNewKey("download-gui-filter-divider-on", Boolean.TRUE.toString());
    public static MLConfigs DOWNLOAD_GUI_DIVIDER = addNewKey("download-gui-divider", ProgConst.GUI_DOWNLOAD_DIVIDER_LOCATION);
    public static MLConfigs DOWNLOAD_GUI_DIVIDER_ON = addNewKey("download-gui-divider-on", Boolean.TRUE.toString());
    public static MLConfigs DOWNLOAD_GUI_TABLE_WIDTH = addNewKey("download-gui-table-width");
    public static MLConfigs DOWNLOAD_GUI_TABLE_SORT = addNewKey("download-gui-table-sort");
    public static MLConfigs DOWNLOAD_GUI_TABLE_UP_DOWN = addNewKey("download-gui-table-up-down");
    public static MLConfigs DOWNLOAD_GUI_TABLE_VIS = addNewKey("download-gui-table-vis");
    public static MLConfigs DOWNLOAD_GUI_TABLE_ORDER = addNewKey("download-gui-table-order");
    public static MLConfigs DOWNLOAD_SHOW_NOTIFICATION = addNewKey("download-show-notification", Boolean.TRUE.toString());

    // Gui Abo
    public static MLConfigs ABO_SEARCH_NOW = addNewKey("abo-search-now", Boolean.TRUE.toString());
    public static MLConfigs ABO_MINUTE_MIN_SIZE = addNewKey("abo-minute-min-size", 0); //Vorgabe beim Anlegen eines Abos
    public static MLConfigs ABO_MINUTE_MAX_SIZE = addNewKey("abo-minute-max-size", SelectedFilter.FILTER_DURATION_MAX_MIN); //Vorgabe beim Anlegen eines Abos
    public static MLConfigs ABO_DIALOG_EDIT_SIZE = addNewKey("abo-dialog-edit-size");
    public static MLConfigs ABO_GUI_FILTER_DIVIDER = addNewKey("abo-gui-filter-divider", ProgConst.GUI_ABO_FILTER_DIVIDER_LOCATION);
    public static MLConfigs ABO_GUI_FILTER_DIVIDER_ON = addNewKey("abo-gui-filter-divider-on", Boolean.TRUE.toString());
    public static MLConfigs ABO_GUI_DIVIDER = addNewKey("abo-gui-divider", ProgConst.GUI_ABO_DIVIDER_LOCATION);
    public static MLConfigs ABO_GUI_DIVIDER_ON = addNewKey("abo-gui-divider-on", Boolean.TRUE.toString());
    public static MLConfigs ABO_GUI_TABLE_WIDTH = addNewKey("abo-gui-table-width");
    public static MLConfigs ABO_GUI_TABLE_SORT = addNewKey("abo-gui-table-sort");
    public static MLConfigs ABO_GUI_TABLE_UP_DOWN = addNewKey("abo-gui-table-up-down");
    public static MLConfigs ABO_GUI_TABLE_VIS = addNewKey("abo-gui-table-vis");
    public static MLConfigs ABO_GUI_TABLE_ORDER = addNewKey("abo-gui-table-order");

    // Meldungen
    public static MLConfigs MSG_VISIBLE = addNewKey("msg-visible", Boolean.FALSE.toString());
    public static MLConfigs MSG_PANEL_LOGS_DIVIDER = addNewKey("msg-panel-logs-divider", ProgConst.GUI_MSG_LOG_DIVIDER_LOCATION);
    public static MLConfigs MSG_PANEL_DIVIDER = addNewKey("msg-panel-divider", ProgConst.GUI_MSG_DIVIDER_LOCATION);

    // ConfigDialog
    public static MLConfigs CONFIG_DIALOG_SIZE = addNewKey("config-dialog-size");
    public static MLConfigs CONFIG_DIALOG_ACCORDION = addNewKey("config_dialog-accordion", Boolean.TRUE.toString());
    public static MLConfigs CONFIG_DIALOG_SET_DIVIDER = addNewKey("config-dialog-set-divider", ProgConst.CONFIG_DIALOG_SET_DIVIDER);
    public static MLConfigs CONFIG_DIALOG_IMPORT_SET_SIZE = addNewKey("config-dialog-import-set-size", "800:700");

    // StartDialog
    public static MLConfigs START_DIALOG_DOWNLOAD_PATH = addNewKey("start-dialog-download-path", PSystemUtils.getStandardDownloadPath());

    // FilmInfoDialog
    public static MLConfigs FILM_INFO_DIALOG_SHOW_URL = addNewKey("film-info-dialog-show-url", Boolean.TRUE.toString());
    public static MLConfigs FILM_INFO_DIALOG_SHOW_URL_INDERTERMINATE = addNewKey("film-info-dialog-show-url-indeterminate", Boolean.TRUE.toString());

    // MediaDB
    public static MLConfigs MEDIA_CONFIG_DIALOG_SIZE = addNewKey("media-config-dialog-size", "800:700");
    public static MLConfigs MEDIA_CONFIG_DIALOG_ACCORDION = addNewKey("media-config-dialog-accordion", Boolean.TRUE.toString());
    public static MLConfigs MEDIA_DIALOG_SIZE = addNewKey("media-dialog-size", "800:700");
    public static MLConfigs MEDIA_DB_SUFFIX = addNewKey("media-db-suffix");
    public static MLConfigs MEDIA_DB_WITH_OUT_SUFFIX = addNewKey("media-db-with-out-suffix");
    public static MLConfigs MEDIA_DB_NO_HIDDEN_FILES = addNewKey("media-db-no-hidden-files");

    // Filter Abo
    public static MLConfigs FILTER_ABO_SENDER = addNewKey("filter-abo-sender");
    public static MLConfigs FILTER_ABO_DESCRIPTION = addNewKey("filter-abo-beschreibung");
    public static MLConfigs FILTER_ABO_KIND = addNewKey("filter-abo-kind");

    // Filter Download
    public static MLConfigs FILTER_DOWNLOAD_SENDER = addNewKey("filter-download-sender");
    public static MLConfigs FILTER_DOWNLOAD_SOURCE = addNewKey("filter-download-source");
    public static MLConfigs FILTER_DOWNLOAD_KIND = addNewKey("filter-download-kind");
    public static MLConfigs FILTER_DOWNLOAD_ABO = addNewKey("filter-download-abo");
    public static MLConfigs FILTER_DOWNLOAD_STATE = addNewKey("filter-download-state");

    // Farben
    public static MLConfigs COLOR__FILM_LIVESTREAM = addNewKey("COLOR_FILM_LIVESTREAM");
    public static MLConfigs COLOR__FILM_HISTORY = addNewKey("COLOR_FILM_HISTORY");
    public static MLConfigs COLOR__FILM_NEW = addNewKey("COLOR_FILM_NEW");
    public static MLConfigs COLOR__FILM_RESERVED = addNewKey("COLOR_FILM_RESERVED");
    public static MLConfigs COLOR__FILM_GEOBLOCK_BACKGROUND = addNewKey("COLOR_FILM_GEOBLOCK_BACKGROUND");
    public static MLConfigs COLOR__FILM_GEOBLOCK_BACKGROUND_SEL = addNewKey("COLOR_FILM_GEOBLOCK_BACKGROUND_SEL");
    public static MLConfigs COLOR__DOWNLOAD_IS_ABO = addNewKey("COLOR_DOWNLOAD_IS_ABO");
    public static MLConfigs COLOR__DOWNLOAD_IS_DIREKT_DOWNLOAD = addNewKey("COLOR_DOWNLOAD_IS_DIREKT_DOWNLOAD");
    public static MLConfigs COLOR__DOWNLOAD_SHOW = addNewKey("COLOR_DOWNLOAD_SHOW");
    public static MLConfigs COLOR__DOWNLOAD_WAIT = addNewKey("COLOR_DOWNLOAD_WAIT");
    public static MLConfigs COLOR__DOWNLOAD_WAIT_SEL = addNewKey("COLOR_DOWNLOAD_WAIT_SEL");
    public static MLConfigs COLOR__DOWNLOAD_RUN = addNewKey("COLOR_DOWNLOAD_RUN");
    public static MLConfigs COLOR__DOWNLOAD_RUN_SEL = addNewKey("COLOR_DOWNLOAD_RUN_SEL");
    public static MLConfigs COLOR__DOWNLOAD_FINISHED = addNewKey("COLOR_DOWNLOAD_FINISHED");
    public static MLConfigs COLOR__DOWNLOAD_FINISHED_SEL = addNewKey("COLOR_DOWNLOAD_FINISHED_SEL");
    public static MLConfigs COLOR__DOWNLOAD_ERROR = addNewKey("COLOR_DOWNLOAD_ERROR");
    public static MLConfigs COLOR__DOWNLOAD_ERROR_SEL = addNewKey("COLOR_DOWNLOAD_ERROR_SEL");
    public static MLConfigs COLOR__ABO_SWITCHED_OFF = addNewKey("COLOR_ABO_SWITCHED_OFF");
    public static MLConfigs COLOR__ABO_SWITCHED_OFF_SEL = addNewKey("COLOR_ABO_SWITCHED_OFF_SEL");
    public static MLConfigs COLOR__FILTER_REGEX = addNewKey("COLOR_FILTER_REGEX");
    public static MLConfigs COLOR__FILTER_REGEX_ERROR = addNewKey("COLOR_FILTER_REGEX_ERROR");
    public static MLConfigs COLOR__DOWNLOAD_NAME_EXISTS = addNewKey("COLOR_DOWNLOAD_NAME_EXISTS");
    public static MLConfigs COLOR__DOWNLOAD_NAME_NEW = addNewKey("COLOR_DOWNLOAD_NAME_NEW");
    public static MLConfigs COLOR__DOWNLOAD_NAME_OLD = addNewKey("COLOR_DOWNLOAD_NAME_OLD");
    public static MLConfigs COLOR__DOWNLOAD_NAME_ERROR = addNewKey("COLOR_DOWNLOAD_NAME_ERROR");


    public static String PARAMETER_INFO = PConst.LINE_SEPARATOR + "\t"
            + "\"__system-parameter__xxx\" können nur im Konfigfile geändert werden" + PConst.LINE_SEPARATOR
            + "\t" + "und sind auch nicht für ständige Änderungen gedacht." + PConst.LINE_SEPARATOR
            + "\t" + "Wird eine Zeile gelöscht, wird der Parameter wieder mit dem Standardwert angelegt." + PConst.LINE_SEPARATOR
            + PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "Timeout für direkte Downloads, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getInitValue() + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "max. Startversuche für fehlgeschlagene Downloads, am Ende aller Downloads" + PConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: " +
            SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getInitValue() + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "max. Startversuche für fehlgeschlagene Downloads, direkt beim Download," + PConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getInitValue() + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "Beim Dialog \"Download weiterführen\" wird nach dieser Zeit der DownloadXml weitergeführt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND.getInitValue() + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "Downloadfehlermeldung wird xx Sedunden lang angezeigt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getInitValue() + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR
            + "\t" + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getKey() + PConst.LINE_SEPARATOR
            + "\t" + "Downloadprogress im Terminal (-auto) anzeigen: "
            + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getInitValue() + PConst.LINE_SEPARATOR;

    public static void logAllConfigs() {
        ArrayList<String> list = new ArrayList<>();

        list.add(PLog.LILNE2);
        list.add("Programmeinstellungen");
        list.add("===========================");
        for (final String[] s : ProgConfig.getAll()) {
            if (!s[1].isEmpty()) {
                list.add(s[0] + "\t\t" + s[1]);
            }
        }
        list.add(PLog.LILNE2);
        PStringUtils.appendString(list, "|  ", "=");

        PLog.emptyLine();
        PLog.sysLog(list);
        PLog.emptyLine();
    }


    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, 5, 1000);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, 0, 100);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, 0, 100);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECOND, 5, 1000);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, 5, 1000);
    }
}
