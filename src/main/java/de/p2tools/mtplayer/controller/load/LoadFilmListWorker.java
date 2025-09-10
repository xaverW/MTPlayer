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


package de.p2tools.mtplayer.controller.load;


import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboSearchDownloadsFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.controller.update.WhatsNewFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDayFactory;
import de.p2tools.p2lib.mediathek.film.P2FilmlistFactory;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadFilmlist;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadFilmListWorker {
    private static boolean doneAtProgramStart = false;
    private final ProgData progData;

    public LoadFilmListWorker(ProgData progData) {
        this.progData = progData;
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(true);
                workOnFilmListLoadStart();
                if (event.getAct() == P2LoadFilmlist.PROGRESS_INDETERMINATE) {
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
                progData.maskerPane.setMaskerProgress(P2LoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                P2Duration.onlyPing("Filme geladen: Nachbearbeiten");
                afterLoadingFilmList();
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(false);
            }
        });
    }

    public void workOnFilmListLoadStart() {
        // the channel combo will be reset, therefore save the filter
        progData.worker.saveFilter();
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadingFilmList() {
        boolean search = !ProgData.AUDIOLIST_IS_DOWNLOADING.get();
        new Thread(() -> {
            List<String> logList = new ArrayList<>();

            logList.add("Themen suchen");
            progData.filmList.loadTheme();

            logList.add("Abos eintragen");
            AboFactory.setAboForList(false, ProgData.getInstance().filmList);

            logList.add("Bookmarks eintragen");
            BookmarkFactory.markBookmarks();

            logList.add("Blacklist filtern");
            progData.maskerPane.setMaskerText("Blacklist filtern");
            BlacklistFilterFactory.markFilmsIfBlack(false, false);

            progData.blackList.sortAndCleanTheList();
            progData.filmListFilter.sortAndCleanTheList();

            logList.add("Filme in Downloads eingetragen");
            progData.maskerPane.setMaskerText("Downloads eingetragen");
            addFilmInDownloads();

            P2Log.sysLog(logList);
            P2Duration.onlyPing("Filme nachbearbeiten: Ende");

            progData.maskerPane.setMaskerText("Abos suchen");
            workOnFilmListLoadFinished(search);

            progData.pEventHandler.notifyListener(PEvents.EVENT_FILTER_FILM_CHANGED);

            String filmDate = P2FilmlistFactory.getAgeAsStringDate(progData.filmList.metaData);
            ProgConfig.SYSTEM_FILMLIST_DATE.setValue(progData.filmList.isEmpty() ? "" : filmDate);

            //damit auf jeden Fall, aus
            if (!ProgData.AUDIOLIST_IS_DOWNLOADING.get()) {
                progData.maskerPane.switchOffMasker();
            }

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
                        ProgTipOfDayFactory.showDialog(ProgData.getInstance(), false);
                        if (ProgConfig.CHECK_SET_PROGRAM_START.get()) {
                            SetFactory.checkPrograms(progData.primaryStage, progData, false);
                        }
                    });
                }
            }
        }).start();
    }

    private void workOnFilmListLoadFinished(boolean search) {
        Platform.runLater(() -> {
            // alle Sender laden
            ThemeListFactory.allChannelListFilm.setAll(Arrays.asList(progData.filmList.sender));

            // und jetzt noch die Themen für den Sender des aktuellen Filters laden
            ThemeListFactory.createThemeList(false, progData, progData.filterWorkerFilm.getActFilterSettings().getChannel());

            if (search && (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode)) {
                // wenn gewollt oder im AutoMode immer suchen
                AboSearchDownloadsFactory.searchForDownloadsFromAbosAndMaybeStart();
            }

            // activate the saved filter
            progData.worker.resetFilter();
        });
    }

    private static synchronized void addFilmInDownloads() {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        P2Duration.counterStart("addFilmInList");
        ProgData progData = ProgData.getInstance();
        int counter = 50; //todo das dauert sonst viel zu lang
        for (DownloadData d : progData.downloadList) {
            if (d.isAudio()) {
                continue;
            }

            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(progData.filmList.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
            d.initResolution();
        }
        P2Duration.counterStop("addFilmInList");
    }
}
