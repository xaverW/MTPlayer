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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryDelDownloadFiles;
import de.p2tools.mtplayer.controller.data.downloaderror.DownloadErrorData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.mediadb.MediaCollectionData;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.controller.tools.MTInfoFile;
import de.p2tools.mtplayer.controller.tools.MTSubtitle;
import de.p2tools.mtplayer.gui.dialog.DownloadSubtitleDialog;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadErrorDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StartDownloadFactory {
    static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = 3;

    private StartDownloadFactory() {
    }

    public static void downloadSubtitle(boolean audio, FilmDataMTP filmData, boolean subtitel) {
        // Menü
        SetData setData = ProgData.getInstance().setDataList.getSetDataForDownloads("");
        if (setData == null) {
            // braucht's eigentlich nicht, aber DOWNLOAD klappt sonst nicht!!
            new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.SAVE);
            return;
        }

        ArrayList<FilmDataMTP> list = new ArrayList<>();
        list.add(filmData);
        DownloadData downloadData = new DownloadData(audio, list, setData);
        if (subtitel) {
            downloadData.setSubtitle(true);
        } else {
            downloadData.setInfoFile(true);
        }

        final StringProperty pathProp = new SimpleStringProperty(downloadData.getDestPath());
        final StringProperty nameProp = new SimpleStringProperty(downloadData.getFileNameWithoutSuffix());
        final BooleanProperty okProp = new SimpleBooleanProperty(false);
        DownloadSubtitleDialog downloadSubtitleDialog = new DownloadSubtitleDialog(ProgData.getInstance(), subtitel,
                pathProp, nameProp, okProp);
        downloadSubtitleDialog.showDialog();
        if (!okProp.get()) {
            return;
        }

        if (!pathProp.getValueSafe().isEmpty()) {
            downloadData.setPathName(pathProp.getValueSafe(), nameProp.getValueSafe());
            makeDirAndLoadInfoSubtitle(downloadData);
        }
    }

    static void makeDirAndLoadInfoSubtitle(DownloadData download) {
        try {
            Files.createDirectories(Paths.get(download.getDestPath()));
            if (download.isInfoFile()) {
                MTInfoFile.writeInfoFile(download);
            }
            if (download.isSubtitle()) {
                new MTSubtitle().writeSubtitle(download);
            }
        } catch (final IOException ignored) {
        } catch (final Exception ex) {
            P2Log.errorLog(945120690, ex);
        }
    }

    static void finalizeDownload(DownloadData download) {
        final StartDownloadDto startDownloadDto = download.getStartDownloadDto();
        cleanUpDestFile(download);

        if (download.isStateError()) {
            Platform.runLater(() -> {
                ProgData.getInstance().downloadErrorList.add(new DownloadErrorData(download.getTitle(),
                        download.getUrl(),
                        download.getDestPathFile(),
                        download.getStartDownloadDto().getErrorMsg(),
                        download.getStartDownloadDto().getErrorStream()));

                if (!ProgData.autoMode && ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW.getValue()) {
                    // nur wenn gewollt und nicht AutoMode
                    new DownloadErrorDialogController(download);
                }
            });
        }

        // und die Finished-Msg ausgeben
        LogMsgFactory.finishedMsg(download);

        if (download.isStateError()) {
            download.setProgress(DownloadConstants.PROGRESS_NOT_STARTED);

        } else if (!download.isStateStopped()) {
            //dann ist er gelaufen
            startDownloadDto.setTimeLeftSeconds(0);
            download.setProgress(DownloadConstants.PROGRESS_FINISHED);
            download.getDownloadSize().setActuallySize(0);

            if (startDownloadDto.getInputStream() != null) {
                download.setBandwidthEnd(startDownloadDto.getInputStream().getSumBandwidth());
            }

            download.setRemaining(-1 * startDownloadDto.getStartTime().diffInSeconds());
        }

        download.setNo(P2LibConst.NUMBER_NOT_STARTED);
        ProgData.getInstance().downloadGuiController.tableRefresh();
        startDownloadDto.setProcess(null);
        startDownloadDto.setInputStream(null);
//        startDownloadDto.setStartTime(null);

        checkMediaList(download);
    }

    static void checkMediaList(DownloadData download) {
        // wenn der Download in die MediaList geschrieben wurde, aktualisieren
        String path = download.getDestPath();
        MediaCollectionData mediaCollectionData = ProgData.getInstance().mediaCollectionDataList.getMediaCollectionDataIntern(path);
        if (mediaCollectionData != null) {
            // dann gibts den Pfad in der Mediensammlung -> Mediensammlung aktualisieren
            File fileMedia = new File(mediaCollectionData.getPath());
            if (fileMedia.exists()) {
                P2Log.sysLog(new String[]{"Mediensammlung aktualisieren: ", mediaCollectionData.getPath()});
                Platform.runLater(() -> MediaDataWorker.updateCollection(mediaCollectionData));
            }
        }
    }

    static void cleanUpDestFile(DownloadData download) {
        File destFile = download.getFile();
        if (!destFile.exists()) {
            return;
        }

        // die Dateien der gestoppten/gelöschten Downloads evtl. noch löschen
        if (download.isStateStopped() && download.getStartDownloadDto().isDeleteAfterStop()) {
            P2Log.sysLog(new String[]{"Gestoppter Download, auch die Datei löschen: ", destFile.getAbsolutePath()});
            DownloadFactoryDelDownloadFiles.deleteDownloadFiles(download, true);
            return;
        }

        // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
        // die tatsächliche Größe nehmen, beim Abbrechen wird die DownloadSize auf -1 gesetzt
        final long length = download.getFile().length();
        if (length == 0) {
            // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
            P2Log.sysLog(new String[]{"Restart/Aufräumen: leere Datei löschen", destFile.getAbsolutePath()});
            DownloadFactoryDelDownloadFiles.deleteDownloadFiles(download, false);

        } else if (length < ProgConst.MIN_DATEI_GROESSE_FILM) {
            P2Log.sysLog(new String[]{"Restart/Aufräumen: Zu kleine Datei löschen", destFile.getAbsolutePath()});
            DownloadFactoryDelDownloadFiles.deleteDownloadFiles(download, false);
        }
    }

    static boolean checkDownloadWasOK(ProgData progData, DownloadData download, StringProperty errorMsg) {
        // prüfen, ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        // dazu die tatsächliche Dateigröße ermitteln
        if (download.getFile().exists()) {
            download.getDownloadSize().setActuallySize(download.getFile().length());
        } else {
            download.getDownloadSize().setActuallySize(0);
        }

        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            // nur dann stimmt der Progress
            final double progress = download.getProgress();
            if (progress > DownloadConstants.PROGRESS_NOT_STARTED && progress < DownloadConstants.PROGRESS_NEARLY_FINISHED) {
                // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
                String str = "Download fehlgeschlagen, Datei zu klein, nur " + String.format("%.0f", progress) + " % erreicht.\n" +
                        "Soll aus der URL: " + download.getDownloadSize().getTargetSize() + " Byte\n" +
                        "Ist aus der Datei: " + download.getDownloadSize().getActuallySize() + " Byte\n";
                errorMsg.setValue(str);
                P2Log.errorLog(696510258, str);
                return false;
            }
        }

        final File file = new File(download.getDestPathFile());
        if (!file.exists()) {
            String str = "Download fehlgeschlagen: Datei existiert nicht: " + download.getDestPathFile();
            errorMsg.setValue(str);
            P2Log.errorLog(550236231, str);
            return false;

        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            String str = "Download fehlgeschlagen: Datei zu klein: " + download.getDestPathFile();
            errorMsg.setValue(str);
            P2Log.errorLog(795632500, str);
            return false;

        } else {
            if (download.isAbo()) {
                progData.historyListAbos.addHistoryDataToHistory(download.getTheme(), download.getTitle(), download.getHistoryUrl());
            }
            return true;
        }
    }

    public static void canAlreadyStarted(DownloadData downloadData) {
        if (downloadData.isStateStartedRun()) {

            if (downloadData.getDurationMinute() > 0
                    && downloadData.getStartDownloadDto().getTimeLeftSeconds() > 0
                    && downloadData.getDownloadSize().getActuallySize() > 0
                    && downloadData.getDownloadSize().getTargetSize() > 0) {

                // macht nur dann Sinn
                final long alreadyLoadedSeconds = downloadData.getDurationMinute() * 60
                        * downloadData.getDownloadSize().getActuallySize()
                        / downloadData.getDownloadSize().getTargetSize();

                if (alreadyLoadedSeconds >
                        (downloadData.getStartDownloadDto().getTimeLeftSeconds() * 1.1 /* plus 10% zur Sicherheit */)) {
                    downloadData.getStartDownloadDto().setStartViewing(true);
                }
            }
        }
    }
}
