/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.controller.tools.MTInfoFile;
import de.p2tools.mtplayer.controller.tools.MTSubtitle;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadOnlyStopDialogController;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadStopDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class DownloadFactoryDelDownloadFiles {

    private DownloadFactoryDelDownloadFiles() {
    }

    public static void deleteFilesOfDownload(DownloadData download) {
        // Download nur löschen, wenn er nicht läuft
        if (download == null) {
            return;
        }

        if (download.isStateStartedRun()) {
            P2Alert.showErrorAlert("Film löschen", "Download läuft noch", "Download erst stoppen!");
            return;
        }

        ObservableList<File> fileList = getDownloadFileList(download);
        ObservableList<DownloadData> downloadList = FXCollections.observableArrayList(download);
//        if (fileList.isEmpty()) {
//            PAlert.showErrorAlert("Film löschen", "Es gibt keine Datei", "Der Download hat noch keine Filmdatei.");
//            return;
//        }


        if (ProgConfig.DOWNLOAD_STOP.getValue() == ProgConfigAskBeforeDelete.DOWNLOAD_STOP__DELETE_FILE) {
            // dann soll immer sofort gelöscht werden
            deleteDownloadFiles(download, true);

        } else {
            // erst mal fragen
            DownloadStopDialogController downloadStopDialogController =
                    new DownloadStopDialogController(downloadList, fileList,
                            DownloadStopDialogController.DOWN_ONLY_DEL);
            if (downloadStopDialogController.getState() == P2DialogExtra.STATE.STATE_DOWN_AND_FILE) {
                // dann muss er hier auch gleich gelöscht werden
                deleteDownloadFiles(download, true);
            }
        }
    }

    public static boolean stopDownloadAndDeleteFile(List<DownloadData> downloads, boolean deleteDownload) {
        boolean delDownload;
        ObservableList<File> fileList;

        ObservableList<DownloadData> foundDownloadList = FXCollections.observableArrayList();
        for (DownloadData download : downloads) {
            // DELETE: dann können alle Downloads gelöscht werden, ABBRECHEN: dann nur gestartet
            if (deleteDownload ||
                    download.isStateStartedWaiting() || download.isStateStartedRun() || download.isStateError()) {
                // nur dann läuft er
                foundDownloadList.add(download);
            }
        }

        if (foundDownloadList.isEmpty()) {
            // gibt nix zu tun
            return false;
        }

        fileList = getDownloadFileList(foundDownloadList);
        if (fileList.isEmpty()) {
            // dann wird nur nach dem Löschen des Downloads gefragt
            delDownload = askForDownload(foundDownloadList, deleteDownload);
        } else {
            // dann auch nach dem Löschen der Dateien fragen
            delDownload = askForDownloadAndFile(foundDownloadList, fileList, deleteDownload);
        }
        return delDownload;
    }

    private static boolean askForDownload(ObservableList<DownloadData> foundDownloadList, boolean delete) {
        // gibt noch keine Dateien
        boolean delDownload;
        if (ProgConfig.DOWNLOAD_ONLY_STOP.getValue() == ProgConfigAskBeforeDelete.DOWNLOAD_ONLY_STOP__DELETE) {
            // DL löschen, Dateien nicht
            P2Log.sysLog("Stop Download: DL löschen");
            foundDownloadList.forEach(downloadData -> downloadData.stopDownload(false));
            delDownload = true;

        } else {
            // dann erstmal fragen
            P2Log.sysLog("Stop Download: Erst mal fragen");
            DownloadOnlyStopDialogController downloadStopDialogController =
                    new DownloadOnlyStopDialogController(foundDownloadList, delete);

            if (downloadStopDialogController.getState() == P2DialogExtra.STATE.STATE_OK) {
                // dann soll DL gelöscht werden
                P2Log.sysLog("Stop Download: DL löschen");
                foundDownloadList.forEach(downloadData -> downloadData.stopDownload(false));
                delDownload = true;

            } else {
                //dann soll nix gemacht werden
                P2Log.sysLog("Stop Download: Abbruch");
                delDownload = false;
            }
        }
        return delDownload;
    }

    private static boolean askForDownloadAndFile(ObservableList<DownloadData> foundDownloadList,
                                                 ObservableList<File> fileList, boolean deleteDownload) {
        // dann sind schon Dateien da, nach dem Löschen fragen
        boolean delDownload;
        try {
            switch (ProgConfig.DOWNLOAD_STOP.getValue()) {
                case ProgConfigAskBeforeDelete.DOWNLOAD_STOP__DO_NOT_DELETE:
                    // DL löschen, Dateien nicht
                    P2Log.sysLog("Stop Download: DL löschen, Dateien nicht");
                    foundDownloadList.forEach(downloadData -> downloadData.stopDownload(false));
                    delDownload = true;
                    break;

                case ProgConfigAskBeforeDelete.DOWNLOAD_STOP__DELETE_FILE:
                    // DL und Dateien löschen
                    P2Log.sysLog("Stop Download: DL und Dateien löschen");
                    foundDownloadList.forEach(downloadData -> {
                        if (downloadData.isStateStartedRun() || downloadData.isStateStartedWaiting()) {
                            // dann wird er gestoppt und danach gelöscht, klappt aber nur bei Downloads die schon gestartet sind
                            downloadData.stopDownload(true);

                        } else {
                            // dann muss er hier auch gleich gelöscht werden
                            deleteFilesOfDownload(downloadData);
                        }
                    });
                    delDownload = true;
                    break;

                default:
                    // dann erstmal fragen
                    P2Log.sysLog("Stop Download: Erst mal fragen");
                    DownloadStopDialogController downloadStopDialogController =
                            new DownloadStopDialogController(foundDownloadList, fileList,
                                    deleteDownload ? DownloadStopDialogController.DOWN_STOP_DEL : DownloadStopDialogController.DOWN_ONLY_STOP);

                    if (downloadStopDialogController.getState() == P2DialogExtra.STATE.STATE_DOWN_AND_FILE) {
                        // dann soll DL und Datei gelöscht werden
                        P2Log.sysLog("Stop Download: DL und Dateien löschen");

                        foundDownloadList.forEach(downloadData -> {
                            if (downloadData.isStateStartedRun() || downloadData.isStateStartedWaiting()) {
                                // dann wird er gestoppt und danach gelöscht, klappt aber nur bei Downloads die schon gestartet sind
                                downloadData.stopDownload(true);

                            } else {
                                // dann muss er hier auch gleich gelöscht werden
                                deleteDownloadFiles(downloadData, true);
                            }
                        });
                        delDownload = true;

                    } else if (downloadStopDialogController.getState() == P2DialogExtra.STATE.STATE_ONLY_DOWNLOAD) {
                        // dann soll nur der DL gelöscht werden
                        P2Log.sysLog("Stop Download: Nur DL löschen");
                        foundDownloadList.forEach(downloadData -> downloadData.stopDownload(false));
                        delDownload = true;

                    } else {
                        //dann soll nix gemacht werden
                        P2Log.sysLog("Stop Download: Abbruch");
                        delDownload = false;
                    }
            }
        } catch (Exception ex) {
            P2Alert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen");
            P2Log.errorLog(503231450, "Fehler beim löschen");
            delDownload = false;
        }
        return delDownload;
    }

    private static ObservableList<File> getDownloadFileList(List<DownloadData> downList) {
        ObservableList<File> delFileList = FXCollections.observableArrayList();
        downList.forEach(downloadData -> {
            delFileList.addAll(getDownloadFileList(downloadData));
        });
        return delFileList;
    }

    private static ObservableList<File> getDownloadFileList(DownloadData downloadData) {
        ObservableList<File> delFileList = FXCollections.observableArrayList();
        // Film
        File file = downloadData.getFile();
        if (file.exists()) {
            delFileList.add(file);
        }
        // Infofile
        if (downloadData.isInfoFile()) {
            Path infoPath = MTInfoFile.getInfoFilePath(downloadData);
            if (infoPath != null) {
                file = infoPath.toFile();
                if (file.exists()) {
                    delFileList.add(file);
                }
            }
        }
        // Untertitel
        if (downloadData.isSubtitle()) {
            Path subtitlePath = MTSubtitle.getSubtitlePath(downloadData);
            if (subtitlePath != null) {
                file = subtitlePath.toFile();
                if (file.exists()) {
                    delFileList.add(file);
                }
            }
        }
        // Subtitel
        if (downloadData.isSubtitle()) {
            Path subtitlePathSrt = MTSubtitle.getSrtPath(downloadData);
            if (subtitlePathSrt != null) {
                file = subtitlePathSrt.toFile();
                if (file.exists()) {
                    delFileList.add(file);
                }
            }
        }
        return delFileList;
    }

    public static void deleteDownloadFiles(DownloadData downloadData, boolean showErrorDialog) {
        List<File> fileList = getDownloadFileList(downloadData);
        if (fileList.isEmpty()) {
            return;
        }

        String delFile = "";
        try {
            for (File f : fileList) {
                delFile = f.getAbsolutePath();
                P2Log.sysLog(new String[]{"Datei löschen: ", f.getAbsolutePath()});
                if (!f.delete()) {
                    throw new Exception();
                }
            }
        } catch (Exception ex) {
            final String df = delFile;
            if (showErrorDialog) {
                Platform.runLater(() -> {
                    P2Alert.showErrorAlert("Datei löschen",
                            "Konnte die Datei nicht löschen!",
                            "Fehler beim Löschen von:" + P2LibConst.LINE_SEPARATORx2 + df);
                    P2Log.errorLog(989754125, "Fehler beim löschen: " + df);
                });
            }
        }
    }
}
