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

package de.mtplayer.mtp.controller.mediaDb;

import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MediaDataList extends SimpleListProperty<MediaData> {

    private FilteredList<MediaData> filteredList = null;
    private SortedList<MediaData> sortedList = null;
    private BooleanProperty searching = new SimpleBooleanProperty(false);

    public MediaDataList() {
        super(FXCollections.observableArrayList());
    }

    // searching-property
    public boolean getSearching() {
        return searching.get();
    }

    public BooleanProperty searchingProperty() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching.set(searching);
    }

    // sorted/filtered list
    public SortedList<MediaData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<MediaData> getFilteredList() {
        if (filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPredicate(Predicate<MediaData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filterdListSetPredFalse() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filterdListSetPredTrue() {
        filteredList.setPredicate(p -> true);
    }


    // **************************************************************
    // INTERNAL
    // MediaDataList INTERN anlegen und die gespeicherten EXTERNEN anfügen
    public synchronized void createMediaDb() {
        if (getSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new CreateMediaDb());
        th.setName("createInternalMediaDb");
        th.start();
    }

    // **************************************************************
    // EXTERNAL
    // MediaDataList EXTERN: eine collection anlegen
    public synchronized void createExternalCollection(MediaCollectionData mediaCollectionData) {
        if (null == ProgData.getInstance().mediaCollectionDataList.getMediaCollectionData(mediaCollectionData.getId())) {
            // evtl. erst mal die Collection anlegen
            ProgData.getInstance().mediaCollectionDataList.add(mediaCollectionData);
        }

        if (getSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        // und jetzt Medien suchen
        Thread th = new Thread(new CreateMediaDb(mediaCollectionData));
        th.setName("createExternalCollection");
        th.start();
    }

    public synchronized void updateExternalCollection(MediaCollectionData mediaCollectionData) {
        // eine externe Collection überprüfen
        if (getSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        removeMediaData(mediaCollectionData.getId());
        mediaCollectionData.setCount(0);

        createExternalCollection(mediaCollectionData);
    }

    public synchronized void removeMediaAndCollection(long id) {
        // remove collection AND all media of this collection
        if (getSearching()) {
            // dann mach mers gerade schon :)
            return;
        }

        removeMediaData(id);
        ProgData.getInstance().mediaCollectionDataList.removeMediaCollectionData(id);
        writeExternalMediaData();
    }

    private void removeMediaData(long collectionId) {
        // remove all media with this collectionId
        final Iterator<MediaData> iterator = iterator();
        while (iterator.hasNext()) {
            MediaData mediaData = iterator.next();
            if (mediaData.getCollectionId() == collectionId) {
                iterator.remove();
            }
        }
    }

    public List<MediaData> getExternalMediaData() {
        return this.stream().filter(mediaData -> mediaData.isExternal()).collect(Collectors.toList());
    }

    public void checkExternalMediaData() {
        // checks duplicates in the mediaDataList and creates the counter in the pathList
        // beim kompletten Neuladen der MediaDB können ja nur externe doppelt sein
        final HashSet<String> hashSet = new HashSet<>(size());
        Iterator<MediaData> it = iterator();
        while (it.hasNext()) {
            MediaData mediaData = it.next();
            if (!mediaData.isExternal()) {
                continue;
            }

            final String h = mediaData.getHash();
            if (!hashSet.add(h)) {
                it.remove();
            }
        }
    }

    public void countMediaData() {
        // creates the counter in the MediaCollectionDataList
        final MediaCollectionDataList mediaCollectionDataList = ProgData.getInstance().mediaCollectionDataList;
        mediaCollectionDataList.stream().forEach(collectionData -> collectionData.setCount(0));

        this.stream().forEach(mediaData -> {
            MediaCollectionData mediaCollectionData = mediaCollectionDataList.getMediaCollectionData(mediaData.getCollectionId());
            if (mediaCollectionData != null) {
                mediaCollectionData.setCount(mediaCollectionData.getCount() + 1);
            }
        });
    }


    // ******************************************************
    // EXTERNAL MediaData aus File lesen und schreiben
    private Path getPathMediaDB() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(ProgConst.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            PLog.errorLog(951201201, ex);
        }
        return urlPath;
    }

    public List<MediaData> loadSavedExternalMediaData() {
        final Path urlPath = getPathMediaDB();
        return new ReadMediaDb().read(urlPath);
    }

    public synchronized void writeExternalMediaData() {
        final Path path = getPathMediaDB();

        ArrayList<String> logList = new ArrayList<>();
        logList.add("MediaDB schreiben (" + ProgData.getInstance().mediaDataList.size() + " Dateien) :");
        logList.add("   --> Start Schreiben nach: " + path.toString());

        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                PLog.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Mediensammlung kann nicht angelegt werden: " + PConst.LINE_SEPARATOR +
                                path.toString()));
                return;
            }

            List<MediaData> externalMediaData = getExternalMediaData();
            new WriteMediaDb().write(path, externalMediaData);
            logList.add("   --> geschrieben!");

        } catch (final Exception ex) {
            logList.add("   --> Fehler, nicht geschrieben!");
            PLog.errorLog(931201478, ex, "nach: " + path.toString());
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:" + PConst.LINE_SEPARATOR +
                            path.toString()));
        }

        PLog.sysLog(logList);
    }

}
