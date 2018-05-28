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

package de.mtplayer.mtp.controller.filmlist.loadFilmlist;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.SearchFilmListUrls;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;

public class ImportNewFilmlist {

    private final EventListenerList eventListenerList;
    private final ReadFilmlist readFilmlist;
    public SearchFilmListUrls searchFilmListUrls;

    public ImportNewFilmlist() {
        eventListenerList = new EventListenerList();
        readFilmlist = new ReadFilmlist();
        searchFilmListUrls = new SearchFilmListUrls();
        readFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                for (final ListenerFilmlistLoad l : eventListenerList.getListeners(ListenerFilmlistLoad.class)) {
                    l.start(event);
                }
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                for (final ListenerFilmlistLoad l : eventListenerList.getListeners(ListenerFilmlistLoad.class)) {
                    l.progress(event);

                }
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
            }
        });
    }

    public void addAdListener(ListenerFilmlistLoad listener) {
        eventListenerList.add(ListenerFilmlistLoad.class, listener);
    }

    // #########################################################
    // Filmeliste importieren, URL automatisch wählen
    // #########################################################
    public void importFilmListAuto(Filmlist filmlist, Filmlist filmlistDiff, int days) {
//        Daten.getInstance().loadFilmlist.setStop(false);
        Thread th = new Thread(new importAutoThread(filmlist, filmlistDiff, days));
        th.setName("importFilmListAuto");
        th.start();
    }

    public enum STATE {
        COMPLETE, DIFF
    }

    private class importAutoThread implements Runnable {
        private final Filmlist filmlist;
        private final Filmlist filmlistDiff;
        private STATE state;
        private final int days;

        public importAutoThread(Filmlist filmlist, Filmlist filmlistDiff, int days) {
            this.filmlist = filmlist;
            this.filmlistDiff = filmlistDiff;
            this.days = days;
        }

        @Override
        public void run() {
            boolean ret;
            if (filmlist.isTooOldForDiff()) {
                // dann eine komplette Liste laden
                state = STATE.COMPLETE;
                filmlist.clear();
                ret = loadList(filmlist);
            } else {
                // nur ein Update laden
                state = STATE.DIFF;
                ret = loadList(filmlistDiff);
                if (!ret || filmlistDiff.isEmpty()) {
                    // wenn diff, dann nochmal mit einer kompletten Liste versuchen
                    state = STATE.COMPLETE;
                    filmlist.clear();
                    filmlistDiff.clear();
                    ret = loadList(filmlist);
                }
            }
            if (!ret /* filmlist ist schon wieder null -> "FilmeLaden" */) {
                PLog.errorLog(951235497, "Es konnten keine Filme geladen werden!");
            }
            reportFinished(ret);
        }

        private boolean loadList(Filmlist list) {
            boolean ret = false;
            final ArrayList<String> usedUrls = new ArrayList<>();
            String updateUrl = "";

            switch (state) {
                case COMPLETE:
                    updateUrl = searchFilmListUrls.searchCompleteListUrl(usedUrls);
                    break;
                case DIFF:
                    updateUrl = searchFilmListUrls.searchDiffListUrl(usedUrls);
                    break;
            }

            if (updateUrl.isEmpty()) {
                return false;
            }

            // 5 mal mit einem anderen Server probieren, wenns nicht klappt
            final int maxRetries = state == STATE.DIFF ? 2 : 5; // bei diff nur 2x probieren, dann eine

            // liste laden
            for (int i = 0; i < maxRetries; ++i) {
                ret = loadFileUrl(updateUrl, list, days);
                if (ret && i < 1 && list.isOlderThan(5 * 60 * 60 /* sekunden */)) {
                    // Laden hat geklappt ABER: Liste zu alt, dann gibts einen 2. Versuch
                    PLog.sysLog("Filmliste zu alt, neuer Versuch");
                    ret = false;
                }

                if (ret) {
                    // hat geklappt, nix wie weiter
                    return true;
                }

                switch (state) {
                    case COMPLETE:
                        // nächste Adresse in der Liste wählen
                        updateUrl = searchFilmListUrls.filmlistUrlList_akt.getRand(usedUrls);
                        break;
                    case DIFF:
                        // nächste Adresse in der Liste wählen
                        updateUrl = searchFilmListUrls.filmlistUrlList_diff.getRand(usedUrls);
                        break;
                }

                usedUrls.add(updateUrl);
                // nur wenn nicht abgebrochen, weitermachen
                if (ProgData.getInstance().loadFilmlist.getStop()) {
                    break;
                }

            }
            return ret;
        }
    }

    // #######################################
    // Filmeliste importieren, mit fester URL/Pfad
    // #######################################
    public void importFilmlistFromFile(String path, Filmlist filmlist, int days) {
//        Daten.getInstance().loadFilmlist.setStop(false);
        Thread th = new Thread(new FilmImportFileThread(path, filmlist, days));
        th.setName("importFilmlistFromFile");
        th.start();

    }

    private class FilmImportFileThread implements Runnable {

        private final String path;
        private final Filmlist filmlist;
        private final int days;

        public FilmImportFileThread(String path, Filmlist filmlist, int days) {
            this.path = path;
            this.filmlist = filmlist;
            this.days = days;
        }

        @Override
        public void run() {
            reportFinished(loadFileUrl(path, filmlist, days));
        }
    }

    // #######################################
    // #######################################

    private boolean loadFileUrl(String fileUrl, Filmlist filmlist, int days) {
        boolean ret = false;
        try {
            if (!fileUrl.isEmpty()) {
                PLog.sysLog("Filmliste laden von: " + fileUrl);
                readFilmlist.readFilmlist(fileUrl, filmlist, days);
                if (!filmlist.isEmpty()) {
                    ret = true;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(965412378, ex);
        }
        return ret;

    }

    private synchronized void reportFinished(boolean ok) {
        for (final ListenerFilmlistLoad l : eventListenerList.getListeners(ListenerFilmlistLoad.class)) {
            l.finished(new ListenerFilmlistLoadEvent("", "", 0, 0, !ok));
        }
    }
}
