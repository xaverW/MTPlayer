/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.film;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.UpdateCheckFactory;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDayFactory;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.mtfilm.loadfilmlist.LoadFilmlist;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class LoadFilmFactory {
    private static LoadFilmFactory instance;
    public static LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste
    private static boolean doneAtProgramStart = false;

    public void loadFilmlistProgStart() {
        initLoadFactoryConst();
        loadFilmlist.loadFilmlistProgStart();
    }

    public void loadNewListFromWeb(boolean alwaysLoadNew) {
        //es wird immer eine neue Filmliste aus dem Web geladen
        initLoadFactoryConst();
        loadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew, ProgInfos.getLocalFilmListFile());
    }

    public synchronized static final LoadFilmFactory getInstance() {
        return instance == null ? instance = new LoadFilmFactory(new FilmlistMTP(), new FilmlistMTP()) : instance;
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.debug = ProgData.debug;

        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        LoadFactoryConst.DOWNLOAD_MAX_BANDWIDTH_KBYTE = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE;
        LoadFactoryConst.downloadMaxBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();

        LoadFactoryConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE.getValue();
        LoadFactoryConst.firstProgramStart = ProgData.firstProgramStart;
        LoadFactoryConst.localFilmListFile = ProgInfos.getLocalFilmListFile();
        LoadFactoryConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART.getValue();

        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.filmlist = ProgData.getInstance().filmlist;
        LoadFactoryConst.loadFilmlist = loadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.filmListUrl = ProgData.filmListUrl;

        // ProgData.getInstance().filmListFilter.clearCounter();//todo evtl. nur beim Neuladen einer kompletten Liste??
        if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_OFF) {
            //ist sonst evtl. noch von "vorher" gesetzt
            LoadFactoryConst.checker = null;
        } else if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_ON) {
            //dann sollen Filme geprüft werden
            ProgData.getInstance().filmListFilter.clearCounter();
            LoadFactoryConst.checker = filmData -> BlacklistFilterFactory.checkFilmListFilterAndCountHits(filmData,
                    ProgData.getInstance().filmListFilter, true);
        } else {
            //dann ist er inverse
            ProgData.getInstance().filmListFilter.clearCounter();
            LoadFactoryConst.checker = filmData -> !BlacklistFilterFactory.checkFilmListFilterAndCountHits(filmData,
                    ProgData.getInstance().filmListFilter, true);
        }
    }

    private LoadFilmFactory(Filmlist<FilmDataMTP> filmlistNew, Filmlist<FilmDataMTP> filmlistDiff) {
        loadFilmlist = new LoadFilmlist(filmlistNew, filmlistDiff);
        loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().worker.workOnLoadStart();
                if (event.progress == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, true, true);
                }
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
                PDuration.onlyPing("Filme geladen: Nachbearbeiten");
                afterLoadingFilmlist();
            }
        });
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadingFilmlist() {
        new Thread(() -> {
            List<String> logList = new ArrayList<>();

            logList.add("Themen suchen");
            ProgData.getInstance().filmlist.loadTheme();

            logList.add("Abos eintragen");
            AboFactory.setAboForFilmlist();

            logList.add("Bookmarks eintragen");
            FilmTools.markBookmarks();

            logList.add("Blacklist filtern");
            ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
            BlacklistFilterFactory.markFilmBlack(false);

            ProgData.getInstance().blackList.sortAndCleanTheList();
            ProgData.getInstance().filmListFilter.sortAndCleanTheList();

            logList.add("Filme in Downloads eingetragen");
            ProgData.getInstance().maskerPane.setMaskerText("Downloads eingetragen");
            ProgData.getInstance().downloadList.addFilmInList();

            PLog.sysLog(logList);

            if (ProgData.firstProgramStart) {
                ProgSave.saveAll(); // damit nichts verloren geht
            }
            PDuration.onlyPing("Filme nachbearbeiten: Ende");

            ProgData.getInstance().maskerPane.setMaskerText("Abos suchen");
            ProgData.getInstance().worker.workOnLoadFinished();
            ProgData.getInstance().filmFilterRunner.filter();

            String filmDate = FilmlistFactory.getAgeAsStringDate(ProgData.getInstance().filmlist.metaData);
            ProgConfig.SYSTEM_FILMLIST_DATE.setValue(ProgData.getInstance().filmlist.isEmpty() ? "" : filmDate);

            if (!doneAtProgramStart) {
                doneAtProgramStart = true;
                MediaDataWorker.createMediaDb();
                UpdateCheckFactory.checkProgUpdate();
                Platform.runLater(() -> ProgTipOfDayFactory.showDialog(ProgData.getInstance(), false));
            }

            //damit auf jedem Fall, aus
            ProgData.getInstance().maskerPane.switchOffMasker();
        }).start();
    }
}
