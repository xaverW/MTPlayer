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
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.file.Files;

/**
 * Download files via an external program.
 */
public class DownloadExternal extends Thread {

    private final ProgData progData;
    private final DownloadData download;
    private final StringProperty errMsg = new SimpleStringProperty();

    private final int stat_start = 0;
    private final int stat_running = 1;
    private final int stat_restart = 3;
    private final int stat_checking = 4;

    // ab hier ist schluss
    private final int stat_finished_ok = 10;
    private final int stat_finished_error = 11;
    private final int stat_finished_abort = 12;
    private final int stat_end = 99;

    public DownloadExternal(ProgData progData, DownloadData download) {
        super();
        setName("EXTERNAL PROGRAM DL THREAD: " + download.getTitle());

        this.progData = progData;
        this.download = download;
        this.download.setStateStartedRun();
    }

    @Override
    public synchronized void run() {
        LogMsgFactory.startMsg(download);
        StartDownloadFactory.makeDirAndLoadInfoSubtitle(download);
        runWhile();
        StartDownloadFactory.finalizeDownload(download);
    }

    private void runWhile() {
        int stat = stat_start;
        if (!new CheckDownloadFileExists().checkIfContinue(progData, download, false)) {
            // dann abbrechen, Downloadmanager liefert immer TRUE!
            return;
        }

        try {
            while (stat < stat_end) {
                stat = downloadLoop(stat);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(395623710, ex);
            download.setStateError(ex.getLocalizedMessage());
        }
    }

    private int downloadLoop(int stat) {
        switch (stat) {
            case stat_start:
                // versuch das Programm zu Starten
                stat = startDownload();
                break;

            case stat_running:
                // hier läuft der Download bis zum Abbruch oder Ende
                stat = runDownload();
                break;

            case stat_restart:
                stat = restartDownload();
                break;

            case stat_checking:
                stat = checkDownload();
                break;

            case stat_finished_error:
                download.setStateError(errMsg.getValueSafe());
                stat = stat_end;
                break;

            case stat_finished_ok:
                download.setStateFinished();
                stat = stat_end;
                break;

            case stat_finished_abort:
                stat = stat_end;
                break;
        }

        return stat;
    }

    private int startDownload() {
        // versuch das Programm zu starten
        // die Reihenfolge: startCounter - startmeldung ist wichtig!
        int retStat;
        download.getDownloadStartDto().addStartCounter();
        final RuntimeExecDownload runtimeExecDownload = new RuntimeExecDownload(download);
        download.getDownloadStartDto().setProcess(runtimeExecDownload.exec(true /* log */));
        if (download.getDownloadStartDto().getProcess() != null) {

            if (download.isProgramDownloadmanager()) {
                // Downloadmanager, dann wars das
                if (download.isAbo()) {
                    progData.historyListAbos.addHistoryDataToHistory(download.getTheme(), download.getTitle(), download.getHistoryUrl());
                }
                retStat = stat_finished_ok;

            } else {
                retStat = stat_running;
            }

        } else {
            retStat = stat_restart;
        }
        return retStat;
    }

    private int runDownload() {
        // hier läuft der Download bis zum Abbruch oder Ende
        int retStatus = stat_running;
        try {
            if (download.isStateStopped()) {
                // abbrechen
                retStatus = stat_finished_abort;
                if (download.getDownloadStartDto().getProcess() != null) {
                    download.getDownloadStartDto().getProcess().destroy();
                }
                download.stopDownload(); // nochmal da RUNTIME_EXEC ja weiter läuft

            } else {
                Process process = download.getDownloadStartDto().getProcess();
                final int exitV = process.exitValue(); //liefert ex wenn noch nicht fertig
                if (exitV != 0) {
                    retStatus = stat_restart;
                } else {
                    retStatus = stat_checking;
                }
            }
        } catch (final Exception ex) {
            try {
                this.wait(2000);
            } catch (final InterruptedException ignored) {
            }
        }
        return retStatus;
    }

    private int restartDownload() {
        int retStatus = stat_restart;
        // erst mal die alte Datei löschen
        try {
            Files.deleteIfExists(download.getFile().toPath());
        } catch (final Exception ex) {
            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
            P2Log.errorLog(989895674, ex,
                    "file exists: " + download.getDestPathFile());
        }

        // counter prüfen und bei einem Maxwert checkIfCancelDownload, sonst endlos
        if (download.getDownloadStartDto().getStartCounter() < StartDownloadFactory.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART) {
            // dann nochmal von vorne
            retStatus = stat_start;
        } else {
            // dann wars das
            errMsg.setValue("Der Download mit dem Programm: [" + download.getProgramName() + "] hat nicht geklappt.");
            retStatus = stat_finished_error;
        }
        return retStatus;
    }

    private int checkDownload() {
        if (download.getSource().equals(DownloadConstants.SRC_BUTTON) || download.isProgramDownloadmanager()) {
            // für die direkten Starts mit dem Button und die remote downloads wars das dann
            return stat_finished_ok;
        }

        // dann noch die tatsächliche Größe setzen
        if (download.getFile().exists()) {
            download.getDownloadSize().setActuallySize(download.getFile().length());
        } else {
            download.getDownloadSize().setActuallySize(0);
        }

        if (StartDownloadFactory.checkDownloadWasOK(progData, download, errMsg)) {
            // fertig und OK
            return stat_finished_ok;

        } else {
            // fertig und fehlerhaft
            return stat_finished_error;
        }
    }
}
