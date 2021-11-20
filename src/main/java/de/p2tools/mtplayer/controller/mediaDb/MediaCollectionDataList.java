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

package de.p2tools.mtplayer.controller.mediaDb;

import de.p2tools.p2Lib.configFile.pData.PDataList;
import de.p2tools.p2Lib.tools.PIndex;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class MediaCollectionDataList extends SimpleListProperty<MediaCollectionData> implements PDataList<MediaCollectionData> {

    public static final String TAG = "MediaCollectionDataList";
    private FilteredList<MediaCollectionData> filteredListInternal = null;
    private SortedList<MediaCollectionData> sortedListInternal = null;
    private FilteredList<MediaCollectionData> filteredListExternal = null;
    private SortedList<MediaCollectionData> sortedListExternal = null;

    public MediaCollectionDataList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public boolean add(MediaCollectionData mediaCollectionData) {
        if (mediaCollectionData.getId() <= 0) {
            mediaCollectionData.setId(PIndex.getIndex());
        }
        return super.add(mediaCollectionData);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller MediaCollectionData";
    }

    @Override
    public MediaCollectionData getNewItem() {
        return new MediaCollectionData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(MediaCollectionData.class)) {
            add((MediaCollectionData) obj);
        }
    }

    public SortedList<MediaCollectionData> getSortedListInternal() {
        if (sortedListInternal == null) {
            filteredListInternal = new FilteredList<>(this, p -> !p.isExternal());
            sortedListInternal = new SortedList<>(filteredListInternal);
        }
        return sortedListInternal;
    }

    public SortedList<MediaCollectionData> getSortedListExternal() {
        if (sortedListExternal == null) {
            filteredListExternal = new FilteredList<>(this, p -> p.isExternal());
            sortedListExternal = new SortedList<>(filteredListExternal);
        }
        return sortedListExternal;
    }


    public MediaCollectionDataList getMediaCollectionDataList(boolean external) {
        MediaCollectionDataList list = new MediaCollectionDataList();
        list.addAll(this.stream().filter(l -> l.isExternal() == external).collect(Collectors.toList()));
        return list;
    }

    public String getNextMediaCollectionName(boolean external) {
        String name;
        int count;

        if (external) {
            name = "Extern ";
            count = this.getMediaCollectionDataList(true).size();

        } else {
            name = "Intern ";
            count = this.getMediaCollectionDataList(false).size();
        }

        while (getMediaCollectionData(name + count) != null) {
            ++count;
        }

        return name + count;
    }

    public MediaCollectionData addNewMediaCollectionData(String path, String collectionName, boolean external) {
        MediaCollectionData mediaCollectionData = new MediaCollectionData(path, collectionName, external);
        add(mediaCollectionData);
        return mediaCollectionData;
    }


    public MediaCollectionData getMediaCollectionData(String path, boolean external) {
        MediaCollectionData mediaCollectionData = this.stream()
                .filter(collectionData -> (collectionData.equals(external)))
                .filter(collectionData -> collectionData.getPath().equals(path))
                .findAny().orElse(null);
        return mediaCollectionData;
    }

    public MediaCollectionData getMediaCollectionData(long id) {
        MediaCollectionData mediaCollectionData = this.stream()
                .filter(m -> (m.getId() == id))
                .findAny().orElse(null);
        return mediaCollectionData;
    }

    private MediaCollectionData getMediaCollectionData(String collectionName) {
        MediaCollectionData mediaCollectionData = this.stream()
                .filter(m -> m.getCollectionName().equals(collectionName))
                .findAny().orElse(null);
        return mediaCollectionData;
    }

    public void cleanUpInternalMediaCollectionData() {
        // checks duplicates in the INTERN mediaCollectionDataList
        final HashSet<String> hashSet = new HashSet<>(size());
        Iterator<MediaCollectionData> it = iterator();
        while (it.hasNext()) {
            MediaCollectionData mediaCollectionData = it.next();
            if (mediaCollectionData.isExternal()) {
                continue;
            }

            final String h = mediaCollectionData.getHash();
            if (!hashSet.add(h)) {
                it.remove();
            }
        }
        hashSet.clear();
    }

    synchronized void removeMediaCollectionData(long id) {
        // remove collection
        Iterator<MediaCollectionData> iterator = iterator();
        while (iterator.hasNext()) {
            MediaCollectionData mediaCollectionData = iterator.next();
            if (mediaCollectionData.getId() == id) {
                iterator.remove();
            }
        }
    }
}
