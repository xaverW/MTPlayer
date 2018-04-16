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

package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mtp.controller.config.Daten;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;

import java.util.Collection;

@SuppressWarnings("serial")
public class MediaDbListExtern extends SimpleListProperty<MediaDbDataExtern> {

    private final Daten daten;
    private SortedList<MediaDbDataExtern> sortedList = null;


    public MediaDbListExtern(Daten daten) {
        super(FXCollections.observableArrayList());
        this.daten = daten;
    }

    public SortedList<MediaDbDataExtern> getSortedList() {
        if (sortedList == null) {
            sortedList = new SortedList<>(this);
        }
        return sortedList;
    }

    public synchronized boolean mediaDbDataSetAll(Collection<? extends MediaDbData> mediaDbData) {
        this.clear();
        mediaDbData.stream().forEach(e -> addMediaDbData(e));
        return true;
    }

    public synchronized boolean mediaDbDataAddAll(Collection<? extends MediaDbData> mediaDbData) {
        mediaDbData.stream().forEach(e -> addMediaDbData(e));
        return true;
    }

    public synchronized boolean mediaDbDataAdd(MediaDbData mediaDbData) {
        addMediaDbData(mediaDbData);
        return true;
    }

    private void addMediaDbData(MediaDbData md) {
        if (!md.isExtern()) {
            return;
        }

        MediaDbDataExtern extern = stream().filter(m -> m.equal(md)).findAny().orElse(null);
        if (extern != null) {
            extern.setSize(extern.getSize() + 1);
        } else {
            MediaDbDataExtern mde = new MediaDbDataExtern(md.getCollection(), md.getPath());
            mde.setSize(1);
            super.add(mde);
        }
    }

}
