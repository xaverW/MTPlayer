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
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.filter.FastFilter;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.filter.live.LiveFilter;
import de.p2tools.mtplayer.gui.chart.ChartGenerateFactory;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.pdata.P2Data;
import de.p2tools.p2lib.configfile.pdata.P2DataProgConfig;
import de.p2tools.p2lib.mediathek.download.GetProgramStandardPath;
import de.p2tools.p2lib.mediathek.download.MtBandwidthTokenBucket;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.P2ShutDown;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

import java.time.LocalDate;
import java.util.List;

public class ProgConfig extends P2DataProgConfig {

    private static ProgConfig instance;

    private ProgConfig() {
        super("ProgConfig");
    }

    public static ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile, boolean save) {
        ProgData progData = ProgData.getInstance();

        // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
        SYSTEM_PROG_VERSION.set(P2InfoFactory.getProgVersion());
        SYSTEM_PROG_BUILD_NO.set(P2InfoFactory.getBuildNo());
        SYSTEM_PROG_BUILD_DATE.set(P2InfoFactory.getBuildDateR());

        configFile.addConfigs(ProgConfig.getInstance()); // ProgConfig
        configFile.addConfigs(ProgColorList.getInstance()); // Color

        configFile.addConfigs(progData.setDataList);

        // =================
        // Filter, Filme
        final FastFilter fastFilter = progData.filterWorkerFilm.getFastFilterSettings(); // Fast-Filter
        configFile.addConfigs(fastFilter);

        final FilmFilter akt_sf = progData.filterWorkerFilm.getActFilterSettings(); // akt-Filter
        configFile.addConfigs(akt_sf);

        final FilmFilter sSkt_sf = progData.filterWorkerFilm.getStoredFilterSettings(); // akt-Filter
        configFile.addConfigs(sSkt_sf);

        final FilmFilter sSmallAkt_sf = progData.filterWorkerFilm.getStoredSmallFilterSettings(); // akt-Filter
        configFile.addConfigs(sSmallAkt_sf);

        configFile.addConfigs(progData.filterWorkerFilm.getFilmFilterList()); // Filterprofile
        configFile.addConfigs(progData.filterWorkerFilm.getBackwardFilterList()); // Filterprofile
        configFile.addConfigs(progData.filterWorkerFilm.getForwardFilterList()); // Filterprofile

        // =================
        // Filter, Audio
        final FastFilter fastAudioFilter = progData.filterWorkerAudio.getFastFilterSettings(); // Fast-Filter
        configFile.addConfigs(fastAudioFilter);

        final FilmFilter akt_audio_sf = progData.filterWorkerAudio.getActFilterSettings(); // akt-Filter
        configFile.addConfigs(akt_audio_sf);
        final FilmFilter sSkt_audio_sf = progData.filterWorkerAudio.getStoredFilterSettings(); // akt-Filter
        configFile.addConfigs(sSkt_audio_sf);
        final FilmFilter sSmallAkt_audio_sf = progData.filterWorkerAudio.getStoredSmallFilterSettings(); // akt-Filter
        configFile.addConfigs(sSmallAkt_audio_sf);

        configFile.addConfigs(progData.filterWorkerAudio.getFilmFilterList()); // Filterprofile
        configFile.addConfigs(progData.filterWorkerAudio.getBackwardFilterList()); // Filterprofile
        configFile.addConfigs(progData.filterWorkerAudio.getForwardFilterList()); // Filterprofile

        // Filter
        configFile.addConfigs(progData.textFilterListFilm); // ist der "sortierte" Textfilter (Thema, Titel ..)
        configFile.addConfigs(progData.textFilterListAudio); // ist der "sortierte" Textfilter (Thema, Titel ..)
        configFile.addConfigs(progData.stringFilterLists); // sind die Textfilter in den CBO's

        // Live-Filter
        final LiveFilter akt_live = progData.liveFilmFilterWorker.getActFilterSettings(); // Live-Filter
        configFile.addConfigs(akt_live);

        // Rest
        configFile.addConfigs(progData.aboList);
        configFile.addConfigs(progData.filmListFilter);
        configFile.addConfigs(progData.blackList);
        configFile.addConfigs(progData.cleaningDataListMedia);
        configFile.addConfigs(progData.cleaningDataListPropose);
        configFile.addConfigs(progData.proposeList);
        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.offerList);
        configFile.addConfigs(progData.utDataList);
        configFile.addConfigs(progData.markDataList);
        if (save) {
            // dann nur die selbst angelegten Downloads
            configFile.addConfigs(progData.downloadList.getCopyForSaving());
        } else {
            // beim lesen in die DownloadListe einsortieren
            configFile.addConfigs(progData.downloadList);
        }
        configFile.addConfigs(progData.mediaCollectionDataList);
    }


    // ============================================
    // Programm-Configs, änderbar nur im Config-File

    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addIntProp("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);

    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addIntProp("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);

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
    // ========================================================


    // ===============================================================
    // ====== SYSTEM =================================================
    // ===============================================================

    static {
        addComment("Prog-Version");
    }

    // Configs der Programmversion, nur damit sie (zur Update-Suche: Anzeige WhatsNew) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStrProp("system-prog-version", P2InfoFactory.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStrProp("system-prog-build-no", P2InfoFactory.getBuildNo());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStrProp("system-prog-build-date", P2InfoFactory.getBuildDateR()); // 2024.08.12

    static {
        addComment("ProgrammUpdateSuche");
    }

    // Configs zur ProgrammUpdateSuche
    public static StringProperty SYSTEM_SEARCH_UPDATE_TODAY_DONE = addStrProp("system-search-update-today-done"); // Datum, wenn heute, dann heute schon mal gemacht
    public static StringProperty SYSTEM_SEARCH_UPDATE_LAST_DATE = addStrProp("system-search-update-last-date"); // Datum der letzten Prüfung
    public static BooleanProperty SYSTEM_SEARCH_UPDATE = addBoolProp("system-search-update" + P2Data.TAGGER + "system-update-search-act", Boolean.TRUE); // nach einem Update suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBoolProp("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBoolProp("system-update-search-daily", Boolean.FALSE); //daily suchen

    static {
        addEmptyLine();
    }

    // configs zum Anzeigen der WhatsNew
    public static StringProperty SYSTEM_WHATS_NEW_DATE_LAST_SHOWN = addStrProp("system-whats-new-date-last-shown", P2LDateFactory.toStringR(LocalDate.MIN)); // Date der letzten Anzeige

    static {
        addEmptyLine();
    }

    // Configs zum Aktualisieren beim Programmupdate
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBoolProp("system-after-update-filter", Boolean.FALSE);
    public static BooleanProperty SYSTEM_AFTER_UPDATE_THEME_EXACT_FILTER = addBoolProp("system-after-update-theme-exact-filter", Boolean.FALSE);
    public static BooleanProperty SYSTEM_AFTER_UPDATE_RBTV = addBoolProp("system-after-update-rbtv", Boolean.FALSE);
    public static BooleanProperty SYSTEM_ABO_START_TIME = addBoolProp("system-abo-start-time", Boolean.FALSE);
    public static BooleanProperty SYSTEM_CHANGE_LOG_DIR = addBoolProp("system-change-log-dir", Boolean.FALSE);
    public static BooleanProperty SYSTEM_USE_NEW_BOOKMARK_FILE = addBoolProp("system-use-new-bookmark-file", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_FILTER = addBoolProp("system-small-filter", Boolean.FALSE);
    public static BooleanProperty SYSTEM_UPDATE_LOAD_FILMLIST_PROGRAMSTART = addBoolProp("system-update-load-filmlist-programstart", Boolean.FALSE);
    public static BooleanProperty SYSTEM_UPDATE_OFFER_FILTER = addBoolProp("system-update-offer-filter", Boolean.FALSE);
    public static BooleanProperty SYSTEM_RESET_COLOR_LIST = addBoolProp("system-reset-color-list", Boolean.FALSE);

    // Fenstereinstellungen
    static {
        addComment("Fenstereinstellungen");
    }

    public static StringProperty SYSTEM_SIZE_GUI = addStrProp("system-size-gui", "1100:800");
    public static BooleanProperty SYSTEM_GUI_LAST_START_WAS_MAXIMISED = addBoolProp("system-gui-last-start-was-maximised", Boolean.FALSE); // Programm wurde Maximised beendet
    public static BooleanProperty SYSTEM_GUI_START_ALWAYS_MAXIMISED = addBoolProp("system-gui-start-always-maximised", Boolean.FALSE);

    static {
        addEmptyLine();
    }

    //Configs zur Anzeige der Diakritika in der Filmliste
    //TRUE: dann werden Diakritika nicht geändert und angezeigt --> das kommt weg
    public static BooleanProperty SYSTEM_SHOW_DIACRITICS = addBoolProp("system-show-diacritics", Boolean.TRUE);

    //Configs zur Anzeige der Diakritika in der Filmliste
    //TRUE: dann werden Diakritika nicht geändert und angezeigt
    public static BooleanProperty SYSTEM_REMOVE_DIACRITICS = addBoolProp("system-remove-diacritics", Boolean.FALSE);

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_BLACKLIST = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_MEDIA = new SimpleIntegerProperty(-1);

    // Configs
    public static BooleanProperty SYSTEM_USE_OWN_PROGRAM_ICON = addBoolProp("system-use-own-program-icon" + P2Data.TAGGER + "system-use--own-program-icon", Boolean.FALSE);
    public static StringProperty SYSTEM_PROGRAM_ICON_PATH = addStrProp("system-program-icon", ""); //ein eigenes Programm-Icon
    public static BooleanProperty SYSTEM_TRAY = addBoolProp("system-tray", Boolean.FALSE);
    public static BooleanProperty SYSTEM_TRAY_USE_OWN_ICON = addBoolProp("system-tray-own-icon", Boolean.FALSE);
    public static StringProperty SYSTEM_TRAY_ICON_PATH = addStrProp("system-tray-icon", ""); //ein eigenes Tray-Icon
    public static StringProperty SYSTEM_USERAGENT = addStrProp("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static IntegerProperty SYSTEM_FILMLIST_FILTER = addIntProp("system-filmlist-filter", BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
    public static StringProperty SYSTEM_SHUT_DOWN_CALL = addStrProp("system-shut-down-call", P2ShutDown.getShutDownCommand()); // shutDown call
    public static BooleanProperty SYSTEM_SHUT_DOWN_CALL_ON = addBoolProp("system-shut-down-call-on", false); // das shutDown ist aktiv
    public static BooleanProperty SYSTEM_QUITT_DIALOG_MINIMIZE = addBoolProp("system-quitt-dialog-minimize", false); // Programm minimize nach "Warten"
    public static BooleanProperty CHECK_SET_PROGRAM_START = addBoolProp("check-set-program-start", Boolean.TRUE); // die Sets prüfen
    public static StringProperty CHECK_SET_DIALOG_SIZE = addStrProp("check-set-dialog-size", "400:500");

    //Configs Statusbar
    static {
        addComment("Statusbar");
    }

    public static BooleanProperty SYSTEM_STATUS_BAR_ON = addBoolProp("system-status-bar-on", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_SEL = addBoolProp("system-status-bar-field-sel", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_LEFT = addBoolProp("system-status-bar-field-left", true);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_DOT = addBoolProp("system-status-bar-field-dot", false);
    public static BooleanProperty SYSTEM_STATUS_BAR_FIELD_RIGHT = addBoolProp("system-status-bar-field-right", true);

    // Proxy
    static {
        addComment("Proxy");
    }

    public static BooleanProperty SYSTEM_USE_PROXY = addBoolProp("system-use-proxy", Boolean.FALSE);
    public static StringProperty SYSTEM_PROXY_HOST = addStrProp("system-proxy-host", "");
    public static StringProperty SYSTEM_PROXY_PORT = addStrProp("system-proxy-port", "");
    public static StringProperty SYSTEM_PROXY_USER = addStrProp("system-proxy-user", "");
    public static StringProperty SYSTEM_PROXY_PWD = addStrProp("system-proxy-pwd", "");

    static {
        addEmptyLine();
    }

    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBoolProp("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_USE_OFFERTABLE = addBoolProp("system-use-offertable", true);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBoolProp("system-only-ascii", Boolean.FALSE);
    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStrProp("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStrProp("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_PLAY_FILME = addStrProp("system-prog-play-filme");
    public static BooleanProperty SYSTEM_MARK_GEO = addBoolProp("system-mark-geo", Boolean.TRUE);
    public static StringProperty SYSTEM_GEO_HOME_PLACE = addStrProp("system-geo-home-place", FilmDataMTP.GEO_DE);
    public static StringProperty SYSTEM_LOG_DIR = addStrProp("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBoolProp("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_ONLY_ONE_INSTANCE = addBoolProp("system-only-one-instance", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBoolProp("system-ssl-always-true");
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addIntProp("system-filter-wait-time", 250);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBoolProp("system-filter-return", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILTER_FIRST_ROW = addBoolProp("system-filter-first-row", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILTER_NONE_ROW = addBoolProp("system-filter-none-row", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILTER_REG_EX_ONLY_CONTAIN = addBoolProp("system-filter-reg-ex-only-contain", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStrProp("system-download-dir-new-version", ""); // wird beim Download auf das gewählte, gesetzt

    public static IntegerProperty SYSTEM_FONT_SIZE = addIntProp("system-font-size", 0);
    public static BooleanProperty SYSTEM_FONT_SIZE_CHANGE = addBoolProp("system-font-size-change", Boolean.FALSE); // für die Schriftgröße


    public static BooleanProperty SYSTEM_DARK_THEME = addBoolProp("system-dark-theme", Boolean.FALSE); // DARK oder LIGHT
    public static BooleanProperty SYSTEM_GUI_THEME_1 = addBoolProp("system-gui-theme-1", Boolean.TRUE); // Theme 1 oder 2
    public static StringProperty SYSTEM_CSS_ADDER = addStrProp("system-css-adder");

    public static BooleanProperty SYSTEM_DARK_START = addBoolProp("system-dark-theme-start", Boolean.FALSE);
    public static BooleanProperty SYSTEM_GUI_THEME_1_START = addBoolProp("system-gui-theme-1-start", Boolean.FALSE);

    public static BooleanProperty SYSTEM_THEME_CHANGED = addBoolProp("system-theme-changed"); // hat sich geändert
    public static StringProperty SYSTEM_ICON_COLOR = addStrProp("system-icon-color", ProgConst.ICON_COLOR_LIGHT_1); // die aktuelle Icon-Farbe
    public static StringProperty SYSTEM_GUI_COLOR = addStrProp("system-gui-color", ProgConst.GUI_COLOR_LIGHT_1); // die aktuelle GUI-Farbe
    public static StringProperty SYSTEM_BACKGROUND_COLOR = addStrProp("system-backup-color", ProgConst.GUI_BACKGROUND_LIGHT_1); // die aktuelle Hintergrund-Farbe
    public static StringProperty SYSTEM_TITLE_BAR_COLOR = addStrProp("system-backup-color", ProgConst.GUI_TITLE_BAR_LIGHT_1); // die aktuelle Hintergrund-Farbe
    public static StringProperty SYSTEM_TITLE_BAR_SEL_COLOR = addStrProp("system-backup-color", ProgConst.GUI_TITLE_BAR_SEL_LIGHT_1); // die aktuelle Hintergrund-Farbe

    public static StringProperty SYSTEM_ICON_THEME_DARK_1 = addStrProp("system-icon-theme-dark-1", ProgConst.ICON_COLOR_DARK_1);
    public static StringProperty SYSTEM_ICON_THEME_DARK_2 = addStrProp("system-icon-theme-dark-2", ProgConst.ICON_COLOR_DARK_2);
    public static StringProperty SYSTEM_ICON_THEME_LIGHT_1 = addStrProp("system-icon-theme-light-1", ProgConst.ICON_COLOR_LIGHT_1);
    public static StringProperty SYSTEM_ICON_THEME_LIGHT_2 = addStrProp("system-icon-theme-light2", ProgConst.ICON_COLOR_LIGHT_2);

    public static StringProperty SYSTEM_GUI_THEME_DARK_1 = addStrProp("system-gui-theme-dark-1", ProgConst.GUI_COLOR_DARK_1);
    public static StringProperty SYSTEM_GUI_THEME_DARK_2 = addStrProp("system-gui-theme-dark-2", ProgConst.GUI_COLOR_DARK_2);
    public static StringProperty SYSTEM_GUI_THEME_LIGHT_1 = addStrProp("system-gui-theme-light-1", ProgConst.GUI_COLOR_LIGHT_1);
    public static StringProperty SYSTEM_GUI_THEME_LIGHT_2 = addStrProp("system-gui-theme-light2", ProgConst.GUI_COLOR_LIGHT_2);

    public static BooleanProperty SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1 = addBoolProp("system-gui-background-transparent-dark-1", ProgConst.GUI_BACKGROUND_TRANSPARENT_DARK_1);
    public static BooleanProperty SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2 = addBoolProp("system-gui-background-transparent-dark-2", ProgConst.GUI_BACKGROUND_TRANSPARENT_DARK_2);
    public static BooleanProperty SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1 = addBoolProp("system-gui-background-transparent-light-1", ProgConst.GUI_BACKGROUND_TRANSPARENT_LIGHT_1);
    public static BooleanProperty SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2 = addBoolProp("system-gui-background-transparent-light-2", ProgConst.GUI_BACKGROUND_TRANSPARENT_LIGHT_2);

    public static StringProperty SYSTEM_GUI_BACKGROUND_DARK_1 = addStrProp("system-gui-background-dark-1", ProgConst.GUI_BACKGROUND_DARK_1);
    public static StringProperty SYSTEM_GUI_BACKGROUND_DARK_2 = addStrProp("system-gui-background-dark-2", ProgConst.GUI_BACKGROUND_DARK_2);
    public static StringProperty SYSTEM_GUI_BACKGROUND_LIGHT_1 = addStrProp("system-gui-background-light-1", ProgConst.GUI_BACKGROUND_LIGHT_1);
    public static StringProperty SYSTEM_GUI_BACKGROUND_LIGHT_2 = addStrProp("system-gui-background-light2", ProgConst.GUI_BACKGROUND_LIGHT_2);

    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_1 = addBoolProp("system-gui-title-bar-transparent-dark-1", ProgConst.GUI_TITLE_BAR_TRANSPARENT_DARK_1);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_2 = addBoolProp("system-gui-title-bar-transparent-dark-2", ProgConst.GUI_TITLE_BAR_TRANSPARENT_DARK_1);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_1 = addBoolProp("system-gui-title-bar-transparent-light-1", ProgConst.GUI_TITLE_BAR_TRANSPARENT_LIGHT_1);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_2 = addBoolProp("system-gui-title-bar-transparent-light-2", ProgConst.GUI_TITLE_BAR_TRANSPARENT_LIGHT_2);

    public static StringProperty SYSTEM_GUI_TITLE_BAR_DARK_1 = addStrProp("system-gui-title-bar-dark-1", ProgConst.GUI_TITLE_BAR_DARK_1);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_DARK_2 = addStrProp("system-gui-title-bar-dark-2", ProgConst.GUI_TITLE_BAR_DARK_2);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_LIGHT_1 = addStrProp("system-gui-title-bar-light-1", ProgConst.GUI_TITLE_BAR_LIGHT_1);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_LIGHT_2 = addStrProp("system-gui-title-bar-light2", ProgConst.GUI_TITLE_BAR_LIGHT_2);

    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1 = addBoolProp("system-gui-title-bar-sel-transparent-dark-1", ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2 = addBoolProp("system-gui-title-bar-sel-transparent-dark-2", ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1 = addBoolProp("system-gui-title-bar-sel-transparent-light-1", ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1);
    public static BooleanProperty SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2 = addBoolProp("system-gui-title-bar-sel-transparent-light-2", ProgConst.GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2);

    public static StringProperty SYSTEM_GUI_TITLE_BAR_SEL_DARK_1 = addStrProp("system-gui-title-bar-sel-dark-1", ProgConst.GUI_TITLE_BAR_SEL_DARK_1);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_SEL_DARK_2 = addStrProp("system-gui-title-bar-sel-dark-2", ProgConst.GUI_TITLE_BAR_SEL_DARK_2);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_1 = addStrProp("system-gui-title-bar-sel-light-1", ProgConst.GUI_TITLE_BAR_SEL_LIGHT_1);
    public static StringProperty SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_2 = addStrProp("system-gui-title-bar-sel-light2", ProgConst.GUI_TITLE_BAR_SEL_LIGHT_2);

    // Einstellungen Filmliste
    static {
        addComment("Filmliste");
    }

    public static StringProperty SYSTEM_AUDIOLIST_DATE_TIME = addStrProp("system-audiolist-date", ""); // DateTimeFormatter DT_FORMATTER_dd_MM_yyyy___HH__mm
    public static StringProperty SYSTEM_FILMLIST_DATE = addStrProp("system-filmlist-date", "");
    public static BooleanProperty SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART = addBoolProp("system-load-filmlist-on-programstart", Boolean.TRUE);
    public static BooleanProperty SYSTEM_USE_AUDIOLIST = addBoolProp("system-use-audiolist", Boolean.TRUE);
    public static BooleanProperty SYSTEM_USE_LIVE = addBoolProp("system-use-live", Boolean.TRUE);
    public static StringProperty SYSTEM_MARK_DOUBLE_CHANNEL_LIST = addStrProp("system-mark-double-channel-list", "ARD,ZDF");
    public static IntegerProperty SYSTEM_AUDIOLIST_COUNT_DOUBLE = addIntProp("system-audiolist-count-double", 0); // Anzahl der doppelten Filme
    public static BooleanProperty SYSTEM_FILMLIST_REMOVE_DOUBLE = addBoolProp("system-filmlist-remove-double", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE = addBoolProp("system-filmlist-double-with-theme-title", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILMLIST_MARK_UT = addBoolProp("system-filmlist-mark-ut", Boolean.TRUE);
    public static BooleanProperty SYSTEM_FILMLIST_MARK = addBoolProp("system-filmlist-mark", Boolean.TRUE);
    public static BooleanProperty SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY = addBoolProp("system-load-new-filmlist-immediately", Boolean.FALSE);
    public static StringProperty SYSTEM_LOAD_NOT_SENDER = addStrProp("system-load-not-sender", "");
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addIntProp("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addIntProp("system-load-filmlist-min-duration", 0); //es werden nur Filme mit mind. x Minuten geladen
    public static StringProperty SYSTEM_PATH_VLC = addStrProp("system-path-vlc", GetProgramStandardPath.getTemplatePathVlc());
    public static StringProperty SYSTEM_PATH_FFMPEG = addStrProp("system-path-ffmpeg", GetProgramStandardPath.getTemplatePathFFmpeg());
    public static IntegerProperty SYSTEM_FILMLIST_COUNT_DOUBLE = addIntProp("system-filmlist-count-double", 0); // Anzahl der doppelten Filme

    // Download
    static {
        addComment("Download");
    }

    public static BooleanProperty DOWNLOAD_START_NOW = addBoolProp("download-start-now", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_BEEP = addBoolProp("download-beep");
    public static IntegerProperty DOWNLOAD_ONLY_STOP = addIntProp("download-only-stop", ProgConfigAskBeforeDelete.DOWNLOAD_STOP__ASK); // das sind Downloads ohne Dateien
    public static IntegerProperty DOWNLOAD_STOP = addIntProp("download-stop", ProgConfigAskBeforeDelete.DOWNLOAD_STOP__ASK); // das sind Downloads mit bereits geladenen Dateien
    public static IntegerProperty DOWNLOAD_CONTINUE = addIntProp("download-continue", ProgConfigAskBeforeDelete.DOWNLOAD_RESTART__ASK);
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addIntProp("download-max-downloads", 2);
    public static IntegerProperty DOWNLOAD_MAX_BANDWIDTH_BYTE = addIntProp("download-max-bandwidth-byte", MtBandwidthTokenBucket.BANDWIDTH_RUN_FREE);
    public static BooleanProperty DOWNLOAD_DIALOG_ERROR_SHOW = addBoolProp("download-dialog-error-show", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_ONLY_HISTORY = addBoolProp("download-only-history", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_DIALOG_ERROR_TIME = addBoolProp("download-dialog-error-time", Boolean.TRUE);

    // ===============================================================
    // ====== Filter =================================================
    // ===============================================================

    static {
        addComment("Filter");
    }

    // Blacklist
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_FUTURE = addBoolProp("system-blacklist-show-no-future");
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_GEO = addBoolProp("system-blacklist-show-no-geo", Boolean.TRUE);
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_NO_DOUBLE = addBoolProp("system-blacklist-show-no-double", Boolean.TRUE);
    public static BooleanProperty SYSTEM_BLACKLIST_SHOW_ABO = addBoolProp("system-blacklist-show-abo");
    public static IntegerProperty SYSTEM_BLACKLIST_MAX_FILM_DAYS = addIntProp("system-blacklist-max-film-days", 0);
    public static IntegerProperty SYSTEM_BLACKLIST_MIN_FILM_DURATION = addIntProp("system-blacklist-min-film-duration", 0); // Minuten
    public static StringProperty BLACKLIST_TABLE_WIDTH = addStrProp("blacklist-table-width");
    public static StringProperty BLACKLIST_TABLE_SORT = addStrProp("blacklist-table-sort");
    public static StringProperty BLACKLIST_TABLE_UP_DOWN = addStrProp("blacklist-table-up-down");
    public static StringProperty BLACKLIST_TABLE_VIS = addStrProp("blacklist-table-vis");
    public static StringProperty BLACKLIST_TABLE_ORDER = addStrProp("blacklist-table-order");

    // Filmfilter
    public static StringProperty FILMFILTER_TABLE_WIDTH = addStrProp("filmfilter-table-width");
    public static StringProperty FILMFILTER_TABLE_SORT = addStrProp("filmfilter-table-sort");
    public static StringProperty FILMFILTER_TABLE_UP_DOWN = addStrProp("filmfilter-table-up-down");
    public static StringProperty FILMFILTER_TABLE_VIS = addStrProp("filmfilter-table-vis");
    public static StringProperty FILMFILTER_TABLE_ORDER = addStrProp("filmfilter-table-order");

    // Filter Filme
    public static BooleanProperty FILMFILTER_SMALL_FILTER = addBoolProp("filmfilter-small-filter", Boolean.TRUE);
    public static IntegerProperty FILTER_FILM_SEL_FILTER = addIntProp("filter-film-sel-filter"); // das ausgewählte Filterprofiel

    // Filter Audio
    public static BooleanProperty AUDIOFILTER_SMALL_FILTER = addBoolProp("audiofilter-small-filter", Boolean.TRUE);
    public static IntegerProperty FILTER_AUDIO_SEL_FILTER = addIntProp("filter-audio-sel-filter"); // das ausgewählte Filterprofiel

    // Filter Abo
    public static StringProperty FILTER_ABO_CHANNEL = addStrProp("filter-abo-channel");
    public static StringProperty FILTER_ABO_NAME = addStrProp("filter-abo-name");
    public static StringProperty FILTER_ABO_SEARCH_TEXT = addStrProp("filter-abo-search-text");
    public static StringProperty FILTER_ABO_DESCRIPTION = addStrProp("filter-abo-description");
    public static StringProperty FILTER_ABO_TYPE = addStrProp("filter-abo-type");

    // Filter Download
    public static StringProperty FILTER_DOWNLOAD_LIST = addStrProp("filter-download-list");
    public static StringProperty FILTER_DOWNLOAD_CHANNEL = addStrProp("filter-download-channel");
    public static StringProperty FILTER_DOWNLOAD_SOURCE = addStrProp("filter-download-source");
    public static StringProperty FILTER_DOWNLOAD_TYPE = addStrProp("filter-download-type");
    public static StringProperty FILTER_DOWNLOAD_ABO = addStrProp("filter-download-abo");
    public static StringProperty FILTER_DOWNLOAD_STATE = addStrProp("filter-download-state");

    // Bookmark
    static {
        addComment("Bookmark");
    }

    public static StringProperty BOOKMARK_DIALOG_SIZE = addStrProp("bookmark-dialog-size", "600:800");
    public static StringProperty BOOKMARK_DIALOG_DEL_SIZE = addStrProp("bookmark-dialog-del-size", "450:350");
    public static StringProperty BOOKMARK_TABLE_WIDTH = addStrProp("bookmark-table-width");
    public static StringProperty BOOKMARK_TABLE_SORT = addStrProp("bookmark-table-sort");
    public static StringProperty BOOKMARK_TABLE_UP_DOWN = addStrProp("bookmark-table-up-down");
    public static StringProperty BOOKMARK_TABLE_VIS = addStrProp("bookmark-table-vis");
    public static StringProperty BOOKMARK_TABLE_ORDER = addStrProp("bookmark-table-order");
    public static DoubleProperty BOOKMARK_DIALOG_INFO_DIVIDER = addDoubleProp("bookmark-dialog-info-divider", 0.5);
    public static BooleanProperty BOOKMARK_DIALOG_SHOW_TABLE_TOOL_TIP = addBoolProp("bookmark-dialog-show-table-tool-tip", Boolean.FALSE);
    public static BooleanProperty BOOKMARK_DIALOG_SMALL_TABLE_ROW = addBoolProp("bookmark-dialog-small-table-row", Boolean.TRUE);
    public static BooleanProperty BOOKMARK_DIALOG_SHOW_INFO = addBoolProp("bookmark-dialog-show-info", Boolean.TRUE);

    public static BooleanProperty BOOKMARK_DEL_ALL = addBoolProp("bookmark-del-all", Boolean.FALSE);
    public static BooleanProperty BOOKMARK_DEL_SHOWN = addBoolProp("bookmark-del-shown", Boolean.FALSE);
    public static BooleanProperty BOOKMARK_DEL_OLD = addBoolProp("bookmark-del-old", Boolean.FALSE);
    public static IntegerProperty BOOKMARK_DEL_OLD_COUNT_DAYS = addIntProp("bookmark-del-count-old-days", 10);

    // Tips
    static {
        addComment("Tips");
    }

    public static BooleanProperty SYSTEM_SHOW_TIPS = addBoolProp("system-show-tips", Boolean.TRUE); // Dialog Tips wurde schon mal gezeigt
    public static StringProperty TIPS_DIALOG_SIZE = addStrProp("tips-dialog-size", "800:600");

    // ===============================================================
    // ====== GUI Filme ===============================================
    // ===============================================================
    static {
        addComment("GUI Filme");
    }

    public static BooleanProperty SYSTEM_SMALL_TABLE_ROW_FILM = addBoolProp("system-small-table-row-film", Boolean.TRUE);
    public static BooleanProperty FILM_GUI_SHOW_MENU = addBoolProp("film-gui-show-menu", Boolean.TRUE);
    public static BooleanProperty FILM_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("film-gui-show-table-tool-tip", Boolean.FALSE);
    public static BooleanProperty FILM_GUI_FILTER_DIALOG_IS_SHOWING = addBoolProp("film-gui-filter-dialog-is-showing", Boolean.FALSE);
    public static StringProperty FILM_GUI_TABLE_WIDTH = addStrProp("film-gui-table-width");
    public static StringProperty FILM_GUI_TABLE_SORT = addStrProp("film-gui-table-sort");
    public static StringProperty FILM_GUI_TABLE_UP_DOWN = addStrProp("film-gui-table-up-down");
    public static StringProperty FILM_GUI_TABLE_VIS = addStrProp("film-gui-table-vis");
    public static StringProperty FILM_GUI_TABLE_ORDER = addStrProp("film-gui-table-order");
    public static StringProperty ADD_BLACK_DIALOG_SIZE = addStrProp("add-black-dialog-size", "600:400");
    public static DoubleProperty FILM_PANE_INFO_DIVIDER = addDoubleProp("film-pane-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);

    // Gui Film -> fast search
    public static BooleanProperty FAST_FILM_SEARCH_ON = addBoolProp("fast-film-search-on", Boolean.FALSE);
    public static IntegerProperty FAST_FILM_SEARCH_WHERE = addIntProp("fast-film-search-where", ProgConst.SEARCH_FAST_THEME_TITLE);
    public static BooleanProperty FAST_AUDIO_SEARCH_ON = addBoolProp("fast-audio-search-on", Boolean.FALSE);
    public static IntegerProperty FAST_AUDIO_SEARCH_WHERE = addIntProp("fast-audio-search-where", ProgConst.SEARCH_FAST_THEME_TITLE);

    // Film Info Pane
    public static BooleanProperty FILM__FILTER_IS_SHOWING = addBoolProp("film--filter-is-showing", Boolean.TRUE);
    public static DoubleProperty FILM__FILTER_DIVIDER = addDoubleProp("film--filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty FILM__FILTER_IS_RIP = addBoolProp("film--filter-is-rip", Boolean.FALSE);
    public static StringProperty FILM__FILTER_DIALOG_SIZE = addStrProp("film--filter-dialog-size", "400:600");

    public static BooleanProperty FILM__INFO_IS_SHOWING = addBoolProp("film--info-is-showing", Boolean.TRUE);
    public static DoubleProperty FILM__INFO_DIVIDER = addDoubleProp("film--info-divider", ProgConst.GUI_DIVIDER_LOCATION);

    public static BooleanProperty FILM__INFO_PANE_IS_RIP = addBoolProp("film--info-pane-is-rip", false);
    public static StringProperty FILM__INFO_DIALOG_SIZE = addStrProp("film--info-dialog-size", "400:600");

    public static BooleanProperty FILM__BUTTON_PANE_IS_RIP = addBoolProp("film--button-pane-is-rip", false);
    public static StringProperty FILM__BUTTON_DIALOG_SIZE = addStrProp("film--button-dialog-size", "400:600");

    public static BooleanProperty FILM__MEDIA_PANE_IS_RIP = addBoolProp("film--media-pane-is-rip", false);
    public static StringProperty FILM__MEDIA_DIALOG_SIZE = addStrProp("film--media-dialog-size", "400:600");

    // ===============================================================
    // ====== GUI Filme ===============================================
    // ===============================================================
    static {
        addComment("GUI Audio");
    }

    public static BooleanProperty SYSTEM_SMALL_TABLE_ROW_AUDIO = addBoolProp("system-small-table-row-audio", Boolean.TRUE);
    public static BooleanProperty AUDIO_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("audio-gui-show-table-tool-tip", Boolean.FALSE);
    public static DoubleProperty AUDIO_PANE_INFO_DIVIDER = addDoubleProp("audio-pane-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);
    public static BooleanProperty AUDIO_GUI_SHOW_MENU = addBoolProp("audio-gui-show-menu", Boolean.TRUE);
    public static StringProperty AUDIO_GUI_TABLE_WIDTH = addStrProp("audio-gui-table-width");
    public static StringProperty AUDIO_GUI_TABLE_SORT = addStrProp("audio-gui-table-sort");
    public static StringProperty AUDIO_GUI_TABLE_UP_DOWN = addStrProp("audio-gui-table-up-down");
    public static StringProperty AUDIO_GUI_TABLE_VIS = addStrProp("audio-gui-table-vis");
    public static StringProperty AUDIO_GUI_TABLE_ORDER = addStrProp("audio-gui-table-order");

    // Audio Info Pane
    public static BooleanProperty AUDIO__FILTER_IS_SHOWING = addBoolProp("audio--filter-is-showing", Boolean.TRUE);
    public static DoubleProperty AUDIO__FILTER_DIVIDER = addDoubleProp("audio--filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty AUDIO__FILTER_IS_RIP = addBoolProp("audio--filter-is-rip", Boolean.FALSE);
    public static StringProperty AUDIO__FILTER_DIALOG_SIZE = addStrProp("audio--filter-dialog-size", "400:600");

    public static BooleanProperty AUDIO__INFO_IS_SHOWING = addBoolProp("audio--info-is-showing", Boolean.TRUE);
    public static DoubleProperty AUDIO__INFO_DIVIDER = addDoubleProp("audio--info-divider", ProgConst.GUI_DIVIDER_LOCATION);

    public static BooleanProperty AUDIO__INFO_PANE_IS_RIP = addBoolProp("audio--info-pane-is-rip", false);
    public static StringProperty AUDIO__INFO_DIALOG_SIZE = addStrProp("audio--info-dialog-size", "400:600");

    public static BooleanProperty AUDIO__BUTTON_PANE_IS_RIP = addBoolProp("audio--button-pane-is-rip", false);
    public static StringProperty AUDIO__BUTTON_DIALOG_SIZE = addStrProp("audio--button-dialog-size", "400:600");

    public static BooleanProperty AUDIO__MEDIA_PANE_IS_RIP = addBoolProp("audio--media-pane-is-rip", false);
    public static StringProperty AUDIO__MEDIA_DIALOG_SIZE = addStrProp("audio--media-dialog-size", "400:600");

    // ===============================================================
    // ====== GUI Live-Filme =========================================
    // ===============================================================
    static {
        addComment("GUI Live");
    }

    public static BooleanProperty SYSTEM_SMALL_TABLE_ROW_LIVE = addBoolProp("system-small-table-row-live", Boolean.TRUE);
    public static BooleanProperty LIVE_FILM_GUI_SHOW_MENU = addBoolProp("live-film-gui-show-menu", Boolean.TRUE);
    public static BooleanProperty LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("live-film-gui-show-table-tool-tip", Boolean.FALSE);

    public static StringProperty LIVE_FILM_GUI_TABLE_WIDTH = addStrProp("live-film-gui-table-width");
    public static StringProperty LIVE_FILM_GUI_TABLE_SORT = addStrProp("live-film-gui-table-sort");
    public static StringProperty LIVE_FILM_GUI_TABLE_UP_DOWN = addStrProp("live-film-gui-table-up-down");
    public static StringProperty LIVE_FILM_GUI_TABLE_VIS = addStrProp("live-film-gui-table-vis");
    public static StringProperty LIVE_FILM_GUI_TABLE_ORDER = addStrProp("live-film-gui-table-order");
    public static DoubleProperty LIVE_FILM_PANE_INFO_DIVIDER = addDoubleProp("live-film-pane-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);

    public static StringProperty LIVE_FILM_GUI_SEARCH_ARD = addStrProp("live-film-gui-search-ard");
    public static StringProperty LIVE_FILM_GUI_SEARCH_ZDF = addStrProp("live-film-gui-search-zdf");
    public static StringProperty LIVE_FILM_GUI_SEARCH_URL_ARD = addStrProp("live-film-gui-search-url-ard");
    public static StringProperty LIVE_FILM_GUI_SEARCH_URL_ZDF = addStrProp("live-film-gui-search-url-zdf");

    // Live Info Pane
    public static DoubleProperty LIVE_FILM__FILTER_DIVIDER = addDoubleProp("live-film--filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty LIVE_FILM__FILTER_IS_RIP = addBoolProp("live-film--filter-is-rip", Boolean.FALSE);
    public static StringProperty LIVE_FILM__FILTER_DIALOG_SIZE = addStrProp("live-film--filter-dialog-size", "400:600");
    public static BooleanProperty LIVE_FILM__FILTER_IS_SHOWING = addBoolProp("live-film--filter-is-showing", Boolean.TRUE);

    public static BooleanProperty LIVE_FILM__INFO_IS_SHOWING = addBoolProp("live-film--info-is-showing", Boolean.TRUE);
    public static DoubleProperty LIVE_FILM__INFO_DIVIDER = addDoubleProp("live-film--info-divider", ProgConst.GUI_DIVIDER_LOCATION);

    public static BooleanProperty LIVE_FILM__INFO_PANE_IS_RIP = addBoolProp("live-film--info-pane-is-rip", false);
    public static StringProperty LIVE_FILM__INFO_DIALOG_SIZE = addStrProp("live-film--info-dialog-size", "400:600");

    public static BooleanProperty LIVE_FILM__BUTTON_PANE_IS_RIP = addBoolProp("live-film--button-pane-is-rip", false);
    public static StringProperty LIVE_FILM__BUTTON_DIALOG_SIZE = addStrProp("live-film--button-dialog-size", "400:600");

    public static BooleanProperty LIVE_FILM__MEDIA_PANE_IS_RIP = addBoolProp("live-film--media-pane-is-rip");
    public static StringProperty LIVE_FILM__MEDIA_DIALOG_SIZE = addStrProp("live-film--media-dialog-size", "400:600");

    // ===============================================================
    // ====== GUI Downloads ==========================================
    // ===============================================================
    static {
        addComment("GUI Download");
    }

    public static BooleanProperty SYSTEM_SMALL_TABLE_ROW_DOWNLOAD = addBoolProp("system-small-table-row-download", Boolean.TRUE);
    public static StringProperty DOWNLOAD_PATH = addStrProp("download-path" + TAGGER + "start-dialog-download-path", P2InfoFactory.getStandardDownloadPath());
    public static BooleanProperty DOWNLOAD_GUI_SHOW_MENU = addBoolProp("download-gui-show-menu", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("download-gui-show-table-tool-tip", Boolean.FALSE);
    public static List<String> DOWNLOAD_DIALOG_DOWNLOAD_PATH = addListProp("download-dialog-download-path"); // gesammelten Downloadpfade im Downloaddialog
    public static StringProperty DOWNLOAD_DIALOG_HD_HEIGHT_LOW = addStrProp("download-dialog-hd-height-low", FilmDataMTP.RESOLUTION_NORMAL); // letzte verwendete Auflösung
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBoolProp("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBoolProp("download-dialog-start-download-not", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_TIME = addBoolProp("download-dialog-start-download-time", Boolean.FALSE);
    public static StringProperty DOWNLOAD_DIALOG_START_AT_TIME_SIZE = addStrProp("download-dialog-start-at-time-size", "800:400");
    public static StringProperty DOWNLOAD_DIALOG_ADD_SIZE = addStrProp("download-dialog-add-size", "700:700");
    public static StringProperty DOWNLOAD_DIALOG_ADD_MORE_SIZE = addStrProp("download-dialog-add-more-size", "800:700");
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStrProp("download-dialog-continue-size", "600:500");
    public static StringProperty DOWNLOAD_DIALOG_ERROR_STREAM_SIZE = addStrProp("download-dialog-error-steam-size", "600:500");
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStrProp("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStrProp("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStrProp("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStrProp("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStrProp("download-gui-table-order");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBoolProp("download-show-notification", Boolean.TRUE);
    public static DoubleProperty DOWNLOAD_GUI_MEDIA_DIVIDER = addDoubleProp("download-gui-media-divider", 0.5);
    public static DoubleProperty DOWNLOAD_PANE_INFO_DIVIDER = addDoubleProp("download-pane-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);
    public static StringProperty DOWNLOAD_STOP_DIALOG_SIZE = addStrProp("download-stop-dialog-size", "950:600");
    public static StringProperty DOWNLOAD_ONLY_STOP_DIALOG_SIZE = addStrProp("download-only-stop-dialog-size", "600:400");

    public static BooleanProperty DOWNLOAD_DIALOG_ADD_SET_ALL = addBoolProp("download-dialog-add-set-all", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_ADD_RESOLUTION_ALL = addBoolProp("download-dialog-add-resolution-all", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_ADD_PATH_ALL = addBoolProp("download-dialog-add-path-all", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_ADD_SUBTITLE_ALL = addBoolProp("download-dialog-add-subtitle-all", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_ADD_INFO_ALL = addBoolProp("download-dialog-add-info-all", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_ADD_START_TIME_ALL = addBoolProp("download-dialog-add-start-time-all", Boolean.TRUE);

    // DownloadChart
    public static IntegerProperty DOWNLOAD_CHART_SHOW_WHAT = addIntProp("download-chart-show-what", ChartGenerateFactory.GEN_CHART_SHOW_DOWN);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_EXISTING = addBoolProp("download-chart-only-existing", Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_CHART_ONLY_RUNNING = addBoolProp("download-chart-only-running", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN = addIntProp("download-chart-max-time-to-show-min", 30); //MAX Minuten im Chart

    // Download Info-Panes
    public static BooleanProperty DOWNLOAD__FILTER_IS_SHOWING = addBoolProp("download--filter-is-showing", Boolean.TRUE);
    public static StringProperty DOWNLOAD__FILTER_DIALOG_SIZE = addStrProp("download--filter-dialog-size", "400:600");
    public static BooleanProperty DOWNLOAD__FILTER_IS_RIP = addBoolProp("download--filter-is-rip", Boolean.FALSE);
    public static DoubleProperty DOWNLOAD__FILTER_DIVIDER = addDoubleProp("download--filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);

    public static BooleanProperty DOWNLOAD__INFO_IS_SHOWING = addBoolProp("download--is-showing", Boolean.TRUE);
    public static DoubleProperty DOWNLOAD__INFO_DIVIDER = addDoubleProp("download--info-divider", ProgConst.GUI_DIVIDER_LOCATION);

    public static BooleanProperty DOWNLOAD__INFO_PANE_IS_RIP = addBoolProp("download--info-pane-is-rip");
    public static StringProperty DOWNLOAD__INFO_DIALOG_SIZE = addStrProp("download--info-dialog-size", "400:600");

    public static BooleanProperty DOWNLOAD__MEDIA_PANE__IS_RIP = addBoolProp("download--media-pane-is-rip");
    public static StringProperty DOWNLOAD__MEDIA_DIALOG_SIZE = addStrProp("download--media-dialog-size", "400:600");

    public static BooleanProperty DOWNLOAD__CHART_PANE_IS_RIP = addBoolProp("download--chart-pane-is-rip");
    public static StringProperty DOWNLOAD__CHART_DIALOG_SIZE = addStrProp("download--chart-dialog-size", "400:600");

    public static BooleanProperty DOWNLOAD__ERROR_PANE_IS_RIP = addBoolProp("download--error-pane-is-rip");
    public static StringProperty DOWNLOAD__ERROR_DIALOG_SIZE = addStrProp("download--error-dialog-size", "400:600");

    public static BooleanProperty DOWNLOAD__LIST_PANE_IS_RIP = addBoolProp("download--list-pane-is-rip");
    public static StringProperty DOWNLOAD__LIST_DIALOG_SIZE = addStrProp("download--list-dialog-size", "400:600");

    // ===============================================================
    // ====== GUI Abos ===============================================
    // ===============================================================
    static {
        addComment("GUI Abo");
    }

    public static BooleanProperty SYSTEM_SMALL_TABLE_ROW_ABO = addBoolProp("system-small-table-row-abo", Boolean.TRUE);
    public static BooleanProperty ABO_GUI_SHOW_MENU = addBoolProp("abo-gui-show-menu", Boolean.TRUE);
    public static BooleanProperty ABO_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("abo-gui-show-table-tool-tip", Boolean.FALSE);
    public static BooleanProperty ABO_SEARCH_NOW = addBoolProp("abo-search-now", Boolean.TRUE);
    public static IntegerProperty ABO_MINUTE_MIN_SIZE = addIntProp("abo-minute-min-size", FilterCheck.FILTER_ALL_OR_MIN); //Vorgabe beim Anlegen eines Abos
    public static IntegerProperty ABO_MINUTE_MAX_SIZE = addIntProp("abo-minute-max-size", FilterCheck.FILTER_DURATION_MAX_MINUTE); //Vorgabe beim Anlegen eines Abos
    public static StringProperty ABO_DIALOG_EDIT_SIZE = addStrProp("abo-dialog-edit-size", "600:750");
    public static StringProperty ABO_GUI_TABLE_WIDTH = addStrProp("abo-gui-table-width");
    public static StringProperty ABO_GUI_TABLE_SORT = addStrProp("abo-gui-table-sort");
    public static StringProperty ABO_GUI_TABLE_UP_DOWN = addStrProp("abo-gui-table-up-down");
    public static StringProperty ABO_GUI_TABLE_VIS = addStrProp("abo-gui-table-vis");
    public static StringProperty ABO_GUI_TABLE_ORDER = addStrProp("abo-gui-table-order");
    public static DoubleProperty ABO_PANE_INFO_DIVIDER = addDoubleProp("abo-pane-info-divider", ProgConst.GUI_INFO_DIVIDER_LOCATION);
    public static StringProperty ABO_DEL_DIALOG_SIZE = addStrProp("abo-del-dialog-size", "600:400");
    public static IntegerProperty ABO_ONLY_STOP = addIntProp("abo-only-stop", ProgConfigAskBeforeDelete.DOWNLOAD_STOP__ASK);

    // Abo Info-Panes
    public static BooleanProperty ABO__FILTER_IS_SHOWING = addBoolProp("abo--filter-is-showing", Boolean.TRUE);
    public static DoubleProperty ABO__FILTER_DIVIDER = addDoubleProp("abo--filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty ABO__FILTER_IS_RIP = addBoolProp("abo--filter-is-rip", Boolean.FALSE);
    public static StringProperty ABO__FILTER_DIALOG_SIZE = addStrProp("abo--filter-dialog-size", "400:600");

    public static BooleanProperty ABO__INFO_IS_SHOWING = addBoolProp("abo--info-is-showing", Boolean.TRUE);
    public static DoubleProperty ABO__INFO_DIVIDER = addDoubleProp("abo--info-divider", ProgConst.GUI_DIVIDER_LOCATION);

    public static BooleanProperty ABO__INFO_PANE_IS_RIP = addBoolProp("abo---info-pane-is-rip", Boolean.FALSE);
    public static StringProperty ABO__INFO__DIALOG_SIZE = addStrProp("abo--info-dialog-size", "400:600");
    public static BooleanProperty ABO__LIST_PANE_IS_RIP = addBoolProp("abo--list-pane-is-rip", Boolean.FALSE);
    public static StringProperty ABO__LIST_DIALOG_SIZE = addStrProp("abo--list-dialog--size", "400:600");

    // ===============================================================
    // ====== MEDIA ==================================================
    // ===============================================================
    static {
        addComment("Media");
    }

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

    // Gui Media Search -> Infobereich Film
    public static IntegerProperty INFO_FILM_BUILD_SEARCH_FROM_FOR_MEDIA = addIntProp("info-film-build-search-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_FILM_SEARCH_IN_WHAT_FOR_MEDIA = addIntProp("info-film-search-in-what-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_FILM_CLEAN_MEDIA = addBoolProp("info-film-clean-media", Boolean.TRUE);
    public static BooleanProperty INFO_FILM_CLEAN_EXACT_MEDIA = addBoolProp("info-film-clean-exact-media", Boolean.FALSE);
    public static BooleanProperty INFO_FILM_CLEAN_AND_OR_MEDIA = addBoolProp("info-film-clean-and-or-media", Boolean.FALSE);
    public static BooleanProperty INFO_FILM_CLEAN_LIST_MEDIA = addBoolProp("info-film-clean-list-media", Boolean.TRUE);

    public static IntegerProperty INFO_FILM_BUILD_SEARCH_FROM_FOR_ABO = addIntProp("info-film-build-search-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_FILM_SEARCH_IN_WHAT_FOR_ABO = addIntProp("info-film-search-in-what-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_FILM_CLEAN_ABO = addBoolProp("info-film-clean-abo", Boolean.TRUE);
    public static BooleanProperty INFO_FILM_CLEAN_EXACT_ABO = addBoolProp("info-film-clean-exact-abo", Boolean.FALSE);
    public static BooleanProperty INFO_FILM_CLEAN_AND_OR_ABO = addBoolProp("info-film-clean-and-or-abo", Boolean.FALSE);
    public static BooleanProperty INFO_FILM_CLEAN_LIST_ABO = addBoolProp("info-film-clean-list-abo", Boolean.TRUE);

    // Gui Media Search -> Infobereich Audio
    public static IntegerProperty INFO_AUDIO_BUILD_SEARCH_FROM_FOR_MEDIA = addIntProp("info-audio-build-search-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_AUDIO_SEARCH_IN_WHAT_FOR_MEDIA = addIntProp("info-audio-search-in-what-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_AUDIO_CLEAN_MEDIA = addBoolProp("info-audio-clean-media", Boolean.TRUE);
    public static BooleanProperty INFO_AUDIO_CLEAN_EXACT_MEDIA = addBoolProp("info-audio-clean-exact-media", Boolean.FALSE);
    public static BooleanProperty INFO_AUDIO_CLEAN_AND_OR_MEDIA = addBoolProp("info-audio-clean-and-or-media", Boolean.FALSE);
    public static BooleanProperty INFO_AUDIO_CLEAN_LIST_MEDIA = addBoolProp("info-audio-clean-list-media", Boolean.TRUE);

    public static IntegerProperty INFO_AUDIO_BUILD_SEARCH_FROM_FOR_ABO = addIntProp("info-audio-build-search-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_AUDIO_SEARCH_IN_WHAT_FOR_ABO = addIntProp("info-audio-search-in-what-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_AUDIO_CLEAN_ABO = addBoolProp("info-audio-clean-abo", Boolean.TRUE);
    public static BooleanProperty INFO_AUDIO_CLEAN_EXACT_ABO = addBoolProp("info-audio-clean-exact-abo", Boolean.FALSE);
    public static BooleanProperty INFO_AUDIO_CLEAN_AND_OR_ABO = addBoolProp("info-audio-clean-and-or-abo", Boolean.FALSE);
    public static BooleanProperty INFO_AUDIO_CLEAN_LIST_ABO = addBoolProp("info-audio-clean-list-abo", Boolean.TRUE);

    // Gui Media Search -> Infobereich Live-Film
    public static IntegerProperty INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_MEDIA = addIntProp("info-live-film-build-search-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_MEDIA = addIntProp("info-live-film-search-in-what-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_MEDIA = addBoolProp("info-live-film-clean-media", Boolean.TRUE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_EXACT_MEDIA = addBoolProp("info-live-film-clean-exact-media", Boolean.FALSE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_AND_OR_MEDIA = addBoolProp("info-live-film-clean-and-or-media", Boolean.FALSE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_LIST_MEDIA = addBoolProp("info-live-film-clean-list-media", Boolean.TRUE);

    public static IntegerProperty INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_ABO = addIntProp("info-live-film-build-search-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_ABO = addIntProp("info-live-film-search-in-what-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_ABO = addBoolProp("info-live-film-clean-abo", Boolean.TRUE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_EXACT_ABO = addBoolProp("info-live-film-clean-exact-abo", Boolean.FALSE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_AND_OR_ABO = addBoolProp("info-live-film-clean-and-or-abo", Boolean.FALSE);
    public static BooleanProperty INFO_LIVE_FILM_CLEAN_LIST_ABO = addBoolProp("info-live-film-clean-list-abo", Boolean.TRUE);

    // Gui Media Search -> Infobereich Download
    public static IntegerProperty INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_MEDIA = addIntProp("info-download-build-search-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_MEDIA = addIntProp("info-download-search-in-what-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_MEDIA = addBoolProp("info-download-clean-media", Boolean.TRUE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_EXACT_MEDIA = addBoolProp("info-download-clean-exact-media", Boolean.FALSE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_AND_OR_MEDIA = addBoolProp("info-download-clean-and-or-media", Boolean.FALSE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_LIST_MEDIA = addBoolProp("info-download-clean-list-media", Boolean.TRUE);

    public static IntegerProperty INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_ABO = addIntProp("info-download-build-search-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_ABO = addIntProp("info-download-search-in-what-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_ABO = addBoolProp("info-download-clean-abo", Boolean.TRUE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_EXACT_ABO = addBoolProp("info-download-clean-exact-abo", Boolean.FALSE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_AND_OR_ABO = addBoolProp("info-download-clean-and-or-abo", Boolean.FALSE);
    public static BooleanProperty INFO_DOWNLOAD_CLEAN_LIST_ABO = addBoolProp("info-download-clean-list-abo", Boolean.TRUE);

    // Gui Media Search -> MediaDialog
    public static IntegerProperty DIALOG_BUILD_SEARCH_FROM_FOR_MEDIA = addIntProp("dialog-build-search-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty DIALOG_BUILD_SEARCH_FROM_FOR_ABO = addIntProp("dialog-build-search-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT
    public static IntegerProperty DIALOG_BUILD_SEARCH_FROM_FOR_HISTORY = addIntProp("dialog-build-search-for-history", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //aus was der Suchbegriff gebaut wird: T/Th/TT

    public static IntegerProperty DIALOG_SEARCH_IN_WHAT_FOR_MEDIA = addIntProp("dialog-search-in-what-for-media", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static IntegerProperty DIALOG_SEARCH_IN_WHAT_FOR_ABO = addIntProp("dialog-search-in-what-for-abo", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT
    public static IntegerProperty DIALOG_SEARCH_IN_WHAT_FOR_HISTORY = addIntProp("dialog-search-in-what-for-history", ProgConst.MEDIA_SEARCH_TITEL_OR_NAME); //wo bei Medien gesucht wird: T/Th/TT

    public static StringProperty GUI_MEDIA_CONFIG_DIALOG_SIZE = addStrProp("gui-media-config-dialog-size", "800:700");

    // ===============================================================
    // ====== Dialoge ================================================
    // ===============================================================
    static {
        addComment("Dialoge");
    }

    // ConfigDialog
    public static StringProperty DOWNLOAD_SUBTITLE_DIALOG_SIZE = addStrProp("download-subtitle-dialog-size", "500:300");
    public static StringProperty IMPORT_MV_DIALOG_SIZE = addStrProp("import-mv-dialog-size", "600:800");
    public static StringProperty BLACK_DIALOG_SIZE = addStrProp("black-dialog-size", "700:800");
    public static StringProperty CONFIG_DIALOG_SIZE = addStrProp("config-dialog-size", "800:800");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBoolProp("config_dialog-accordion", Boolean.TRUE);
    public static DoubleProperty CONFIG_DIALOG_SET_DIVIDER = addDoubleProp("config-dialog-set-divider", ProgConst.CONFIG_DIALOG_SET_DIVIDER);
    public static StringProperty CONFIG_DIALOG_IMPORT_SET_SIZE = addStrProp("config-dialog-import-set-size", "800:700");
    public static DoubleProperty CONFIG_DIALOG_FILMLIST_FILTER_SPLITPANE = addDoubleProp("config-dialog-filmlist-filter-splitpane", 0.7);
    public static DoubleProperty CONFIG_DIALOG_BLACKLIST_SPLITPANE = addDoubleProp("config-dialog-blacklist-splitpane", 0.7);

    // FilmInfoDialog
    public static StringProperty FILM_INFO_DIALOG_SIZE = addStrProp("film-info-dialog-size", "500:600");
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_URL = addBoolProp("film-info-dialog-show-url", Boolean.FALSE);
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_WEBSITE_URL = addBoolProp("film-info-dialog-show-website-url", Boolean.FALSE);
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_DESCRIPTION = addBoolProp("film-info-dialog-show-description", Boolean.TRUE);

    // ===============================================================
    // ====== Shortcuts ===============================================
    static {
        addComment("Shortcuts");
    }

    // Shortcuts, Programmweit
    public static String SHORTCUT_LOAD_FILMLIST_INIT = "Ctrl+L";
    public static StringProperty SHORTCUT_LOAD_FILMLIST = addStrProp("SHORTCUT_LOAD_FILMLIST", SHORTCUT_LOAD_FILMLIST_INIT);

    public static String SHORTCUT_UPDATE_FILMLIST_INIT = "Alt+L";
    public static StringProperty SHORTCUT_UPDATE_FILMLIST = addStrProp("SHORTCUT_UPDATE_FILMLIST", SHORTCUT_UPDATE_FILMLIST_INIT);

    public static String SHORTCUT_CENTER_INIT = "Ctrl+W";
    public static StringProperty SHORTCUT_CENTER_GUI = addStrProp("SHORTCUT_CENTER_GUI", SHORTCUT_CENTER_INIT);

    public static String SHORTCUT_MINIMIZE_INIT = "Alt+M";
    public static StringProperty SHORTCUT_MINIMIZE_GUI = addStrProp("SHORTCUT_MINIMIZE_GUI", SHORTCUT_MINIMIZE_INIT);

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

    public static String SHORTCUT_SHOW_BLACKLIST_INIT = "ALT+B";
    public static StringProperty SHORTCUT_SHOW_BLACKLIST = addStrProp("SHORTCUT_SHOW_BLACKLIST", SHORTCUT_SHOW_BLACKLIST_INIT);

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

    public static String SHORTCUT_PLAY_FILM_ALL_INIT = "Ctrl+Shift+P";
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


    // ========================================================
    // ========================================================
    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }
}
