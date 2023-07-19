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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.dialog.DownloadContinueDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadErrorDialogController;
import de.p2tools.mtplayer.gui.tools.MTInfoFile;
import de.p2tools.mtplayer.gui.tools.MTSubtitle;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static de.p2tools.mtplayer.controller.starter.StarterClass.*;

/**
 * Download files via an external program.
 */
public class ExternalProgramDownload extends Thread {

    private final ProgData progData;
    private final DownloadData download;
    private File file;
    private String exMessage = "";
    private boolean retAbort;
    private boolean dialogAbortIsVis;

    private final int stat_start = 0;
    private final int stat_running = 1;
    private final int stat_restart = 3;
    private final int stat_checking = 4;

    // ab hier ist schluss
    private final int stat_finished_ok = 10;
    private final int stat_finished_error = 11;
    private final int stat_finished_abort = 12;
    private final int stat_end = 99;

    private long fileSize = -1;

    public ExternalProgramDownload(ProgData progData, DownloadData download) {
        super();
        setName("EXTERNAL PROGRAM DL THREAD: " + download.getTitle());

        this.progData = progData;
        this.download = download;
        this.download.setStateStartedRun();
        file = new File(this.download.getDestPathFile());
        try {
            if (this.download.getInfoFile()) {
                MTInfoFile.writeInfoFile(this.download);
            }
            if (this.download.isSubtitle()) {
                new MTSubtitle().writeSubtitle(this.download);
            }

            if (!download.getSetData().isPlay()) {
                // sonst wird ja nur abgespielt
                Files.createDirectories(Paths.get(this.download.getDestPath()));
            }
        } catch (final IOException ignored) {
        } catch (final Exception ex) {
            PLog.errorLog(469365281, ex);
        }
    }

    @Override
    public synchronized void run() {
        int stat = stat_start;

        if (checkIfCancelDownload()) {
            finalizeDownload(download);
            return;
        }

        try {
            while (stat < stat_end) {
                stat = downloadLoop(stat);
            }
        } catch (final Exception ex) {
            exMessage = ex.getLocalizedMessage();
            PLog.errorLog(395623710, ex);
            if (download.getStart().getRestartCounter() == 0) {
                // nur beim ersten Mal melden -> nervt sonst
                Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
            }
            download.setStateError();
            download.setErrorMessage(exMessage);
        }
        finalizeDownload(download);
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
                download.setStateError();
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
        download.getStart().setStartCounter(download.getStart().getStartCounter() + 1);
        startMsg(download);
        final RuntimeExec runtimeExec = new RuntimeExec(download);
        download.getStart().setProcess(runtimeExec.exec(true /* log */));
        if (download.getStart().getProcess() != null) {
            if (download.getProgramDownloadmanager()) {
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
                if (download.getStart().getProcess() != null) {
                    download.getStart().getProcess().destroy();
                }
                download.stopDownload(); // nochmal da RUNTIME_EXEC ja weiter läuft

            } else {
                final int exitV = download.getStart().getProcess().exitValue(); //liefert ex wenn noch nicht fertig
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
        if (!download.getProgramRestart()) {
            // dann wars das
            retStatus = stat_finished_error;
        } else if (fileSize == -1) {
            // noch nichts geladen
            deleteIfEmpty(file);
            if (file.exists()) {
                // dann bestehende Datei weitermachen
                fileSize = file.length();
                retStatus = stat_start;
            } else {
                // counter prüfen und bei einem Maxwert checkIfCancelDownload, sonst endlos
                if (download.getStart().getStartCounter() < DownloadConstants.START_COUNTER_MAX) {
                    // dann nochmal von vorne
                    retStatus = stat_start;
                } else {
                    // dann wars das
                    retStatus = stat_finished_error;
                }
            }
        } else {
            // jetzt muss das File wachsen, sonst kein Restart
            if (!file.exists()) {
                // dann wars das
                retStatus = stat_finished_error;
            } else if (file.length() > fileSize) {
                // nur weitermachen wenn die Datei tasächlich wächst
                fileSize = file.length();
                retStatus = stat_start;
            } else {
                // dann wars das
                retStatus = stat_finished_error;
            }
        }
        return retStatus;
    }

    private int checkDownload() {
        int retStatus = stat_checking;

        if (download.getSource().equals(DownloadConstants.SRC_BUTTON) || download.getProgramDownloadmanager()) {
            // für die direkten Starts mit dem Button und die remote downloads wars das dann
            retStatus = stat_finished_ok;
        } else if (check(progData, download)) {
            // fertig und OK
            retStatus = stat_finished_ok;
        } else {
            // fertig und fehlerhaft
            retStatus = stat_finished_error;
        }
        return retStatus;
    }

    private boolean checkIfCancelDownload() {
        if (download.getProgramDownloadmanager()) {
            // da kümmert sich ein anderes Programm darum
            return false;
        }
        if (!file.exists()) {
            // dann ist alles OK
            return false;
        }

        dialogAbortIsVis = true;
        retAbort = true;
        Platform.runLater(() -> {
            retAbort = checkCancel();
            dialogAbortIsVis = false;
        });
        while (dialogAbortIsVis) {
            try {
                wait(100);
            } catch (final Exception ignored) {
            }
        }
        return retAbort;
    }

    private boolean checkCancel() {
        boolean ret = false;
        if (file.exists()) {

            AskBeforeDeleteState.ContinueDownload result;
            boolean isNewName = false;

            if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == AskBeforeDeleteState.DOWNLOAD_RESTART__CONTINUE) {
                //weiterführen
                result = AskBeforeDeleteState.ContinueDownload.CONTINUE;

            } else if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == AskBeforeDeleteState.DOWNLOAD_RESTART__RESTART) {
                //neu starten
                result = AskBeforeDeleteState.ContinueDownload.RESTART;

            } else {
                //vorher fragen
                DownloadContinueDialogController downloadContinueDialogController =
                        new DownloadContinueDialogController(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE,
                                progData, download, false /* weiterführen */);

                result = downloadContinueDialogController.getResult();
                isNewName = downloadContinueDialogController.isNewName();
            }

            switch (result) {
                case CANCEL:
                    // dann wars das
                    download.stopDownload();
                    ret = true;
                    break;

                case RESTART:
                    if (!isNewName) {
                        // alte Datei vorher löschen
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (final Exception ex) {
                            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                            PLog.errorLog(945120398, ex, "file exists: " + download.getDestPathFile());
                        }
                    } else {
                        // wenn Name geändert den Programmaufruf nochmal mit dem geänderten Dateinamen bauen
                        download.makeProgParameter();
                        try {
                            Files.createDirectories(Paths.get(download.getDestPath()));
                        } catch (final IOException ignored) {
                        }
                    }
                    file = new File(download.getDestPathFile());
                    break;

            }
        }
        return ret;
    }
}
