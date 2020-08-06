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

import java.util.List;

public class MediaDataWorker {

    private static ProgData progData = ProgData.getInstance();

    private MediaDataWorker() {
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
