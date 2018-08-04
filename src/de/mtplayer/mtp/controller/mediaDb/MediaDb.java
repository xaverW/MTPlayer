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


package de.mtplayer.mtp.controller.mediaDb;

import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MediaDb {

    final static ProgData PROG_DATA = ProgData.getInstance();

    static void checkExternalMediaData(List<MediaData> mediaDataList) {
        // checks duplicates in the mediaDataList and creates the counter in the pathList
        HashSet<String> hashSet = new HashSet<>(mediaDataList.size());
        Iterator<MediaData> it = mediaDataList.iterator();
        while (it.hasNext()) {
            MediaData md = it.next();
            if (!md.isExternal()) {
                continue;
            }

            final String h = md.getHash();
            if (!hashSet.add(h)) {
                it.remove();
            }
        }

        countExternalMediaData(mediaDataList);
    }

    static void countExternalMediaData(List<MediaData> mediaDataList) {
        // creates the counter in the pathList
        final MediaPathList mediaPathList = ProgData.getInstance().mediaPathList;

        mediaPathList.stream().forEach(m -> m.setCount(0));
        mediaDataList.stream().filter(md -> md.isExternal()).forEach(mediaData -> {

            if (!mediaPathList.containExternal(mediaData)) {
                mediaPathList.addExternal(mediaData.getCollectionName(), mediaData.getPath());
            }
            mediaPathList.addCounter(mediaData);

        });
    }

    static void removeCollectionMedia(List<MediaData> mediaDataList, MediaPathData mediaPathData) {
        // remove all media of this collection
        Iterator<MediaData> itMedia = mediaDataList.iterator();
        while (itMedia.hasNext()) {
            MediaData md = itMedia.next();
            if (md.isExternal() && md.getCollectionName().equals(mediaPathData.getCollectionName())) {
                itMedia.remove();
            }
        }
    }

    static void removeCollection(List<MediaData> mediaDataList, MediaPathData mediaPathData) {
        // remove collection AND all media of this collection
        removeCollectionMedia(mediaDataList, mediaPathData);

        Iterator<MediaPathData> itPath = PROG_DATA.mediaPathList.iterator();
        while (itPath.hasNext()) {
            MediaPathData md = itPath.next();
            if (md.isExternal() && md.getCollectionName().equals(mediaPathData.getCollectionName())) {
                itPath.remove();
            }
        }
    }

    static Path getFilePath() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(ProgConst.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return urlPath;
    }

    static List<MediaData> loadSavedList() {
        final Path urlPath = MediaDb.getFilePath();
        return new ReadMediaDb(PROG_DATA).read(urlPath);
    }

    static synchronized void writeList(List<MediaData> mediaDataList) {
        final Path path = getFilePath();

        ArrayList<String> list = new ArrayList<>();
        list.add("MediaDB schreiben (" + PROG_DATA.mediaList.size() + " Dateien) :");
        list.add("   --> Start Schreiben nach: " + path.toString());

        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                PLog.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Mediensammlung kann nicht angelegt werden: \n" +
                                path.toString()));
                return;
            }

            List<MediaData> mediaList = mediaDataList.stream().filter(m -> m.isExternal()).collect(Collectors.toList());
            new WriteMediaDb().write(path, mediaList);
            list.add("   --> geschrieben!");

        } catch (final Exception ex) {
            list.add("   --> Fehler, nicht geschrieben!");
            PLog.errorLog(931201478, ex, "nach: " + path.toString());
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:\n" +
                            path.toString()));
        }

        PLog.userLog(list);
    }


}
