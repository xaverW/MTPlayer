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

import de.mtplayer.mLib.tools.PlayerMsg;
import de.mtplayer.mtp.MTPlayerController;
import de.mtplayer.mtp.controller.data.*;
import de.mtplayer.mtp.controller.data.abo.AboList;
import de.mtplayer.mtp.controller.data.download.DownloadList;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.filmlist.LoadFilmlist;
import de.mtplayer.mtp.controller.mediaDb.MediaList;
import de.mtplayer.mtp.controller.mediaDb.MediaPathList;
import de.mtplayer.mtp.controller.starter.StarterClass;
import de.mtplayer.mtp.gui.AboGuiController;
import de.mtplayer.mtp.gui.DownloadGuiController;
import de.mtplayer.mtp.gui.FilmGuiController;
import de.mtplayer.mtp.gui.dialog.FilmInfosDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.filmListFilter.FilmListFilter;
import de.mtplayer.mtp.tools.storedFilter.StoredFilter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {

    private static ProgData instance;

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen
    public NameLists nameLists = null; // Liste aller Sender, Themen, ...

    // zentrale Klassen
    public StarterClass starterClass = null; // Klasse zum Ausführen der Programme (für die Downloads): VLC, flvstreamer, ...
    public LoadFilmlist loadFilmlist; // erledigt das updaten der Filmliste
    public static final MTColor mTColor = new MTColor(); // verwendete Farben
    public StoredFilter storedFilter = null; // gespeicherte Filterprofile
    public FilmListFilter filmListFilter = null;

    // Gui
    public Stage primaryStage = null;
    public MTPlayerController mtPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public DownloadGuiController downloadGuiController = null; // Tab mit den Downloads
    public AboGuiController aboGuiController = null; // Tab mit den Abos

    public FilmInfosDialogController filmInfosDialogController = null;

    // Programmdaten
    public Filmlist filmlist = null; // ist die komplette Filmliste
    public Filmlist filmlistFiltered = null; // Filmliste, wie im TabFilme angezeigt
    public DownloadList downloadList = null; // Filme die als "Download" geladen werden sollen
    public DownloadList downloadListButton = null; // Filme die über "Tab Filme" als Button/Film abspielen gestartet werden
    public AboList aboList = null;
    public BlackList blackList = null;
    public SetList setList = null;
    public MediaList mediaList = null;
    public MediaPathList mediaPathList = null;
    public HistoryList history = null; // alle angesehenen Filme
    public HistoryList erledigteAbos = null; // erfolgreich geladenen Abos
    public ReplaceList replaceList = null;
    public PlayerMsg playerMsg = null;

    private ProgData() {
        playerMsg = new PlayerMsg();
        replaceList = new ReplaceList();
        storedFilter = new StoredFilter(this);
        filmlist = new Filmlist();
        loadFilmlist = new LoadFilmlist(this);

        filmlistFiltered = new Filmlist();
        blackList = new BlackList(this);

        setList = new SetList();

        aboList = new AboList(this);

        downloadList = new DownloadList(this);
        downloadListButton = new DownloadList(this);

        filmListFilter = new FilmListFilter(this);

        erledigteAbos = new HistoryList(ProgConst.FILE_ERLEDIGTE_ABOS,
                ProgInfos.getSettingsDirectory_String());

        history = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String());

        mediaList = new MediaList();
        mediaPathList = new MediaPathList();

        starterClass = new StarterClass(this);

        nameLists = new NameLists(this);


        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000), ae -> {
            downloadList.makeDownloadInfos();
            Listener.notify(Listener.EREIGNIS_TIMER, ProgData.class.getName());
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(5));
        timeline.play();
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


    public void initDialogs() {
        filmInfosDialogController = new FilmInfosDialogController(this);
    }


}