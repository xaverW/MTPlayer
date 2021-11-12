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

package de.p2tools.mtplayer.controller.filmlist.loadFilmlist;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.Filmlist;
import de.p2tools.mtplayer.controller.filmlist.filmlistUrls.SearchFilmListUrls;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;

public class ImportNewFilmlistFromServer {

    private final EventListenerList eventListenerList;
    private final ReadFilmlist readFilmlist;
    private final ProgData progData;
    private final int REDUCED_BANDWIDTH = 55;//ist ein Wert, der nicht eingestellt werden kann
    private int savedBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();

    public ImportNewFilmlistFromServer(ProgData progData) {
        this.progData = progData;
        eventListenerList = new EventListenerList();
        readFilmlist = new ReadFilmlist();
        readFilmlist.addAdListener(new ListenerLoadFilmlist() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                // save download bandwidth
                if (ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue() == REDUCED_BANDWIDTH) {
                    PLog.sysLog("Bandbreite reduzieren: Ist schon reduziert!!!!");
                } else {
                    savedBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
                    PLog.sysLog("Bandbreite zurücksetzen für das Laden der Filmliste von: " + savedBandwidth + " auf " + REDUCED_BANDWIDTH);
                    Platform.runLater(() -> {
                        ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.setValue(REDUCED_BANDWIDTH);
                    });
                }

                for (final ListenerLoadFilmlist l : eventListenerList.getListeners(ListenerLoadFilmlist.class)) {
                    l.start(event);
                }
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                for (final ListenerLoadFilmlist l : eventListenerList.getListeners(ListenerLoadFilmlist.class)) {
                    l.progress(event);
                }
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
                // reset download bandwidth
                PLog.sysLog("Bandbreite wieder herstellen: " + savedBandwidth);
                Platform.runLater(() -> {
                    ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.setValue(savedBandwidth);
                });
            }
        });
    }

    public void addAdListener(ListenerLoadFilmlist listener) {
        eventListenerList.add(ListenerLoadFilmlist.class, listener);
    }

    // #########################################################
    // Filmeliste importieren, URL automatisch wählen
    // #########################################################
    public void importFilmListAuto(Filmlist filmlist, Filmlist filmlistDiff) {
        Thread th = new Thread(new ImportAutoThread(filmlist, filmlistDiff));
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


        public ImportAutoThread(Filmlist filmlist, Filmlist filmlistDiff) {
            this.filmlist = filmlist;
            this.filmlistDiff = filmlistDiff;
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
                ret = loadFileUrl(updateUrl, list);
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
    public void importFilmlistFromFile(String path, Filmlist filmlist) {
        Thread th = new Thread(new FilmImportFileThread(path, filmlist));
        th.setName("importFilmlistFromFile");
        th.start();

    }

    private class FilmImportFileThread implements Runnable {

        private final String path;
        private final Filmlist filmlist;

        public FilmImportFileThread(String path, Filmlist filmlist) {
            this.path = path;
            this.filmlist = filmlist;
        }

        @Override
        public void run() {
            final boolean ok = loadFileUrl(path, filmlist);
            reportFinished(ok);
        }
    }

    // #######################################
    // #######################################

    private boolean loadFileUrl(String fileUrl, Filmlist filmlist) {
        boolean ret = false;
        try {
            if (!fileUrl.isEmpty()) {
                readFilmlist.readFilmlist(fileUrl, filmlist);
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
        for (final ListenerLoadFilmlist l : eventListenerList.getListeners(ListenerLoadFilmlist.class)) {
            l.finished(new ListenerFilmlistLoadEvent("", "", 0, 0, !ok));
        }
    }
}
