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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadContinueDialogController;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class CheckDownloadFileExists {
    public CheckDownloadFileExists() {
    }

    boolean checkIfContinue(ProgData progData, DownloadData download, boolean httpDownload) {
        if (!httpDownload && download.getProgramDownloadmanager()) {
            // da kümmert sich ein anderes Programm darum
            return false;
        }

        if (!download.getDownloadStartDto().getFile().exists()) {
            // dann ist alles OK
            return true;
        }

        // dann nachfragen was passieren soll
        AtomicBoolean dialogBreakIsVis = new AtomicBoolean(true);
        AtomicBoolean retBreak = new AtomicBoolean(true);
        Platform.runLater(() -> {
            retBreak.set(checkContinue(progData, download, httpDownload));
            dialogBreakIsVis.set(false);
        });
        while (dialogBreakIsVis.get()) {
            try {
                wait(200);
            } catch (final Exception ignored) {

            }
        }
        return retBreak.get();
    }

    private boolean checkContinue(ProgData progData, DownloadData download, boolean httpDownload) {
        if (!download.getDownloadStartDto().getFile().exists()) {
            // dann passts
            return true;
        }

        boolean ccontinue = true;
        ProgConfigAskBeforeDelete.ContinueDownload result;
        boolean isNewName = false;

        if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == ProgConfigAskBeforeDelete.DOWNLOAD_RESTART__CONTINUE) {
            //weiterführen
            result = ProgConfigAskBeforeDelete.ContinueDownload.CONTINUE;

        } else if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == ProgConfigAskBeforeDelete.DOWNLOAD_RESTART__RESTART) {
            //neu starten
            result = ProgConfigAskBeforeDelete.ContinueDownload.RESTART;

        } else {
            //vorher fragen
            DownloadContinueDialogController downloadContinueDialogController =
                    new DownloadContinueDialogController(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE,
                            progData, download, httpDownload);
            result = downloadContinueDialogController.getResult();
        }
        if (!httpDownload && result == ProgConfigAskBeforeDelete.ContinueDownload.CONTINUE) {
            // geht dann nicht
            result = ProgConfigAskBeforeDelete.ContinueDownload.RESTART;
        }

        switch (result) {
            case CANCEL:
                // dann wars das
                download.stopDownload();
                ccontinue = false;
                break;

            case CONTINUE:
                download.getDownloadStartDto().setDownloaded(download.getDownloadStartDto().getFile().length());
                break;

            case RESTART:
                if (!isNewName) {
                    // dann mit gleichem Namen und Datei vorher löschen
                    try {
                        Files.deleteIfExists(download.getDownloadStartDto().getFile().toPath());
                        download.getDownloadStartDto().setFile(new File(download.getDestPathFile()));
                    } catch (final Exception ex) {
                        // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                        PLog.errorLog(945757586, ex,
                                "file exists: " + download.getDestPathFile());
                    }
                } else {
                    if (!httpDownload) {
                        // wenn Name geändert den Programmaufruf nochmal mit dem geänderten Dateinamen bauen
                        download.makeProgParameter();
                    }
                    try {
                        Files.createDirectories(Paths.get(download.getDestPath()));
                    } catch (final IOException ignored) {
                    }
                    download.getDownloadStartDto().setFile(new File(download.getDestPathFile()));
                }
                break;
        }

        return ccontinue;
    }
}
