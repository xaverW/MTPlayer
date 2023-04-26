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
import de.p2tools.mtplayer.controller.starter.DownloadState;
import de.p2tools.mtplayer.gui.dialog.DeleteFilmFileDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadStopDialogController;
import de.p2tools.mtplayer.gui.tools.MTInfoFile;
import de.p2tools.mtplayer.gui.tools.MTSubtitle;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadFactory {

    private DownloadFactory() {
    }

    public static void deleteFilmFile(ArrayList<DownloadData> downloadList) {
        // Download nur löschen, wenn er nicht läuft
        if (downloadList == null || downloadList.isEmpty() ||
                ProgConfig.DOWNLOAD_STOP.getValue() == DownloadState.DOWNLOAD_STOP__NOTHING) {
            // dann ist die Liste leer oder er will sowieso nicht löschen, nix wie weiter
            return;
        }

        ArrayList<DownloadData> delDownloadList = new ArrayList<>();
        delDownloadList.addAll(downloadList.stream().filter(downloadData -> !downloadData.isStarted()).collect(Collectors.toList()));
        if (delDownloadList.isEmpty()) {
            // gibt nix zum Löschen
            return;
        }

        try {
            ObservableList<File> delFileList = FXCollections.observableArrayList();
            delDownloadList.stream().forEach(downloadData -> {
                // Film
                File file = new File(downloadData.getDestPathFile());
                if (file.exists()) {
                    delFileList.add(file);
                }

                // Infofile
                if (downloadData.getInfoFile()) {
                    Path infoPath = MTInfoFile.getInfoFilePath(downloadData);
                    if (infoPath != null) {
                        file = infoPath.toFile();
                        if (file.exists()) {
                            delFileList.add(file);
                        }
                    }
                }

                // Unteritel
                if (downloadData.isSubtitle()) {
                    Path subtitlePath = MTSubtitle.getSubtitlePath(downloadData);
                    if (subtitlePath != null) {
                        file = subtitlePath.toFile();
                        if (file.exists()) {
                            delFileList.add(file);
                        }
                    }
                }
                if (downloadData.isSubtitle()) {
                    Path subtitlePathSrt = MTSubtitle.getSrtPath(downloadData);
                    if (subtitlePathSrt != null) {
                        file = subtitlePathSrt.toFile();
                        if (file.exists()) {
                            delFileList.add(file);
                        }
                    }
                }
            });

            if (delFileList.isEmpty()) {
                // dann gibts eh nix zu tun
                return;
            }

            if (ProgConfig.DOWNLOAD_STOP.getValue() == DownloadState.DOWNLOAD_STOP__ASK) {
                // dann will er erst gefragt werden
                DownloadStopDialogController downloadStopDialogController = new DownloadStopDialogController(delFileList);
                if (downloadStopDialogController.isDelete()) {
                    // dann soll gelöscht werden
                    deleteFile(delFileList);
                }

            } else if (ProgConfig.DOWNLOAD_STOP.getValue() == DownloadState.DOWNLOAD_STOP__DELETE) {
                // dann will er nicht gefragt werden, wird sofort gelöscht
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                    System.out.println("================>");
                }
                deleteFile(delFileList);
            }
        } catch (Exception ex) {
            PAlert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen");
            PLog.errorLog(503231450, "Fehler beim löschen");
        }
    }

    private static boolean deleteFile(List<File> list) {
        String delFile = "";
        boolean ret = true;
        try {
            for (File f : list) {
                delFile = f.getAbsolutePath();
                PLog.sysLog(new String[]{"Datei löschen: ", f.getAbsolutePath()});
                if (!f.delete()) {
                    throw new Exception();
                }
            }
        } catch (Exception ex) {
            ret = false;
            PAlert.showErrorAlert("Datei löschen",
                    "Konnte die Datei nicht löschen!",
                    "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 + delFile);
            PLog.errorLog(989754125, "Fehler beim löschen: " + delFile);
        }
        return ret;
    }

    public static void deleteFilmFile(DownloadData download) {
        // Download nur löschen, wenn er nicht läuft
        if (download == null) {
            return;
        }

        if (download.isStateStartedRun()) {
            PAlert.showErrorAlert("Film löschen", "Download läuft noch", "Download erst stoppen!");
        }


        try {
            // Film
            File filmFile = new File(download.getDestPathFile());
            if (!filmFile.exists()) {
                PAlert.showErrorAlert("Film löschen", "", "Die Datei existiert nicht!");
                return;
            }

            // Infofile
            File infoFile = null;
            if (download.getInfoFile()) {
                Path infoPath = MTInfoFile.getInfoFilePath(download);
                if (infoPath != null) {
                    infoFile = infoPath.toFile();
                }
            }

            // Unteritel
            File subtitleFile = null;
            if (download.isSubtitle()) {
                Path subtitlePath = MTSubtitle.getSubtitlePath(download);
                if (subtitlePath != null) {
                    subtitleFile = subtitlePath.toFile();
                }
            }
            File subtitleFileSrt = null;
            if (download.isSubtitle()) {
                Path subtitlePathSrt = MTSubtitle.getSrtPath(download);
                if (subtitlePathSrt != null) {
                    subtitleFileSrt = subtitlePathSrt.toFile();
                }
            }

            String downloadPath = download.getDestPath();
            // dann sollen nur die Filmdateien gelöscht werden
            new DeleteFilmFileDialogController(downloadPath, filmFile, infoFile, subtitleFile, subtitleFileSrt);

        } catch (Exception ex) {
            PAlert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 +
                    download.getDestPathFile());
            PLog.errorLog(915236547, "Fehler beim löschen: " + download.getDestPathFile());
        }
    }
}
