/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MediaDb {

    final static Daten daten = Daten.getInstance();

    static void removeCollection(List<MediaDbData> mediaDbList, String collection) {
        Iterator<MediaDbData> it = mediaDbList.iterator();
        while (it.hasNext()) {
            MediaDbData md = it.next();
            if (md.isExtern() && md.getCollectionName().equals(collection)) {
                it.remove();
            }
        }
        writeList(mediaDbList);
    }

    static Path getFilePath() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(Const.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return urlPath;
    }

    static List<MediaDbData> loadSavedList() {
        final Path urlPath = MediaDb.getFilePath();
        return new ReadMediaDb(daten).read(urlPath);
    }

    static synchronized void writeList(List<MediaDbData> mediaDbData) {
        final Path path = getFilePath();

        ArrayList<String> list = new ArrayList<>();
        list.add("MediaDB schreiben (" + daten.mediaDbList.size() + " Dateien) :");
        list.add("   --> Start Schreiben nach: " + path.toString());

        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                PLog.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Mediensammlung kann nicht angelegt werden: \n" +
                                path.toString()));
                return;
            }

            List<MediaDbData> mediaList = mediaDbData.stream().filter(m -> m.isExtern()).collect(Collectors.toList());
            new WriteMediaDb().write(path, mediaList);
            list.add("   --> geschrieben!");

        } catch (final Exception ex) {
            list.add("   --> Fehler, nicht geschrieben!");
            PLog.errorLog(931201478, ex, "nach: " + path.toString());
            Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:\n" +
                            path.toString()));
        }

        PLog.userLog(list);
    }


}
