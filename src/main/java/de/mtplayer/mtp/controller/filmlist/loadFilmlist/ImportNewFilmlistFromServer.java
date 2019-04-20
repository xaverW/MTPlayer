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

public class ImportNewFilmlistFromServer {

    private final EventListenerList eventListenerList;
    private final ReadFilmlist readFilmlist;
    private final ProgData progData;

    public ImportNewFilmlistFromServer(ProgData progData) {
        this.progData = progData;
        eventListenerList = new EventListenerList();
        readFilmlist = new ReadFilmlist();
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
        Thread th = new Thread(new ImportAutoThread(filmlist, filmlistDiff, days));
        th.setName("importFilmListAuto");
        th.start();
    }

    public enum STATE {
        COMPLETE, DIFF
    }

    private class ImportAutoThread implements Runnable {
        private final Filmlist filmlist;
        private final Filmlist filmlistDiff;
        private STATE state;
        private final int days;

        public ImportAutoThread(Filmlist filmlist, Filmlist filmlistDiff, int days) {
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
                PLog.addSysLog("komplette Filmliste laden");
                ret = loadList(filmlist);
            } else {
                // nur ein Update laden
                state = STATE.DIFF;
                PLog.addSysLog("Diffliste laden");
                ret = loadList(filmlistDiff);
                if (!ret || filmlistDiff.isEmpty()) {
                    // wenn diff, dann nochmal mit einer kompletten Liste versuchen
                    state = STATE.COMPLETE;
                    filmlist.clear();
                    filmlistDiff.clear();
                    PLog.addSysLog("Diffliste war leer, komplette Filmliste laden");
                    ret = loadList(filmlist);
                }
            }
            if (!ret) {
                PLog.errorLog(951235497, "Es konnten keine Filme geladen werden!");
            }
            reportFinished(ret);
        }

        private boolean loadList(Filmlist list) {
            boolean ret = false;
            final ArrayList<String> usedUrls = new ArrayList<>();
            String updateUrl;
            final int maxRetries = (state == STATE.DIFF ? 2 : 3); // 3x (bei diff nur 2x) probieren, eine Liste zu laden


            updateUrl = getUpdateUrl(state, usedUrls);
            if (updateUrl.isEmpty()) {
                return false;
            }


            for (int i = 0; i < maxRetries; ++i) {
                ret = loadFileUrl(updateUrl, list, days);
                if (ret && i < 1 && list.isOlderThan(5 * 60 * 60 /* sekunden */)) {
                    // Laden hat geklappt ABER: Liste zu alt, dann gibts einen 2. Versuch
                    PLog.addSysLog("Filmliste zu alt, neuer Versuch");
                    ret = false;
                }

                if (ret) {
                    // hat geklappt, nix wie weiter
                    return true;
                }

                if (ProgData.getInstance().loadFilmlist.isStop()) {
                    // wenn abgebrochen wurde, nicht weitermachen
                    return false;
                }

                // dann hat das Laden schon mal nicht geklappt
                SearchFilmListUrls.setUpdateFilmlistUrls(); // für die DownloadURLs "aktualisieren" setzen
                updateUrl = getUpdateUrl(state, usedUrls);

            }

            return ret;
        }
    }

    private String getUpdateUrl(STATE state, ArrayList<String> usedUrls) {
        // nächste Adresse in der Liste wählen
        final String updateUrl;

        switch (state) {
            case DIFF:
                updateUrl = progData.searchFilmListUrls.getFilmlistUrlForDiffList(usedUrls);
                break;
            case COMPLETE:
            default:
                updateUrl = progData.searchFilmListUrls.getFilmlistUrlForCompleteList(usedUrls);
                break;
        }

        return updateUrl;
    }

    // #######################################
    // Filmeliste importieren, mit fester URL/Pfad
    // #######################################
    public void importFilmlistFromFile(String path, Filmlist filmlist, int days) {
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
            final boolean ok = loadFileUrl(path, filmlist, days);
            reportFinished(ok);
        }
    }

    // #######################################
    // #######################################

    private boolean loadFileUrl(String fileUrl, Filmlist filmlist, int days) {
        boolean ret = false;
        try {
            if (!fileUrl.isEmpty()) {
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
