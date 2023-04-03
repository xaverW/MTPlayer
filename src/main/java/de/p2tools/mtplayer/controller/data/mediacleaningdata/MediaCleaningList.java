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

package de.p2tools.mtplayer.controller.data.mediacleaningdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.mediadb.MediaCleaningFactory;
import de.p2tools.p2lib.configfile.pdata.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class MediaCleaningList extends SimpleListProperty<MediaCleaningData> implements PDataList<MediaCleaningData> {

    public String TAG = "MediaCleaningList";
    private final ProgData progData;

    private FilteredList<MediaCleaningData> filteredList = null;
    private SortedList<MediaCleaningData> sortedList = null;

    public MediaCleaningList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    public void initList() {
        if (isEmpty()) {
            MediaCleaningFactory.initMediaCleaningList(this);
        }
    }

    public SortedList<MediaCleaningData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<MediaCleaningData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<MediaCleaningData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPred(Predicate<MediaCleaningData> predicate) {
        getFilteredList().setPredicate(predicate);
    }

    public String[] getSearchArr() {
        List<String> arr = new ArrayList<>();
        this.stream().forEach(e -> arr.add(e.getCleaningData()));
        return arr.toArray(new String[0]);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller MediaCleaningData";
    }

    @Override
    public MediaCleaningData getNewItem() {
        return new MediaCleaningData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(MediaCleaningData.class)) {
            add((MediaCleaningData) obj);
        }
    }

    @Override
    public synchronized boolean add(MediaCleaningData b) {
        return super.add(b);
    }
}
