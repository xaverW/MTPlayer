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
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MTInfoFile {

    public static void writeInfoFile(Download datenDownload) {
        PLog.sysLog(new String[]{"Infofile schreiben nach: ", datenDownload.getZielPfad()});

        new File(datenDownload.getZielPfad()).mkdirs();
        final Path path = Paths.get(datenDownload.getFileNameWithoutSuffix() + ".txt");
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(path));
             OutputStreamWriter osw = new OutputStreamWriter(dos);
             BufferedWriter br = new BufferedWriter(osw)) {
            if (datenDownload.getFilm() != null) {
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_CHANNEL] + ":      " + datenDownload.getFilm().arr[FilmXml.FILM_CHANNEL]);
                br.write("\n");
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_THEME] + ":       " + datenDownload.getFilm().arr[FilmXml.FILM_THEME]);
                br.write("\n\n");
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_TITLE] + ":       " + datenDownload.getFilm().arr[FilmXml.FILM_TITLE]);
                br.write("\n\n");
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_DATE] + ":       " + datenDownload.getFilm().arr[FilmXml.FILM_DATE]);
                br.write("\n");
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_TIME] + ":        " + datenDownload.getFilm().arr[FilmXml.FILM_TIME]);
                br.write("\n");
                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_DURATION] + ":       " + datenDownload.getFilm().arr[FilmXml.FILM_DURATION]);
                br.write("\n");
                br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_GROESSE] + ":  " + datenDownload.getDownloadSize());
                br.write("\n\n");

                br.write(FilmXml.COLUMN_NAMES[FilmXml.FILM_WEBSEITE] + '\n');
                br.write(datenDownload.getFilm().arr[FilmXml.FILM_WEBSEITE]);
                br.write("\n\n");
            }

            br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_URL] + '\n');
            br.write(datenDownload.getUrl());
            br.write("\n\n");
            if (!datenDownload.getUrlRtmp().isEmpty()
                    && !datenDownload.getUrlRtmp().equals(datenDownload.getUrl())) {
                br.write(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_URL_RTMP] + '\n');
                br.write(datenDownload.getUrlRtmp());
                br.write("\n\n");
            }

            if (datenDownload.getFilm() != null) {
                int anz = 0;
                for (final String s : datenDownload.getFilm().arr[FilmXml.FILM_DESCRIPTION].split(" ")) {
                    anz += s.length();
                    br.write(s + ' ');
                    if (anz > 50) {
                        br.write("\n");
                        anz = 0;
                    }
                }
            }
            br.write("\n\n");
            br.flush();
            PLog.userLog(new String[]{"Infofile", "  geschrieben"});
        } catch (final IOException ex) {
            PLog.errorLog(975410369, datenDownload.getZielPfadDatei());
        }
    }

}
