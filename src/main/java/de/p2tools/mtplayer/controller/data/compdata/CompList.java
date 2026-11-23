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

package de.p2tools.mtplayer.controller.data.compdata;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class CompList extends SimpleListProperty<CompData> implements P2DataList<CompData> {

    public String TAG = "CompList";

    private final ObservableList<CompData> undoList = FXCollections.observableArrayList();

    public CompList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller CompData";
    }

    @Override
    public CompData getNewItem() {
        return new CompData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(CompData.class)) {
            add((CompData) obj);
        }
    }

    public void init() {
        forEach(CompDataProps::init);
    }

    public ObservableList<CompData> getUndoList() {
        return undoList;
    }

    public synchronized void addBlackDataToUndoList(List<CompData> list) {
        undoList.setAll(list);
    }

    public synchronized void undoBlackData() {
        if (undoList.isEmpty()) {
            return;
        }
        addAll(undoList);
        undoList.clear();
    }

    public int up(int idx, boolean up) {
        final CompData compData = this.remove(idx);
        int newIdx = idx;
        if (up) {
            if (newIdx > 0) {
                --newIdx;
            }
        } else if (newIdx < size()) {
            ++newIdx;
        }
        this.add(newIdx, compData);
        return newIdx;
    }
}
