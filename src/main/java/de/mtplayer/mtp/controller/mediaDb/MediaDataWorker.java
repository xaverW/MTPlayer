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

import de.mtplayer.mtp.controller.config.ProgData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;

public class MediaDataWorker {

    ProgData progData;
    private BooleanProperty searching = new SimpleBooleanProperty(false);

    public MediaDataWorker(ProgData progData) {
        this.progData = progData;
    }

    // **************************************************************
    // INTERNAL
    // MediaDataList INTERN anlegen und die gespeicherten EXTERNEN anfügen
    public synchronized void createMediaDb() {
        if (searching.get()) {
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
    public synchronized void createExternalCollection(MediaCollectionData mediaCollectionData) {
        if (null == progData.mediaCollectionDataList.getMediaCollectionData(mediaCollectionData.getId())) {
            // evtl. erst mal die Collection anlegen
            progData.mediaCollectionDataList.add(mediaCollectionData);
        }

        if (searching.get()) {
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
    public synchronized void updateExternalCollection(MediaCollectionData mediaCollectionData) {
        if (searching.get()) {
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
    public synchronized void removeMediaCollection(List<Long> idList) {
        if (searching.get()) {
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
    private class CreateTheMediaDB implements Runnable {
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

    private class UpdateExternal implements Runnable {
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
