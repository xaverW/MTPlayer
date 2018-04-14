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

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.film.FilmList;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.SearchFilmListUrls;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;

public class ImportFilmlist {

    private final EventListenerList eventListenerList;
    private final ReadFilmlist readFilmlist;
    public SearchFilmListUrls searchFilmListUrls;

    public ImportFilmlist() {
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
            public synchronized void fertig(ListenerFilmlistLoadEvent event) {
            }
        });
    }

    // #########################################################
    // Filmeliste importieren, URL automatisch wählen
    // #########################################################
    public void importFilmListAuto(FilmList filmList, FilmList filmListDiff, int days) {
        Daten.getInstance().loadFilmlist.setStop(false);
        Thread th = new Thread(new importAutoThread(filmList, filmListDiff, days));
        th.setName("importFilmListAuto");
        th.start();
    }

    public enum STATE {
        AKT, DIFF
    }

    private class importAutoThread implements Runnable {
        private final FilmList filmList;
        private final FilmList filmListDiff;
        private STATE state;
        private final int days;

        public importAutoThread(FilmList filmList, FilmList filmListDiff, int days) {
            this.filmList = filmList;
            this.filmListDiff = filmListDiff;
            this.days = days;
        }

        @Override
        public void run() {
            boolean ret;
            if (filmList.isTooOldForDiff()) {
                // dann eine komplette Liste laden
                state = STATE.AKT;
                filmList.clear();
                ret = searchActList(filmList);
            } else {
                // nur ein Update laden
                state = STATE.DIFF;
                ret = searchActList(filmListDiff);
                if (!ret || filmListDiff.isEmpty()) {
                    // wenn diff, dann nochmal mit einer kompletten Liste versuchen
                    state = STATE.AKT;
                    filmList.clear();
                    filmListDiff.clear();
                    ret = searchActList(filmList);
                }
            }
            if (!ret /* filmList ist schon wieder null -> "FilmeLaden" */) {
                PLog.errorLog(951235497, "Es konnten keine Filme geladen werden!");
            }
            fertigMelden(ret);
        }

        private boolean searchActList(FilmList liste) {
            boolean ret = false;
            final ArrayList<String> versuchteUrls = new ArrayList<>();
            String updateUrl = "";

            switch (state) {
                case AKT:
                    updateUrl = searchFilmListUrls.searchCompleteListUrl(versuchteUrls);
                    break;
                case DIFF:
                    updateUrl = searchFilmListUrls.searchDiffListUrl(versuchteUrls);
                    break;
            }

            if (updateUrl.isEmpty()) {
                return false;
            }

            // 5 mal mit einem anderen Server probieren, wenns nicht klappt
            final int maxRetries = state == STATE.DIFF ? 2 : 5; // bei diff nur 2x probieren, dann eine
            // akt-liste laden
            for (int i = 0; i < maxRetries; ++i) {
                ret = urlLaden(updateUrl, liste, days);
                if (ret && i < 1 && liste.isOlderThan(5 * 60 * 60 /* sekunden */)) {
                    // Laden hat geklappt ABER: Liste zu alt, dann gibts einen 2. Versuch
                    PLog.sysLog("Filmliste zu alt, neuer Versuch");
                    ret = false;
                }

                if (ret) {
                    // hat geklappt, nix wie weiter
                    return true;
                }

                switch (state) {
                    case AKT:
                        // nächste Adresse in der Liste wählen
                        updateUrl = searchFilmListUrls.filmListUrlList_akt.getRand(versuchteUrls);
                        break;
                    case DIFF:
                        // nächste Adresse in der Liste wählen
                        updateUrl = searchFilmListUrls.filmListUrlList_diff.getRand(versuchteUrls);
                        break;
                }
                versuchteUrls.add(updateUrl);
                // nur wenn nicht abgebrochen, weitermachen
                if (Daten.getInstance().loadFilmlist.getStop()) {
                    break;
                }

            }
            return ret;
        }
    }

    // #######################################
    // Filmeliste importieren, mit fester URL/Pfad
    // #######################################
    public void importFilmlistFromFile(String pfad, FilmList filmList, int days) {
        Daten.getInstance().loadFilmlist.setStop(false);
        Thread th = new Thread(new FilmImportFileThread(pfad, filmList, days));
        th.setName("importFilmlistFromFile");
        th.start();

    }

    private class FilmImportFileThread implements Runnable {

        private final String pfad;
        private final FilmList filmList;
        private final int days;

        public FilmImportFileThread(String pfad, FilmList filmList, int days) {
            this.pfad = pfad;
            this.filmList = filmList;
            this.days = days;
        }

        @Override
        public void run() {
            fertigMelden(urlLaden(pfad, filmList, days));
        }
    }

    // #######################################
    // #######################################
    public void addAdListener(ListenerFilmlistLoad listener) {
        eventListenerList.add(ListenerFilmlistLoad.class, listener);
    }

    private boolean urlLaden(String dateiUrl, FilmList filmList, int days) {
        boolean ret = false;
        try {
            if (!dateiUrl.isEmpty()) {
                PLog.sysLog("Filmliste laden von: " + dateiUrl);
                readFilmlist.readFilmListe(dateiUrl, filmList, days);
                if (!filmList.isEmpty()) {
                    ret = true;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(965412378, ex);
        }
        return ret;

    }

    private synchronized void fertigMelden(boolean ok) {
        for (final ListenerFilmlistLoad l : eventListenerList.getListeners(ListenerFilmlistLoad.class)) {
            l.fertig(new ListenerFilmlistLoadEvent("", "", 0, 0, 0, !ok));
        }
    }
}
