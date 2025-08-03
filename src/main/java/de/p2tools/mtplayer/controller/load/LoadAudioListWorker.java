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


import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadFilmlist;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadAudioListWorker {
    private static boolean doneAtProgramStart = false;
    private final ProgData progData;

    public LoadAudioListWorker(ProgData progData) {
        this.progData = progData;

        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_AUDIO_LIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(true);
                if (event.getAct() == P2LoadFilmlist.PROGRESS_INDETERMINATE) {
                    progData.maskerPane.setMaskerVisible(true, true, true);
                }
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_AUDIO_LIST_LOAD_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_AUDIO_LIST_LOAD_LOADED) {
            @Override
            public void pingGui() {
                // wird nach dem Laden mehrfach aufgerufen
                progData.maskerPane.setMaskerVisible(true, true, false);
                progData.maskerPane.setMaskerProgress(P2LoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_AUDIO_LIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                P2Duration.onlyPing("Filme geladen: Nachbearbeiten");
                afterLoading();
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(false);
            }
        });
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoading() {
        new Thread(() -> {
            List<String> logList = new ArrayList<>();

            logList.add("Themen suchen");
            progData.audioList.loadTheme();

            logList.add("Bookmarks eintragen");
            BookmarkFactory.markBookmarks();

            logList.add("Blacklist filtern");
            progData.maskerPane.setMaskerText("Blacklist filtern");
            progData.audioListFiltered.setAll(progData.audioList);

            logList.add("Filme in Downloads eingetragen");
            progData.maskerPane.setMaskerText("Downloads eingetragen");
            addFilmInDownloads();

            P2Log.sysLog(logList);
            P2Duration.onlyPing("Filme nachbearbeiten: Ende");

            workOnFilmListLoadFinished();

            progData.pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);

            //damit auf jeden Fall, aus
            progData.maskerPane.switchOffMasker();

        }).start();
    }

    public void workOnFilmListLoadFinished() {
        Platform.runLater(() -> {
            // alle Sender laden
            ThemeListFactory.allChannelList.setAll(Arrays.asList(progData.audioList.sender));
            // und jetzt noch die Themen für den Sender des aktuellen Filters laden
            ThemeListFactory.createThemeList(progData, progData.filterWorker.getActFilterSettings().getChannel());

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
