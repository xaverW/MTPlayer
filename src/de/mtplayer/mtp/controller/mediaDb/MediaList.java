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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.Collection;
import java.util.function.Predicate;

public class MediaList extends SimpleListProperty<MediaData> {

    private FilteredList<MediaData> filteredList = null;
    private SortedList<MediaData> sortedList = null;

    private BooleanProperty propSearch = new SimpleBooleanProperty(false);


    public MediaList() {
        super(FXCollections.observableArrayList());
    }

    public boolean isPropSearch() {
        return propSearch.get();
    }

    public BooleanProperty propSearchProperty() {
        return propSearch;
    }

    public void setPropSearch(boolean propSearch) {
        this.propSearch.set(propSearch);
    }

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

    public synchronized void filterdListClearPred() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filterdListClearPred(boolean pred) {
        filteredList.setPredicate(p -> pred);
    }

    public synchronized boolean setAll(Collection<? extends MediaData> mediaData) {
        return super.setAll(mediaData);
    }

    public synchronized boolean addAll(Collection<? extends MediaData> mediaData) {
        return super.addAll(mediaData);
    }

    public synchronized boolean add(MediaData mediaData) {
        return super.add(mediaData);
    }

    public synchronized void checkExternalMediaData() {
        MediaDb.checkExternalMediaData(this);
    }

    public synchronized void createMediaDb() {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new CreateMediaDb(this));
        th.setName("createMediaDb");
        th.start();
    }

    public synchronized void createCollection(MediaPathData mediaPathData) {
        createCollection(mediaPathData.getPath(), mediaPathData.getCollectionName());
    }

    public synchronized void createCollection(String path, String collection) {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new CreateMediaDb(this, path, collection));
        th.setName("createCollection");
        th.start();
    }

    public synchronized void updateCollection(MediaPathData mediaPathData) {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        MediaDb.removeCollectionMedia(this, mediaPathData);
        mediaPathData.setCount(0);
        
        createCollection(mediaPathData);
    }

    public synchronized void removeCollection(MediaPathData mediaPathData) {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        MediaDb.removeCollection(this, mediaPathData);
        MediaDb.writeList(this);
    }
}
