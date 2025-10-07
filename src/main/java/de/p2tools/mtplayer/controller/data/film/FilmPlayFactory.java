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


package de.p2tools.mtplayer.controller.data.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.starter.RuntimeExecPlay;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.date.P2Date;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.ArrayList;
import java.util.List;

public class FilmPlayFactory {
    private FilmPlayFactory() {
    }

    public static void playUrl(DownloadData download) {
        // aus Menü
        FilmDataMTP film = new FilmDataMTP();
        // und jetzt die tatsächlichen URLs des Downloads eintragen
        film.arr[FilmDataMTP.FILM_URL] = download.getUrl();
        film.arr[FilmDataMTP.FILM_URL_SMALL] = "";
        // und starten
        playFilm(download.isAudio(), film);
    }

    public static void playFilmListWithSet(boolean audio, SetData psetData, List<FilmDataMTP> list) {
        // Button/Menü: Film mit Set starten
        if (list.isEmpty()) {
            return;
        }

        if (psetData.isPlay()) {
            // dann ist es das Set zum Abspielen der Filme
            play(audio, list);
            return;
        }

        if (psetData.isSaveAbo()) {
            // wenn ein Set zum Speichern gewählt wurde, einen Download anlegen und starten
            FilmSaveFactory.saveFilmList(audio, list, psetData);
            return;
        }

        // und starten, dann nur den selektierten
//        final Optional<FilmDataMTP> filmDataMTP = ProgData.getInstance().filmGuiController.getSel(false, false);
//        filmDataMTP.ifPresent(dataMTP -> ProgData.getInstance().startDownload.startUrlWithProgram(dataMTP, psetData, ""));

        // todo aus filmGui oder liveFilmGui??
        ProgData.getInstance().startDownload.startUrlWithProgram(audio, list.get(0), psetData);
    }

    public static synchronized void playFilmList(boolean audio, ArrayList<FilmDataMTP> list) {
        // aus Menü/Button Film abspielen
        if (list.isEmpty()) {
            return;
        }
        play(audio, list);
    }

    public static synchronized void playFilm(boolean audio, FilmDataMTP mtp) {
        // aus Menü
        if (mtp != null) {
            ArrayList<FilmDataMTP> list = new ArrayList<>();
            list.add(mtp);
            play(audio, list);
        }
    }

    private static void play(boolean audio, List<FilmDataMTP> filmList) {
        // Film abspielen, Menü, Button
        SetData setData = ProgData.getInstance().setDataList.getSetDataPlay();
        if (setData == null) {
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.PLAY);
            return;
        }

        DownloadData downloadData = new DownloadData(audio, filmList, setData);
        final ArrayList<String> list = new ArrayList<>();
        startMsg(downloadData, list);

        ProgData.getInstance().historyList.addFilmDataListToHistory(filmList);
        final RuntimeExecPlay runtimeExec = new RuntimeExecPlay(downloadData);
        Process process = runtimeExec.exec(true /* log */);
        if (process != null) {
            // dann läuft er
            list.add("Film wurde gestartet");

        } else {
            // nicht gestartet
            list.add("Film konnte nicht gestartet werden");
            P2Alert.showErrorAlert("Film starten", "Kann den Film mit dem Aufruf nicht starten:" +
                    "\n" +
                    "\n" +
                    "------------------------------------------------" +
                    "\n" +
                    downloadData.getProgramCall() +
                    "\n" +
                    "------------------------------------------------" +
                    "\n" +
                    "\n" +
                    "Bitte im Programmmenü unter\n" +
                    " -> Einstellungen -> Aufzeichnen und Abspielen \n" +
                    "die Einstellungen zum Abspielen und Aufzeichnen von Filmen " +
                    "prüfen.");
        }
        list.add(P2Log.LILNE3);
        P2Log.sysLog(list.toArray(new String[0]));
    }

    static void startMsg(DownloadData downloadData, ArrayList<String> list) {
        list.add(P2Log.LILNE2);
        list.add("Film abspielen");
        list.add("URL: " + downloadData.getUrl());
        list.add("Startzeit: " + P2DateConst.F_FORMAT_HH__mm__ss.format(new P2Date()));
        list.add("Programmaufruf: " + downloadData.getProgramCall());
        list.add("Programmaufruf[]: " + downloadData.getProgramCallArray());
        list.add(P2Log.LILNE3);
    }
}
