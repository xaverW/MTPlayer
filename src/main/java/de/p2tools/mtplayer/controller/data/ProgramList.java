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

package de.p2tools.mtplayer.controller.data;

import de.p2tools.p2lib.configfile.pdata.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

@SuppressWarnings("serial")
public class ProgramList extends SimpleListProperty<ProgramData> implements PDataList<ProgramData> {
    public static final String TAG = "ProgramList";

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

}
