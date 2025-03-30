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
import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadListFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.controller.update.WhatsNewFactory;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDayFactory;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.LoadFilmlist;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class LoadFilmFactory {
    private static LoadFilmFactory instance;
    public LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste
    private static boolean doneAtProgramStart = false;
    private final ProgData progData;

    public LoadFilmFactory(ProgData progData) {
        this.progData = progData;

//        public synchronized static LoadFilmFactory getInstance() {
//            return instance == null ? instance = new LoadFilmFactory(new FilmListMTP(), new FilmListMTP()) : instance;
//        }
        Filmlist<FilmDataMTP> filmlistNew = new FilmListMTP();
        Filmlist<FilmDataMTP> filmlistDiff = new FilmListMTP();

        loadFilmlist = new LoadFilmlist(progData.pEventHandler, filmlistNew, filmlistDiff);
//        loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
//            @Override
//            public synchronized void start(P2LoadEvent event) {
//                ProgData.FILMLIST_IS_DOWNLOADING.setValue(true);
//                progData.worker.workOnFilmListLoadStart();
//                if (event.getAct() == PROGRESS_INDETERMINATE) {
//                    // ist dann die gespeicherte Filmliste
//                    progData.maskerPane.setMaskerVisible(true, true, false);
//                } else {
//                    progData.maskerPane.setMaskerVisible(true, true, true);
//                }
//                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
//            }
//
//            @Override
//            public synchronized void progress(P2LoadEvent event) {
//                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
//            }
//
//            @Override
//            public void loaded(P2LoadEvent event) {
//                // wird nach dem Laden mehrfach aufgerufen
//                progData.maskerPane.setMaskerVisible(true, true, false);
//                progData.maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
//                ProgData.FILMLIST_IS_DOWNLOADING.setValue(false);
//            }
//
//            @Override
//            public synchronized void finished(P2LoadEvent event) {
//                P2Duration.onlyPing("Filme geladen: Nachbearbeiten");
//                afterLoadingFilmList();
//            }
//        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(true);
                progData.worker.workOnFilmListLoadStart();
                if (event.getAct() == LoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, true, false);
                } else {
                    progData.maskerPane.setMaskerVisible(true, true, true);
                }
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_LOADED) {
            @Override
            public void pingGui() {
                // wird nach dem Laden mehrfach aufgerufen
                progData.maskerPane.setMaskerVisible(true, true, false);
                progData.maskerPane.setMaskerProgress(LoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(false);
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                P2Duration.onlyPing("Filme geladen: Nachbearbeiten");
                afterLoadingFilmList();
            }
        });
    }

    public void loadFilmlistProgStart() {
        initLoadFactoryConst();
        loadFilmlist.loadFilmlistProgStart();
    }

    public void loadNewListFromWeb(boolean alwaysLoadNew) {
        //es wird immer eine neue Filmliste aus dem Web geladen
        initLoadFactoryConst();

        if (!alwaysLoadNew && progData.filmGuiController != null /*mal vorsichtshalber*/) {
            // sonst machts keinen Sinn, sind dann ja alle neu
            progData.filmGuiController.getSel(true, false); // damit die letzte Pos gesetzt wird
        }

        loadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew/*, ProgInfos.getLocalFilmListFile()*/);
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.debug = ProgData.debug;

        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();

        LoadFactoryConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE.getValue();
        LoadFactoryConst.firstProgramStart = ProgData.firstProgramStart;
        LoadFactoryConst.localFilmListFile = ProgInfos.getLocalFilmListFile();
        LoadFactoryConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART.getValue()
                || ProgData.autoMode; // wenn gewollt oder im AutoMode immer laden

        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.filmlist = progData.filmList;
        LoadFactoryConst.loadFilmlist = loadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.filmListUrl = ProgData.filmListUrl;

        // ProgData.getInstance().filmListFilter.clearCounter(); //todo evtl. nur beim Neuladen einer kompletten Liste??
        if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_OFF) {
            //ist sonst evtl. noch von "vorher" gesetzt
            LoadFactoryConst.checker = null;

        } else if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_ON) {
            //dann sollen Filme geprüft werden
            progData.filmListFilter.clearCounter();
            LoadFactoryConst.checker = filmData -> BlacklistFilterFactory.checkFilmAndCountHits(filmData,
                    progData.filmListFilter, true);

        } else {
            //dann ist er inverse
            progData.filmListFilter.clearCounter();
            LoadFactoryConst.checker = filmData -> !BlacklistFilterFactory.checkFilmAndCountHits(filmData,
                    progData.filmListFilter, true);
        }
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadingFilmList() {
        new Thread(() -> {
            List<String> logList = new ArrayList<>();

            logList.add("Themen suchen");
            progData.filmList.loadTheme();

            logList.add("Abos eintragen");
            AboFactory.setAboForFilmlist();

            logList.add("Bookmarks eintragen");
            FilmToolsFactory.markBookmarks();

            logList.add("Blacklist filtern");
            progData.maskerPane.setMaskerText("Blacklist filtern");
            BlacklistFilterFactory.markFilmBlack(false);

            progData.blackList.sortAndCleanTheList();
            progData.filmListFilter.sortAndCleanTheList();

            logList.add("Filme in Downloads eingetragen");
            progData.maskerPane.setMaskerText("Downloads eingetragen");
            DownloadListFactory.addFilmInDownloads();

            P2Log.sysLog(logList);
            P2Duration.onlyPing("Filme nachbearbeiten: Ende");

            progData.maskerPane.setMaskerText("Abos suchen");
            progData.worker.workOnFilmListLoadFinished();

            // PListener.notify(PListener.EVENT_FILTER_CHANGED, LoadFilmFactory.class.getSimpleName());
            progData.pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);

            String filmDate = FilmlistFactory.getAgeAsStringDate(progData.filmList.metaData);
            ProgConfig.SYSTEM_FILMLIST_DATE.setValue(progData.filmList.isEmpty() ? "" : filmDate);

            //damit auf jeden Fall, aus
            progData.maskerPane.switchOffMasker();

            if (ProgData.firstProgramStart) {
                ProgData.firstProgramStart = false;
                Platform.runLater(ProgSave::saveAll); // damit nichts verloren geht
            }
            if (!doneAtProgramStart) {
                doneAtProgramStart = true;
                MediaDataWorker.createMediaDb();

                if (!ProgData.autoMode) {
                    // sonst macht es ja keinen Sinn
                    WhatsNewFactory.checkUpdate();
                    Platform.runLater(() -> {
                        ProgTipOfDayFactory.showDialog(progData, false);
                        if (ProgConfig.CHECK_SET_PROGRAM_START.get()) {
                            SetFactory.checkPrograms(progData.primaryStage, progData, false);
                        }
                    });
                }
            }
        }).start();
    }
}
