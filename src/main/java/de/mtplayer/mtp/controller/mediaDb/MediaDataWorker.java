/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
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
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.nio.file.Path;
import java.util.List;

public class MediaDataWorker {

    private static ProgData progData = ProgData.getInstance();
    private static BooleanProperty exportIsWorking = new SimpleBooleanProperty(false);

    private MediaDataWorker() {
    }

    // **************************************************************
    // MediaDataList in eine Textdatei schreiben
    public static synchronized void exportMediaDB(List<MediaData> list, String pathStr, boolean internDB, boolean externDB) {
        if (exportIsWorking.get()) {
            // dann mach mers gerade schon :)
            return;
        }

        Path path = Path.of(pathStr);
        int export;
        if (internDB && !externDB) {
            export = ProgConst.MEDIA_COLLECTION_EXPORT_INTERN;
        } else if (!internDB && externDB) {
            export = ProgConst.MEDIA_COLLECTION_EXPORT_EXTERN;
        } else {
            export = ProgConst.MEDIA_COLLECTION_EXPORT_INTERN_EXTERN;
        }


        if (path.toFile().exists()) {
            PAlert.BUTTON button = PAlert.showAlert_yes_no("Hinweis", "Mediensammlung in Datei exportieren",
                    "Die Zieldatei existiert bereits:" + P2LibConst.LINE_SEPARATOR +
                            path.toString() +
                            P2LibConst.LINE_SEPARATORx2 +
                            "Soll die Datei überschrieben werden?");
            if (button.equals(PAlert.BUTTON.YES)) {
                path.toFile().delete();
            } else {
                // dann nix tun
                return;
            }
        }

        exportIsWorking.set(true);
        Thread th = new Thread(new ExportMediaDB(list, exportIsWorking, path, export));
        th.setName("ExportMediaDB");
        th.start();
    }

    // **************************************************************
    // INTERNAL
    // MediaDataList INTERN anlegen und die gespeicherten EXTERNEN anfügen
    public static synchronized void createMediaDb() {
        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new CreateTheMediaDB());
        th.setName("CreateTheMediaDB -> createInternalMediaDb");
        th.start();
    }

    // **************************************************************
    // EXTERNAL
    // MediaDataList EXTERN: eine neue collection anlegen
    public static synchronized void createExternalCollection(MediaCollectionData mediaCollectionData) {
        if (progData.mediaCollectionDataList.getMediaCollectionData(mediaCollectionData.getId()) == null) {
            // evtl. erst mal die Collection anlegen
            progData.mediaCollectionDataList.add(mediaCollectionData);
        }

        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        // und jetzt Medien suchen
        Thread th = new Thread(new CreateTheMediaDB(mediaCollectionData));
        th.setName("CreateTheMediaDB -> createExternalCollection");
        th.start();
    }

    // **************************************************************
    // EXTERNAL
    // MediaDataList EXTERN: eine collection neu einlesen
    public static synchronized void updateExternalCollection(MediaCollectionData mediaCollectionData) {
        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new UpdateExternal(mediaCollectionData));
        th.setName("UpdateExternal");
        th.start();
    }

    // **************************************************************
    // INTERN/EXTERNAL: eine collection und ihre medien löschen
    // EXTERNAL media in Datei schreiben
    public static synchronized void removeMediaCollection(List<Long> idList) {
        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        boolean extern = false;
        for (Long id : idList) {
            MediaCollectionData mediaCollectionData = progData.mediaCollectionDataList.getMediaCollectionData(id);
            if (mediaCollectionData.isExternal()) {
                extern = true;
            }
            progData.mediaCollectionDataList.removeMediaCollectionData(id);
            progData.mediaDataList.removeMediaData(id);
        }

        if (extern) {
            // muss nur dann geschrieben werden
            new WriteMediaDb(progData).writeExternalMediaData();
        }
    }

    // create/update threads
    private static class CreateTheMediaDB implements Runnable {
        MediaCollectionData mediaCollectionData = null;

        // nur eine mediaCollection einlesen
        public CreateTheMediaDB(MediaCollectionData mediaCollectionData) {
            this.mediaCollectionData = mediaCollectionData;
        }

        // interne MediaDB einlesen und extern anfügen
        public CreateTheMediaDB() {
        }

        @Override
        public void run() {
            new CreateMediaDb().createDB(mediaCollectionData);
        }
    }

    private static class UpdateExternal implements Runnable {
        MediaCollectionData mediaCollectionData;

        public UpdateExternal(MediaCollectionData mediaCollectionData) {
            this.mediaCollectionData = mediaCollectionData;
        }

        @Override
        public void run() {
            // erst mal die collection entfernen
            progData.mediaDataList.removeMediaData(mediaCollectionData.getId());
            mediaCollectionData.setCount(0);

            // dann wieder einlesen und hinzufügen
            new CreateMediaDb().createDB(mediaCollectionData);
        }
    }
}
