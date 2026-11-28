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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SortList extends SimpleListProperty<SortData> implements P2DataList<SortData> {

    public final String TAG = "SortList";
    private final ProgData progData;

    private final ObservableList<SortData> undoList = FXCollections.observableArrayList();

    public SortList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller SortData";
    }

    @Override
    public SortData getNewItem() {
        return new SortData("");
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(SortData.class)) {
            add((SortData) obj);
        }
    }

    public void init() {
        if (isEmpty()) {
            SortData sortData = new SortData("Tatort / Sport");
            sortData.getCompList().add(new CompData("Tatort"));
            sortData.getCompList().add(new CompData("Sport"));
            add(sortData);


            // #:.*\(S\d{1,2}\/E\d{1,2}\).*
            // #:.*\(\d{1,2}\).*
            // #:.*\(\d{1,2}\/\d{1,2}\).*
            sortData = new SortData("Serien");
            sortData.getCompList().add(new CompData("#:.*\\(S\\d{1,2}\\/E\\d{1,2}\\).*"));
            sortData.getCompList().add(new CompData("#:.*\\(\\d{1,2}\\).*"));
            sortData.getCompList().add(new CompData("#:.*\\(\\d{1,2}\\/\\d{1,2}\\).*"));
            add(sortData);
        }
    }

    public ObservableList<SortData> getUndoList() {
        return undoList;
    }

    public synchronized void addBlackDataToUndoList(List<SortData> list) {
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
        final SortData sortData = this.remove(idx);
        int newIdx = idx;
        if (up) {
            if (newIdx > 0) {
                --newIdx;
            }
        } else if (newIdx < size()) {
            ++newIdx;
        }
        this.add(newIdx, sortData);
        return newIdx;
    }
}
