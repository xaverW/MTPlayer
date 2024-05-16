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


package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.dialog.AutomodeContinueDialogController;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class StarterThread extends Thread {
    // ********************************************
    // Hier wird dann gestartet
    // ewige Schleife, die die Downloads startet
    // ********************************************
    private DownloadData download;
    private final java.util.Timer bandwidthCalculationTimer;
    private ProgData progData;
    private final BooleanProperty paused;
    private final BooleanProperty searchFilms;
    private final BooleanProperty checkQuitAfterDownload = new SimpleBooleanProperty(false); // Prüfen, ob autoMode aktiv ist


    public StarterThread(ProgData progData,
                         BooleanProperty paused, BooleanProperty searchFilms) {
        super();
        this.progData = progData;
        this.paused = paused;
        this.searchFilms = searchFilms;

        setName("DownloadStarter Daemon Thread");
        setDaemon(true);
        bandwidthCalculationTimer = new java.util.Timer("BandwidthCalculationTimer");
    }

    @Override
    public synchronized void run() {
        while (!isInterrupted()) {
            try {
                if (searchFilms.getValue()) {
                    // vorher und während des Suchens der Filmliste machmer nix
                    Thread.sleep(5_000);
                    continue;
                }

                while ((download = getNextStart()) != null) {
                    if (searchFilms.getValue()) {
                        break;
                    }
                    startDownload(download);
                    // alle 5 Sekunden einen Download starten
                    Thread.sleep(5_000);
                }

                if (searchFilms.getValue()) {
                    // wenn nochmal die Filmliste geholt wird, dann gibts auf jeden Fall noch eine Runde
                    continue;
                }

                if (ProgData.autoMode &&
                        ProgData.downloadSearchDone &&
                        !checkQuitAfterDownload.getValue()) {
                    // dann haben wir den "Automodus"
                    // Downloads wurden gesucht, gestartet und jetzt das Beenden prüfen
                    checkQuitAfterDownload.setValue(true);
                    quitProgramAfterDownload();
                }

                Thread.sleep(3_000);
            } catch (final Exception ex) {
                P2Log.errorLog(613822015, ex);
            }
        }
    }

    private void quitProgramAfterDownload() {
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
                ProgQuit.quit(true);
                //dann wurde das "Beenden" abgebrochen
                ProgData.autoMode = false;
            }
        });
    }

    private synchronized DownloadData getNextStart() throws InterruptedException {
        // ersten passenden Download der Liste zurückgeben oder null
        if (paused.getValue()) {
            //beim Löschen der Downloads kann das Starten etwas "pausiert" werden
            //damit ein zu löschender Download nicht noch schnell gestartet wird
            sleep(5_000);
            paused.setValue(false);
        }
        return progData.downloadList.getNextStart();
    }

    /**
     * This will start the download process.
     *
     * @param download The {@link DownloadData} info object for download.
     */
    public void startDownload(DownloadData download) {
        download.getDownloadStartDto().startDownload();
        Thread downloadThread;

        switch (download.getType()) {
            case DownloadConstants.TYPE_PROGRAM:
                downloadThread = new DownloadExternal(progData, download);
                downloadThread.start();
                break;
            case DownloadConstants.TYPE_DOWNLOAD:
            default:
                downloadThread = new DownloadDirectHttp(progData, download, bandwidthCalculationTimer);
                downloadThread.start();
                break;
        }
    }
}
