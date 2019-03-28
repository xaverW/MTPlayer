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

package de.mtplayer.mtp.controller.starter;

import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mLib.tools.MLInputStream;
import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.tools.log.PLog;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class StarterClass {
    // Tags Filme

    private final ProgData progData;
    private Start start = null;
    private boolean paused = false;
    private boolean searchFilms = false;

    // ===================================
    // Public
    // ===================================
    public StarterClass(ProgData progData) {
        this.progData = progData;
        start = new Start();
        start.start();

        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
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

    public synchronized void startUrlWithProgram(Film ersterFilm, SetData pSet, String resolution) {
        // url mit dem Programm mit der Nr. starten (Button oder TabDownload "rechte Maustaste")
        // Quelle "Button" ist immer ein vom User gestarteter Film, also Quelle_Button!!!!!!!!!!!

        final String url = ersterFilm.arr[FilmXml.FILM_URL];
        if (!url.isEmpty()) {
            final Download download = new Download(pSet, ersterFilm, DownloadConstants.SRC_BUTTON, null, "", "", resolution);
            progData.downloadList.startDownloads(download);

            start.startDownload(download); // da nicht in der ListeDownloads

            // und jetzt noch in die Downloadliste damit die Farbe im Tab Filme passt
            progData.downloadListButton.addWithNr(download);
        }
    }

    public void setPaused() {
        paused = true;
    }

    static boolean check(ProgData progData, Download download) {
        // prüfen ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        boolean ret = false;

        final double progress = download.getProgress();

        if (progress > DownloadConstants.PROGRESS_NOT_STARTED && progress < DownloadConstants.PROGRESS_NEARLY_FINISHED) {
            // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
            PLog.errorLog(696510258, "Download fehlgeschlagen: 99,5% wurden nicht erreicht: " + download.getDestPathFile());
            return false;
        }

        final File file = new File(download.getDestPathFile());
        if (!file.exists()) {
            PLog.errorLog(550236231, "Download fehlgeschlagen: Datei existiert nicht: " + download.getDestPathFile());
        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            PLog.errorLog(795632500, "Download fehlgeschlagen: Datei zu klein: " + download.getDestPathFile());
        } else {
            if (download.isAbo()) {
                progData.erledigteAbos.addHistoryDataToHistory(download.getTheme(), download.getTitle(), download.getHistoryUrl());
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

    static void startMsg(Download download) {
        final ArrayList<String> text = new ArrayList<>();
        final boolean play = download.getSource().equals(DownloadConstants.SRC_BUTTON);
        if (play) {
            text.add("Film abspielen");
        } else {
            if (download.getStart().getStartCounter() > 1) {
                text.add("Download starten - Restart (Summe Starts: " + download.getStart().getStartCounter() + ')');
            } else {
                text.add("Download starten");
            }
            text.add("Programmset: " + download.getSetData() == null ? "" : download.getSetData().getVisibleName());
            text.add("Ziel: " + download.getDestPathFile());
        }
        text.add("URL: " + download.getUrl());
        text.add("Startzeit: " + StringFormatters.FORMATTER_HHmmss.format(download.getStart().getStartTime()));
        if (download.getArt().equals(DownloadConstants.ART_DOWNLOAD)) {
            text.add(DownloadConstants.ART_DOWNLOAD);
        } else {
            text.add("Programmaufruf: " + download.getProgramCall());
            text.add("Programmaufruf[]: " + download.getProgramCallArray());
        }
        PLog.sysLog(text.toArray(new String[text.size()]));
    }

    private void restartMsg(Download download) {
        final ArrayList<String> text = new ArrayList<>();
        text.add("Fehlerhaften Download neu starten - Restart (Summe Starts: " + download.getStart().getRestartCounter() + ')');
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        PLog.sysLog(text.toArray(new String[text.size()]));
    }

    private static void finishedMsg(final Download download) {
        final de.mtplayer.mtp.controller.starter.Start start = download.getStart();
        if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_BEEP.get())) {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (final Exception ignored) {
            }
        }
        final ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE3);
        if (download.isStateStoped()) {
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

        list.add("Startzeit: " + StringFormatters.FORMATTER_HHmmss.format(start.getStartTime()));
        list.add("Endzeit: " + StringFormatters.FORMATTER_HHmmss.format(new MDate().getTime()));

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

        if (download.getArt().equals(DownloadConstants.ART_DOWNLOAD)) {
            if (start.getInputStream() != null) {
                list.add("Bytes gelesen: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumByte(), true));
                list.add("Bandbreite: " + SizeTools.humanReadableBandwidth(start.getInputStream().getSumBandwidth()));
            }
        }
        list.add("URL: " + download.getUrl());
        if (download.getArt().equals(DownloadConstants.ART_DOWNLOAD)) {
            list.add(DownloadConstants.ART_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }
        list.add(PLog.LILNE3);
        PLog.sysLog(list);

        if (!download.getSource().equals(DownloadConstants.SRC_BUTTON) && !download.isStateStoped()) {
            //war ein Abo und wurde nicht abgebrochen
            MTNotification.addNotification(download, download.isStateError());
        }
    }


    static void finalizeDownload(Download download) {

        final de.mtplayer.mtp.controller.starter.Start start = download.getStart();
        deleteIfEmpty(new File(download.getDestPathFile()));
        setFileSize(download);

        finishedMsg(download);

        if (download.isStateError()) {
            download.setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
//            MLProperty.setProperty(download.progressProperty(), DownloadConstants.PROGRESS_NOT_STARTED);
        } else if (!download.isStateStoped()) {
            //dann ist er gelaufen
            start.setTimeLeft(0);
            download.setProgress(DownloadConstants.PROGRESS_FINISHED);
//            MLProperty.setProperty(download.progressProperty(), DownloadConstants.PROGRESS_FINISHED);
            download.getDownloadSize().setAktFileSize(-1);

            if (start.getInputStream() != null) {
                download.setBandwidth("Ø " + SizeTools.humanReadableBandwidth(start.getInputStream().getSumBandwidth()));
            }

            final long dauer = start.getStartTime().diffInMinutes();
            if (dauer == 0) {
                download.setRemaining("Dauer: " + start.getStartTime().diffInSeconds() + " s");
            } else {
                download.setRemaining("Dauer: " + start.getStartTime().diffInMinutes() + " Min");
            }
        }

        start.setProcess(null);
        start.setInputStream(null);
        start.setStartTime(null);
    }

    /**
     * tatsächliche Dateigröße eintragen
     *
     * @param download {@link Download} with the info of the file
     */

    static void setFileSize(Download download) {
        try {
            final File testFile = new File(download.getDestPathFile());
            if (testFile.exists()) {
                final long length = testFile.length();
                if (length > 0) {
                    download.getDownloadSize().setSize(length);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(461204780,
                    "Fehler beim Ermitteln der Dateigröße: " + download.getDestPathFile());
        }
    }

    // ********************************************
    // Hier wird dann gestartet
    // Ewige Schleife die die Downloads startet
    // ********************************************
    private class Start extends Thread {

        private Download download;
        /**
         * The only {@link java.util.Timer} used for all
         * {@link MLInputStream.BandwidthCalculationTask} calculation tasks.
         */
        private final java.util.Timer bandwidthCalculationTimer;

        public Start() {
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
                    progData.downloadListButton.cleanUpButtonStarts(); // Button Starts aus der Liste
                    // löschen
                    sleep(3 * 1000);
                } catch (final Exception ex) {
                    PLog.errorLog(613822015, ex);
                }
            }
        }

        private synchronized Download getNextStart() throws InterruptedException {
            // erstes passende Element der Liste zurückgeben oder null
            // und versuchen dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
            if (paused) {
                // beim Löschen der Downloads, kann das Starten etwas "pausiert" werden
                // damit ein zu Löschender Download nicht noch schnell gestartet wird
                sleep(5 * 1000);
                paused = false;
            }

            Download download = progData.downloadList.getNextStart();
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
         * @param download The {@link Download} info object for download.
         */
        private void startDownload(Download download) {
            download.getStart().startDownload();
            Thread downloadThread;

            switch (download.getArt()) {
                case DownloadConstants.ART_PROGRAM:
                    downloadThread = new ExternalProgramDownload(progData, download);
                    downloadThread.start();
                    break;
                case DownloadConstants.ART_DOWNLOAD:
                default:
                    downloadThread = new DirectHttpDownload(progData, download, bandwidthCalculationTimer);
                    downloadThread.start();
                    break;
            }
        }
    }
}
