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
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.BlackList;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilterFactory;
import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.mtFilm.film.Filmlist;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.LoadFilmlist;
import de.p2tools.p2Lib.mtFilm.tools.LoadFactoryConst;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.List;

public class LoadFilmFactory {
    private static LoadFilmFactory instance;
    public static LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste

    private LoadFilmFactory(Filmlist<FilmDataMTP> filmlistNew, Filmlist<FilmDataMTP> filmlistDiff) {
        loadFilmlist = new LoadFilmlist(filmlistNew, filmlistDiff);
        loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public synchronized void start(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().worker.workOnLoadStart();
                if (event.progress == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, true);
                }
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public synchronized void progress(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerVisible(true, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public synchronized void finished(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
                PDuration.onlyPing("Filme geladen: Nachbearbeiten");
                afterLoadingFilmlist();
                ProgSave.saveAll(); // damit nichts verloren geht
                PDuration.onlyPing("Filme nachbearbeiten: Ende");

                if (!ProgConfig.ABO_SEARCH_NOW.getValue() && !ProgData.automode) {
                    //wird sonst eh noch gemacht
                    ProgData.getInstance().maskerPane.setMaskerVisible(false);
                }
                ProgData.getInstance().worker.workOnLoadFinished();
                ProgData.getInstance().filmFilterRunner.filter();
            }
        });
    }

    public void loadNewListFromWeb(boolean alwaysLoadNew) {
        //es wird immer eine neue Filmliste aus dem Web geladen
        initLoadFactoryConst();
        loadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew, ProgInfos.getLocalFilmListFile());
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadingFilmlist() {
        List<String> logList = new ArrayList<>();
        logList.add("Themen suchen");
        ProgData.getInstance().filmlist.loadTheme();

        if (!ProgData.getInstance().aboList.isEmpty()) {
            logList.add("Abos eintragen");
            AboFactory.setAboForFilmlist(ProgData.getInstance().filmlist, ProgData.getInstance().aboList);
        }

        if (!ProgData.getInstance().bookmarks.isEmpty()) {
            logList.add("Bookmarks eintragen");
            FilmTools.markBookmarks();
        }

        logList.add("Blacklist filtern");
        ProgData.getInstance().filmlist.filterListWithBlacklist(true);

        logList.add("Filme in Downloads eingetragen");
        ProgData.getInstance().downloadList.addFilmInList();

        PLog.sysLog(logList);
    }


    public void loadFilmlistProgStart(boolean firstProgramStart) {
        initLoadFactoryConst();
        loadFilmlist.loadFilmlistProgStart(firstProgramStart,
                ProgInfos.getLocalFilmListFile(), ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART.getValue());
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.debug = ProgData.debug;
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        LoadFactoryConst.DOWNLOAD_MAX_BANDWIDTH_KBYTE = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE;
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.loadFilmlist = loadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadFactoryConst.filmlist = ProgData.getInstance().filmlist;

        LoadFactoryConst.FilmChecker filmChecker = filmData ->
                checkFilmAgainstBlacklist(filmData, ProgData.getInstance().filmLoadBlackList);

        if (ProgConfig.SYSTEM_USE_FILTER_LOAD_FILMLIST.getValue()) {
            //nur dann sollen Filme geprüft werden
            LoadFactoryConst.checker = filmChecker;
        } else {
            //ist sonst evtl. noch von "vorher" gesetzt
            LoadFactoryConst.checker = null;
        }
    }

    public synchronized static final LoadFilmFactory getInstance() {
        return instance == null ? instance = new LoadFilmFactory(new FilmlistMTP(), new FilmlistMTP()) : instance;
    }

    private static synchronized boolean checkFilmAgainstBlacklist(FilmData film, BlackList list) {
        for (final BlackData blackData : list) {
            if (FilmFilterFactory.checkFilmWithBlacklistFilter(blackData, film)) {
                return true;
            }
        }
        return false;
    }

}
