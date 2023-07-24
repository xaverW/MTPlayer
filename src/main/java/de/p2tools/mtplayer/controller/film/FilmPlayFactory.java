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
import de.p2tools.mtplayer.controller.starter.RuntimeExecPlayFilm;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilmPlayFactory {
    private FilmPlayFactory() {
    }

    public static void playFilmListWithSet(SetData psetData) {
        // Button/Menü: Film mit Set starten
        List<FilmDataMTP> list = ProgData.getInstance().filmGuiController.getSelList(true);
        if (list.isEmpty()) {
            return;
        }

        if (psetData.isPlay()) {
            // dann ist es das Set zum Abspielen der Filme
            play(list);
            return;
        }

        if (psetData.isSave()) {
            // wenn ein Set zum Speichern gewählt wurde, einen Download anlegen und starten
            FilmSaveFactory.saveFilmList(list, psetData);
            return;
        }

        // und starten, dann nur den selektierten
        final Optional<FilmDataMTP> filmDataMTP = ProgData.getInstance().filmGuiController.getSel(false, false);
        filmDataMTP.ifPresent(dataMTP -> ProgData.getInstance().starterClass.startUrlWithProgram(dataMTP, psetData, ""));
    }

    public static synchronized void playFilmList() {
        // aus Menü/Button Film abspielen
        ArrayList<FilmDataMTP> list = ProgData.getInstance().filmGuiController.getSelList(true);
        if (list.isEmpty()) {
            return;
        }
        play(list);
    }

    public static synchronized void playFilm() {
        // aus Menü
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
        filmSelection.ifPresent(FilmPlayFactory::playFilm);
    }

    public static synchronized void playFilm(FilmDataMTP mtp) {
        // aus Menü
        if (mtp != null) {
            ArrayList<FilmDataMTP> list = new ArrayList<>();
            list.add(mtp);
            play(list);
        }
    }

    private static void play(List<FilmDataMTP> filmList) {
        SetData setData = ProgData.getInstance().setDataList.getSetDataPlay();
        if (setData == null) {
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.PLAY);
            return;
        }

        DownloadData downloadData = new DownloadData(filmList, setData);
        playNow(filmList, downloadData);
    }

    private static void playNow(List<FilmDataMTP> filmList, DownloadData downloadData) {
        // versuch das Programm zu starten
        final ArrayList<String> list = new ArrayList<>();
        startMsg(downloadData, list);

        ProgData.getInstance().history.addFilmDataListToHistory(filmList);
        final RuntimeExecPlayFilm runtimeExec = new RuntimeExecPlayFilm(downloadData);
        Process process = runtimeExec.exec(true /* log */);
        if (process != null) {
            // dann läuft er
            list.add("Film wurde gestartet");
        } else {
            // nicht gestartet
            list.add("Film konnte nicht gestartet werden");
        }
        list.add(PLog.LILNE3);
        PLog.sysLog(list.toArray(new String[0]));
    }

    static void startMsg(DownloadData downloadData, ArrayList<String> list) {
        list.add(PLog.LILNE2);
        list.add("Film abspielen");
        list.add("URL: " + downloadData.getUrl());
        list.add("Startzeit: " + DateFactory.F_FORMAT_HH__mm__ss.format(new PDate()));
        list.add("Programmaufruf: " + downloadData.getProgramCall());
        list.add("Programmaufruf[]: " + downloadData.getProgramCallArray());
        list.add(PLog.LILNE3);
    }
}
