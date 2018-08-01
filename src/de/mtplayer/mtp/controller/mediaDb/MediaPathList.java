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

import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class MediaPathList extends SimpleListProperty<MediaPathData> {

    private FilteredList<MediaPathData> filteredListInternal = null;
    private SortedList<MediaPathData> sortedListInternal = null;
    private FilteredList<MediaPathData> filteredListExternal = null;
    private SortedList<MediaPathData> sortedListExternal = null;

    public MediaPathList() {
        super(FXCollections.observableArrayList());
    }


    public FilteredList<MediaPathData> getFilteredListInternal() {
        if (filteredListInternal == null) {
            filteredListInternal = new FilteredList<>(this, p -> !p.isExternal());
        }
        return filteredListInternal;
    }

    public SortedList<MediaPathData> getSortedListInternal() {
        filteredListInternal = getFilteredListInternal();
        if (sortedListInternal == null) {
            sortedListInternal = new SortedList<>(filteredListInternal);
        }
        return sortedListInternal;
    }

    public FilteredList<MediaPathData> getFilteredListExternal() {
        if (filteredListExternal == null) {
            filteredListExternal = new FilteredList<>(this, p -> p.isExternal());
        }
        return filteredListExternal;
    }

    public SortedList<MediaPathData> getSortedListExternal() {
        filteredListExternal = getFilteredListExternal();
        if (sortedListExternal == null) {
            sortedListExternal = new SortedList<>(filteredListExternal);
        }
        return sortedListExternal;
    }


    public MediaPathList getInternalList() {
        MediaPathList list = new MediaPathList();
        list.addAll(this.stream().filter(l -> !l.isExternal()).collect(Collectors.toList()));
        return list;
    }

    public MediaPathList getExternalList() {
        MediaPathList list = new MediaPathList();
        list.addAll(this.stream().filter(l -> l.isExternal()).collect(Collectors.toList()));
        return list;
    }

    public boolean addExternal(String collection, String path) {
        boolean ret = false;
        MediaPathData md = this.stream()
                .filter(m -> m.isExternal())
                .filter(m -> m.getCollectionName().equals(collection))
                .findAny().orElse(null);
        if (md != null) {
            md.setPath(path);
        } else {
            add(new MediaPathData(path, collection, true));
            ret = true;
        }
        return ret;
    }

    public boolean containExternal(MediaData dm) {
        return containExternal(dm.getCollectionName());
    }

    public boolean containExternal(String collection) {
        MediaPathData md = this.stream()
                .filter(m -> m.isExternal())
                .filter(m -> m.getCollectionName().equals(collection))
                .findAny().orElse(null);
        if (md != null) {
            return true;
        } else {
            return false;
        }
    }

    public int addCounter(MediaData dm) {
        MediaPathData md = this.stream()
                .filter(m -> m.isExternal())
                .filter(m -> m.getCollectionName().equals(dm.getCollectionName()))
                .findAny().orElse(null);
        if (md != null) {
            md.setCount(md.getCount() + 1);
            return md.getCount();
        } else {
            return 0;
        }
    }

    public boolean addInternal(MediaPathData dmp) {
        if (containInternal(dmp)) {
            return false;
        }
        add(dmp);
        return true;
    }

    public boolean containInternal(MediaPathData dm) {
        MediaPathData md = this.stream()
                .filter(m -> !m.isExternal())
                .filter(m -> m.getPath().equals(dm.getPath()))
                .findAny().orElse(null);
        if (md != null) {
            return true;
        } else {
            return false;
        }
    }
}
