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

import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;

import java.util.Collection;

@SuppressWarnings("serial")
public class MediaListExternal extends SimpleListProperty<MediaDataExternal> {

    private SortedList<MediaDataExternal> sortedList = null;


    public MediaListExternal() {
        super(FXCollections.observableArrayList(
                param -> new Observable[]{
                        param.countProperty()
                }
        ));
    }

    public SortedList<MediaDataExternal> getSortedList() {
        if (sortedList == null) {
            sortedList = new SortedList<>(this);
        }
        return sortedList;
    }

    public synchronized boolean mediaDbDataSetAll(Collection<? extends MediaData> mediaDbData) {
        this.clear();
        mediaDbData.stream().forEach(e -> addMediaDbData(e));
        return true;
    }

    public synchronized boolean mediaDbDataAddAll(Collection<? extends MediaData> mediaDbData) {
        mediaDbData.stream().forEach(e -> addMediaDbData(e));
        return true;
    }

    public synchronized boolean mediaDbDataAdd(MediaData mediaData) {
        addMediaDbData(mediaData);
        return true;
    }

    private void addMediaDbData(MediaData md) {
        if (!md.isExternal()) {
            return;
        }

        MediaDataExternal external = stream().filter(m -> m.equalCollection(md)).findAny().orElse(null);
        if (external != null) {
            external.setCount(external.getCount() + 1);
        } else {
            MediaDataExternal mde = new MediaDataExternal(md.getCollectionName(), md.getPath());
            mde.setCount(1);
            super.add(mde);
        }
    }

}
