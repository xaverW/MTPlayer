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
import de.p2tools.mtplayer.controller.data.download.DownloadInfos;
import de.p2tools.mtplayer.controller.data.download.DownloadList;
import de.p2tools.mtplayer.controller.data.download.ReplaceList;
import de.p2tools.mtplayer.controller.data.mediacleaningdata.MediaCleaningList;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.controller.filmfilter.ActFilmFilterWorker;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterRunner;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.mtplayer.controller.mediadb.MediaCollectionDataList;
import de.p2tools.mtplayer.controller.mediadb.MediaDataList;
import de.p2tools.mtplayer.controller.starter.StarterClass;
import de.p2tools.mtplayer.controller.worker.CheckForNewFilmlist;
import de.p2tools.mtplayer.controller.worker.Worker;
import de.p2tools.mtplayer.gui.AboGuiController;
import de.p2tools.mtplayer.gui.DownloadGuiController;
import de.p2tools.mtplayer.gui.FilmGuiController;
import de.p2tools.mtplayer.gui.chart.ChartData;
import de.p2tools.mtplayer.gui.dialog.QuitDialogController;
import de.p2tools.mtplayer.gui.filter.AboFilterController;
import de.p2tools.mtplayer.gui.filter.DownloadFilterController;
import de.p2tools.mtplayer.gui.filter.FilmFilterControllerClearFilter;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.ProgTray;
import de.p2tools.p2lib.configfile.pdata.PData;
import de.p2tools.p2lib.guitools.pmask.PMaskerPane;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {

    private static ProgData instance;

    // flags
    public static boolean debug = false; //Debugmodus
    public static boolean autoMode = false; //Automodus: start, laden, beenden
    public static boolean duration = false; //Duration ausgeben
    public static boolean startMinimized = false; //Minimiert starten
    public static boolean reset = false; //Programm auf Starteinstellungen zurücksetzen
    public static boolean firstProgramStart = false; //ist der allererste Programmstart: Init wird gemacht

    // Infos
    public static String configDir = ""; //Verzeichnis zum Speichern der Programmeinstellungen
    public static String filmListUrl = ""; //URL von der die Filmliste geladen werde soll

    // zentrale Klassen
    public StarterClass starterClass; // Klasse zum Ausführen der Programme (für die Downloads): VLC, ...
    //    public MTColor mTColor; // verwendete Farben
    public ProgShortcut progShortcut; // verwendete Shortcuts
    public ActFilmFilterWorker actFilmFilterWorker; // gespeicherte Filterprofile
    public FilmFilterRunner filmFilterRunner;

    // Gui
    public Stage primaryStage = null;
    public PMaskerPane maskerPane = null;
    public MTPlayerController mtPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public DownloadGuiController downloadGuiController = null; // Tab mit den Downloads
    public DownloadFilterController downloadFilterController = null;
    public AboGuiController aboGuiController = null; // Tab mit den Abos
    public AboFilterController aboFilterController = null;
    public FilmFilterControllerClearFilter filmFilterControllerClearFilter = null;
    public final ChartData chartData;
    public final ProgTray progTray;
    public QuitDialogController quitDialogController = null;
//    public BooleanProperty guiFilmIsVisible = new SimpleBooleanProperty(false);
//    public BooleanProperty guiDownloadIsVisible = new SimpleBooleanProperty(false);
//    public BooleanProperty guiAboIsVisible = new SimpleBooleanProperty(false);

    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...
    public DownloadInfos downloadInfos;

    // Programmdaten
    public FilmlistMTP filmlist; // ist die komplette Filmliste
    public FilmlistMTP filmlistFiltered; // Filmliste, wie im TabFilme angezeigt

    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public DownloadList downloadListButton; // Filme die über "Tab Filme" als Button/Film abspielen gestartet werden
    public AboList aboList;
    public BlackList filmListFilter;
    public BlackList blackList;
    public MediaCleaningList mediaCleaningList;
    public SetDataList setDataList;
    public MediaDataList mediaDataList;
    public MediaCollectionDataList mediaCollectionDataList = null;

    public ReplaceList replaceList;

    public HistoryList history; // alle angesehenen Filme
    public HistoryList erledigteAbos; // erfolgreich geladenen Abos
    public HistoryList bookmarks; // markierte Filme
    public CheckForNewFilmlist checkForNewFilmlist;

    private ProgData() {
//        mTColor = new MTColor();
        progShortcut = new ProgShortcut();
        replaceList = new ReplaceList();

        actFilmFilterWorker = new ActFilmFilterWorker(this);
        filmlist = new FilmlistMTP();
        filmlistFiltered = new FilmlistMTP();

        filmListFilter = new BlackList(this, "FilmListFilter" + PData.TAGGER + "FilmTitleList");
        blackList = new BlackList(this, "BlackList");
        mediaCleaningList = new MediaCleaningList(this);
        setDataList = new SetDataList();
        aboList = new AboList(this);
        downloadList = new DownloadList(this);
        downloadListButton = new DownloadList(this);

        filmFilterRunner = new FilmFilterRunner(this);

        history = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String(), false);
        erledigteAbos = new HistoryList(ProgConst.FILE_ERLEDIGTE_ABOS,
                ProgInfos.getSettingsDirectory_String(), false);
        bookmarks = new HistoryList(ProgConst.FILE_BOOKMARKS,
                ProgInfos.getSettingsDirectory_String(), true);

        mediaDataList = new MediaDataList();
        mediaCollectionDataList = new MediaCollectionDataList();

        starterClass = new StarterClass(this);
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
        PDuration.onlyPing("Timer gestartet");
    }

    private void doTimerWorkOneSecond() {
        Listener.notify(Listener.EVENT_TIMER, ProgData.class.getName());
    }

    private void doTimerWorkHalfSecond() {
        Listener.notify(Listener.EVENT_TIMER_HALF_SECOND, ProgData.class.getName());
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
