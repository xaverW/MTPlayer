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

package de.mtplayer.mtp.controller.data;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Iterator;

@SuppressWarnings("serial")
public class ProgList extends SimpleListProperty<ProgData> {

    public ProgList() {
        super(FXCollections.observableArrayList());
    }

    public ProgData remove(String name) {
        ProgData ret = null;
        final Iterator<ProgData> it = iterator();
        ProgData prog;
        while (it.hasNext()) {
            prog = it.next();
            if (prog.getName().equals(name)) {
                it.remove();
                ret = prog;
                break;
            }
        }
        return ret;
    }

    public int auf(int idx, boolean auf) {
        final ProgData prog = this.remove(idx);
        int neu = idx;
        if (auf) {
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
