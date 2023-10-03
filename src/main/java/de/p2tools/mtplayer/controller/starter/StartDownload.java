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

package de.p2tools.mtplayer.controller.starter;


import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class StartDownload {
    private final ProgData progData;
    private final StarterThread starterThread;
    private final BooleanProperty paused = new SimpleBooleanProperty(false);
    private final BooleanProperty searchFilms = new SimpleBooleanProperty(true); // beim Programmstart muss zuerst die Filmliste geladen werden

    // ===================================
    // Public
    // ===================================
    public StartDownload(ProgData progData) {
        this.progData = progData;
        starterThread = new StarterThread(progData, paused, searchFilms);
        starterThread.start();

        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                searchFilms.setValue(true);
            }

            @Override
            public void finished(P2LoadEvent event) {
                searchFilms.setValue(false);
            }
        });
    }

    public synchronized void startUrlWithProgram(FilmDataMTP film, SetData pSet, String resolution) {
        // url mit dem Programm mit der Nr. starten (Button oder TabFilm, TabDownload "rechte Maustaste")
        // Quelle "Button" ist immer ein vom User gestarteter Film, also Quelle_Button!!!!!!!!!!!
        final String url = film.arr[FilmDataXml.FILM_URL];
        if (!url.isEmpty()) {
            final DownloadData download = new DownloadData(DownloadConstants.SRC_BUTTON, pSet, film, null, "", "", resolution);
            progData.downloadList.startDownloads(download);
            starterThread.startDownload(download); // da nicht in der ListeDownloads
        }
    }

    public void setPaused() {
        paused.setValue(true);
    }
}
