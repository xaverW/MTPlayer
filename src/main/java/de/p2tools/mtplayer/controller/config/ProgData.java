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
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkList;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningDataList;
import de.p2tools.mtplayer.controller.data.download.DownloadInfos;
import de.p2tools.mtplayer.controller.data.download.DownloadList;
import de.p2tools.mtplayer.controller.data.download.ReplaceList;
import de.p2tools.mtplayer.controller.data.downloaderror.DownloadErrorList;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.mtplayer.controller.data.history.HistoryList;
import de.p2tools.mtplayer.controller.data.propose.ProposeList;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.data.utdata.UtDataList;
import de.p2tools.mtplayer.controller.filter.FilterWorker;
import de.p2tools.mtplayer.controller.filter.StringFilter;
import de.p2tools.mtplayer.controller.filter.TextFilterList;
import de.p2tools.mtplayer.controller.filter.audio.AudioFilterRunner;
import de.p2tools.mtplayer.controller.filterfilm.FilmFilterRunner;
import de.p2tools.mtplayer.controller.filterlive.LiveFilmFilterWorker;
import de.p2tools.mtplayer.controller.load.LoadAudioListWorker;
import de.p2tools.mtplayer.controller.load.LoadFilmListWorker;
import de.p2tools.mtplayer.controller.mediadb.MediaCollectionDataList;
import de.p2tools.mtplayer.controller.mediadb.MediaDataList;
import de.p2tools.mtplayer.controller.starter.StartDownload;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.controller.worker.CheckForNewFilmlist;
import de.p2tools.mtplayer.controller.worker.Worker;
import de.p2tools.mtplayer.gui.*;
import de.p2tools.mtplayer.gui.chart.ChartData;
import de.p2tools.mtplayer.gui.dialog.BookmarkDialogController;
import de.p2tools.mtplayer.gui.filter.AboFilterController;
import de.p2tools.mtplayer.gui.filter.DownloadFilterController;
import de.p2tools.mtplayer.gui.filter.audio.AudioFilterControllerClearFilter;
import de.p2tools.mtplayer.gui.filter.film.FilmFilterControllerClearFilter;
import de.p2tools.mtplayer.gui.tools.ProgTray;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.p2event.P2EventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

public class ProgData {

    private static ProgData instance;

    // flags
    public static boolean debug = false; // DebugModus
    public static boolean autoMode = false; // Automodus: start, laden, beenden
    public static boolean raspberry = false; // läuft auf einem Raspberry
    public static Busy busy; // zeigt Fortschritt (Abos suchen, ...) an

    public static String gui = ""; // zur Kontrolle/Debug
    public static String dialog = ""; // zur Kontrolle/Debug

    public P2EventHandler pEventHandler;

    public static boolean downloadSearchDone = false; // wird gesetzt, wenn das erste mal Downloads gesucht wurden
    public static boolean duration = false; // Duration ausgeben
    public static boolean startMinimized = false; // Minimiert starten, Startparameter -m
    public static boolean showUpdateAppParameter = false; // immer ein ProgrammUpdate anzeigen, Startparameter -s
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht

    public static BooleanProperty FILMLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Filmliste geladen, LoadFilmList.propLoadFilmlist kann nicht genommen werden, kann sonst nicht einfach zurückgesetzt werden
    public static BooleanProperty AUDIOLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Audioliste geladen

    public static BooleanProperty FILM_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty AUDIO_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty LIVE_FILM_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty ABO_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);

    // Infos
    public static String configDir = ""; //Verzeichnis zum Speichern der Programmeinstellungen
    public static String filmListUrl = ""; //URL von der die Filmliste geladen werden soll

    // zentrale Klassen
    public LoadFilmListWorker loadFilmListWorker;
    public LoadAudioListWorker loadAudioListWorker;

    public StartDownload startDownload; // Klasse zum Ausführen der Programme (für die Downloads): VLC, ...
    public PShortcut pShortcut; // verwendete Shortcuts
    //
    public FilterWorker filterWorkerFilm; // gespeicherte Filterprofile FILME
    public FilterWorker filterWorkerAudio; // gespeicherte Filterprofile Audios
    public TextFilterList textFilterListFilm; // ist die eine CBO mit den gespeicherten Textfiltern (Thema, Titel, ..)
    public TextFilterList textFilterListAudio; // ist die eine CBO mit den gespeicherten Textfiltern (Thema, Titel, ..)
    public StringFilter stringFilterLists; // sind die Text-Filter in den CBO's aller Filter

    public LiveFilmFilterWorker liveFilmFilterWorker; // Live
    public FilmFilterRunner filmFilterRunner; // Filme
    public AudioFilterRunner audioFilterRunner; // Audios

    // Gui
    public Stage primaryStage = null;
    public P2MaskerPane maskerPane = new P2MaskerPane();
    public MTPlayerController mtPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public AudioGuiController audioGuiController = null; // Tab mit den Filmen
    public LiveFilmGuiController liveFilmGuiController = null; // Tab mit den Filmen
    public DownloadGuiController downloadGuiController = null; // Tab mit den Downloads
    public DownloadFilterController downloadFilterController = null;
    public AboGuiController aboGuiController = null; // Tab mit den Abos
    public AboFilterController aboFilterController = null;
    public FilmFilterControllerClearFilter filmFilterControllerClearFilter = null;
    public AudioFilterControllerClearFilter audioFilterControllerClearFilter = null;
    public CheckForNewFilmlist checkForNewFilmlist;
    public final ChartData chartData;
    public final ProgTray progTray;
    public BookmarkDialogController bookmarkDialogController = null;

    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...
    public DownloadInfos downloadInfos;

    // Programmdaten
    public FilmListMTP filmList; // ist die komplette Filmliste
    public FilmListMTP filmListFiltered; // Filmliste, wie im TabFilme angezeigt

    public FilmListMTP audioList; // ist die komplette Audioliste
    public FilmListMTP audioListFiltered; // Audioliste, wie im TabAudio angezeigt

    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public AboList aboList;
    public BlackList filmListFilter;
    public BlackList blackList;
    public CleaningDataList cleaningDataListMedia;
    public CleaningDataList cleaningDataListPropose;
    public SetDataList setDataList;
    public ReplaceList replaceList;
    public UtDataList utDataList;
    public UtDataList markDataList;
    public DownloadErrorList downloadErrorList;

    public MediaDataList mediaDataList;
    public MediaCollectionDataList mediaCollectionDataList = null;
    public HistoryList historyList; // alle angesehenen Filme
    public HistoryList historyListAbos; // erfolgreich geladenen Abos
    public BookmarkList bookmarkList; // markierte Filme
    public ProposeList proposeList;
    public final BlackListFilter blackListFilterFilmList;
    public final BlackListFilter blackListFilterBlackList;

    private ProgData() {
        pEventHandler = new P2EventHandler(false);
        busy = new Busy();
        pShortcut = new PShortcut();
        replaceList = new ReplaceList();
        utDataList = new UtDataList(true);
        markDataList = new UtDataList(false);
        downloadErrorList = new DownloadErrorList();

        filmList = new FilmListMTP();
        filmListFiltered = new FilmListMTP();

        audioList = new FilmListMTP();
        audioListFiltered = new FilmListMTP();

        textFilterListFilm = new TextFilterList("FilterTextListFilm");
        textFilterListAudio = new TextFilterList("FilterTextListAudio");

        stringFilterLists = new StringFilter();
        filterWorkerFilm = new FilterWorker(false);
        liveFilmFilterWorker = new LiveFilmFilterWorker(this);
        filterWorkerAudio = new FilterWorker(true);


        filmListFilter = new BlackList(this, "FilmListFilter");
        blackList = new BlackList(this, "BlackList");
        cleaningDataListMedia = new CleaningDataList(false);
        cleaningDataListPropose = new CleaningDataList(true);
        setDataList = new SetDataList();
        aboList = new AboList(this);
        downloadList = new DownloadList(this);
//        downloadListButton = new DownloadList(this);

        filmFilterRunner = new FilmFilterRunner(this);
        audioFilterRunner = new AudioFilterRunner(this);

        historyList = new HistoryList(ProgConst.FILE_HISTORY, HistoryList.HISTORY_LIST.HISTORY);
        historyListAbos = new HistoryList(ProgConst.FILE_FINISHED_ABOS, HistoryList.HISTORY_LIST.ABO);
        bookmarkList = new BookmarkList();

        mediaDataList = new MediaDataList();
        mediaCollectionDataList = new MediaCollectionDataList();

        proposeList = new ProposeList(this);

        blackListFilterFilmList = new BlackListFilter();
        blackListFilterBlackList = new BlackListFilter();

        loadFilmListWorker = new LoadFilmListWorker(this);
        loadAudioListWorker = new LoadAudioListWorker(this);

        startDownload = new StartDownload(this);
        downloadInfos = new DownloadInfos(this);
        chartData = new ChartData();
        progTray = new ProgTray(this);
        checkForNewFilmlist = new CheckForNewFilmlist(this);

        worker = new Worker(this);
    }

    public synchronized static ProgData getInstance() {
        return instance == null ? instance = new ProgData() : instance;
    }
}
