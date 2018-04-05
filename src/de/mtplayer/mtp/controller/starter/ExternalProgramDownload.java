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

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.gui.dialog.DownloadContinueDialogController;
import de.mtplayer.mtp.gui.dialog.DownloadErrorDialogController;
import de.mtplayer.mtp.gui.tools.MTInfoFile;
import de.mtplayer.mtp.gui.tools.MTSubtitle;
import de.p2tools.p2Lib.tools.Log;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static de.mtplayer.mtp.controller.starter.StarterClass.*;

/**
 * Download files via an external program.
 */
public class ExternalProgramDownload extends Thread {

    private final Daten daten;
    private final Download download;
    private File file;
    private String exMessage = "";
    private boolean retAbbrechen;
    private boolean dialogAbbrechenIsVis;

    private final int stat_start = 0;
    private final int stat_laufen = 1;
    private final int stat_restart = 3;
    private final int stat_pruefen = 4;

    // ab hier ist schluss
    private final int stat_fertig_ok = 10;
    private final int stat_fertig_fehler = 11;
    private final int stat_fertig_abbruch = 12;
    private final int stat_ende = 99;

    private long filesize = -1;

    public ExternalProgramDownload(Daten daten, Download d) {
        super();
        setName("EXTERNAL PROGRAM DL THREAD: " + d.getTitel());

        this.daten = daten;
        download = d;
        download.setStateStartedRun();
        file = new File(download.getZielPfadDatei());
        try {
            if (download.isInfodatei()) {
                MTInfoFile.writeInfoFile(download);
            }
            if (download.isSubtitle()) {
                new MTSubtitle().writeSubtitle(download);
            }

            Files.createDirectories(Paths.get(download.getZielPfad()));
        } catch (final IOException ignored) {
        } catch (final Exception ex) {
            Log.errorLog(469365281, ex);
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
            while (stat < stat_ende) {
                stat = downloadLoop(stat);
            }
        } catch (final Exception ex) {
            download.setStateError();
            exMessage = ex.getLocalizedMessage();
            Log.errorLog(395623710, ex);
            if (download.getStart().getRestartCounter() == 0) {
                // nur beim ersten Mal melden -> nervt sonst
                Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
            }
        }
        finalizeDownload(download);
    }

    private int downloadLoop(int stat) {
        switch (stat) {
            case stat_start:
                // versuch das Programm zu Starten
                stat = startDownload();
                break;

            case stat_laufen:
                // hier läuft der Download bis zum Abbruch oder Ende
                stat = runDownload();
                break;

            case stat_restart:
                stat = restartDownload();
                break;

            case stat_pruefen:
                stat = checkDownload();
                break;

            case stat_fertig_fehler:
                download.setStateError();
                stat = stat_ende;
                break;

            case stat_fertig_ok:
                download.setStateFinished();
                stat = stat_ende;
                break;

            case stat_fertig_abbruch:
                stat = stat_ende;
                break;
        }

        return stat;
    }

    private int startDownload() {
        // versuch das Programm zu Starten
        // die Reihenfolge: startCounter - startmeldung ist wichtig!

        int retStat;
        download.getStart().setStartCounter(download.getStart().getStartCounter() + 1);
        startmeldung(download);
        final RuntimeExec runtimeExec = new RuntimeExec(download);
        download.getStart().setProcess(runtimeExec.exec(true /* log */));
        if (download.getStart().getProcess() != null) {
            if (download.isProgrammDownloadmanager()) {
                retStat = stat_fertig_ok;
            } else {
                retStat = stat_laufen;
            }
        } else {
            retStat = stat_restart;
        }
        return retStat;
    }

    private int runDownload() {
        // hier läuft der Download bis zum Abbruch oder Ende

        int retStatus = stat_laufen;
        try {
            if (download.isStateStoped()) {
                // abbrechen
                retStatus = stat_fertig_abbruch;
                if (download.getStart().getProcess() != null) {
                    download.getStart().getProcess().destroy();
                }
            } else {
                final int exitV = download.getStart().getProcess().exitValue(); //liefert ex wenn noch nicht fertig
                if (exitV != 0) {
                    retStatus = stat_restart;
                } else {
                    retStatus = stat_pruefen;
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
        if (!download.isProgrammRestart()) {
            // dann wars das
            retStatus = stat_fertig_fehler;
        } else if (filesize == -1) {
            // noch nichts geladen
            deleteIfEmpty(file);
            if (file.exists()) {
                // dann bestehende Datei weitermachen
                filesize = file.length();
                retStatus = stat_start;
            } else {
                // counter prüfen und bei einem Maxwert checkIfCancelDownload, sonst endlos
                if (download.getStart().getStartCounter() < DownloadInfos.STARTCOUNTER_MAX) {
                    // dann nochmal von vorne
                    retStatus = stat_start;
                } else {
                    // dann wars das
                    retStatus = stat_fertig_fehler;
                }
            }
        } else {
            // jetzt muss das File wachsen, sonst kein Restart
            if (!file.exists()) {
                // dann wars das
                retStatus = stat_fertig_fehler;
            } else if (file.length() > filesize) {
                // nur weitermachen wenn die Datei tasächlich wächst
                filesize = file.length();
                retStatus = stat_start;
            } else {
                // dann wars das
                retStatus = stat_fertig_fehler;
            }
        }
        return retStatus;
    }

    private int checkDownload() {
        int retStatus = stat_pruefen;

        if (download.getSource().equals(DownloadInfos.SRC_BUTTON) || download.isProgrammDownloadmanager()) {
            // für die direkten Starts mit dem Button und die remote downloads wars das dann
            retStatus = stat_fertig_ok;
        } else if (pruefen(daten, download)) {
            // fertig und OK
            retStatus = stat_fertig_ok;
        } else {
            // fertig und fehlerhaft
            retStatus = stat_fertig_fehler;
        }
        return retStatus;
    }

    private boolean checkIfCancelDownload() {
        if (download.isProgrammDownloadmanager()) {
            // da kümmert sich ein anderes Programm darum
            return false;
        }
        if (!file.exists()) {
            // dann ist alles OK
            return false;
        }

        dialogAbbrechenIsVis = true;
        retAbbrechen = true;
        Platform.runLater(() -> {
            retAbbrechen = checkCancel();
            dialogAbbrechenIsVis = false;
        });
        while (dialogAbbrechenIsVis) {
            try {
                wait(100);
            } catch (final Exception ignored) {
            }
        }
        return retAbbrechen;
    }

    private boolean checkCancel() {
        boolean result = false;
        if (file.exists()) {
            final DownloadContinueDialogController downloadContinueDialogController =
                    new DownloadContinueDialogController(null, download, false /* weiterführen */);

            switch (downloadContinueDialogController.getResult()) {
                case CANCEL_DOWNLOAD:
                    // dann wars das
                    download.stopDownload();
                    result = true;
                    break;

                case RESTART_DOWNLOAD:
                    if (!downloadContinueDialogController.isNewName()) {
                        // alte Datei vorher löschen
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (final Exception ex) {
                            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                            Log.errorLog(945120398, ex, "file exists: " + download.getZielPfadDatei());
                        }
                    } else {
                        // wenn Name geändert den Programmaufruf nochmal mit dem geänderten Dateinamen bauen
                        download.aufrufBauen();
                        try {
                            Files.createDirectories(Paths.get(download.getZielPfad()));
                        } catch (final IOException ignored) {
                        }
                    }
                    file = new File(download.getZielPfadDatei());
                    break;

            }
        }
        return result;
    }
}
