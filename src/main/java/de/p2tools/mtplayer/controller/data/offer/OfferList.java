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

package de.p2tools.mtplayer.controller.data.offer;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class OfferList extends SimpleListProperty<OfferData> implements P2DataList<OfferData> {

    public static final String TAG = "OfferList";
    private final ObservableList<OfferData> undoList = FXCollections.observableArrayList();

    public OfferList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Filter-Vorschläge";
    }

    @Override
    public OfferData getNewItem() {
        return new OfferData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(OfferData.class)) {
            add((OfferData) obj);
        }
    }

    public void init() {
        addDataToUndoList(this);
        clear();
        add(new OfferData("#:"));
        add(new OfferData("#:.*"));
        add(new OfferData("#:.*.*"));
        add(new OfferData("Nachrichten"));
        add(new OfferData("Sport"));
    }

    public int top(int idx, boolean up) {
        OfferData replace = remove(idx);
        int ret;
        if (up) {
            add(0, replace);
            ret = 0;
        } else {
            add(replace);
            ret = getSize() - 1;
        }
        return ret;
    }

    public int up(int idx, boolean up) {
        OfferData replace = remove(idx);
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

    public ObservableList<OfferData> getUndoList() {
        return undoList;
    }

    public synchronized void addDataToUndoList(List<OfferData> list) {
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
