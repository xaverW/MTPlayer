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

package de.p2tools.mtplayer.controller.data.setdata;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

@SuppressWarnings("serial")
public class ProgramList extends SimpleListProperty<ProgramData> implements P2DataList<ProgramData> {
    public static final String TAG = "ProgramList";
    private final ObservableList<ProgramData> undoList = FXCollections.observableArrayList();

    public ProgramList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste der Programme";
    }

    @Override
    public ProgramData getNewItem() {
        return new ProgramData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(ProgramData.class)) {
            super.add((ProgramData) obj);
        }
    }

    public int moveUp(int idx, boolean moveUp) {
        final ProgramData prog = this.remove(idx);
        int neu = idx;
        if (moveUp) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        this.add(neu, prog);
        return neu;
    }

    public ObservableList<ProgramData> getUndoList() {
        return undoList;
    }

    public synchronized void addDataToUndoList(List<ProgramData> list) {
        undoList.clear();
        undoList.addAll(list);
    }

    public synchronized void undoData() {
        if (undoList.isEmpty()) {
            return;
        }
        addAll(undoList);
        undoList.clear();
    }
}
