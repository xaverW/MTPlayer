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

package de.mtplayer.mtp.controller.loadFilmlist;

import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.film.FilmList;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;

public class ImportFilmList {

    private final EventListenerList eventListenerList;
    private final ReadFilmlist readFilmlist;
    public SearchFilmListUrls searchFilmListUrls;

    public ImportFilmList() {
        eventListenerList = new EventListenerList();
        readFilmlist = new ReadFilmlist();
        searchFilmListUrls = new SearchFilmListUrls();
        readFilmlist.addAdListener(new ListenerFilmListLoad() {
            @Override
            public synchronized void start(ListenerFilmListLoadEvent event) {
                for (final ListenerFilmListLoad l : eventListenerList.getListeners(ListenerFilmListLoad.class)) {
                    l.start(event);
                }
            }

            @Override
            public synchronized void progress(ListenerFilmListLoadEvent event) {
                for (final ListenerFilmListLoad l : eventListenerList.getListeners(ListenerFilmListLoad.class)) {
                    l.progress(event);

                }
            }

            @Override
            public synchronized void fertig(ListenerFilmListLoadEvent event) {
            }
        });
    }

    // #########################################################
    // Filmeliste importieren, URL automatisch wählen
    // #########################################################
    public void filmeImportierenAuto(FilmList filmList, FilmList filmListDiff, int days) {
        Daten.getInstance().loadFilmList.setStop(false);
        new Thread(new FilmeImportierenAutoThread(filmList, filmListDiff, days)).start();
    }

    public enum STATE {
        AKT, DIFF
    }

    private class FilmeImportierenAutoThread implements Runnable {
        private final FilmList filmList;
        private final FilmList filmListDiff;
        private STATE state;
        private final int days;

        public FilmeImportierenAutoThread(FilmList filmList, FilmList filmListDiff, int days) {
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
                Log.errorLog(951235497, "Es konnten keine Filme geladen werden!");
            }
            fertigMelden(ret);
        }

        private boolean searchActList(FilmList liste) {
            boolean ret = false;
            final ArrayList<String> versuchteUrls = new ArrayList<>();
            String updateUrl = "";

            switch (state) {
                case AKT:
                    updateUrl = searchFilmListUrls.suchenAkt(versuchteUrls);
                    break;
                case DIFF:
                    updateUrl = searchFilmListUrls.suchenDiff(versuchteUrls);
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
                    Log.sysLog("Filmliste zu alt, neuer Versuch");
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
                if (Daten.getInstance().loadFilmList.getStop()) {
                    break;
                }

            }
            return ret;
        }
    }

    // #######################################
    // Filmeliste importieren, mit fester URL/Pfad
    // #######################################
    public void filmeImportierenDatei(String pfad, FilmList filmList, int days) {
        Daten.getInstance().loadFilmList.setStop(false);
        new Thread(new FilmeImportierenDateiThread(pfad, filmList, days)).start();

    }

    private class FilmeImportierenDateiThread implements Runnable {

        private final String pfad;
        private final FilmList filmList;
        private final int days;

        public FilmeImportierenDateiThread(String pfad, FilmList filmList, int days) {
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
    public void addAdListener(ListenerFilmListLoad listener) {
        eventListenerList.add(ListenerFilmListLoad.class, listener);
    }

    public void updateDownloadUrlsFilmlisten(boolean akt) {
        searchFilmListUrls.updateURLsFilmlisten(akt);
    }

    private boolean urlLaden(String dateiUrl, FilmList filmList, int days) {
        boolean ret = false;
        try {
            if (!dateiUrl.isEmpty()) {
                Log.sysLog("Filmliste laden von: " + dateiUrl);
                readFilmlist.readFilmListe(dateiUrl, filmList, days);
                if (!filmList.isEmpty()) {
                    ret = true;
                }
            }
        } catch (final Exception ex) {
            Log.errorLog(965412378, ex);
        }
        return ret;

    }

    private synchronized void fertigMelden(boolean ok) {
        for (final ListenerFilmListLoad l : eventListenerList.getListeners(ListenerFilmListLoad.class)) {
            l.fertig(new ListenerFilmListLoadEvent("", "", 0, 0, 0, !ok));
        }
    }
}
