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

package de.p2tools.mtplayer.controller.data.utdata;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class UtDataList extends SimpleListProperty<UtData> implements P2DataList<UtData> {

    public final String TAG;
    private final ObservableList<UtData> undoList = FXCollections.observableArrayList();

    public UtDataList(boolean ut) {
        super(FXCollections.observableArrayList());
        TAG = ut ? "UtDataList" : "MarkDataList";
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Filme mit UT im Film";
    }

    @Override
    public UtData getNewItem() {
        return new UtData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(UtData.class)) {
            add((UtData) obj);
        }
    }

    public void init(boolean ut) {
        addDataToUndoList(this);
        clear();
        if (ut) {
            add(new UtData("ARTE", "(mit Untertitel)"));
            add(new UtData("ARTE", "(Originalversion mit Untertitel)"));
            add(new UtData("ZDF", "- OmU"));
            add(new UtData("", "(OmU)"));
        } else {
            add(new UtData("", "Gebärdensprache"));
        }
    }

    public int up(int idx, boolean up) {
        UtData replace = remove(idx);
        int neu = idx;
        if (up) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        add(neu, replace);
        return neu;
    }

    public ObservableList<UtData> getUndoList() {
        return undoList;
    }

    public synchronized void addDataToUndoList(List<UtData> list) {
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
