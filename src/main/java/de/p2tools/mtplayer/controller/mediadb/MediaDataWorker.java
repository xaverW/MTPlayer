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


package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
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
    // Ganze Mediensammlung erstellen
    // INTERNE suchen und anlegen und die gespeicherten EXTERNEN anfügen
    // Programmstart, User: MediaDialog, ConfigDialog beim Beenden nach Änderungen
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
    // Eine MediaCollection neu einlesen, vorher die vorhandenen löschen
    // vom User ausgelöst, Config
    public static synchronized void updateCollection(MediaCollectionData mediaCollectionData) {
        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        progData.mediaDataList.removeMediaData(mediaCollectionData);
        mediaCollectionData.setCount(0);
        Thread th = new Thread(new CreateTheMediaDB(mediaCollectionData));
        th.setName("updateCollection");
        th.start();
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
    // INTERN/EXTERNAL: eine collection und ihre Medien löschen
    // EXTERNAL media in Datei schreiben
    public static synchronized void removeMediaCollection(List<MediaCollectionData> dataList, boolean external) {
        // aus dem ConfigDialog: Button "Löschen"
        if (progData.mediaDataList.isSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        progData.mediaCollectionDataList.addDataToUndoList(dataList, external);

        dataList.forEach(mediaCollectionData -> {
            mediaCollectionData.setCount(0); // damits beim UNDO stimmt, Liste ist dann ja leer
            progData.mediaCollectionDataList.remove(mediaCollectionData);
            progData.mediaDataList.removeMediaData(mediaCollectionData);
        });

        if (external) {
            // muss nur dann geschrieben werden
            new WriteMediaDb(progData).writeExternalMediaData();
        }
    }

    // create/update threads
    private static class CreateTheMediaDB implements Runnable {
        MediaCollectionData mediaCollectionData = null;

        // interne MediaDB einlesen und extern anfügen
        public CreateTheMediaDB() {
        }

        public CreateTheMediaDB(MediaCollectionData mediaCollectionData) {
            this.mediaCollectionData = mediaCollectionData;
        }

        @Override
        public void run() {
            new CreateMediaDb().createDB(mediaCollectionData);
        }
    }
}
