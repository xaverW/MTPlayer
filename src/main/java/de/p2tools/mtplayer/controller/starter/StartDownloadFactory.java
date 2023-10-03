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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadErrorDialogController;
import de.p2tools.mtplayer.gui.tools.MTInfoFile;
import de.p2tools.mtplayer.gui.tools.MTSubtitle;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StartDownloadFactory {
    static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = 3;

    private StartDownloadFactory() {
    }

    static void makeDirAndLoadInfoSubtitle(DownloadData download) {
        try {
            Files.createDirectories(Paths.get(download.getDestPath()));
            if (download.getInfoFile()) {
                MTInfoFile.writeInfoFile(download);
            }
            if (download.isSubtitle()) {
                new MTSubtitle().writeSubtitle(download);
            }
        } catch (final IOException ignored) {
        } catch (final Exception ex) {
            PLog.errorLog(945120690, ex);
        }
    }

    static void finalizeDownload(DownloadData download) {
        final StartDownloadDto startDownloadDto = download.getDownloadStartDto();
        deleteIfEmpty(new File(download.getDestPathFile()));
        setFileSize(download);

        if (download.isStateError()) {
            final String errMsg;
            if (!download.getDownloadStartDto().getErrMsgList().isEmpty()) {
                // wenn download fehlerhaft, dann letzte Meldung im Download eintragen
                errMsg = download.getDownloadStartDto().getErrMsgList()
                        .get(download.getDownloadStartDto().getErrMsgList().size() - 1);
                download.setErrorMessage(errMsg);
            } else {
                errMsg = "Fehlerhafter Download";
            }
            Platform.runLater(() -> new DownloadErrorDialogController(download, errMsg));
        }

        // und die Finished-MSG ausgeben
        LogDownloadFactory.finishedMsg(download);

        if (download.isStateError()) {
            download.setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
        } else if (!download.isStateStopped()) {
            //dann ist er gelaufen
            startDownloadDto.setTimeLeftSeconds(0);
            download.setProgress(DownloadConstants.PROGRESS_FINISHED);
            download.getDownloadSize().setActFileSize(-1);

            if (startDownloadDto.getInputStream() != null) {
                download.setBandwidthEnd(startDownloadDto.getInputStream().getSumBandwidth());
            }

            download.setRemaining(-1 * startDownloadDto.getStartTime().diffInSeconds());
        }

        download.setNo(P2LibConst.NUMBER_NOT_STARTED);
        ProgData.getInstance().downloadGuiController.tableRefresh();
        startDownloadDto.setProcess(null);
        startDownloadDto.setInputStream(null);
        startDownloadDto.setStartTime(null);
    }

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
        } catch (final Exception ex) {
            PLog.errorLog(461204780,
                    "Fehler beim Ermitteln der Dateigröße: " + download.getDestPathFile());
        }
    }

    static boolean checkDownloadWasOK(ProgData progData, DownloadData download, StringProperty errorMsg) {
        // prüfen, ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        boolean ret = false;
        final double progress = download.getProgress();
        if (progress > DownloadConstants.PROGRESS_NOT_STARTED && progress < DownloadConstants.PROGRESS_NEARLY_FINISHED) {
            // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
            String str = "Download fehlgeschlagen: 99,5% wurden nicht erreicht: " + progress + "%, " + download.getDestPathFile();
            errorMsg.setValue(str);
            PLog.errorLog(696510258, str);
            return false;
        }

        final File file = new File(download.getDestPathFile());
        if (!file.exists()) {
            String str = "Download fehlgeschlagen: Datei existiert nicht: " + download.getDestPathFile();
            errorMsg.setValue(str);
            PLog.errorLog(550236231, str);

        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            String str = "Download fehlgeschlagen: Datei zu klein: " + download.getDestPathFile();
            errorMsg.setValue(str);
            PLog.errorLog(795632500, str);

        } else {
            if (download.isAbo()) {
                progData.historyListAbos.addHistoryDataToHistory(download.getTheme(), download.getTitle(), download.getHistoryUrl());
            }
            ret = true;
        }

        return ret;
    }
}
