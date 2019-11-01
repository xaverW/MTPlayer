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


package de.mtplayer.mtp.controller.data.download;

import de.mtplayer.mtp.gui.dialog.DeleteFilmFileDialogController;
import de.mtplayer.mtp.gui.tools.MTInfoFile;
import de.mtplayer.mtp.gui.tools.MTSubtitle;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.File;
import java.nio.file.Path;

public class DownloadFactory {

    private DownloadFactory() {
    }

    public static void deleteFilmFile(Download download) {
        // Download nur löschen wenn er nicht läuft

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
            new DeleteFilmFileDialogController(downloadPath, filmFile, infoFile, subtitleFile, subtitleFileSrt);


        } catch (Exception ex) {
            PAlert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 +
                    download.getDestPathFile());
            PLog.errorLog(915236547, "Fehler beim löschen: " + download.getDestPathFile());
        }
    }

}
