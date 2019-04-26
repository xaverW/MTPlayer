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

import de.mtplayer.mtp.MTPlayerController;
import de.mtplayer.mtp.controller.data.BlackList;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ReplaceList;
import de.mtplayer.mtp.controller.data.SetDataList;
import de.mtplayer.mtp.controller.data.abo.AboList;
import de.mtplayer.mtp.controller.data.download.DownloadList;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.filmlist.LoadFilmlist;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.SearchFilmListUrls;
import de.mtplayer.mtp.controller.history.HistoryList;
import de.mtplayer.mtp.controller.mediaDb.MediaCollectionDataList;
import de.mtplayer.mtp.controller.mediaDb.MediaDataList;
import de.mtplayer.mtp.controller.starter.StarterClass;
import de.mtplayer.mtp.controller.worker.Worker;
import de.mtplayer.mtp.gui.AboGuiController;
import de.mtplayer.mtp.gui.DownloadGuiController;
import de.mtplayer.mtp.gui.FilmGuiController;
import de.mtplayer.mtp.gui.dialog.FilmInfoDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.filmListFilter.FilmListFilter;
import de.mtplayer.mtp.tools.storedFilter.StoredFilter;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {

    private static ProgData instance;

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean duration = false; // Duration ausgeben
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen

    // zentrale Klassen
    public StarterClass starterClass = null; // Klasse zum Ausführen der Programme (für die Downloads): VLC, flvstreamer, ...
    public LoadFilmlist loadFilmlist; // erledigt das updaten der Filmliste
    public SearchFilmListUrls searchFilmListUrls; // da werden die DownloadURLs der Filmliste verwaltet
    public static final MTColor mTColor = new MTColor(); // verwendete Farben
    public StoredFilter storedFilter = null; // gespeicherte Filterprofile
    public FilmListFilter filmListFilter = null;

    // Gui
    public Stage primaryStage = null;
    public PMaskerPane maskerPane = null;
    public MTPlayerController mtPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public DownloadGuiController downloadGuiController = null; // Tab mit den Downloads
    public AboGuiController aboGuiController = null; // Tab mit den Abos
    public FilmInfoDialogController filmInfoDialogController = null;

    // Worker
    public Worker worker = null; // Liste aller Sender, Themen, ...

    // Programmdaten
    public Filmlist filmlist = null; // ist die komplette Filmliste
    public Filmlist filmlistFiltered = null; // Filmliste, wie im TabFilme angezeigt

    public DownloadList downloadList = null; // Filme die als "Download" geladen werden sollen
    public DownloadList downloadListButton = null; // Filme die über "Tab Filme" als Button/Film abspielen gestartet werden
    public AboList aboList = null;
    public BlackList blackList = null;
    public SetDataList setDataList = null;
    public MediaDataList mediaDataList = null;
    public MediaCollectionDataList mediaCollectionDataList = null;

    public ReplaceList replaceList = null;

    public HistoryList history = null; // alle angesehenen Filme
    public HistoryList erledigteAbos = null; // erfolgreich geladenen Abos
    public HistoryList bookmarks = null; // markierte Filme


    private ProgData() {
        replaceList = new ReplaceList();
        storedFilter = new StoredFilter(this);
        filmlist = new Filmlist();
        loadFilmlist = new LoadFilmlist(this);
        searchFilmListUrls = new SearchFilmListUrls();

        filmlistFiltered = new Filmlist();
        blackList = new BlackList(this);

        setDataList = new SetDataList();

        aboList = new AboList(this);

        downloadList = new DownloadList(this);
        downloadListButton = new DownloadList(this);

        filmListFilter = new FilmListFilter(this);


        history = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String(), false);
        erledigteAbos = new HistoryList(ProgConst.FILE_ERLEDIGTE_ABOS,
                ProgInfos.getSettingsDirectory_String(), false);
        bookmarks = new HistoryList(ProgConst.FILE_BOOKMARKS,
                ProgInfos.getSettingsDirectory_String(), true);


        mediaDataList = new MediaDataList();
        mediaCollectionDataList = new MediaCollectionDataList();

        starterClass = new StarterClass(this);

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
        Platform.runLater(() -> downloadList.makeDownloadInfo());
        Listener.notify(Listener.EREIGNIS_TIMER, ProgData.class.getName());
    }

    private void doTimerWorkHalfSecond() {
        Listener.notify(Listener.EREIGNIS_TIMER_HALF_SECOND, ProgData.class.getName());
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