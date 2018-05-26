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

import de.mtplayer.mLib.tools.*;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.tools.log.PLog;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class StarterClass {
    // Tags Filme

    private final ProgData progData;
    private Starten starten = null;
    private boolean pause = false;

    // ===================================
    // Public
    // ===================================
    public StarterClass(ProgData progData) {
        this.progData = progData;
        starten = new Starten();
        starten.start();
    }

    public synchronized void urlMitProgrammStarten(Film ersterFilm, SetData pSet, String aufloesung) {
        // url mit dem Programm mit der Nr. starten (Button oder TabDownload "rechte Maustaste")
        // Quelle "Button" ist immer ein vom User gestarteter Film, also Quelle_Button!!!!!!!!!!!

        final String url = ersterFilm.arr[FilmXml.FILM_URL];
        if (!url.isEmpty()) {
            final Download download = new Download(pSet, ersterFilm, DownloadInfos.SRC_BUTTON, null, "", "", aufloesung);
            progData.downloadList.startDownloads(download);

            starten.startStarten(download); // da nicht in der ListeDownloads

            // und jetzt noch in die Downloadliste damit die Farbe im Tab Filme passt
            progData.downloadListButton.addWithNr(download);
        }
    }

    public void pause() {
        pause = true;
    }

    static boolean pruefen(ProgData progData, Download datenDownload) {
        // prüfen ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        boolean ret = false;

        final double progress = datenDownload.getProgress();

        if (progress > DownloadInfos.PROGRESS_NICHT_GESTARTET && progress < DownloadInfos.PROGRESS_FAST_FERTIG) {
            // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
            PLog.errorLog(696510258, "Download fehlgeschlagen: 99,5% wurden nicht erreicht" + datenDownload.getZielPfadDatei());
            return false;
        }

        final File file = new File(datenDownload.getZielPfadDatei());
        if (!file.exists()) {
            PLog.errorLog(550236231, "Download fehlgeschlagen: Datei existiert nicht" + datenDownload.getZielPfadDatei());
        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            PLog.errorLog(795632500, "Download fehlgeschlagen: Datei zu klein" + datenDownload.getZielPfadDatei());
        } else {
            if (datenDownload.isAbo()) {
                progData.erledigteAbos.writeHistory(datenDownload.getTheme(), datenDownload.getTitle(), datenDownload.getHistoryUrl());
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
                    PLog.userLog(new String[]{"Restart/Aufräumen: leere Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
                    PLog.userLog(new String[]{"Restart/Aufräumen: Zu kleine Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(795632500, "Fehler beim löschen" + file.getAbsolutePath());
        }
    }

    static void startmeldung(Download download) {
        final ArrayList<String> text = new ArrayList<>();
        final boolean abspielen = download.getSource().equals(DownloadInfos.SRC_BUTTON);
        if (abspielen) {
            text.add("Film abspielen");
        } else {
            if (download.getStart().getStartCounter() > 1) {
                text.add("Download starten - Restart (Summe Starts: " + download.getStart().getStartCounter() + ')');
            } else {
                text.add("Download starten");
            }
            text.add("Programmset: " + download.getSet());
            text.add("Ziel: " + download.getZielPfadDatei());
        }
        text.add("URL: " + download.getUrl());
        text.add("Startzeit: " + StringFormatters.FORMATTER_HHmmss.format(download.getStart().getStartTime()));
        if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
            text.add(DownloadInfos.ART_DOWNLOAD);
        } else {
            text.add("Programmaufruf: " + download.getProgrammAufruf());
            text.add("Programmaufruf[]: " + download.getProgrammAufrufArray());
        }
        PLog.userLog(text.toArray(new String[text.size()]));
    }

    private void reStartmeldung(Download datenDownload) {
        final ArrayList<String> text = new ArrayList<>();
        text.add("Fehlerhaften Download neu starten - Restart (Summe Starts: " + datenDownload.getStart().getRestartCounter() + ')');
        text.add("Ziel: " + datenDownload.getZielPfadDatei());
        text.add("URL: " + datenDownload.getUrl());
        PLog.userLog(text.toArray(new String[text.size()]));
    }

    private static void fertigmeldung(final Download download) {
        final Start start = download.getStart();
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
        } else if (download.getSource().equals(DownloadInfos.SRC_BUTTON)) {
            list.add("Film fertig");
        } else {
            if (download.isStateFinished()) {
                // dann ists gut
                list.add("Download ist fertig und hat geklappt");
            } else if (download.isStateError()) {
                list.add("Download ist fertig und war fehlerhaft");
            }
            if (download.isProgrammDownloadmanager()) {
                list.add("Programm ist ein Downloadmanager");
            }
            list.add("Programmset: " + download.getSet());
            list.add("Ziel: " + download.getZielPfadDatei());
        }
        list.add("Startzeit: " + StringFormatters.FORMATTER_HHmmss.format(start.getStartTime()));
        list.add("Endzeit: " + StringFormatters.FORMATTER_HHmmss.format(new MDate().getTime()));
        list.add("Restarts: " + start.getRestartCounter());
        list.add("Dauer: " + start.getStartTime().diffInSekunden() + " s");
        final long dauer = start.getStartTime().diffInMinuten();
        if (dauer == 0) {
            list.add("Dauer: <1 Min.");
        } else {
            list.add("Dauer: " + start.getStartTime().diffInMinuten() + " Min");
        }
        if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
            if (start.getInputStream() != null) {
                list.add("Bytes gelesen: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumByte(), true));
                list.add("Bandbreite: " + SizeTools.humanReadableBandwidth(start.getInputStream().getSumBandwidth()));
            }
        }
        list.add("URL: " + download.getUrl());
        if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
            list.add(DownloadInfos.ART_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgrammAufruf());
            list.add("Programmaufruf[]: " + download.getProgrammAufrufArray());
        }
        list.add(PLog.LILNE3);
        PLog.userLog(list);

        if (!download.getSource().equals(DownloadInfos.SRC_BUTTON) && !download.isStateStoped()) {
            //war ein Abo und wurde nicht abgebrochen
            MTNotification.addNotification(download, download.isStateError());
        }
    }


    static void finalizeDownload(Download download) {

        final Start start = download.getStart();
        deleteIfEmpty(new File(download.getZielPfadDatei()));
        setFileSize(download);

        fertigmeldung(download);

        if (download.isStateError()) {
            MLProperty.setProperty(download.progressProperty(), DownloadInfos.PROGRESS_NICHT_GESTARTET);
        } else if (!download.isStateStoped()) {
            //dann ist er gelaufen
            start.setTimeLeft(0);
            MLProperty.setProperty(download.progressProperty(), DownloadInfos.PROGRESS_FERTIG);
            download.getDownloadSize().setAktFileSize(-1);

            if (start.getInputStream() != null) {
                download.setBandwidth("Ø " + SizeTools.humanReadableBandwidth(start.getInputStream().getSumBandwidth()));
            }

            final long dauer = start.getStartTime().diffInMinuten();
            if (dauer == 0) {
                download.setRemaining("Dauer: " + start.getStartTime().diffInSekunden() + " s");
            } else {
                download.setRemaining("Dauer: " + start.getStartTime().diffInMinuten() + " Min");
            }
        }

        start.setProcess(null);
        start.setInputStream(null);
        start.setStartTime(null);
    }

    /**
     * tatsächliche Dateigröße eintragen
     *
     * @param datenDownload {@link Download} with the info of the file
     */

    static void setFileSize(Download datenDownload) {
        try {
            final File testFile = new File(datenDownload.getZielPfadDatei());
            if (testFile.exists()) {
                final long length = testFile.length();
                if (length > 0) {
                    datenDownload.getDownloadSize().setSize(length);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(461204780,
                    "Fehler beim Ermitteln der Dateigröße: " + datenDownload.getZielPfadDatei());
        }
    }

    // ********************************************
    // Hier wird dann gestartet
    // Ewige Schleife die die Downloads startet
    // ********************************************
    private class Starten extends Thread {

        private Download datenDownload;
        /**
         * The only {@link java.util.Timer} used for all
         * {@link MLInputStream.BandwidthCalculationTask} calculation tasks.
         */
        private final java.util.Timer bandwidthCalculationTimer;

        public Starten() {
            super();
            setName("DownloadStarter Daemon Thread");
            setDaemon(true);
            bandwidthCalculationTimer = new java.util.Timer("BandwidthCalculationTimer");
        }

        @Override
        public synchronized void run() {
            while (!isInterrupted()) {
                try {
                    while ((datenDownload = getNextStart()) != null) {
                        startStarten(datenDownload);
                        // alle 5 Sekunden einen Download starten
                        sleep(5 * 1000);
                    }
                    progData.downloadListButton.buttonStartsPutzen(); // Button Starts aus der Liste
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
            if (pause) {
                // beim Löschen der Downloads, kann das Starten etwas "pausiert" werden
                // damit ein zu Löschender Download nicht noch schnell gestartet wird
                sleep(5 * 1000);
                pause = false;
            }

            Download download = progData.downloadList.getNextStart();
            if (download == null) {
                // dann versuchen einen Fehlerhaften nochmal zu starten
                download = progData.downloadList.getRestartDownload();
                if (download != null) {
                    reStartmeldung(download);
                }
            }
            return download;
        }

        /**
         * This will start the download process.
         *
         * @param datenDownload The {@link Download} info object for download.
         */
        private void startStarten(Download datenDownload) {
            datenDownload.getStart().downloadStarten();
            Thread downloadThread;

            switch (datenDownload.getArt()) {
                case DownloadInfos.ART_PROGRAMM:
                    downloadThread = new ExternalProgramDownload(progData, datenDownload);
                    downloadThread.start();
                    break;
                case DownloadInfos.ART_DOWNLOAD:
                default:
                    downloadThread = new DirectHttpDownload(progData, datenDownload, bandwidthCalculationTimer);
                    downloadThread.start();
                    break;
            }
        }
    }
}
