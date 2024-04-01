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

package de.p2tools.mtplayer.gui.tools;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFieldNames;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MTInfoFile {
    public static void writeInfoFile(DownloadData download) {
        if (download.getDestPath().isEmpty()) {
            // das darf nicht sein!
            // todo
            PAlert.showErrorAlert("Info File anlegen", "Kann die Info-Datei nicht anlegen.");
            return;
        }

        PLog.sysLog(new String[]{"Infofile schreiben nach: ", download.getDestPath()});

        new File(download.getDestPath()).mkdirs();
        final Path path = getInfoFilePath(download);
        if (path == null) {
            return;
        }

        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(path));
             OutputStreamWriter osw = new OutputStreamWriter(dos);
             BufferedWriter br = new BufferedWriter(osw)) {
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_CHANNEL] + ":        " + download.getChannel());
            br.write(P2LibConst.LINE_SEPARATOR);
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_THEME] + ":         " + download.getTheme());
            br.write(P2LibConst.LINE_SEPARATORx2);
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_TITLE] + ":         " + download.getTitle());
            br.write(P2LibConst.LINE_SEPARATORx2);
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_DATE] + ":         " + download.getFilmDate().toString());
            br.write(P2LibConst.LINE_SEPARATOR);
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_TIME] + ":          " + download.getFilmTime());
            br.write(P2LibConst.LINE_SEPARATOR);
            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_DURATION] + ":   " + download.getDurationMinute());
            br.write(P2LibConst.LINE_SEPARATOR);
            br.write(DownloadFieldNames.DOWNLOAD_SIZE + ":    " + download.getDownloadSize());
            br.write(P2LibConst.LINE_SEPARATORx2);

            br.write(FilmDataXml.COLUMN_NAMES[FilmDataXml.FILM_WEBSITE] + P2LibConst.LINE_SEPARATOR);
            br.write(download.getUrlWebsite());
            br.write(P2LibConst.LINE_SEPARATORx2);

            br.write(DownloadFieldNames.DOWNLOAD_URL + P2LibConst.LINE_SEPARATOR);
            br.write(download.getUrl());
            br.write(P2LibConst.LINE_SEPARATORx2);

            int anz = 0;
            for (final String s : download.getDescription().split(" ")) {
                anz += s.length();
                br.write(s + ' ');
                if (anz > 50) {
                    br.write(P2LibConst.LINE_SEPARATOR);
                    anz = 0;
                }
            }

            br.write(P2LibConst.LINE_SEPARATORx2);
            br.flush();
            PLog.sysLog(new String[]{"Infofile", "  geschrieben"});
        } catch (final IOException ex) {
            PLog.errorLog(975410369, download.getDestPathFile());
        }
    }

    private static String getInfoFileStr(DownloadData download) {
        return download.getPathFileNameWithoutSuffix() + ".txt";
    }

    public static Path getInfoFilePath(DownloadData download) {
        Path path;
        try {
            path = Paths.get(getInfoFileStr(download));
        } catch (Exception ex) {
            path = null;
            PLog.errorLog(987451202, "InfofilePath");
        }
        return path;
    }
}
