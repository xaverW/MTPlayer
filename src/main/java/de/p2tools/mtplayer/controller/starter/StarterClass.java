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


import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.tools.SizeTools;
import de.p2tools.mtplayer.gui.dialog.AutomodeContinueDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class StarterClass {
    private final ProgData progData;
    private StarterThread starterThread = null;
    private boolean paused = false;
    private boolean searchFilms = true; // beim Programmstart muss zuerst die Filmliste geladen werden
    private boolean checkQuitAfterDownload = false; // Prüfen, ob autoMode aktiv ist

    // ===================================
    // Public
    // ===================================
    public StarterClass(ProgData progData) {
        this.progData = progData;
        starterThread = new StarterThread();
        starterThread.start();

        LoadFilmFactory.getInstance().loadFilmlist.filmListLoadNotifier.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                searchFilms = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                searchFilms = false;
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

            // und jetzt noch in die DownloadListe, für die Anzahl in der Toolbar im Tab Filme
//            progData.downloadListButton.addWithNr(download);
        }
    }

    public void setPaused() {
        paused = true;
    }

    static boolean check(ProgData progData, DownloadData download) {
        // prüfen, ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        boolean ret = false;

        final double progress = download.getProgress();

        if (progress > DownloadConstants.PROGRESS_NOT_STARTED && progress < DownloadConstants.PROGRESS_NEARLY_FINISHED) {
            // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
            PLog.errorLog(696510258, "Download fehlgeschlagen: 99,5% wurden nicht erreicht: " + progress + "%, " + download.getDestPathFile());
            return false;
        }

        final File file = new File(download.getDestPathFile());
        if (!file.exists()) {
            PLog.errorLog(550236231, "Download fehlgeschlagen: Datei existiert nicht: " + download.getDestPathFile());
        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            PLog.errorLog(795632500, "Download fehlgeschlagen: Datei zu klein: " + download.getDestPathFile());
        } else {
            if (download.isAbo()) {
                progData.historyListAbos.addHistoryDataToHistory(download.getTheme(), download.getTitle(), download.getHistoryUrl());
            }
            ret = true;
        }
        return ret;
    }

    /**
     * Delete the file if filesize is less that a constant value.
     *
     * @param file The file which is to be deleted.
     */
    static void deleteIfEmpty(File file) {
        try {
            if (file.exists()) {
                // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
                if (file.length() == 0) {
                    // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
                    PLog.sysLog(new String[]{"Restart/Aufräumen: leere Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
                    PLog.sysLog(new String[]{"Restart/Aufräumen: Zu kleine Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(795632500, "Fehler beim löschen" + file.getAbsolutePath());
        }
    }

    static void startMsg(DownloadData download) {
        final ArrayList<String> list = new ArrayList<>();
        final boolean play = download.getSource().equals(DownloadConstants.SRC_BUTTON);
        list.add(PLog.LILNE3);
        if (play) {
            list.add("Film abspielen");
        } else {
            if (download.getStart().getRestartCounter() > 0) {
                list.add("Download starten - Restart (Summe Starts: " + download.getStart().getRestartCounter() + ')');
            } else {
                list.add("Download starten");
            }
            list.add("Programmset: " + download.getSetData() == null ? "" : download.getSetData().getVisibleName());
            list.add("Ziel: " + download.getDestPathFile());
        }
        list.add("URL: " + download.getUrl());
        list.add("Startzeit: " + DateFactory.F_FORMAT_HH__mm__ss.format(download.getStart().getStartTime()));
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            list.add(DownloadConstants.TYPE_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }
        list.add(PLog.LILNE_EMPTY);
        PLog.sysLog(list.toArray(new String[list.size()]));
    }

    private void restartMsg(DownloadData download) {
        final ArrayList<String> text = new ArrayList<>();
        text.add("Fehlerhaften Download neu starten - Restart (Summe Starts: " + download.getStart().getRestartCounter() + ')');
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        PLog.sysLog(text.toArray(new String[text.size()]));
    }

    private static void finishedMsg(final DownloadData download) {
        final Start start = download.getStart();
        if (ProgConfig.DOWNLOAD_BEEP.getValue()) {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (final Exception ignored) {
            }
        }
        final ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE3);
        if (download.isStateStopped()) {
            list.add("Download wurde abgebrochen");
        } else if (download.getSource().equals(DownloadConstants.SRC_BUTTON)) {
            list.add("Film fertig");

        } else {
            if (download.isStateFinished()) {
                // dann ists gut
                list.add("Download ist fertig und hat geklappt");
            } else if (download.isStateError()) {
                list.add("Download ist fertig und war fehlerhaft");
            }
            if (download.getProgramDownloadmanager()) {
                list.add("Programm ist ein Downloadmanager");
            }
            list.add("Programmset: " + download.getSetData() == null ? "" : download.getSetData().getVisibleName());
            list.add("Ziel: " + download.getDestPathFile());
        }

        list.add("Startzeit: " + DateFactory.F_FORMAT_HH__mm__ss.format(start.getStartTime()));
        list.add("Endzeit: " + DateFactory.F_FORMAT_HH__mm__ss.format(new PDate().getTime()));

        if (start.getRestartCounter() > 0) {
            list.add("Restarts: " + start.getRestartCounter());
        }

        final long dauer = start.getStartTime().diffInMinutes();
        if (dauer == 0) {
            list.add("Dauer: " + start.getStartTime().diffInSeconds() + " s");
            //list.add("Dauer: <1 Min.");
        } else {
            list.add("Dauer: " + start.getStartTime().diffInMinutes() + " Min");
        }

        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            if (start.getInputStream() != null) {
                list.add("Bytes gelesen: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumByte(), true));
                list.add("Bandbreite: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumBandwidth(), true));
            }
        }
        list.add("URL: " + download.getUrl());
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            list.add(DownloadConstants.TYPE_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }
        list.add(PLog.LILNE_EMPTY);
        PLog.sysLog(list);

        if (!download.getSource().equals(DownloadConstants.SRC_BUTTON) && !download.isStateStopped()) {
            //war ein Abo und wurde nicht abgebrochen
            MTNotification.addNotification(download, download.isStateError());
        }
    }

    static void finalizeDownload(DownloadData download) {

        final Start start = download.getStart();
        deleteIfEmpty(new File(download.getDestPathFile()));
        setFileSize(download);

        finishedMsg(download);

        if (download.isStateError()) {
            download.setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
        } else if (!download.isStateStopped()) {
            //dann ist er gelaufen
            start.setTimeLeftSeconds(0);
            download.setProgress(DownloadConstants.PROGRESS_FINISHED);
            download.getDownloadSize().setActFileSize(-1);

            if (start.getInputStream() != null) {
                download.setBandwidthEnd(start.getInputStream().getSumBandwidth());
            }

            download.setRemaining(-1 * start.getStartTime().diffInSeconds());
        }

        download.setNo(P2LibConst.NUMBER_NOT_STARTED);
        ProgData.getInstance().downloadGuiController.tableRefresh();
        start.setProcess(null);
        start.setInputStream(null);
        start.setStartTime(null);
    }

    /**
     * tatsächliche Dateigröße eintragen
     *
     * @param download {@link DownloadData} with the info of the file
     */

    static void setFileSize(DownloadData download) {
        try {
            final File destFile = new File(download.getDestPathFile());
            if (destFile.exists()) {
                final long length = destFile.length();
                if (length > 0)
                    if (download.getDownloadSize().getSize() > 0) {
                        //nur wenn der Download schon eine Größe hatte, nicht bei m3u8!
                        download.getDownloadSize().setSize(length);
                    } else {
                        //bei m3u8 nur die aktSize setzen!
                        download.getDownloadSize().setActFileSize(length);
                    }
            }
        } catch (
                final Exception ex) {
            PLog.errorLog(461204780,
                    "Fehler beim Ermitteln der Dateigröße: " + download.getDestPathFile());
        }
    }

    // ********************************************
    // Hier wird dann gestartet
    // ewige Schleife, die die Downloads startet
    // ********************************************
    private class StarterThread extends Thread {

        private DownloadData download;
        private final java.util.Timer bandwidthCalculationTimer;

        public StarterThread() {
            super();
            setName("DownloadStarter Daemon Thread");
            setDaemon(true);
            bandwidthCalculationTimer = new java.util.Timer("BandwidthCalculationTimer");
        }

        @Override
        public synchronized void run() {
            while (!isInterrupted()) {
                try {
                    if (searchFilms) {
                        // da machmer nix
                        sleep(5 * 1000);
                        continue;
                    }

                    while ((download = getNextStart()) != null) {
                        if (searchFilms) {
                            break;
                        }

                        startDownload(download);
                        // alle 5 Sekunden einen Download starten
                        sleep(5 * 1000);
                    }
                    if (searchFilms) {
                        continue;
                    }

//                    progData.downloadListButton.cleanUpButtonStarts(); // Button Starts aus der Liste
                    if (!checkQuitAfterDownload) {
                        // ist für den auto mode
                        // wenn noch nicht geprüft, dann jetzt und beenden, wenn autoMode
                        quitProgramAfterDownload();
                    }

                    sleep(3 * 1000);
                } catch (final Exception ex) {
                    PLog.errorLog(613822015, ex);
                }
            }
        }

        private void quitProgramAfterDownload() {
            checkQuitAfterDownload = true;
            if (ProgData.autoMode) {
                //dann haben wir den "Automodus"
                Platform.runLater(() -> {
                    if (progData.downloadList.countStartedAndRunningDownloads() == 0) {
                        //dann gibts keine gestarteten Downloads und das Programm beendet sich sofort nach dem Start
                        //drum nur eine kurze Info
                        final AutomodeContinueDialogController dialogController = new AutomodeContinueDialogController();
                        if (dialogController.isContinueAutomode()) {
                            ProgQuit.quit(true);
                        } else {
                            // autoMode abgebrochen
                            ProgData.autoMode = false;
                        }

                    } else {
                        //dann gleich den "Quitt-Dialog" anzeigen, ist ja eine Weile zu sehen
                        ProgQuit.quit(true); //->todo
                        //dann wurde das "Beenden" abgebrochen
                        ProgData.autoMode = false;
                    }
                });
            }
        }

        private synchronized DownloadData getNextStart() throws InterruptedException {
            //ersten passenden Download der Liste zurückgeben oder null und versuchen,
            //dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
            if (paused) {
                //beim Löschen der Downloads kann das Starten etwas "pausiert" werden
                //damit ein zu löschender Download nicht noch schnell gestartet wird
                sleep(5 * 1000);
                paused = false;
            }

            DownloadData download = progData.downloadList.getNextStart();
            if (download == null) {
                // dann versuchen einen Fehlerhaften nochmal zu starten
                download = progData.downloadList.getRestartDownload();
                if (download != null) {
                    restartMsg(download);
                }
            }
            return download;
        }

        /**
         * This will start the download process.
         *
         * @param download The {@link DownloadData} info object for download.
         */
        private void startDownload(DownloadData download) {
            download.getStart().startDownload();
            Thread downloadThread;

            switch (download.getType()) {
                case DownloadConstants.TYPE_PROGRAM:
                    downloadThread = new ExternalProgramDownload(progData, download);
                    downloadThread.start();
                    break;
                case DownloadConstants.TYPE_DOWNLOAD:
                default:
                    downloadThread = new DirectHttpDownload(progData, download, bandwidthCalculationTimer);
                    downloadThread.start();
                    break;
            }
        }
    }
}
