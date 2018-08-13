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

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class MediaPathDataList extends SimpleListProperty<MediaPathData> {

    private FilteredList<MediaPathData> filteredListInternal = null;
    private SortedList<MediaPathData> sortedListInternal = null;
    private FilteredList<MediaPathData> filteredListExternal = null;
    private SortedList<MediaPathData> sortedListExternal = null;

    public MediaPathDataList() {
        super(FXCollections.observableArrayList());
    }


    public SortedList<MediaPathData> getSortedListInternal() {
        if (sortedListInternal == null) {
            filteredListInternal = new FilteredList<>(this, p -> !p.isExternal());
            sortedListInternal = new SortedList<>(filteredListInternal);
        }
        return sortedListInternal;
    }

    public SortedList<MediaPathData> getSortedListExternal() {
        if (sortedListExternal == null) {
            filteredListExternal = new FilteredList<>(this, p -> p.isExternal());
            sortedListExternal = new SortedList<>(filteredListExternal);
        }
        return sortedListExternal;
    }


    public MediaPathDataList getInternalMediaPathDataList() {
        MediaPathDataList list = new MediaPathDataList();
        list.addAll(this.stream().filter(l -> !l.isExternal()).collect(Collectors.toList()));
        return list;
    }

    public MediaPathDataList getExternalMediaPathDataList() {
        MediaPathDataList list = new MediaPathDataList();
        list.addAll(this.stream().filter(l -> l.isExternal()).collect(Collectors.toList()));
        return list;
    }

    public String getNextExternCollectionName() {
        String name = "Sammlung ";
        int count = this.getExternalMediaPathDataList().size();

        while (getExternalMediaPathData(name + count) != null) {
            ++count;
        }

        return name + count;
    }

    public MediaPathData addInternalMediaPathData(String path) {
        MediaPathData mediaPathData = new MediaPathData(path, "", false);
        if (getInternalMediaPathData(mediaPathData.getPath()) != null) {
            return null;
        }
        add(mediaPathData);
        return mediaPathData;
    }

    public MediaPathData addExternalMediaPathData(String path, String collection) {
        MediaPathData mediaPathData = new MediaPathData(path, collection, true);
        if (getExternalMediaPathData(mediaPathData.getCollectionName()) != null) {
            return null;
        }
        add(mediaPathData);
        return mediaPathData;
    }

    public MediaPathData getInternalMediaPathData(String path) {
        MediaPathData md = this.stream()
                .filter(m -> (!m.isExternal()))
                .filter(m -> m.getPath().equals(path))
                .findAny().orElse(null);
        return md;
    }

    public MediaPathData getExternalMediaPathData(String collectionName) {
        MediaPathData mediaPathData;
        mediaPathData = this.stream()
                .filter(m -> m.isExternal())
                .filter(m -> m.getCollectionName().equals(collectionName))
                .findAny().orElse(null);
        return mediaPathData;
    }

    public void cleanUpMediaPathData() {
        // checks duplicates in the INTERN mediaPathDataList
        final HashSet<String> hashSet = new HashSet<>(size());
        Iterator<MediaPathData> it = iterator();
        while (it.hasNext()) {
            MediaPathData mediaPathData = it.next();
            if (mediaPathData.isExternal()) {
                continue;
            }

            final String h = mediaPathData.getHash();
            if (!hashSet.add(h)) {
                it.remove();
            }
        }
    }

}
