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

package de.p2tools.mtplayer.controller.data.cleaningdata;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class CleaningDataList extends SimpleListProperty<CleaningData> implements P2DataList<CleaningData> {

    public String TAG;

    private final boolean propose;
    private FilteredList<CleaningData> filteredList = null;
    private SortedList<CleaningData> sortedList = null;

    public CleaningDataList(boolean propose) {
        super(FXCollections.observableArrayList());
        this.TAG = propose ? "CleaningDataListPropose" : "CleaningDataListMedia";
        this.propose = propose;
    }

    public void initList() {
        if (isEmpty()) {
            for (String s : CleaningFactory.REPLACE_LIST) {
                add(new CleaningData(s, true));
            }
            for (String s : CleaningFactory.CLEAN_LIST) {
                add(new CleaningData(s, false));
            }
        }
        this.forEach(cl -> cl.setCleaningString(cl.getCleaningString().trim()));
    }

    public SortedList<CleaningData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<CleaningData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<CleaningData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return propose ? "Liste aller ProposeReplaceData" : "Liste aller ProposeCleaningData";
    }

    @Override
    public CleaningData getNewItem() {
        return new CleaningData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(CleaningData.class)) {
            add((CleaningData) obj);
        }
    }

    @Override
    public synchronized boolean add(CleaningData b) {
        return super.add(b);
    }
}
