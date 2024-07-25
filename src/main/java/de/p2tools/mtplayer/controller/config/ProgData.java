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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.data.abo.AboList;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.blackdata.BlackListFilter;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningDataList;
import de.p2tools.mtplayer.controller.data.download.DownloadInfos;
import de.p2tools.mtplayer.controller.data.download.DownloadList;
import de.p2tools.mtplayer.controller.data.download.ReplaceList;
import de.p2tools.mtplayer.controller.data.downloaderror.DownloadErrorList;
import de.p2tools.mtplayer.controller.data.propose.ProposeList;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.data.utdata.UtDataList;
import de.p2tools.mtplayer.controller.film.FilmListMTP;
import de.p2tools.mtplayer.controller.filmfilter.*;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.mtplayer.controller.mediadb.MediaCollectionDataList;
import de.p2tools.mtplayer.controller.mediadb.MediaDataList;
import de.p2tools.mtplayer.controller.starter.StartDownload;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.controller.worker.CheckForNewFilmlist;
import de.p2tools.mtplayer.controller.worker.Worker;
import de.p2tools.mtplayer.gui.AboGuiController;
import de.p2tools.mtplayer.gui.DownloadGuiController;
import de.p2tools.mtplayer.gui.FilmGuiController;
import de.p2tools.mtplayer.gui.LiveFilmGuiController;
import de.p2tools.mtplayer.gui.chart.ChartData;
import de.p2tools.mtplayer.gui.filter.AboFilterController;
import de.p2tools.mtplayer.gui.filter.DownloadFilterController;
import de.p2tools.mtplayer.gui.filter.FilmFilterControllerClearFilter;
import de.p2tools.mtplayer.gui.tools.ProgTray;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {

    private static ProgData instance;

    // flags
    public static boolean debug = false; // DebugModus
    public static boolean autoMode = false; // Automodus: start, laden, beenden
    public static Busy busy;

    public static boolean downloadSearchDone = false; // wird gesetzt, wenn das erste mal Downloads gesucht wurden
    public static boolean duration = false; // Duration ausgeben
    public static boolean startMinimized = false; // Minimiert starten
    public static boolean showUpdate = false; // immer ein ProgrammUpdate anzeigen
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht
    public static BooleanProperty FILMLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Filmliste geladen, LoadFilmList.propLoadFilmlist kann nicht genommen werden, kann sonst nicht einfach zurückgesetzt werden

    public static BooleanProperty FILM_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty LIVE_FILM_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty ABO_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static long countRunningTimeSeconds = 0; // Gesamtzeit die das Programm läuft

    // Infos
    public static String configDir = ""; //Verzeichnis zum Speichern der Programmeinstellungen
    public static String filmListUrl = ""; //URL von der die Filmliste geladen werden soll

    // zentrale Klassen
    public StartDownload startDownload; // Klasse zum Ausführen der Programme (für die Downloads): VLC, ...
    public PShortcut pShortcut; // verwendete Shortcuts
    public FilmFilterWorker filmFilterWorker; // gespeicherte Filterprofile FILME
    public TextFilterList textFilterList; // ist die eine CBO mit den gespeicherten Textfiltern (Thema, Titel, ..)
    public StringFilter stringFilterLists; // sind die Text-Filter in den CBO's

    public LiveFilmFilterWorker liveFilmFilterWorker; // gespeicherte Filterprofile LIVE
    public FilmFilterRunner filmFilterRunner;

    // Gui
    public Stage primaryStage = null;
    public P2MaskerPane maskerPane = null;
    public MTPlayerController mtPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public LiveFilmGuiController liveFilmGuiController = null; // Tab mit den Filmen
    public DownloadGuiController downloadGuiController = null; // Tab mit den Downloads
    public DownloadFilterController downloadFilterController = null;
    public AboGuiController aboGuiController = null; // Tab mit den Abos
    public AboFilterController aboFilterController = null;
    public FilmFilterControllerClearFilter filmFilterControllerClearFilter = null;
    public CheckForNewFilmlist checkForNewFilmlist;
    public final ChartData chartData;
    public final ProgTray progTray;

    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...
    public DownloadInfos downloadInfos;

    // Programmdaten
    public FilmListMTP filmList; // ist die komplette Filmliste
    public FilmListMTP filmListFiltered; // Filmliste, wie im TabFilme angezeigt

    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public AboList aboList;
    public BlackList filmListFilter;
    public BlackList blackList;
    public CleaningDataList cleaningDataListMedia;
    public CleaningDataList cleaningDataListPropose;
    public SetDataList setDataList;
    public ReplaceList replaceList;
    public UtDataList utDataList;
    public DownloadErrorList downloadErrorList;

    public MediaDataList mediaDataList;
    public MediaCollectionDataList mediaCollectionDataList = null;
    public HistoryList historyList; // alle angesehenen Filme
    public HistoryList historyListAbos; // erfolgreich geladenen Abos
    public HistoryList historyListBookmarks; // markierte Filme
    public ProposeList proposeList;
    public final BlackListFilter blackListFilterFilmList;
    public final BlackListFilter blackListFilterBlackList;

    private ProgData() {
        busy = new Busy();
        pShortcut = new PShortcut();
        replaceList = new ReplaceList();
        utDataList = new UtDataList();
        downloadErrorList = new DownloadErrorList();

        textFilterList = new TextFilterList();
        stringFilterLists = new StringFilter();
        filmFilterWorker = new FilmFilterWorker();
        liveFilmFilterWorker = new LiveFilmFilterWorker();

        filmList = new FilmListMTP();
        filmListFiltered = new FilmListMTP();

        filmListFilter = new BlackList(this, "FilmListFilter");
        blackList = new BlackList(this, "BlackList");
        cleaningDataListMedia = new CleaningDataList(false);
        cleaningDataListPropose = new CleaningDataList(true);
        setDataList = new SetDataList();
        aboList = new AboList(this);
        downloadList = new DownloadList(this);
//        downloadListButton = new DownloadList(this);

        filmFilterRunner = new FilmFilterRunner(this);

        historyList = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String(), HistoryList.HISTORY_LIST.HISTORY);
        historyListAbos = new HistoryList(ProgConst.FILE_FINISHED_ABOS,
                ProgInfos.getSettingsDirectory_String(), HistoryList.HISTORY_LIST.ABO);
        historyListBookmarks = new HistoryList(ProgConst.FILE_BOOKMARKS,
                ProgInfos.getSettingsDirectory_String(), HistoryList.HISTORY_LIST.BOOKMARK);

        mediaDataList = new MediaDataList();
        mediaCollectionDataList = new MediaCollectionDataList();

        proposeList = new ProposeList(this);

        blackListFilterFilmList = new BlackListFilter();
        blackListFilterBlackList = new BlackListFilter();

        startDownload = new StartDownload(this);
        downloadInfos = new DownloadInfos(this);
        chartData = new ChartData();
        progTray = new ProgTray(this);
        checkForNewFilmlist = new CheckForNewFilmlist();

        worker = new Worker(this);
    }

    boolean oneSecond = false;

    public void startTimer() {
        // extra starten, damit er im Einrichtungsdialog nicht dazwischen funkt
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(500), ae -> {

            oneSecond = !oneSecond;
            if (oneSecond) {
                doTimerWorkOneSecond();
            }
            doTimerWorkHalfSecond();

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(5));
        timeline.play();
        P2Duration.onlyPing("Timer gestartet");
    }

    private void doTimerWorkOneSecond() {
        ++countRunningTimeSeconds;
        PListener.notify(PListener.EVENT_TIMER_SECOND, ProgData.class.getName());
    }

    private void doTimerWorkHalfSecond() {
        PListener.notify(PListener.EVENT_TIMER_HALF_SECOND, ProgData.class.getName());
    }

    public synchronized static final ProgData getInstance(String dir) {
        if (!dir.isEmpty()) {
            configDir = dir;
        }
        return getInstance();
    }

    public synchronized static final ProgData getInstance() {
        return instance == null ? instance = new ProgData() : instance;
    }
}
