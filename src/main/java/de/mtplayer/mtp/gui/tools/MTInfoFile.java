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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadXml;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MTInfoFile {

    public static void writeInfoFile(Download download) {
        PLog.sysLog(new String[]{"Infofile schreiben nach: ", download.getDestPath()});

        new File(download.getDestPath()).mkdirs();
        // final Path path = Paths.get(download.getFileNameWithoutSuffix() + ".txt");
        final Path path = getInfoFilePath(download);
        if (path == null) {
            return;
        }

        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(path));
             OutputStreamWriter osw = new OutputStreamWriter(dos);
             BufferedWriter br = new BufferedWriter(osw)) {
            if (download.getFilm() != null) {
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_CHANNEL] + ":        " + download.getFilm().arr[FilmXml.FILM_CHANNEL]);
                br.write(P2LibConst.LINE_SEPARATOR);
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_THEME] + ":         " + download.getFilm().arr[FilmXml.FILM_THEME]);
                br.write(P2LibConst.LINE_SEPARATORx2);
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_TITLE] + ":         " + download.getFilm().arr[FilmXml.FILM_TITLE]);
                br.write(P2LibConst.LINE_SEPARATORx2);
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_DATE] + ":         " + download.getFilm().arr[FilmXml.FILM_DATE]);
                br.write(P2LibConst.LINE_SEPARATOR);
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_TIME] + ":          " + download.getFilm().arr[FilmXml.FILM_TIME]);
                br.write(P2LibConst.LINE_SEPARATOR);
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_DURATION] + ":   " + download.getFilm().arr[FilmXml.FILM_DURATION]);
                br.write(P2LibConst.LINE_SEPARATOR);
                br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_SIZE] + ":    " + download.getDownloadSize());
                br.write(P2LibConst.LINE_SEPARATORx2);

                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_WEBSITE] + P2LibConst.LINE_SEPARATOR);
                br.write(download.getFilm().arr[FilmXml.FILM_WEBSITE]);
                br.write(P2LibConst.LINE_SEPARATORx2);
            }

            br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_URL] + P2LibConst.LINE_SEPARATOR);
            br.write(download.getUrl());
            br.write(P2LibConst.LINE_SEPARATORx2);
            if (!download.getUrlRtmp().isEmpty()
                    && !download.getUrlRtmp().equals(download.getUrl())) {
                br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_URL_RTMP] + P2LibConst.LINE_SEPARATOR);
                br.write(download.getUrlRtmp());
                br.write(P2LibConst.LINE_SEPARATORx2);
            }

            if (download.getFilm() != null) {
                int anz = 0;
                for (final String s : download.getFilm().getDescription().split(" ")) {
                    anz += s.length();
                    br.write(s + ' ');
                    if (anz > 50) {
                        br.write(P2LibConst.LINE_SEPARATOR);
                        anz = 0;
                    }
                }
            }
            br.write(P2LibConst.LINE_SEPARATORx2);
            br.flush();
            PLog.sysLog(new String[]{"Infofile", "  geschrieben"});
        } catch (final IOException ex) {
            PLog.errorLog(975410369, download.getDestPathFile());
        }
    }

    private static String getInfoFileStr(Download download) {
        return download.getFileNameWithoutSuffix() + ".txt";
    }

    public static Path getInfoFilePath(Download download) {
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
