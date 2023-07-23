/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.starter.RuntimeExecStartFilm;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;

public class FilmPlayFactory {
    private FilmPlayFactory() {
    }

    public static synchronized void startFilmUrl(ArrayList<FilmDataMTP> list) {
        playFilm(list);
    }

    public static synchronized void startFilmUrl(FilmDataMTP mtp) {
        // aus Menü
        if (mtp != null) {
            ArrayList<FilmDataMTP> list = new ArrayList<>();
            list.add(mtp);
            playFilm(list);
        }
    }

    public static void playFilm(ArrayList<FilmDataMTP> list) {
        // aus Menü
        SetData setData = ProgData.getInstance().setDataList.getSetDataPlay();
        if (setData == null) {
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.PLAY);
            return;
        }

        DownloadData filmPlayerData = new DownloadData(list, setData);
        startUrlWithProgram(filmPlayerData);
    }

    public static synchronized void startUrlWithProgram(DownloadData filmPlayerData) {
        // url mit dem Programm mit der Nr. starten (Button oder TabFilm, TabDownload "rechte Maustaste")
        // Quelle "Button" ist immer ein vom User gestarteter Film, also Quelle_Button!!!!!!!!!!!

        final String url = filmPlayerData.getFilmList().get(0).arr[FilmDataXml.FILM_URL];
        if (!url.isEmpty()) {
            ProgData.getInstance().downloadList.startDownloads(filmPlayerData);
            startDownload(filmPlayerData); // da nicht in der ListeDownloads

            // und jetzt noch in die DownloadListe damit die Farbe im Tab Filme passt
            ProgData.getInstance().downloadListButton.addWithNr(filmPlayerData);
        }
    }

    private static void startDownload(DownloadData filmPlayerData) {
        // versuch das Programm zu starten
        final ArrayList<String> list = new ArrayList<>();
        startMsg(filmPlayerData, list);

        final RuntimeExecStartFilm runtimeExec = new RuntimeExecStartFilm(filmPlayerData);
        Process process = runtimeExec.exec(true /* log */);
        if (process != null) {
            // dann läuft er
            list.add("Film wurde gestartet");
        } else {
            // nicht gestartet
            list.add("Film konnte nicht gestartet werden");
        }
        PLog.sysLog(list.toArray(new String[0]));
    }

    static void startMsg(DownloadData filmPlayerData, ArrayList<String> list) {
        list.add(PLog.LILNE3);
        list.add("Film abspielen");
        list.add("URL: " + filmPlayerData.getUrl());
        list.add("Startzeit: " + DateFactory.F_FORMAT_HH__mm__ss.format(new PDate()));
        list.add("Programmaufruf: " + filmPlayerData.getProgramCall());
        list.add("Programmaufruf[]: " + filmPlayerData.getProgramCallArray());
        list.add(PLog.LILNE_EMPTY);
    }
}
