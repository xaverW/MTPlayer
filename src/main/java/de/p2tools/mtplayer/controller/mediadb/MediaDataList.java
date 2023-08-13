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

package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class MediaDataList extends SimpleListProperty<MediaData> {

    private FilteredList<MediaData> filteredList = null;
    private SortedList<MediaData> sortedList = null;
    private BooleanProperty searching = new SimpleBooleanProperty(false);
    private boolean stopSearching = false;

    public MediaDataList() {
        super(FXCollections.observableArrayList());
    }

    // searching-property
    public synchronized boolean getSearching() {
        return searching.get();
    }

    public synchronized BooleanProperty searchingProperty() {
        return searching;
    }

    public boolean isSearching() {
        return searching.get();
    }

    public synchronized void setSearching(boolean searching) {
        this.searching.set(searching);
    }

    public boolean isStopSearching() {
        return stopSearching;
    }

    public void setStopSearching(boolean stopSearching) {
        this.stopSearching = stopSearching;
    }

    // sorted/filtered list
    public synchronized SortedList<MediaData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public synchronized FilteredList<MediaData> getFilteredList() {
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

    synchronized void removeMediaData(MediaCollectionData mediaCollectionData) {
        // remove all media with this collectionId, in one way
        List<MediaData> rest = new ArrayList<>();
        this.stream().filter(mediaData -> mediaData.getCollectionId() != mediaCollectionData.getIdInt())
                .forEach(rest::add);
        this.setAll(rest);
    }


    synchronized void checkDuplicateMediaData() {
        // checks duplicates in the mediaDataList and creates the counter in the pathList
        // bei den EXTERNEN kann der Pfad/Dateiname doppelt sein! muss aber nur einmal in die DB
        // da ja über Pfad/Dateiname verglichen wird und nicht über den Datei-Hash
        // beim Löschen, haben die externen Vorrang, d.h. zuerst die internen löschen
        final HashSet<String> hashSet = new HashSet<>(size());
        Iterator<MediaData> it = iterator();
        while (it.hasNext()) {
            // zuerst mal die doppelten externen löschen
            final MediaData mediaData = it.next();
            if (!mediaData.isExternal()) {
                continue;
            }

            // final String h = mediaData.getName() + "##" + mediaData.getPath() + "##" + mediaData.getCollectionId();
            // collId nicht mehr, wenn sich die collections "überschneiden" gibts sonst doppelte
            if (!hashSet.add(mediaData.getName() + mediaData.getPath())) {
                it.remove();
            }
        }

        it = iterator();
        while (it.hasNext()) {
            // jetzt sind alle externen drin, jetzt die internen checken
            final MediaData mediaData = it.next();
            if (mediaData.isExternal()) {
                continue;
            }

            // final String h = mediaData.getName() + "##" + mediaData.getPath() + "##" + mediaData.getCollectionId();
            // collId nicht mehr, wenn sich die collections "überschneiden" gibts sonst doppelte
            if (!hashSet.add(mediaData.getName() + mediaData.getPath())) {
                it.remove();
            }
        }
        hashSet.clear();
    }

    public synchronized void countMediaData(ProgData progData) {
        // creates the counter in the MediaCollectionDataList
        progData.mediaCollectionDataList.forEach(collectionData -> collectionData.setCount(0));

        this.forEach(mediaData -> {
            MediaCollectionData mediaCollectionData = progData.mediaCollectionDataList.getMediaCollectionData(mediaData.getCollectionId());
            if (mediaCollectionData != null) {
                mediaCollectionData.setCount(mediaCollectionData.getCount() + 1);
            }
        });
    }
}
